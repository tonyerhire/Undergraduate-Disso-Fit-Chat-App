package com.android.FYP.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.android.FYP.MainActivity;
import com.android.FYP.R;
import com.android.FYP.fragments.DailyReportFragment;
import com.android.FYP.fragments.MainFragment;
import com.android.FYP.fragments.MonthlyReportFragment;
import com.android.FYP.fragments.WeeklyReportFragment;
import com.android.FYP.utils.StepDetectionServiceHelper;

/**
 * Main view incl. navigation drawer and fragments
 *
     * @author Anthony Awobasivwe

 */
public class MainActivity2 extends BaseActivity implements DailyReportFragment.OnFragmentInteractionListener, WeeklyReportFragment.OnFragmentInteractionListener, MonthlyReportFragment.OnFragmentInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // init preferences
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_notification, false);

        // Load first view
        final android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, new MainFragment(), "MainFragment");
        fragmentTransaction.commit();

        // Start step detection if enabled and not yet started
        StepDetectionServiceHelper.startAllIfEnabled(this);
        //Log.i(LOG_TAG, "MainActivity2 initialized");
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.menu_home;
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(MainActivity2.this , MainActivity.class);
        startActivity(intent);
    }

}
