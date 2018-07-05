package com.nosuchserver.utils;

import android.util.Log;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by rere on 2016/1/11.
 */
public class TagLog {

    private static boolean isDebug = true;

    private static final int LOG_LENGTH = 3000; //分段打印Log

    private static final String PRE_TAG = "XposedModules/";

    private static final boolean IS_XPOSED_MODE = false;

    public static void x(String tag, Object msg) {
        if (isDebug && null != msg) {
            if (msg.toString().length() < LOG_LENGTH) {
                XposedBridge.log(PRE_TAG + tag + ":" + msg.toString());
            } else {
                // msg过长
                String str1 = msg.toString().substring(0, LOG_LENGTH);
                XposedBridge.log(PRE_TAG + tag + ":" + str1);
                x(PRE_TAG + tag, msg.toString().substring(LOG_LENGTH));
            }
        }
    }

    public static void d(String tag, Object msg) {
        if (IS_XPOSED_MODE) {
            x(tag, msg);
        }
        if (isDebug && null != msg) {
            if (msg.toString().length() < LOG_LENGTH) {
                Log.d(PRE_TAG + tag, msg.toString());
            } else {
                // msg过长
                String str1 = msg.toString().substring(0, LOG_LENGTH);
                Log.d(PRE_TAG + tag, str1);
                d(PRE_TAG + tag, msg.toString().substring(LOG_LENGTH));
            }
        }
    }

    public static void i(String tag, Object msg) {
        if (IS_XPOSED_MODE) {
            x(tag, msg);
        }
        if (isDebug && null != msg) {
            if (msg.toString().length() < LOG_LENGTH) {
                Log.i(PRE_TAG + tag, msg.toString());
            } else {
                // msg过长
                String str1 = msg.toString().substring(0, LOG_LENGTH);
                Log.i(PRE_TAG + tag, str1);
                i(PRE_TAG + tag, msg.toString().substring(LOG_LENGTH));
            }
        }
    }

    public static void v(String tag, Object msg) {
        if (IS_XPOSED_MODE) {
            x(tag, msg);
        }
        if (isDebug && null != msg) {
            if (msg.toString().length() < LOG_LENGTH) {
                Log.v(PRE_TAG + tag, msg.toString());
            } else {
                // msg过长
                String str1 = msg.toString().substring(0, LOG_LENGTH);
                Log.v(PRE_TAG + tag, str1);
                v(PRE_TAG + tag, msg.toString().substring(LOG_LENGTH));
            }
        }
    }

    public static void e(String tag, Object msg) {
        if (IS_XPOSED_MODE) {
            x(tag, msg);
        }
        if (isDebug && null != msg) {
            if (msg.toString().length() < LOG_LENGTH) {
                Log.e(PRE_TAG + tag, msg.toString());
            } else {
                // msg过长
                String str1 = msg.toString().substring(0, LOG_LENGTH);
                Log.e(PRE_TAG + tag, str1);
                e(PRE_TAG + tag, msg.toString().substring(LOG_LENGTH));
            }
        }
    }

    public static void w(String tag, Object msg) {
        if (IS_XPOSED_MODE) {
            x(tag, msg);
        }
        if (isDebug && null != msg) {
            if (msg.toString().length() < LOG_LENGTH) {
                Log.w(PRE_TAG + tag, msg.toString());
            } else {
                // msg过长
                String str1 = msg.toString().substring(0, LOG_LENGTH);
                Log.w(PRE_TAG + tag, str1);
                w(PRE_TAG + tag, msg.toString().substring(LOG_LENGTH));
            }
        }
    }

}
