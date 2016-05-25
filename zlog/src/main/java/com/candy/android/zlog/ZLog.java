package com.candy.android.zlog;

import android.util.Log;

/**
 * Created by marcingawel on 25.05.2016.
 */

public class ZLog {
    private static boolean logEnabled = BuildConfig.DEBUG;

    /**
     * Initialize logging class.
     *
     * @param enabled Enable or disable logging
     */
    public static void init(boolean enabled) {
        logEnabled = enabled;
    }

    public static void e(String message) {
        if (logEnabled) {
            Log.e(getCallerClassName(), message);
        }
    }

    public static void d(String message) {
        if (logEnabled) {
            Log.d(getCallerClassName(), message);
        }
    }

    private static String getCallerClassName() {
        String callerName;
        try {
            callerName = new Exception().getStackTrace()[2].getClassName();
        } catch (Exception e) {
            callerName = "Unknown";
        }

        return callerName;
    }
}
