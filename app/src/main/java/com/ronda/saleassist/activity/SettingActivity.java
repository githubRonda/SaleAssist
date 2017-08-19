package com.ronda.saleassist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ronda.saleassist.R;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.base.AppManager;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.serialport.SerialPortFinder;
import com.ronda.saleassist.view.LSpinner;
import com.ronda.saleassist.view.togglebutton.ToggleButton;

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

    private Button btnExit;

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
        setToggleButton(mToggleDiscountDialog, SPUtils.getBoolean(AppConst.SHOW_DISCOUNT_DIALOG, false));

        // 设置toggle的状态 --> 结算时是否打印
        setToggleButton(mTogglePrintBill, SPUtils.getBoolean(AppConst.PRINT_BILL, true));

        // 设置toggle的状态 --> 买家下新订单时是否打印小票
//        setToggleButton(mTogglePrintNewOrder, SPUtils.getBoolean(AppConst.PRINT_NEW_ORDER, true));

        // 设置toggle的状态 --> 启动程序时是否自动检查更新
        setToggleButton(mToggleCheckUpgrade, SPUtils.getBoolean(AppConst.AUTO_UPGRADE, true));
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
                SPUtils.putBoolean(AppConst.SHOW_DISCOUNT_DIALOG, mToggleDiscountDialog.isToggleOn());
                break;
            case R.id.toggle_default_print_bill:
                SPUtils.putBoolean(AppConst.PRINT_BILL, mTogglePrintBill.isToggleOn());
                break;
            case R.id.toggle_print_new_order:
                SPUtils.putBoolean(AppConst.PRINT_NEW_ORDER, mTogglePrintNewOrder.isToggleOn());
                break;
            case R.id.toggle_auto_check_upgrade:
                SPUtils.putBoolean(AppConst.AUTO_UPGRADE, mToggleCheckUpgrade.isToggleOn());
                break;

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
            case R.id.btn_exit:
                AppManager.getInstance().finishAllActivity();
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }


    private void setToggleButton(ToggleButton toggleButton, boolean isCheck){

        if (isCheck){
            if (!toggleButton.isToggleOn()){
                toggleButton.setToggleOn();
            }
        }
        else{
            if (toggleButton.isToggleOn()){
                toggleButton.setToggleOff();
            }
        }

    }


}
