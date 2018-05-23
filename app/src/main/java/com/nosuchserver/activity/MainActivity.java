package com.nosuchserver.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nosuchserver.data.LocalDataIOUtils;
import com.nosuchserver.data.LocalSavaDataBean;
import com.nosuchserver.xposedmodules.utils.TagLog;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rere on 18-5-9.
 */

public class MainActivity extends TestBaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private LinearLayout mLayoutWifi;

    public static List<String> printWifiInfo(Context context) {
        TagLog.i(TAG, "printWifiInfo() : ");
        List<String> mWifiStrList = new ArrayList<>();

        WifiManager wifiManager = (WifiManager)
                context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> scanResults = wifiManager.getScanResults();
        if (null == scanResults || 0 == scanResults.size()) {
            TagLog.i(TAG, "getWifiInfo() : " + "wifi info is null");
            return mWifiStrList;
        }

        TagLog.i(TAG, "getWifiInfo() : start.");

        StringBuffer buffer;
        for (int i = 0; i < scanResults.size(); i++) {
            ScanResult scanResult = scanResults.get(i);
            buffer = new StringBuffer();
            buffer.append("wifi " + i + " : \n" + "" +
                    "\tSSID = " + scanResult.SSID + ",\n"
                    + "\tBSSID = " + scanResult.BSSID + ",");
            TagLog.i(TAG, "getWifiInfo() : " + buffer.toString());
            mWifiStrList.add(buffer.toString());
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
        TagLog.i(TAG, "printWifiInfo() : " + " oriList = " + oriList + ",");

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
        TagLog.i(TAG, "printWifiInfo() : " + " oriList = " + oriList + ",");

        LocalDataIOUtils.saveLocalDataToFile(dataBean);

        TagLog.i(TAG, "getWifiInfo() : end.");
        return mWifiStrList;
    }

    @Override
    protected void addViews(final LinearLayout layout) {
        super.addViews(layout);
        getTextview(layout, "Hello Xposed");

        getButton(layout, "get Wifis", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get Wifis
                mLayoutWifi = new LinearLayout(mContext);
                mLayoutWifi.setOrientation(LinearLayout.VERTICAL);
                layout.addView(mLayoutWifi,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                scanWifis(mContext);
            }
        });

        getButton(layout, "read file", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // read file
                LocalSavaDataBean localSavaDataBean = LocalDataIOUtils.readLocalDataFromFile();
                TagLog.i(TAG, "onClick() : " + " localSavaDataBean = " + localSavaDataBean + ",");
            }
        });

        getButton(layout, "save init data", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save init data
                File file = null;
                try {
                    file = new File(new URI("file:///android_assets/save.json"));
                } catch (URISyntaxException e) {
                    TagLog.e(TAG, "onClick() : " + e.getMessage());
                    return;
                }
                LocalSavaDataBean assetSaveData = LocalDataIOUtils.readLocalDataFromFile(file);
                TagLog.i(TAG, "onClick() : " + " assetSaveData = " + assetSaveData + ",");
            }
        });

        getButton(layout, "jump Config act", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // jump Config act
                ConfigActivity.start(mContext);
            }
        });

        getButton(layout, "save to data/data", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save to data/data
                LocalSavaDataBean data = LocalDataIOUtils.readLocalDataFromFile();
                LocalDataIOUtils.saveLocalDataToFile(data);
            }
        });

        getButton(layout, "get data/data", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get data/data
                LocalSavaDataBean data = LocalDataIOUtils.readLocalDataFromFile();
                TagLog.i(TAG, "onClick() : " + " data = " + data + ",");
            }
        });
    }

    private void scanWifis(Context context) {
        WifiManager wifiManager = (WifiManager)
                context.getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TagLog.i(TAG, "wifi scan onReceive() : " + " intent = " + intent + ",");
                List<String> strings = printWifiInfo(context);

                // draw in layout
                mLayoutWifi.removeAllViews();
                for (String string : strings) {
                    TextView textView = new TextView(context);
                    textView.setText(string);
                    mLayoutWifi.addView(textView,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                }

                context.unregisterReceiver(this);
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifiManager.startScan();
        TagLog.i(TAG, "getWifiInfo() : " + "start Scan");
    }
}
