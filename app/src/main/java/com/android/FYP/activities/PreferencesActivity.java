package com.android.FYP.activities;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.android.FYP.R;
import com.android.FYP.utils.AndroidVersionHelper;
import com.android.FYP.utils.StepDetectionServiceHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PreferencesActivity extends AppCompatPreferenceActivity {
    private static Map<String, String> additionalSummaryTexts;
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                String additionalSummaryText = additionalSummaryTexts.get(preference.getKey());
                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? (((additionalSummaryText != null) ? additionalSummaryText : "") + listPreference.getEntries()[index])
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    public PreferencesActivity() {
        super();
        this.additionalSummaryTexts = new HashMap<>();
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private static void bindPreferenceSummaryToLongValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getLong(preference.getKey(), 0));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
                //|| HelpFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            additionalSummaryTexts.put(getString(R.string.pref_accelerometer_threshold), getString(R.string.pref_summary_accelerometer_threshold));

            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_unit_of_length)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_daily_step_goal)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_weight)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_gender)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_accelerometer_threshold)));


            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            sharedPref.registerOnSharedPreferenceChangeListener(this);

            if (AndroidVersionHelper.supportsStepDetector(getActivity().getPackageManager())) {
                // hide accelerometer threshold if hardware detection is used.
                PreferenceScreen screen = getPreferenceScreen();
                ListPreference accelerometerThresholdPref = (ListPreference) findPreference(getString(R.string.pref_accelerometer_threshold));
                screen.removePreference(accelerometerThresholdPref);
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                this.getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onDetach() {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            sharedPref.unregisterOnSharedPreferenceChangeListener(this);
            super.onDetach();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // Detect changes on preferences and update our internal variable
            if (key.equals(getString(R.string.pref_step_counter_enabled))) {
                boolean isEnabled = sharedPreferences.getBoolean(getString(R.string.pref_step_counter_enabled), true);
                if (isEnabled) {
                    StepDetectionServiceHelper.startAllIfEnabled(getActivity().getApplicationContext());
                } else {
                    StepDetectionServiceHelper.stopAllIfNotRequired(getActivity().getApplicationContext());
                }
            }
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToLongValue(findPreference(getString(R.string.pref_notification_motivation_alert_time)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_notification_motivation_alert_criterion)));

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            sharedPref.registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                this.getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onDetach() {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            sharedPref.unregisterOnSharedPreferenceChangeListener(this);
            super.onDetach();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (isDetached()) {
                return;
            }
            // Detect changes on preferences and update our internal variable
            if (key.equals(getString(R.string.pref_notification_motivation_alert_enabled)) || key.equals(getString(R.string.pref_notification_motivation_alert_time))) {
                boolean isEnabled = sharedPreferences.getBoolean(getString(R.string.pref_notification_motivation_alert_enabled), true);
                if (isEnabled) {
                    StepDetectionServiceHelper.startAllIfEnabled(getActivity().getApplicationContext());
                } else {
                    StepDetectionServiceHelper.stopAllIfNotRequired(getActivity().getApplicationContext());
                }
            }
        }
    }

    /**
     * This fragment shows the help content.
     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public static class HelpFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.help);
//            setHasOptionsMenu(false);
//        }
//
//        @Override
//        public boolean onOptionsItemSelected(MenuItem item) {
//            int id = item.getItemId();
//            if (id == android.R.id.home) {
//                this.getActivity().onBackPressed();
//                return true;
//            }
//            return super.onOptionsItemSelected(item);
//        }
//    }
}
