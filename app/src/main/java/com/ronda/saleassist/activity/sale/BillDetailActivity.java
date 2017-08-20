package com.ronda.saleassist.activity.sale;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.divider.DividerItemDecoration;
import com.ronda.saleassist.adapter.simple2.BaseRecyclerViewAdapter;
import com.ronda.saleassist.adapter.simple2.RecyclerViewHolder;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.bean.BillDetailBean;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lrd on 0015,2016/8/15.
 */
public class BillDetailActivity extends BaseActivty {


    private RecyclerView mRecyclerView;
    private OrderDetailAdapter mAdapter;
    private List<BillDetailBean> mDatas = new ArrayList<>();

    private Bundle empBundle;// 表示员工的bundle, 包含uid，nick, 若bundle.size()==0,则表示调用店老板的报表接口，否则调用员工的报表接口

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        empBundle = getIntent().getExtras();

        initToolbar("订单详情", true);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_order_detail);

        mAdapter = new OrderDetailAdapter(mDatas);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);

        String no = getIntent().getStringExtra("no");


//        if (empBundle.size()>0){ //员工报表
//            UserApi.getMemberOrderDetail(no, empBundle.getString("uid"), volleyStrHandler);
//        }
//        else{ //老板报表
//            UserApi.getOrderDetail(no, volleyStrHandler);
//        }
        //这个是根据该订单号获取该订单的详细信息，可以不区分员工和老板，统一使用老板的接口
        UserApi.getOrderDetail(TAG, token, shopId, no,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        KLog.json(responseStr);

                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");

                            Toast.makeText(BillDetailActivity.this, msg, Toast.LENGTH_SHORT).show();

                            if (status == 1) {
                                JSONObject data = response.getJSONObject("data");
                                JSONArray orderInfo = data.getJSONArray("orderinfo");

                                List<BillDetailBean> list = new Gson().fromJson(orderInfo.toString(), new TypeToken<List<BillDetailBean>>(){}.getType());
                                mDatas.clear();
                                mDatas.addAll(list);

                                ((TextView)findViewById(R.id.txt_no)).setText(list.get(0).getNo());
                                ((TextView)findViewById(R.id.txt_total)).setText(list.get(0).getTotal_price());
                                ((TextView)findViewById(R.id.txt_paymethod)).setText(getPayMethod(list.get(0).getStatus())); //需要进行判断
                                ((TextView)findViewById(R.id.txt_operator)).setText(list.get(0).getOperater());
                                ((TextView)findViewById(R.id.txt_datetime)).setText(list.get(0).getIntime());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finally {
                            mAdapter.notifyDataSetChanged();
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

    private String getPayMethod(String status){
        switch (status){
            case "1":
                return "现金";
            case "2":
                return "支付宝";
            case "3":
                return "会员支付";
            case "4": //这个暂时还不知道
                return "";
            case "11":
                return "挂账(未结)";
            case "12":
                return "挂账(已结)";

        }
        return "";
    }

    public class OrderDetailAdapter extends BaseRecyclerViewAdapter<BillDetailBean> {

        public OrderDetailAdapter(List<BillDetailBean> list) {
            super(list, R.layout.card_order2);
        }

        @Override
        public void bindDataToItemView(RecyclerViewHolder holder, int position) {
            holder.setText(R.id.txt_name, mDatas.get(position).getGoodinfo().getName());
            holder.setText(R.id.txt_category, mDatas.get(position).getGoodinfo().getCategoryname());
            holder.setText(R.id.txt_price, mDatas.get(position).getPrice());
            holder.setText(R.id.txt_discount, mDatas.get(position).getCost());
            holder.setText(R.id.txt_number, mDatas.get(position).getNumber());
            holder.setText(R.id.txt_money, mDatas.get(position).getOrderprice());
        }
    }
}
