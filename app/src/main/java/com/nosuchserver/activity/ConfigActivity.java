package com.nosuchserver.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.nosuchserver.data.LocalDataIOUtils;
import com.nosuchserver.data.LocalSavaDataBean;
import com.nosuchserver.utils.TagLog;
import com.nosuchserver.xposedmodules.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * config activity
 * <p>
 * Created by rere on 18-5-10.
 */

public class ConfigActivity extends BaseActivity {

    private EditText mEtSsid;
    private EditText mEtBssid;
    private Spinner mSpinner;
    private View mBtnSelect;
    private Button mBtnSaveConfig;
    private ToggleButton mTbIsBssidRandomly;
    // data
    private LocalSavaDataBean mDataBean;
    private String mSpinnerSelectStr;

    public static void start(Context context) {
        context.startActivity(new Intent(context, ConfigActivity.class));
    }

    private static String convertTimestampToStr(long recordTimestamp) {
//        String format = "yyyyMMddHHmmss";
        String format = "yyyy.MM.dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(recordTimestamp));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_config);
        assignViews();
    }

    private void assignViews() {
        mEtSsid = findViewById(R.id.et_ssid);
        mEtBssid = findViewById(R.id.et_bssid);
        mSpinner = findViewById(R.id.spinner);
        mBtnSelect = findViewById(R.id.btn_select);
        mBtnSaveConfig = findViewById(R.id.btn_save_config);
        mTbIsBssidRandomly = findViewById(R.id.tb_is_select_bssid_randomly);

        setUpViews();
    }

    private void setUpViews() {
        mDataBean = LocalDataIOUtils.readLocalDataFromFile();
        TagLog.i(TAG, "setUpViews() : " + " mDataBean = " + mDataBean + ",");

        if (null == mDataBean) {
            return;
        }

        mEtSsid.setText(mDataBean.getSsid());
        mEtBssid.setText(mDataBean.getBssidSelect());

        initSpinner(mEtSsid.getText().toString(), mDataBean.getBssidGroup());
        mEtSsid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    initSpinner(mEtSsid.getText().toString(), mDataBean.getBssidGroup());
                }
            }
        });

        mBtnSaveConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveConfigTofile();
            }
        });


        mTbIsBssidRandomly.setChecked(mDataBean.isBssidRandomly());

    }

    private void initSpinner(String targetSsid, List<LocalSavaDataBean.BssidGroupBean> bssidList) {
        if (null == bssidList || bssidList.size() == 0) {
            return;
        }

        ArrayList<String> strList = new ArrayList<>();
        StringBuffer buffer;
        for (LocalSavaDataBean.BssidGroupBean bssidBean : bssidList) {
            if (!TextUtils.isEmpty(targetSsid) && !targetSsid.equals(bssidBean.getSsid())) {
                // not target ssid
                continue;
            }

            buffer = new StringBuffer();
            buffer.append(bssidBean.getBssid())
                    .append("\t\t")
                    .append(convertTimestampToStr(bssidBean.getRecordTime()));

            strList.add(buffer.toString());
        }

        ArrayAdapter<String> adapter
                = new ArrayAdapter<>(mContext, R.layout.layout_spinner_item, strList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!(view instanceof TextView)) {
                    return;
                }

                String content = ((TextView) view).getText().toString();
                mSpinnerSelectStr = content.substring(0, content.indexOf("\t\t"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBtnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mSpinnerSelectStr)) {
                    Toast.makeText(mContext, R.string.act_config_empty_selection, Toast.LENGTH_LONG).show();
                    return;
                }

                mEtBssid.setText(mSpinnerSelectStr);
            }
        });
    }

    // save data

    private void saveConfigTofile() {
        String ssid = mEtSsid.getText().toString();
        String bssid = mEtBssid.getText().toString();
        if (!TextUtils.isEmpty(ssid) && !TextUtils.isEmpty(bssid)) {
            if (null == mDataBean) {
                mDataBean = new LocalSavaDataBean();
            }
            mDataBean.setSsid(ssid);
            mDataBean.setBssidSelect(bssid);
            mDataBean.setBssidRandomly(mTbIsBssidRandomly.isChecked());
            LocalDataIOUtils.saveLocalDataToFile(mDataBean);
            Toast.makeText(mContext, "The new config will be effective after restart.",
                    Toast.LENGTH_LONG).show();
        }
    }
}

