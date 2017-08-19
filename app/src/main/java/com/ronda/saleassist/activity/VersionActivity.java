package com.ronda.saleassist.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.ronda.saleassist.R;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.utils.VersionUtils;


public class VersionActivity extends BaseActivty {

    private TextView tvVersion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);

        initToolbar("版本信息", true);

        String versionName = VersionUtils.getVersionName(this);

        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvVersion.setText("版本号："+versionName);
    }
}
