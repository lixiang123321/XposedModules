package com.nosuchserver.data;

import android.os.Environment;
import android.support.annotation.NonNull;

import com.nosuchserver.utils.TagLog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * FileUtils
 *
 * Created by rere on 18-5-10.
 */

public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    public static String getStrFromFile(File file) throws Exception {
        TagLog.i(TAG, "getStrFromFile() : " + " file = " + file + ",");

        if (null == file) {
            TagLog.w(TAG, "getStrFromFile() : " + "file is null, return.");
            return "";
        }

        FileInputStream fin = new FileInputStream(file);
        String str = readInputStream(fin);

        TagLog.i(TAG, "getStrFromFile() : " + " str = " + str + ",");
        return str;
    }

    @NonNull
    public static String readInputStream(InputStream fin) throws IOException {
        if (null == fin) {
            return "";
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        String str = sb.toString();

        // close
        reader.close();
        fin.close();
        return str;
    }

    public static void setStrToFile(String str, File file) throws Exception {
        TagLog.i(TAG, "setStrToFile() : " + " str = " + str + "," + " file = " + file + ",");

        if (null == file) {
            TagLog.w(TAG, "setStrToFile() : " + "file is null, return.");
            return;
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
        writer.write(str);

        writer.close();
        fileOutputStream.close();
    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }


}

