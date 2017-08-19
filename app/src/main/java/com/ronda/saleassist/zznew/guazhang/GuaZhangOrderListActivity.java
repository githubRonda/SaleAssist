package com.ronda.saleassist.zznew.guazhang;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ronda.saleassist.R;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.bean.GuaZhangOrderDetail;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.utils.MathCompute;
import com.ronda.saleassist.zznew.RefreshListView;
import com.ronda.saleassist.zznew.SuperUtil;
import com.ronda.saleassist.zznew.guazhang.adapter.GuaZhangOrderListAdapter;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GuaZhangOrderListActivity extends BaseActivty {

    private String token = SPUtils.getString(AppConst.TOKEN, "");
    private String shopId = SPUtils.getString(AppConst.CUR_SHOP_ID, "");

    private RefreshListView rlv;
    private GuaZhangOrderListAdapter adapter;
    private String customer;
    List<GuaZhangOrderDetail> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gua_zhang_order_list);
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText("订单列表");
        customer = getIntent().getStringExtra("customer");
        if (customer != null && !customer.isEmpty()) {
            initData();
        }
        list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            list.add(new GuaZhangOrderDetail());
//        }
        adapter = new GuaZhangOrderListAdapter(this, list);

        rlv = (RefreshListView) findViewById(R.id.rlv);
        rlv.setAdapter(adapter);
        rlv.setDividerHeight(0);
        rlv.setVerticalScrollBarEnabled(false);
        rlv.setInterface(new RefreshListView.LoadData() {
            @Override
            public void onLoading() {
                rlv.loadCompleted();
            }
        });
        final TextView iv_menu = (TextView) findViewById(R.id.iv_menu);
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iv_menu.getText().equals("全选")) {
                    iv_menu.setText("取消");
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setChecked(true);
                    }
                } else {
                    iv_menu.setText("全选");
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setChecked(false);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        findViewById(R.id.iv_jiezhang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog dialog = new AlertDialog.Builder(GuaZhangOrderListActivity.this).create();
                dialog.show();

                dialog.getWindow().setContentView(R.layout.dialog_guanzhang_jiesuan_comfirm);

                TextView tv_msg = (TextView) dialog.getWindow().findViewById(R.id.tv_msg);
                Button btn_confirm = (Button) dialog.getWindow().findViewById(R.id.btn_confirm);
                Button btn_cancel = (Button) dialog.getWindow().findViewById(R.id.btn_cancel);

//                float total = 0;
                String total = "0.0";
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isChecked()) {
//                        total += Float.parseFloat(list.get(i).getAllinfo().get(0).getTotal_price());
                        total = MathCompute.roundHalfUp_scale2(MathCompute.add(total, list.get(i).getAllinfo().get(0).getTotal_price()));
                    }
                }

//                tv_msg.setText("当前总额为："+total+"元， 确定要结算？");
                tv_msg.setText(MathCompute.roundHalfUp_scale2(total));


                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        JSONArray jsa = new JSONArray();
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).isChecked()) {
//                        Toast.makeText(GuaZhangOrderListActivity.this,"已选"+list.get(i).getNo(),Toast.LENGTH_SHORT).show();
                                JSONObject jsb = new JSONObject();
                                jsb.put("no", list.get(i).getNo());
                                jsa.add(jsb);
                            }
                        }
                        if (jsa.size() == 0) return;
                        String url = "http://ceshi.edianlai.com/market/api/shop_unpay_order_pay";
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("token", token);
                        params.put("shopid", shopId);
                        params.put("type", "3");
                        params.put("customer", customer);
                        params.put("data", jsa.toString());
                        SuperUtil.sendPost(url, params, new SuperUtil.onResult() {
                            @Override
                            public void onResult(String resString) {
                                JSONObject jsb = JSONObject.parseObject(resString);
                                if (jsb.getString("status").equals("1")) {
                                    initData();
                                }
                                Toast.makeText(GuaZhangOrderListActivity.this, jsb.getString("msg"), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        adapter.setCheck(new GuaZhangOrderListAdapter.OnCheck() {
            @Override
            public void onCheck(GuaZhangOrderDetail guaZhangOrderDetail) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).equals(guaZhangOrderDetail)) {
                        list.get(i).setChecked(false);
                        break;
                    }
                }
            }

            @Override
            public void unCheck(GuaZhangOrderDetail guaZhangOrderDetail) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).equals(guaZhangOrderDetail)) {
                        list.get(i).setChecked(true);
                        break;
                    }
                }
            }
        });
    }

    private void initData() {
        String url = "http://ceshi.edianlai.com/market/api/shop_unpay_order_pay";
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        params.put("shopid", shopId);
        params.put("type", "2");
        params.put("customer", customer);
        SuperUtil.sendPost(url, params, new SuperUtil.onResult() {
            @Override
            public void onResult(String resString) {
                JSONObject jsb = JSONObject.parseObject(resString);
                KLog.json(jsb.toString());
                if (jsb.getString("status").equals("1")) {
                    JSONArray jsa = jsb.getJSONArray("data");
                    list.clear();
                    if (jsa != null) {
                        for (int i = 0; i < jsa.size(); i++) {
                            GuaZhangOrderDetail detail = JSON.toJavaObject(jsa.getJSONObject(i), GuaZhangOrderDetail.class);
                            list.add(detail);
                        }
//                        adapter.notifyDataSetChanged();
                    }
                } else if (jsb.getString("status").equals("0")) {
                    list.clear();
                } else {
                    Toast.makeText(GuaZhangOrderListActivity.this, jsb.getString("msg"), Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
