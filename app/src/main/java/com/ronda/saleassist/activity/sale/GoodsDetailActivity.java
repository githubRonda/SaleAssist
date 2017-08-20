package com.ronda.saleassist.activity.sale;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.GoodsDetailAdapter;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.bean.GoodsDetailBean;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GoodsDetailActivity extends BaseActivty {

    private RecyclerView mRecyclerView;
//    private LinearLayoutManager mLinearLayoutManager;
    private GoodsDetailAdapter mAdapter;
    private List<GoodsDetailBean> mDatas = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_detail);

        final int day = getIntent().getIntExtra("day", 1);
        final String endtime = getIntent().getStringExtra("endtime");
        final String goodid = getIntent().getStringExtra("goodid");


        initToolbar("货物详情", true);


        mRecyclerView = (RecyclerView) findViewById(R.id.rv_good_detail);

        mAdapter = new GoodsDetailAdapter(this, mDatas);
        mRecyclerView.setAdapter(mAdapter);
//        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        KLog.i("day:" + day + ", endtime:" + endtime + ", goodid:" + goodid);

        UserApi.getGoodsDetialInTime(TAG, token, shopId, "" + day, goodid, "", "", "",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        KLog.json(responseStr);

                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");

                            Toast.makeText(GoodsDetailActivity.this, msg, Toast.LENGTH_SHORT).show();

                            if (status == 1) {
                                JSONObject data = response.getJSONObject("data");
                                JSONArray goods = data.getJSONArray("goods");

                                for (int i = 0; i < goods.length(); i++) {
                                    String goodsName = goods.getJSONObject(i).getString("name");
                                    String price = goods.getJSONObject(i).getString("price");
                                    String discount = goods.getJSONObject(i).getString("cost");
                                    String number = goods.getJSONObject(i).getString("number");
                                    String goodsCost = goods.getJSONObject(i).getString("orderprice");
                                    String orderNo = goods.getJSONObject(i).getString("no");
                                    String orderCost = goods.getJSONObject(i).getString("total_price");
                                    int status_t = goods.getJSONObject(i).getInt("status");
                                    String payMethod = status_t == 1 ? "现金" : "支付宝";
                                    String operator = goods.getJSONObject(i).getString("operater");

                                    mAdapter.addData(new GoodsDetailBean(goodsName, price+"元/kg", discount, number+"kg", goodsCost+"元", orderNo, orderCost+"元", payMethod, operator));
                                }
                            }
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


    }
}
