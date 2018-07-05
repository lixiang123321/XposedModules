package com.nosuchserver.xposedmodules;

import android.net.wifi.ScanResult;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.nosuchserver.data.LocalDataIOUtils;
import com.nosuchserver.data.LocalSavaDataBean;
import com.nosuchserver.utils.TagLog;
import com.nosuchserver.utils.ValidControlUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.SELinuxHelper;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Xposed modules entry
 * <p>
 * Created by rere on 18-5-9.
 */
public class XposedHookLoadPackage implements IXposedHookLoadPackage {

    private static final boolean IS_MORE_LOG = false;
	private static final String TAG = XposedHookLoadPackage.class.getSimpleName();
    private static final String KEY_PACKAGE_NAMES_LIST = "com.example.rere.practice";
    private static final String CLASS_WIFI_MANAGER = "android.net.wifi.WifiManager";
    private static final String METHOD_WIFI_MANAGER = "getScanResults";
    private static final String KEY_TARGET_SSID_DEFAULT = "LoveQ";
    private static final String KEY_TARGET_BSSID_DEFAULT = "01:23:45:67:1e:b2";

    private static String KEY_TARGET_SSID = getKeyTargetSsid();

    private static String getKeyTargetSsid() {
        LocalSavaDataBean localData = getLocalDataFromFile(LocalDataIOUtils.KEY_FILE_NAME);
        if (IS_MORE_LOG) {
            TagLog.x(TAG, "getKeyTargetSsid() : " + " localData = " + localData + ",");
        }
        if (null != localData && !TextUtils.isEmpty(localData.getSsid())) {
            TagLog.x(TAG, "getKeyTargetSsid() : " + localData.getSsid());
            return localData.getSsid();
        }

        return KEY_TARGET_SSID_DEFAULT;
    }

    private static String KEY_TARGET_BSSID = getKeyTargetBssid();

    private static String getKeyTargetBssid() {
        LocalSavaDataBean localData = getLocalDataFromFile(LocalDataIOUtils.KEY_FILE_NAME);
        if (IS_MORE_LOG) {
            TagLog.x(TAG, "getKeyTargetBssid() : " + " localData = " + localData + ",");
        }
        if (null != localData && !TextUtils.isEmpty(localData.getBssidSelect())) {
            TagLog.x(TAG, "getKeyTargetBssid() : " + localData.getBssidSelect());
            return localData.getBssidSelect();
        }

        return KEY_TARGET_BSSID_DEFAULT;
    }

    // key
    public static final String KEY_PACKAGE_NAME = "com.example.rere.practice";
    public static final String KEY_EXPOSED_VIRTUAL = "io.va.exposed/virtual";
    public static final String KEY_PREFIX_DATA_USER_0 = "/data/user/0/";
    public static final String KEY_PATH_SEPARATOR = "/";
    public static final String KEY_FILES = "files";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        TagLog.x(TAG, "handleLoadPackage() : " + " lpparam = " + lpparam + ",");

        if (!KEY_PACKAGE_NAMES_LIST.contains(lpparam.packageName)) {
            TagLog.x(TAG, "handleLoadPackage() : not target package");
            return;
        }

        // 18-7-5 add invalid controll
        if (!ValidControlUtils.isValid()) {
            return;
        }

        handleTargetApp(lpparam);
    }

    private void handleTargetApp(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        TagLog.x(TAG, "handleTargetApp() : " + lpparam);
        getAndHookWifi(lpparam);
        HookEntry.hookAtLoadPackage(lpparam);
    }

    private static String readFileInXposed() throws Throwable {
        if (IS_MORE_LOG) {
            TagLog.x(TAG, "readFileInXposed() : ");
        }
        String prefix = KEY_PREFIX_DATA_USER_0;

        String fileName = prefix + KEY_EXPOSED_VIRTUAL + prefix + KEY_PACKAGE_NAME + KEY_PATH_SEPARATOR + KEY_FILES + KEY_PATH_SEPARATOR + LocalDataIOUtils.KEY_FILE_NAME;
        if (IS_MORE_LOG) {
            TagLog.x(TAG, "readFileInXposed() : " + " fileName = " + fileName + ",");
        }

        String str = getStringFromFile(fileName);
        if (IS_MORE_LOG) {
            TagLog.x(TAG, "readFileInXposed() : " + " file str = " + str + ",");
        }
        return str;
    }

    @NonNull
    private static String getStringFromFile(String fileName) throws IOException {
        TagLog.x(TAG, "getStringFromFile() : " + " fileName = " + fileName + ",");
        InputStream fileInputStream = SELinuxHelper.getAppDataFileService().getFileInputStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    private static LocalSavaDataBean getLocalDataFromFile(String fileName) {
        TagLog.x(TAG, "getLocalDataFromFile() : " + " fileName = " + fileName + ",");
        LocalSavaDataBean localSavaDataBean = null;
        try {
            String stringFromFile;
            // stringFromFile = getStringFromFile(fileName);
            stringFromFile = readFileInXposed();
            if (TextUtils.isEmpty(stringFromFile)) {
                return null;
            }
            localSavaDataBean = new Gson().fromJson(stringFromFile, LocalSavaDataBean.class);
            if (IS_MORE_LOG) {
                TagLog.x(TAG, "getLocalDataFromFile() : " + " localSavaDataBean = " + localSavaDataBean + ",");
            }
        } catch (Throwable e) {
            TagLog.e(TAG, "getLocalDataFromFile() : " + e.getLocalizedMessage());
        }
        return localSavaDataBean;
    }

    private void getAndHookWifi(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(CLASS_WIFI_MANAGER, lpparam.classLoader, METHOD_WIFI_MANAGER, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                TagLog.i(TAG, "beforeHookedMethod() : " + " param = " + param + ",");
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object paramResult = param.getResult();
                if (IS_MORE_LOG) {
                    TagLog.x(TAG, "afterHookedMethod() : " + " param.getResult() = " + paramResult + ",");
                }

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

                try {
                    TagLog.x(TAG, "afterHookedMethod() : " + " KEY_TARGET_SSID = " + KEY_TARGET_SSID + ",");
                    TagLog.x(TAG, "afterHookedMethod() : " + " KEY_TARGET_BSSID = " + KEY_TARGET_BSSID + ",");
                } catch (Exception e) {
                    TagLog.x(TAG, "afterHookedMethod() : " + e.getMessage());
                }

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

                if (IS_MORE_LOG) {
                    TagLog.x(TAG, "afterHookedMethod() : " + " param.getResult() = " + param.getResult() + ",");
                }
            }
        });
    }
}
