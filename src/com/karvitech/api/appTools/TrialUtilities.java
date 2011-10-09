//#preprocess
/*
 * TrialUtilities.java
 *
 * © Karvi Technology Inc, 2003-2010
 * Confidential and proprietary.
 */

package com.karvitech.api.appTools;
import java.util.*;
import net.rim.device.api.util.*;




/**
 * 
 */
public class TrialUtilities {
    public static final int TRIAL_DAYS = 7;
    
    private TrialUtilities() {    };
    
    public static boolean isTrial() {
        boolean result = false;
//#ifdef TRIAL_VERSION 
        result = true;
//#endif
        return result;
    }
    
    public static boolean isTrialExpired(long fisrtRunTime) {
        long runTime = System.currentTimeMillis() - fisrtRunTime;
        if(runTime >= DateTimeUtilities.ONEDAY*TRIAL_DAYS)  {
            return true;
        }
        else {
            return false;
        }        
    }
    
    public static int trialDaysRemaining(long fisrtRunTime) {
        long runTime = System.currentTimeMillis() - fisrtRunTime;
        int rundays =   (int)(runTime/DateTimeUtilities.ONEDAY);
        int remainingDays = TRIAL_DAYS - rundays;
        if(remainingDays < 0) {
            return 0;
        }
        else {
            return remainingDays;
        }
    }    
}
