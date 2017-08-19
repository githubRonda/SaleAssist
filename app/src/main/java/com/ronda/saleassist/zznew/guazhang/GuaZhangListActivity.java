package com.ronda.saleassist.zznew.guazhang;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ronda.saleassist.R;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.bean.GuaZhangList;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.zznew.RefreshListView;
import com.ronda.saleassist.zznew.SuperUtil;
import com.ronda.saleassist.zznew.guazhang.adapter.GuaZhangListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuaZhangListActivity extends BaseActivty {

    private String token = SPUtils.getString(AppConst.TOKEN, "");
    private String shopId = SPUtils.getString(AppConst.CUR_SHOP_ID, "");

    private GuaZhangListAdapter guaZhangListAdapter;
    private RefreshListView rlv;
    List<GuaZhangList> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gua_zhang);
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText("挂账管理");
        rlv= (RefreshListView) findViewById(R.id.rlv);

        list = new ArrayList<>();

        guaZhangListAdapter=new GuaZhangListAdapter(this,list);
        rlv.setAdapter(guaZhangListAdapter);
        rlv.setDividerHeight(0);
        rlv.setVerticalScrollBarEnabled(false);
        rlv.setInterface(new RefreshListView.LoadData() {
            @Override
            public void onLoading() {
                rlv.loadCompleted();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        String url = "http://ceshi.edianlai.com/market/api/shop_unpay_order_pay";
        Map<String,String> params = new HashMap<>();
        params.put("token", token);
        params.put("shopid", shopId);
        params.put("type","1");
        SuperUtil.sendPost(url, params, new SuperUtil.onResult() {
            @Override
            public void onResult(String resString) {
                JSONObject jsb= JSONObject.parseObject(resString);
                if(jsb.getString("status").equals("1")){
                    list.clear();
                    JSONArray jsa  = jsb.getJSONArray("data");
                    if(jsa!=null) {
                        for (int i = 0; i < jsa.size(); i++) {
                            GuaZhangList item = JSON.toJavaObject(jsa.getJSONObject(i), GuaZhangList.class);
                            list.add(item);
                        }
                        guaZhangListAdapter.notifyDataSetChanged();
                    }

                }else{
                    Toast.makeText(GuaZhangListActivity.this,jsb.getString("msg"), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
