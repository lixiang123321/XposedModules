package com.nosuchserver.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.nosuchserver.data.FileUtils;
import com.nosuchserver.data.LocalDataIOUtils;
import com.nosuchserver.data.LocalSavaDataBean;
import com.nosuchserver.utils.TagLog;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rere on 18-5-9.
 */

public class MainActivity extends TestBaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
	
    private static final int KEY_PERMISSIONS_REQUEST = 1001;

    private WifiManager mWifiManager;
    private BroadcastReceiver mBroadcastReceiver;
    private LinearLayout mLayoutWifi;
    private LinearLayout mContentLayout;

    // data
    // 18-7-3 save or not flag
    private boolean mIsSaveToFile = false;

    @Override
    protected void addViews(LinearLayout layout) {
        this.mContentLayout = layout;
        /*getButton(layout, "create data", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create data
                LocalSavaDataBean localSavaDataBean = new LocalSavaDataBean();
                localSavaDataBean.setBssidGroup(new ArrayList<>());

                localSavaDataBean.setSsid("TP-LINK_0D54");
                localSavaDataBean.setBssidSelect("14:75:90:dd:0d:54");

                List<LocalSavaDataBean.BssidGroupBean> bssidGroup = localSavaDataBean.getBssidGroup();
                LocalSavaDataBean.BssidGroupBean e = new LocalSavaDataBean.BssidGroupBean();
                e.setSsid("TP-LINK_0D54");
                e.setBssid("14:75:90:dd:0d:54");
                e.setRecordTime(System.currentTimeMillis());
                bssidGroup.add(e);

                LocalSavaDataBean.BssidGroupBean e1 = new LocalSavaDataBean.BssidGroupBean();
                e1.setSsid("TP-LINK_0D54");
                e1.setBssid("14:75:90:dd:0d:55");
                e1.setRecordTime(System.currentTimeMillis());
                bssidGroup.add(e1);

                String s = new Gson().toJson(localSavaDataBean);
                TagLog.i(TAG, "onClick() : " + " localSavaDataBean = " + s + ",");
            }
        });*/

        getButton(layout, "copy assets to data", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // copy assets to data
                try {
                    LocalSavaDataBean localSavaDataBean1 = LocalDataIOUtils.readLocalDataFromFile();
                    if (null != localSavaDataBean1) {
                        TagLog.w(TAG, "onClick() : " + "is already has data file, dont copy");
                        return;
                    }

                    InputStream open = getAssets().open(LocalDataIOUtils.KEY_FILE_NAME);
                    String fileStr = FileUtils.readInputStream(open);
                    TagLog.i(TAG, "onClick() : " + " fileStr = " + fileStr + ",");
                    LocalSavaDataBean localSavaDataBean = new Gson().fromJson(fileStr, LocalSavaDataBean.class);
                    if (null != localSavaDataBean) {
                        LocalDataIOUtils.saveLocalDataToFile(localSavaDataBean);
                    }
                } catch (IOException e) {
                    TagLog.e(TAG, "onClick() : " + e.getLocalizedMessage());
                }
            }
        }).performClick();

        // is save to file;
        addIsSaveToFileToggleButton(layout);

        getButton(layout, "scan wifi and save list to file", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // scan wifi and save list to file
                scanWifisWifiPermissionCheck();
            }
        });

        getButton(layout, "jump to wifi config", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // jump to wifi config
                ConfigActivity.start(mContext);
            }
        });

