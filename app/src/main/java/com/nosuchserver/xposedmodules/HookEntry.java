package com.nosuchserver.xposedmodules;

import android.location.Location;
import android.view.View;

import com.nosuchserver.utils.TagLog;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by rere on 18-7-2.
 */

public class HookEntry {

    private static final String TAG = HookEntry.class.getSimpleName();

    public static void hookAtLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        TagLog.x(TAG, "getAndHookDeviceInfo() : " + " lpparam = " + lpparam + ",");
        // hookViewGetIdForTest(lpparam);

        hookLocation(lpparam);
    }

    private static void hookViewGetIdForTest(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        TagLog.x(TAG, "hookViewGetIdForTest() : ");

        XposedHelpers.findAndHookMethod(View.class.getName(), lpparam.classLoader, "getId", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);

                try {
                    hookTest();
                } catch (Throwable throwable) {
                    TagLog.x(TAG, "beforeHookedMethod() : " + throwable.getMessage());
                }

            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });



    }

    private static void hookLocation(XC_LoadPackage.LoadPackageParam lpparam) {
        TagLog.x(TAG, "hookLocation() : " + " lpparam = " + lpparam + ",");

        final double latitude = 23.15792;
        final double longtitude = 113.27324;

        XC_MethodHook xc_methodHook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                TagLog.x(TAG, "afterHookedMethod() : " + " param = " + param + ",");
                TagLog.x(TAG, "afterHookedMethod() : " + " param.method.getName() = " + param.method.getName() + ",");

                super.afterHookedMethod(param);
            }
        };
        XposedHelpers.findAndHookMethod(Location.class, "getLatitude", xc_methodHook);
        XposedHelpers.findAndHookMethod(Location.class, "getLongitude", xc_methodHook);
    }

    private static void hookTest() {
        TagLog.x(TAG, "hookTest() : ");



    }

    private static final String CLASS_GPS_MANAGER = "android.location.LocationManager";
    private static final String METHOD_GPS_MANAGER = "getLastLocation";

    private static final String CLASS_LOCATION = "android.location.Location";
    private static final String METHOD_LOCATION = "getLongitude";
    private static final String METHOD_LOCATION2 = "getLatitude";

    private void getAndHookGps(XC_LoadPackage.LoadPackageParam lpparam) {
        // gps
        /*findAndHookMethod(CLASS_GPS_MANAGER, lpparam.classLoader, METHOD_GPS_MANAGER, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                TagLog.x(TAG, "gps afterHookedMethod() : " + " param = " + param + ",");
            }
        });*/

        // location
        findAndHookMethod(CLASS_LOCATION, lpparam.classLoader, METHOD_LOCATION, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                TagLog.x(TAG, METHOD_LOCATION + "afterHookedMethod() : " + " param = " + param + ",");
            }
        });

        findAndHookMethod(CLASS_LOCATION, lpparam.classLoader, METHOD_LOCATION2, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                TagLog.x(TAG, METHOD_LOCATION2 + "afterHookedMethod() : " + " param = " + param + ",");
            }
        });
    }

}
