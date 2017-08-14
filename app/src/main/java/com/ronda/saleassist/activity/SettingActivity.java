package com.ronda.saleassist.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.ronda.saleassist.R;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.base.AppManager;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.base.SPHelper;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.serialport.SerialPortFinder;
import com.ronda.saleassist.view.LSpinner;
import com.ronda.saleassist.view.togglebutton.ToggleButton;
import com.tencent.bugly.beta.Beta;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class SettingActivity extends BaseActivty implements View.OnClickListener {

    private LinearLayout llLocation;
    private LinearLayout llVersionInfo;
    private LinearLayout llCheckUpdate;
    private LinearLayout llModifyPass;
    private LinearLayout llUserSuggestion;

    private ToggleButton mToggleDiscountDialog;  //折扣对话框
    private ToggleButton mTogglePrintBill;  // 是否打印账单
    private ToggleButton mTogglePrintNewOrder;  // 是否打印新订单
    private ToggleButton mToggleCheckUpgrade;

//    private TextView mTxtBluetoothAddr;

    private Button btnExit;

    private ProgressDialog checkDialog, progress;
    String version = null;
    String description = null;
    String url = null;
    private LSpinner<String> mSpinnerCmd;
    private LSpinner<String> mSpinnerWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initToolbar("设置", true);

        initView();
        initEvent();
    }

    private void initView() {
        llCheckUpdate = (LinearLayout) findViewById(R.id.ll_check_update);
        llLocation = (LinearLayout) findViewById(R.id.ll_location);
        llVersionInfo = (LinearLayout) findViewById(R.id.ll_version_info);
        llModifyPass = (LinearLayout) findViewById(R.id.ll_modify_pass);
        llUserSuggestion = (LinearLayout) findViewById(R.id.ll_user_suggestion);

        btnExit = (Button) findViewById(R.id.btn_exit);

        mToggleDiscountDialog = (ToggleButton) findViewById(R.id.toggle_discount_dialog);
        mTogglePrintBill = (ToggleButton) findViewById(R.id.toggle_default_print_bill);
        mTogglePrintNewOrder = (ToggleButton) findViewById(R.id.toggle_print_new_order);
        mToggleCheckUpgrade = (ToggleButton) findViewById(R.id.toggle_auto_check_upgrade);


        mSpinnerWeight = (LSpinner<String>) findViewById(R.id.spinner_weight);
        mSpinnerCmd = (LSpinner<String>) findViewById(R.id.spinner_cmd);

        // 重量获取的串口设置
        SerialPortFinder finder = new SerialPortFinder();
        mSpinnerWeight.setData(finder.getAllDevicesPath());
        int index = mSpinnerWeight.getData().indexOf(SPUtils.getString(AppConst.WEIGHT_SERIAL_PORT, ""));
        if (index == -1) {
            index = 0;
        }
        mSpinnerWeight.setSelection(index);


        // 指令的串口设置
        mSpinnerCmd.setData(finder.getAllDevicesPath());
        index = mSpinnerCmd.getData().indexOf(SPUtils.getString(AppConst.CMD_SERIAL_PORT, ""));
        if (index == -1) {
            index = 0;
        }
        mSpinnerCmd.setSelection(index);


        // 设置toggle的状态 --> 点击菜品时是否显示折扣对话框
        if (SPHelper.getSettingDiscountDialog()) {
            mToggleDiscountDialog.setToggleOn();
        } else {
            mToggleDiscountDialog.setToggleOff();
        }

        // 设置toggle的状态 --> 点击菜品时是否显示折扣对话框
        if (SPHelper.getSettingPrintBill()) {
            mTogglePrintBill.setToggleOn();
        } else {
            mTogglePrintBill.setToggleOff();
        }

        // 设置toggle的状态 --> 买家下新订单时是否打印小票
        if (SPHelper.getSettingPrintNewOrder()){
            mTogglePrintNewOrder.setToggleOn();
        }else{
            mTogglePrintNewOrder.setToggleOff();
        }

        // 设置toggle的状态 --> 启动程序时是否自动检查更新
        if (SPHelper.getSettingAutoCheck()) {
            mToggleCheckUpgrade.toggleOn();
        } else {
            mToggleCheckUpgrade.toggleOff();
        }
    }

    private void initEvent() {
        mToggleDiscountDialog.setOnClickListener(this);
        mTogglePrintBill.setOnClickListener(this);
        mTogglePrintNewOrder.setOnClickListener(this);
        mToggleCheckUpgrade.setOnClickListener(this);
        llCheckUpdate.setOnClickListener(this);
        llLocation.setOnClickListener(this);
        llVersionInfo.setOnClickListener(this);
        llModifyPass.setOnClickListener(this);
        llUserSuggestion.setOnClickListener(this);
        btnExit.setOnClickListener(this);

        //findViewById(R.id.btn_clear_bluetooth_addr).setOnClickListener(this);

        mSpinnerWeight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SPUtils.putString(AppConst.WEIGHT_SERIAL_PORT, mSpinnerWeight.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mSpinnerCmd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SPUtils.putString(AppConst.CMD_SERIAL_PORT, mSpinnerCmd.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggle_discount_dialog:
                if (SPHelper.getSettingDiscountDialog()) {
                    mToggleDiscountDialog.setToggleOff();
                    SPHelper.setSettingDiscountDialog(false);
                } else {
                    mToggleDiscountDialog.setToggleOn();
                    SPHelper.setSettingDiscountDialog(true);
                }
                break;
            case R.id.toggle_default_print_bill:
                if (SPHelper.getSettingPrintBill()){
                    mTogglePrintBill.setToggleOff();
                    SPHelper.setSettingPrintBill(false);
                }
                else{
                    mTogglePrintBill.setToggleOn();
                    SPHelper.setSettingPrintBill(true);
                }
                break;
            case R.id.toggle_print_new_order:
                if (SPHelper.getSettingPrintNewOrder()){
                    mTogglePrintNewOrder.setToggleOff();
                    SPHelper.setSettingPrintNewOrder(false);
                }
                else {
                    mTogglePrintNewOrder.setToggleOn();
                    SPHelper.setSettingPrintNewOrder(true);
                }
                break;
            case R.id.toggle_auto_check_upgrade:
                if (SPHelper.getSettingAutoCheck()){
                    mToggleCheckUpgrade.setToggleOff();
                    SPHelper.setSettingAutoCheck(false);
                }
                else{
                    mToggleCheckUpgrade.setToggleOn();
                    SPHelper.setSettingAutoCheck(true);
                }
                break;

//            case R.id.btn_clear_bluetooth_addr:
//                SPHelper.setMainBluetoothAddr("");
//                // 设置蓝牙地址的显示
//                String addr = SPHelper.getMainBluetoothAddr();
//                if (addr.isEmpty()){
//                    mTxtBluetoothAddr.setText("无缓存蓝牙地址");
//                    Bluetooth.getBluetoothInstance().cancelAutoConnect();
//                }else {
//                    mTxtBluetoothAddr.setText("缓存蓝牙:"+addr);
//                }
//
//                break;
//            case R.id.ll_location:
//                startActivity(new Intent(this, LocationActivity.class));
//                break;
//            case R.id.ll_version_info:
//                startActivity(new Intent(this, VersionActivity.class).putExtra("version", getVersion()));
//                break;
//            case R.id.ll_check_update:
////                checkUpdate();
//                Beta.checkUpgrade();
//                break;
//            case R.id.ll_modify_pass:
//                startActivity(new Intent(this, ResetPassActivity.class));
//                break;
//            case R.id.ll_user_suggestion:
//                startActivity(new Intent(this, UserSuggestionActivity.class));
//                break;
//            case R.id.btn_exit:
//
//                Bluetooth.getBluetoothInstance().stop();
//                AppManager.getInstance().finishAllActivity();
//                //AppManager.getInstance().clear();
//                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
//                startActivity(intent);
//                UserApi.cancelAll();
//                break;
        }
    }


}
