package com.nosuchserver.xposedmodules;

import android.net.wifi.ScanResult;
import android.text.TextUtils;

import com.nosuchserver.data.LocalDataIOUtils;
import com.nosuchserver.data.LocalSavaDataBean;
import com.nosuchserver.xposedmodules.utils.TagLog;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.services.BaseService;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Xposed modules entry
 * <p>
 * Created by rere on 18-5-9.
 */
public class XposedHookLoadPackage implements IXposedHookLoadPackage {

    // config
    private static final String KEY_TARGET_APP = "com.example.rere.practice";
    private static final String KEY_TARGET_SSID_DEFAULT = "Test";
    private static final String KEY_TARGET_BSSID_DEFAULT = "12:34:56:78:90:12";

    private static final String KEY_TARGET_SSID = getKeyTargetSsid();

    private static String getKeyTargetSsid() {
        LocalSavaDataBean localData = LocalDataIOUtils.readLocalDataFromFileInXposed();
        TagLog.x(TAG, "getKeyTargetSsid() : " + " localData = " + localData + ",");
        if (null != localData && !TextUtils.isEmpty(localData.getSsid())) {
            TagLog.x(TAG, "getKeyTargetSsid() : " + localData.getSsid());
            //return localData.getSsid();
        }

        return KEY_TARGET_SSID_DEFAULT;
    }

    private static final String KEY_TARGET_BSSID = getKeyTargetBssid();

    private static String getKeyTargetBssid() {
        LocalSavaDataBean localData = LocalDataIOUtils.readLocalDataFromFileInXposed();
        TagLog.x(TAG, "getKeyTargetBssid() : " + " localData = " + localData + ",");
        if (null != localData && !TextUtils.isEmpty(localData.getBssidSelect())) {
            TagLog.x(TAG, "getKeyTargetBssid() : " + localData.getBssidSelect());
            //return localData.getBssidSelect();
        }

        return KEY_TARGET_BSSID_DEFAULT;
    }


    private static final String CLASS_WIFI_MANAGER = "android.net.wifi.WifiManager";
    private static final String METHOD_WIFI_MANAGER = "getScanResults";

    private static final String CLASS_GPS_MANAGER = "android.location.LocationManager";
    private static final String METHOD_GPS_MANAGER = "getLastLocation";

    private static final String CLASS_LOCATION = "android.location.Location";
    private static final String METHOD_LOCATION = "getLongitude";
    private static final String METHOD_LOCATION2 = "getLatitude";


    private static final String TAG = XposedHookLoadPackage.class.getSimpleName();

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //TagLog.x(TAG, "handleLoadPackage() : " + lpparam.packageName);

        if (!KEY_TARGET_APP.contains(lpparam.packageName)) {
            return;
        }

        TagLog.x(TAG, "handleLoadPackage() : " + "target appear. : " + lpparam.packageName);
        handleTargetApp(lpparam);
    }

    private void handleTargetApp(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        TagLog.x(TAG, "handleTargetApp() : " + lpparam);

        getAndHookWifi(lpparam);
        getAndHookGps(lpparam);
    }

    private XC_MethodHook.Unhook getAndHookWifi(XC_LoadPackage.LoadPackageParam lpparam) {
        return findAndHookMethod(CLASS_WIFI_MANAGER, lpparam.classLoader, METHOD_WIFI_MANAGER, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                TagLog.i(TAG, "beforeHookedMethod() : " + " param = " + param + ",");
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object paramResult = param.getResult();
                TagLog.x(TAG, "afterHookedMethod() : " + " param.getResult() = " + paramResult + ",");

                boolean isHookSuccess = false;
                boolean isHasTargetSSID = false;
                if (paramResult instanceof List) {
                    for (Object o : ((List) (paramResult))) {
                        if (o instanceof ScanResult) {
                            isHookSuccess = true;
                            if (KEY_TARGET_SSID.equals(((ScanResult) o).SSID)) {
                                isHasTargetSSID = true;
                            }
                        }
                    }
                }

                TagLog.x(TAG, "afterHookedMethod() : " + " isHookSuccess = " + isHookSuccess + ",");
                TagLog.x(TAG, "afterHookedMethod() : " + " isHasTargetSSID = " + isHasTargetSSID + ",");

                if (isHookSuccess && !isHasTargetSSID) {
                    ScanResult scanResult0 = (ScanResult) ((List) paramResult).get(0);
                    scanResult0.SSID = KEY_TARGET_SSID;
                    scanResult0.BSSID = KEY_TARGET_BSSID;
                }

                /*// remove all target SSID for test
                if (isHookSuccess && isHasTargetSSID) {
                    for (Object o : ((List) (paramResult))) {
                        if (o instanceof ScanResult) {
                            ((ScanResult) o).SSID = "ABC";
                        }
                    }
                }*/

                TagLog.x(TAG, "afterHookedMethod() : " + " param.getResult() = " + param.getResult() + ",");
            }
        });
    }

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
