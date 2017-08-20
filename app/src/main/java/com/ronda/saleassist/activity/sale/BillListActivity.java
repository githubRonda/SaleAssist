package com.ronda.saleassist.activity.sale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.OrderAdapter;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.bean.OrderBean;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * 销售管理 中的销售记录列表
 * 并且也对应员工管理中的该员工的销售记录列表
 */
public class BillListActivity extends BaseActivty {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView rvOrderList;
    private ArrayList<OrderBean> mDatas = new ArrayList<OrderBean>();
    private OrderAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private String token = SPUtils.getString(AppConst.TOKEN, "");
    private String shopId = SPUtils.getString(AppConst.CUR_SHOP_ID, "");


    private int pageCount = 1; //分页加载的页数
    private int pageSize = 12; //每页的大小

    private int lastVisibleItem;

    private Bundle empBundle;// 表示员工的bundle, 包含uid，nick, 若bundle.size()==0,则表示调用店老板的报表接口，否则调用员工的报表接口.empBundle 最开始是由EmpManageActivity传入的

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        empBundle = getIntent().getExtras();


        //KLog.e("empBundle : " + empBundle + "  " + (empBundle == null) + " ,size" + empBundle.size());

        String title = "销售记录";
        if (empBundle.size() > 0) {
            title = empBundle.getString("nick") + "员工-销售记录";
        }
        initToolbar(title, true);

        initView();

        initEvent();

        loadOrderData(pageCount = 1);//第一次进入页面时也要加载一下RecyclerView的数据
    }

    private void initEvent() {

        //RecyclerView的item点击事件
        mAdapter.setOnItemClickListener(new OrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(BillListActivity.this, BillDetailActivity.class);
                intent.putExtra("no", ((TextView) view.findViewById(R.id.tv_order_no)).getText());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });


        //设置下拉刷新的回调事件
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadOrderData(pageCount = 1);//初始化第一页数据

                KLog.i("lastVisibleItem:" + lastVisibleItem);
            }
        });

        //设置上拉加载更多
        rvOrderList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                KLog.i("onScrollStateChanged");

                //上拉加载更多
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mAdapter.getItemCount()) {//数据为空的时候，也不会执行.当有数据且滑倒底部时才执行

                    KLog.i("上拉加载更多, 当前pageCount:" + pageCount);
                    loadOrderData(++pageCount); //加载更多
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                KLog.i("onScrolled");
                lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    private void initView() {

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_order_list);
        rvOrderList = (RecyclerView) findViewById(R.id.rv_order_list);

        mAdapter = new OrderAdapter(this, mDatas);

        rvOrderList.setAdapter(mAdapter);
        mLinearLayoutManager = new LinearLayoutManager(this);
        rvOrderList.setLayoutManager(mLinearLayoutManager);
        rvOrderList.setItemAnimator(new DefaultItemAnimator());
        rvOrderList.setHasFixedSize(true);

        // 调整进度条距离屏幕顶部的距离
        mSwipeRefreshLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

    }

    private void loadOrderData(final int page) {

        if (page == 1) { //表示初始化数据
            //mDatas.clear();//这样直接清除的话,存在问题：刷新时，会加载第一页数据，此时即使RecyclerView显示的item改变,也不会触发OnScrollListener，自然也不会回调onScrolled()方法，lastVisibleItem不会更新。
            mAdapter.clearData();
        }

        //设置显示加载数据的动画
        mSwipeRefreshLayout.setRefreshing(true);

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String responseStr) {
                //访问后台结束时关闭加载动画
                mSwipeRefreshLayout.setRefreshing(false);

                KLog.json(responseStr);


                try {
                    JSONObject response = new JSONObject(responseStr);
                    int status = response.getInt("status");
                    String msg = response.getString("msg");

                    if (status == -9) {
                        Toast.makeText(BillListActivity.this, "当前已是全部订单", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BillListActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                    if (status == 1) {

                        if (page != 1) {
                            pageCount++;//加载更多请求成功，pageCount++,status = -9是暂无更多订单
                        }

                        JSONArray data = response.getJSONArray("data");
                        ArrayList<OrderBean> list = new Gson().fromJson(data.toString(), new TypeToken<ArrayList<OrderBean>>() {
                        }.getType());
                        mDatas.addAll(list);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    mAdapter.notifyDataSetChanged();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //访问后台结束时关闭加载动画
                mSwipeRefreshLayout.setRefreshing(false);
            }
        };


        if (empBundle.size() > 0) {// 店员报表的接口
            UserApi.getMemberOrderList(TAG, token, shopId, empBundle.getString("uid"), pageSize, page, listener, errorListener);
        } else { //说明是店老板的报表
            UserApi.getOrderList(TAG, token, shopId, pageSize, page,  listener, errorListener);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        rvOrderList.clearOnScrollListeners();
    }
}
