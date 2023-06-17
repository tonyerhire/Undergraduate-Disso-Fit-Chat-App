package com.android.FYP.receivers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.android.FYP.Factory;
import com.android.FYP.models.WalkingMode;
import com.android.FYP.persistence.StepCountPersistenceHelper;
import com.android.FYP.persistence.WalkingModePersistenceHelper;
import com.android.FYP.utils.StepDetectionServiceHelper;

/**
 * Stores the current step count in database.
 *
 * @author Anthony Awobasivwe
 */
public class StepCountPersistenceReceiver extends WakefulBroadcastReceiver {
    private static final String LOG_CLASS = StepCountPersistenceReceiver.class.getName();
    private WalkingMode oldWalkingMode;
    /**
     * The application context
     */
    private Context context;
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StepCountPersistenceHelper.storeStepCounts(service, context, oldWalkingMode);
            StepDetectionServiceHelper.stopAllIfNotRequired(false, context);
            context.getApplicationContext().unbindService(mServiceConnection);
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_CLASS, "Storing the steps");
        this.context = context.getApplicationContext();
        if (intent.hasExtra(WalkingModePersistenceHelper.BROADCAST_EXTRA_OLD_WALKING_MODE)) {
            oldWalkingMode = WalkingModePersistenceHelper.getItem(intent.getLongExtra(WalkingModePersistenceHelper.BROADCAST_EXTRA_OLD_WALKING_MODE, (long) -1), context);
        }
        if(oldWalkingMode == null){
            oldWalkingMode = WalkingModePersistenceHelper.getActiveMode(context);
        }
        // bind to service
        Intent serviceIntent = new Intent(context, Factory.getStepDetectorServiceClass(context.getPackageManager()));
        context.getApplicationContext().bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

    }
}