/*
        getButton(layout, "call method for xposed hook", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call Integer for xposed hook
                View view = new View(mContext);
                view.getId();
            }
        });
*/
    }

    private void addIsSaveToFileToggleButton(LinearLayout layout) {
        TagLog.i(TAG, "addIsSaveToFileToggleButton() : ");
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        layout.addView(linearLayout);

        TextView textView = new TextView(mContext);
        textView.setText(" mIsSaveToFile = ");

        ToggleButton toggleButton = new ToggleButton(mContext);
        toggleButton.setChecked(mIsSaveToFile);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mIsSaveToFile = isChecked;
            }
        });

        linearLayout.addView(textView);
        linearLayout.addView(toggleButton);
    }

    private void scanWifisWifiPermissionCheck() {
        TagLog.i(TAG, "scanWifisWifiPermissionCheck() : ");
        final String[] permissions = new String[]{
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,

        };
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mContext, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                TagLog.i(TAG, "scanWifisWifiPermissionCheck() : request permissions");
                ActivityCompat.requestPermissions(this,
                        permissions,
                        KEY_PERMISSIONS_REQUEST);
                return;
            }
        }
        scanWifis();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case KEY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scanWifis();
                } else {
                    Toast.makeText(mContext, "permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void scanWifis() {
        TagLog.i(TAG, "scanWifis() : ");
        if (null == mWifiManager) {
            mWifiManager = (WifiManager)
                    getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }
        if (null == mBroadcastReceiver) {
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    TagLog.i(TAG, "wifi scan onReceive() : " + " intent = " + intent + ",");
                    onWifiScanBroadcastReceive();
                }
            };
            registerReceiver(mBroadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        }
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        mWifiManager.startScan();
        TagLog.i(TAG, "getWifiInfo() : " + "start Scan");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mBroadcastReceiver) {
            unregisterReceiver(mBroadcastReceiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mBroadcastReceiver) {
            registerReceiver(mBroadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        }
    }

    private void onWifiScanBroadcastReceive() {
        TagLog.i(TAG, "onWifiScanBroadcastReceive() : ");
        if (null == mWifiManager) {
            mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }

        List<ScanResult> scanResults = mWifiManager.getScanResults();
        if (null == scanResults || 0 == scanResults.size()) {
            TagLog.i(TAG, "getWifiInfo() : " + "wifi info is null");
            return;
        }

        TagLog.i(TAG, "onWifiScanBroadcastReceive() : " + " scanResults.size() = " + scanResults.size() + ",");
        printWifiInfosToView(scanResults);

        TagLog.i(TAG, "onWifiScanBroadcastReceive() : " + " mIsSaveToFile = " + mIsSaveToFile + ",");
        if (mIsSaveToFile) {
            saveWifiInfosToFile(scanResults);
        }
    }

    private void printWifiInfosToView(List<ScanResult> scanResults) {
        TagLog.i(TAG, "printWifiInfosToView() : " + " scanResults = " + scanResults + ",");
        if (null == scanResults || 0 == scanResults.size()) {
            TagLog.i(TAG, "getWifiInfo() : " + "wifi info is null");
            return;
        }
        if (null != mLayoutWifi) {
            ((ViewGroup) mLayoutWifi.getParent()).removeView(mLayoutWifi);
        }

        mLayoutWifi = new LinearLayout(mContext);
        mLayoutWifi.setOrientation(LinearLayout.VERTICAL);
        mContentLayout.addView(mLayoutWifi,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        // add wifis
        for (ScanResult scanResult : scanResults) {
            TextView textView = new TextView(mContext);
            textView.setText(scanResult.SSID + " : " + scanResult.BSSID);
            mLayoutWifi.addView(textView,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    private void saveWifiInfosToFile(List<ScanResult> scanResults) {
        TagLog.i(TAG, "saveWifiInfosToFile() : " + " scanResults = " + scanResults + ",");
        if (null == scanResults || 0 == scanResults.size()) {
            TagLog.i(TAG, "getWifiInfo() : " + "wifi info is null");
            return;
        }
        // save to file
        LocalSavaDataBean dataBean = LocalDataIOUtils.readLocalDataFromFile();
        if (null == dataBean) {
            dataBean = new LocalSavaDataBean();
        }
        if (null == dataBean.getBssidGroup()) {
            dataBean.setBssidGroup(new ArrayList<LocalSavaDataBean.BssidGroupBean>());
        }
        List<LocalSavaDataBean.BssidGroupBean> oriList = dataBean.getBssidGroup();
        TagLog.i(TAG, "saveWifiInfosToFile() : " + " oriList = " + oriList + ",");

        // 18-5-10 filter target, sort by time
        ArrayList<LocalSavaDataBean.BssidGroupBean> currentList = new ArrayList<>();
        for (int i = 0; i < scanResults.size(); i++) {
            ScanResult scanResult = scanResults.get(i);

            if (!TextUtils.isEmpty(dataBean.getSsid()) && !dataBean.getSsid().equals(scanResult.SSID)) {
                // not our target ssid
                continue;
            }

            boolean isAlreadyAdd = false;
            for (int j = 0; j < oriList.size(); j++) {
                if (oriList.get(j).getSsid().equals(scanResult.SSID)
                        && oriList.get(j).getBssid().equals(scanResult.BSSID)) {
                    // update time
                    oriList.get(j).setRecordTime(System.currentTimeMillis());
                    isAlreadyAdd = true;
                    break;
                }
            }

            if (!isAlreadyAdd) {
                LocalSavaDataBean.BssidGroupBean e
                        = new LocalSavaDataBean.BssidGroupBean();
                e.setSsid(scanResult.SSID);
                e.setBssid(scanResult.BSSID);
                e.setRecordTime(System.currentTimeMillis());
                currentList.add(e);
            }

        }
        // add new list
        oriList.addAll(currentList);

        LocalSavaDataBean.BssidGroupBean.sort(oriList);
        TagLog.i(TAG, "saveWifiInfosToFile() : " + " oriList = " + oriList + ",");

        LocalDataIOUtils.saveLocalDataToFile(dataBean);
    }


}
