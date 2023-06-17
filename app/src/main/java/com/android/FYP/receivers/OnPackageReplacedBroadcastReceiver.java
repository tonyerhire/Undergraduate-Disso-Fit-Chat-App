package com.android.FYP.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.android.FYP.R;
import com.android.FYP.utils.StepDetectionServiceHelper;

/**
 * Receives the broadcast if the own package is replaced and
 * starts the step detection and it's required services if
 * step detection is enabled.
 */
public class OnPackageReplacedBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // init preferences
        PreferenceManager.setDefaultValues(context, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(context, R.xml.pref_notification, false);

        // start all services
        StepDetectionServiceHelper.startAllIfEnabled(context);
    }
}
