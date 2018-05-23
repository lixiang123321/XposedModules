package com.nosuchserver.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;

import com.nosuchserver.xposedmodules.utils.TagLog;


/**
 * base activity
 *
 * Created by rere on 2017/1/20.
 */

public abstract class BaseActivity extends Activity{

    protected final String TAG = getClass().getSimpleName();

    protected Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TagLog.i(TAG, "onCreate()");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TagLog.i(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        TagLog.i(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        TagLog.i(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TagLog.i(TAG, "onDestroy()");
    }

    @Override
    public void finish() {
        super.finish();
        TagLog.i(TAG, "finish()");
    }
}
