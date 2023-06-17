package com.android.FYP;

import android.content.pm.PackageManager;

import com.android.FYP.services.AbstractStepDetectorService;
import com.android.FYP.services.AccelerometerStepDetectorService;
import com.android.FYP.services.HardwareStepDetectorService;
import com.android.FYP.utils.AndroidVersionHelper;

/**
 * Factory class
 *
 * @author Anthony Awobasivwe
 */
public class Factory {

    /**
     * Returns the class of the step detector service which should be used
     *
     * The used step detector service depends on different soft- and hardware preferences.
     * @param pm An instance of the android PackageManager
     * @return The class of step detector
     */
    public static Class<? extends AbstractStepDetectorService> getStepDetectorServiceClass(PackageManager pm){
        if(pm != null && AndroidVersionHelper.supportsStepDetector(pm)) {
            return HardwareStepDetectorService.class;
        }else{
            return AccelerometerStepDetectorService.class;
        }
    }
}
