package com.nosuchserver.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.nosuchserver.application.OurApplication;
import com.nosuchserver.utils.TagLog;

import java.io.File;

/**
 * LocalDataIOUtils
 *
 * Created by rere on 18-5-10.
 */

public class LocalDataIOUtils {

    public static final String KEY_FILE_NAME = "xposedWifi.json";

    private static final String TAG = LocalDataIOUtils.class.getSimpleName();


    public static LocalSavaDataBean readLocalDataFromFile() {
        return readLocalDataFromFile(getLocalDataDataFile());
    }

    public static LocalSavaDataBean readLocalDataFromFile(File file) {
        TagLog.i(TAG, "readLocalDataFromFile() : ");

        try {
            String strFromFile = FileUtils.getStrFromFile(file);
            LocalSavaDataBean localSavaDataBean
                    = new Gson().fromJson(strFromFile, LocalSavaDataBean.class);
            return localSavaDataBean;
        } catch (Exception e) {
            TagLog.e(TAG, "readLocalDataFromFile() : " + e.getMessage());
            return null;
        }
    }

    public static void saveLocalDataToFile(LocalSavaDataBean dataBean, File file) {
        TagLog.i(TAG, "saveLocalDataToFile() : " + " dataBean = " + dataBean + ",");

        String str = new Gson().toJson(dataBean, LocalSavaDataBean.class);

        if (TextUtils.isEmpty(str)) {
            TagLog.w(TAG, "saveLocalDataToFile() : " + "data is empty");
            str = "";
        }

        try {
            FileUtils.setStrToFile(str, file);
        } catch (Exception e) {
            TagLog.e(TAG, "saveLocalDataToFile() : " + e.getMessage());
        }
    }

    public static void saveLocalDataToFile(LocalSavaDataBean dataBean) {
        saveLocalDataToFile(dataBean, getLocalDataDataFile());
    }

    private static @Nullable
    File getLocalDataDataFile() {
        TagLog.i(TAG, "getLocalDataDataFile() : ");
        Context context = OurApplication.getInstance();
        return getLocalDataDataFile(context);
    }

    @NonNull
    public static File getLocalDataDataFile(Context context) {
        File filesDir = context.getFilesDir();
        TagLog.i(TAG, "getLocalDataDataFile() : " + " context = " + context + "," + "filesDir: " + filesDir);
        //TagLog.x(TAG, "getLocalDataDataFile() : " + " context = " + context + "," + "filesDir: " + filesDir);

        File file = new File(filesDir.getAbsolutePath(), KEY_FILE_NAME);
        TagLog.i(TAG, "getLocalDataDataFile() : " + "file name : " + file.getAbsolutePath());
        //TagLog.x(TAG, "getLocalDataDataFile() : " + "file name : " + file.getAbsolutePath());
        return file;
    }

}
