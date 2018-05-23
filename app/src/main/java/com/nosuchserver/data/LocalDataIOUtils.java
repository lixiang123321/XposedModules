package com.nosuchserver.data;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.nosuchserver.application.OurApplication;
import com.nosuchserver.xposedmodules.utils.TagLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.robv.android.xposed.SELinuxHelper;

/**
 * LocalDataIOUtils
 *
 * Created by rere on 18-5-10.
 */

public class LocalDataIOUtils {

    private static final String DATA_DATA_DIR = "/data/data/com.nosuchserver.xposedmodules/files";
    private static final String DIR_NAME = "xposedModules";
    private static final String FILE_NAME = "data.json";

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

    /**
     * Deprecated
     * Use {@link LocalDataIOUtils#getLocalDataDataFile()} Instead
     */
    @Deprecated
    private static @Nullable File getLocalFile() {
        TagLog.i(TAG, "getLocalFile() : ");
        File root = android.os.Environment.getExternalStorageDirectory();
        TagLog.i(TAG, "getLocalFile() : " + "External file system root: " + root);

        File dir = new File(root.getAbsolutePath() + "/" + DIR_NAME);
        dir.mkdirs();

        File file = new File(dir, FILE_NAME);
        TagLog.i(TAG, "getLocalFile() : " + "file name : " + file.getAbsolutePath());
        return file;
    }

    private static @Nullable File getLocalDataDataFile() {
        TagLog.i(TAG, "getLocalDataDataFile() : ");
        Context context = OurApplication.getInstance();
        File filesDir = context.getFilesDir();
        TagLog.i(TAG, "getLocalDataDataFile() : " + " context = " + context + ","+ "filesDir: " + filesDir);
        File dir = new File(filesDir.getAbsolutePath() + "/" + DIR_NAME);
        dir.mkdirs();

        File file = new File(dir, FILE_NAME);
        TagLog.i(TAG, "getLocalDataDataFile() : " + "file name : " + file.getAbsolutePath());
        return file;
    }

    public static LocalSavaDataBean readLocalDataFromFileInXposed() {
        TagLog.x(TAG, "readLocalDataFromFileInXposed() : ");
        try {
            String fileName = DATA_DATA_DIR + "/" + DIR_NAME + "/" + FILE_NAME;
            TagLog.x(TAG, "readLocalDataFromFileInXposed() : " + " fileName = " + fileName + ",");
            InputStream fileInputStream = SELinuxHelper.getAppDataFileService().getFileInputStream(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String str = sb.toString();
            TagLog.x(TAG, "readLocalDataFromFileInXposed() : " + " str = " + str + ",");
            LocalSavaDataBean localSavaDataBean
                    = new Gson().fromJson(str, LocalSavaDataBean.class);
            return localSavaDataBean;
        } catch (Exception e) {
            TagLog.x(TAG, "readLocalDataFromFileInXposed() : " + e.getMessage());
            return null;
        }
    }
}
