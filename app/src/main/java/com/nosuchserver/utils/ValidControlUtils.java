package com.nosuchserver.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rere on 18-7-5.
 */

public class ValidControlUtils {

    private static final String TAG = ValidControlUtils.class.getSimpleName();

    public static boolean isValid() {
        TagLog.i(TAG, "isValid() : ");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Date deadline = new Date();
        try {
            deadline = simpleDateFormat.parse("2018-08-03");
        } catch (ParseException e) {
            TagLog.e(TAG, "isValid() : " + e.getLocalizedMessage());
        }
        TagLog.i(TAG, "isValid() : " + " date = " + date + ",");
        TagLog.i(TAG, "isValid() : " + " deadline = " + deadline + ",");

        boolean isBeforeDeadline = date.before(deadline);
        TagLog.i(TAG, "isValid() : " + " isBeforeDeadline = " + isBeforeDeadline + ",");
        return isBeforeDeadline;
    }
}
