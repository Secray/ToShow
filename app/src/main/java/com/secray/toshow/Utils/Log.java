package com.secray.toshow.Utils;

/**
 * @author xiekui
 * @date 2017-7-28
 */

public class Log {
    public static final String TAG = "ToShow";
    private static boolean isDebug = true;

    public static void i(String msg) {
        if (isDebug) {
            android.util.Log.i(TAG, msg);
        }
    }

    public static void w(String msg) {
        if (isDebug) {
            android.util.Log.w(TAG, msg);
        }
    }

    public static void v(String msg) {
        if (isDebug) {
            android.util.Log.v(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (isDebug) {
            android.util.Log.e(TAG, msg);
        }
    }

    public static void d(String msg) {
        if (isDebug) {
            android.util.Log.d(TAG, msg);
        }
    }


    public static void i(String tag, String msg) {
        if (isDebug) {
            android.util.Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isDebug) {
            android.util.Log.w(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (isDebug) {
            android.util.Log.v(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            android.util.Log.e(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            android.util.Log.d(tag, msg);
        }
    }

    public static String dumpStack(Throwable throwable) {
        String stack = null;
        if (isDebug) {
            stack = android.util.Log.getStackTraceString(throwable);
        }
        return stack;
    }
}
