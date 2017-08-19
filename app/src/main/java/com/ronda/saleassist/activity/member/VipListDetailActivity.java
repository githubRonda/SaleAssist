package com.ronda.saleassist.activity.member;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.simple.CommonAdapter;
import com.ronda.saleassist.adapter.simple.ViewHolder;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.bean.MemberBean;
import com.ronda.saleassist.bean.VipLevelBean;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.utils.MathCompute;
import com.ronda.saleassist.utils.ToastUtils;
import com.ronda.saleassist.view.DigitKeyboardView;
import com.socks.library.KLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class VipListDetailActivity extends BaseActivty implements View.OnClickListener {

    private MemberBean mIntentData;
    private List<VipLevelBean> mIntentLevelData;

    private int curSelectLevelPosition = 0; //用于记录更改会员等级时Spinner的position


    private String token = SPUtils.getString(AppConst.TOKEN, "");
    private String shopId = SPUtils.getString(AppConst.CUR_SHOP_ID, "");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_list_detail);

        initToolbar("会员详细信息", true);

        mIntentData = (MemberBean) getIntent().getSerializableExtra("data");
        mIntentLevelData = (List<VipLevelBean>) getIntent().getSerializableExtra("levelInfo");


        initView();

        initEvent();
    }


    private void initView() {

        ((TextView) findViewById(R.id.txt_nickname)).setText(mIntentData.getName());
        ((TextView) findViewById(R.id.txt_mobile)).setText(mIntentData.getMobile());
        ((TextView) findViewById(R.id.txt_vip_level)).setText(mIntentData.getLevelname());
        ((TextView) findViewById(R.id.txt_preferences)).setText(mIntentData.getLevelcost());
        ((TextView) findViewById(R.id.txt_laterpay_power)).setText(mIntentData.getBill().equals("0") ? "无挂账权限" : "有挂账权限"); //0-->无挂账权限，1-->有挂账权限
        ((TextView) findViewById(R.id.txt_money)).setText(mIntentData.getMoney());

        initMonthCostView();
        initAllCostView();
    }

    private void initEvent() {
        findViewById(R.id.btn_update).setOnClickListener(this);
        findViewById(R.id.btn_recharge).setOnClickListener(this);

    }

    private void initMonthCostView() {
        List<MemberBean.CostBean> monthCostList = mIntentData.getMonth();
        for (int i = 0; i < monthCostList.size(); i++) {
            switch (monthCostList.get(i).getStatus()) {
                //1现金2支付宝3会员11挂账未结账12挂账已结账
                case "1":
                    ((TextView) findViewById(R.id.txt_month_cash)).setText(MathCompute.roundHalfUp_scale2(monthCostList.get(i).getCost()));
                    break;
                case "2":
                    ((TextView) findViewById(R.id.txt_month_alipay)).setText(MathCompute.roundHalfUp_scale2(monthCostList.get(i).getCost()));
                    break;
                case "3":
                    ((TextView) findViewById(R.id.txt_month_vip)).setText(MathCompute.roundHalfUp_scale2(monthCostList.get(i).getCost()));
                    break;
                case "11":
                    ((TextView) findViewById(R.id.txt_month_guazhang_unpayed)).setText(MathCompute.roundHalfUp_scale2(monthCostList.get(i).getCost()));
                    break;
                case "12":
                    ((TextView) findViewById(R.id.txt_month_guazhang_payed)).setText(MathCompute.roundHalfUp_scale2(monthCostList.get(i).getCost()));
                    break;
            }
        }
        ((TextView) findViewById(R.id.txt_month_cost)).setText(MathCompute.roundHalfUp_scale2(mIntentData.getMonthCost()));
    }

    private void initAllCostView() {
        List<MemberBean.CostBean> allCostList = mIntentData.getAll();
        for (int i = 0; i < allCostList.size(); i++) {
            switch (allCostList.get(i).getStatus()) {
                //1现金2支付宝3会员11挂账未结账12挂账已结账
                case "1":
                    ((TextView) findViewById(R.id.txt_all_cash)).setText(MathCompute.roundHalfUp_scale2(allCostList.get(i).getCost()));
                    break;
                case "2":
                    ((TextView) findViewById(R.id.txt_all_alipay)).setText(MathCompute.roundHalfUp_scale2(allCostList.get(i).getCost()));
                    break;
                case "3":
                    ((TextView) findViewById(R.id.txt_all_vip)).setText(MathCompute.roundHalfUp_scale2(allCostList.get(i).getCost()));
                    break;
                case "11":
                    ((TextView) findViewById(R.id.txt_all_guazhang_unpayed)).setText(MathCompute.roundHalfUp_scale2(allCostList.get(i).getCost()));
                    break;
                case "12":
                    ((TextView) findViewById(R.id.txt_all_guazhang_payed)).setText(MathCompute.roundHalfUp_scale2(allCostList.get(i).getCost()));
                    break;
            }
        }
        ((TextView) findViewById(R.id.txt_all_cost)).setText(MathCompute.roundHalfUp_scale2(mIntentData.getAllCost()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                showUpdateLevelDialog();
                break;
            case R.id.btn_recharge:
                showRechargeDialog();
                break;
        }
    }

    // 会员充值的dialog
    private void showRechargeDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setContentView(R.layout.dialog_common_edit_input);

        TextView txt_title = (TextView) dialog.getWindow().findViewById(R.id.txt_title);
        final EditText edit_input = (EditText) dialog.getWindow().findViewById(R.id.edit_input);
        Button btn_cancel = (Button) dialog.getWindow().findViewById(R.id.btn_cancel);
        Button btn_confirm = (Button) dialog.getWindow().findViewById(R.id.btn_confirm);
        DigitKeyboardView keyboard = (DigitKeyboardView) dialog.getWindow().findViewById(R.id.ll_keyboard);

        txt_title.setText("请输入金额");

        keyboard.bindEditTextViews(getWindow(), edit_input);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String money = edit_input.getText().toString().trim();

                if (money.isEmpty()) {
                    edit_input.setError("金额不能为空");
                    return;
                }

                UserApi.rechargeMember(TAG, token, shopId, "", mIntentData.getUserid(), money, "",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String responseStr) {
                                KLog.json(responseStr);

                                try {
                                    JSONObject response = new JSONObject(responseStr);
                                    int status = response.getInt("status");
                                    String msg = response.getString("msg");

                                    if (status == 1) {
                                        JSONArray data = response.getJSONArray("data");
                                        MemberBean bean = new Gson().fromJson(data.getJSONObject(0).toString(), new TypeToken<MemberBean>() {
                                        }.getType());
                                        //更新 会员余额 View
                                        ((TextView) findViewById(R.id.txt_money)).setText(bean.getMoney());
                                    }

                                    Toast.makeText(VipListDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ToastUtils.showToast(R.string.no_respnose);
                            }
                        });

                dialog.dismiss();
            }
        });
    }

    //修改会员等级的dialog
    private void showUpdateLevelDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setContentView(R.layout.dialog_choose_vip_level);


        final CheckBox check_box = (CheckBox) dialog.getWindow().findViewById(R.id.check_box);
        Spinner spinner_vip_level = (Spinner) dialog.getWindow().findViewById(R.id.spinner_vip_level);

        Button btn_cancel = (Button) dialog.getWindow().findViewById(R.id.btn_cancel);
        Button btn_confirm = (Button) dialog.getWindow().findViewById(R.id.btn_confirm);


        MySpinnerAdapter adapter = new MySpinnerAdapter(this, mIntentLevelData, android.R.layout.simple_list_item_1);
        spinner_vip_level.setAdapter(adapter);

        spinner_vip_level.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                curSelectLevelPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        /**********给界面赋值*****************/
        int pos = 0;
        for (int i = 0; i < mIntentLevelData.size(); i++) {
            if (((TextView) findViewById(R.id.txt_vip_level)).getText().toString().equals(mIntentLevelData.get(i).getName())) {
                pos = i;
                break;
            }
        }
        spinner_vip_level.setSelection(pos);

        check_box.setChecked(((TextView) findViewById(R.id.txt_laterpay_power)).getText().toString().equals("有挂账权限"));


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取check_box是否选中
                String bill = "0";//是否有挂账权限
                if (check_box.isChecked()) {
                    bill = "1";
                }

                UserApi.modifyMemberLevel(TAG, token, shopId, mIntentData.getUserid(), mIntentLevelData.get(curSelectLevelPosition).getId(), bill,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String responseStr) {
                                KLog.json(responseStr);

                                try {
                                    JSONObject response = new JSONObject(responseStr);
                                    int status = response.getInt("status");
                                    String msg = response.getString("msg");

                                    if (status == 1) {
                                        JSONArray data = response.getJSONArray("data");
                                        MemberBean bean = new Gson().fromJson(data.getJSONObject(0).toString(), new TypeToken<MemberBean>() {
                                        }.getType());
                                        //更新View
                                        ((TextView) findViewById(R.id.txt_vip_level)).setText(bean.getLevelname());
                                        ((TextView) findViewById(R.id.txt_preferences)).setText(bean.getLevelcost());
                                        ((TextView) findViewById(R.id.txt_laterpay_power)).setText("0".equals(bean.getBill())? "无挂账权限" : "有挂账权限"); //0-->无挂账权限，1-->有挂账权限
                                    }

                                    Toast.makeText(VipListDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                ToastUtils.showToast(R.string.no_respnose);
                            }
                        });

                dialog.dismiss();
            }
        });
    }


    class MySpinnerAdapter extends CommonAdapter<VipLevelBean> {

        public MySpinnerAdapter(Context context, List<VipLevelBean> datas, int layoutId) {
            super(context, datas, layoutId);//@android:id/text1
        }

        @Override
        public void convert(ViewHolder holder, VipLevelBean bean) {
            holder.setText(android.R.id.text1, bean.getName());
        }
    }
}
