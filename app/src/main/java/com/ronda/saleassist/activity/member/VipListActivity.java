package com.ronda.saleassist.activity.member;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.BaseRecyclerViewAdapter;
import com.ronda.saleassist.adapter.RecyclerViewHolder;
import com.ronda.saleassist.adapter.divider.DividerItemDecoration;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.bean.MemberBean;
import com.ronda.saleassist.bean.VipLevelBean;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.utils.MathCompute;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 会员列表，点击之后进入会员详情查看
 *
 * 本页面中是所有数据一起从后台获取的，一部分用于本Activity显示，另一部分用于传给下一级Activity显示
 */

public class VipListActivity extends BaseActivty {

    private int pageCount = 0; //分页加载的页码,从0开始，而CategoryFragment中是从1开始的
    private int pageSize = 30; //每页的大小

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private MemberAdapter mMemberAdapter;
    private List<MemberBean> mDatas = new ArrayList<>();

    private List<VipLevelBean> mLevelDatas = new ArrayList<>(); //记录会员等级信息，用于传给下一级的Activity


    private String token = SPUtils.getString(AppConst.TOKEN, "");
    private String shopId = SPUtils.getString(AppConst.CUR_SHOP_ID, "");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_list);

        initToolbar("会员列表", true);

        initView();

        initEvent();

        loadData(0);
    }


    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()));
        mSwipeRefreshLayout.setRefreshing(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mMemberAdapter = new MemberAdapter(mDatas);
        mRecyclerView.setAdapter(mMemberAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadData(0);//重新加载
    }

    private void initEvent() {
        // 下拉刷新事件
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(0);
            }
        });

        //设置上拉加载更多
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int firstVisibleItem;
            private int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE && firstVisibleItem != 0 && lastVisibleItem + 1 == mMemberAdapter.getItemCount()) {//数据为空的时候，也不会执行.当有数据且滑倒底部时才执行
                    //设置正在加载中
                    mMemberAdapter.setLoadStatus(MemberAdapter.FLAG_LOADING);
                    loadData(pageCount + 1);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstVisibleItem = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                lastVisibleItem = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
            }
        });

        // 列表 点击事件
        mMemberAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, RecyclerViewHolder holder, int position) {
                Intent intent = new Intent(VipListActivity.this, VipListDetailActivity.class);
                intent.putExtra("data", mDatas.get(position));
                intent.putExtra("levelInfo", (Serializable) mLevelDatas);
                startActivity(intent);
            }
        });
    }

    private void loadData(final int page) {
        mSwipeRefreshLayout.setRefreshing(true);

        UserApi.getAllMember(TAG, token, shopId, page, pageSize,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        KLog.json(responseStr);

                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");

                            if (page == 0) { //初始化时
                                pageCount = 0;
                                mDatas.clear(); //初始化需要clear,但是加载更多时不需要
                            } else {
                                pageCount++;//加载更多请求成功，pageCount++,status = -9是店铺内暂无任何货物
                            }

                            if (status == 0) {
                                Toast.makeText(VipListActivity.this, "当前已是全部信息", Toast.LENGTH_SHORT).show();
                                mMemberAdapter.setLoadStatus(MemberAdapter.FLAG_END);
                            } else {
                                Toast.makeText(VipListActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }

                            if (status == 1) {
                                JSONArray data = response.getJSONArray("data");

                                Gson gson = new Gson();
                                List<MemberBean> members = gson.fromJson(data.toString(), new TypeToken<List<MemberBean>>() {
                                }.getType());


                                JSONArray level_infos = response.getJSONArray("level_info");
                                List<VipLevelBean> level_info = new Gson().fromJson(level_infos.toString(), new TypeToken<List<VipLevelBean>>() {
                                }.getType());

                                mLevelDatas.clear();
                                mLevelDatas.addAll(level_info);

                                mDatas.addAll(members);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            mMemberAdapter.notifyDataSetChanged();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mMemberAdapter.setLoadStatus(PushAdActivity.PushAdAdapter.FLAG_HIDDEN);
                                }
                            }, 300);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        ToastUtils.showToast(R.string.no_respnose);
                    }
                });
    }

    class MemberAdapter extends BaseRecyclerViewAdapter<MemberBean> {

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_FOOTER = 2;

        //上拉加载更多
        public static final int FLAG_PULL_UP = 0;//默认显示
        //正在加载中
        public static final int FLAG_LOADING = 1;
        //已经到底了
        public static final int FLAG_END = 2;
        //网络出错
        public static final int FLAG_NETWORK_EEROR = 3;
        //隐藏 footer
        public static final int FLAG_HIDDEN = 4;

        //当前的加载状态-默认为 FLAG_PULL_UP
        private int mLoadStatus = FLAG_PULL_UP; //记录上面的几个状态


        public MemberAdapter(List<MemberBean> list) {
            super(list);
        }

        @Override
        public int getItemViewType(int position) {
            if (position + 1 == getItemCount()) {//最后一个item设置为footerView
                return TYPE_FOOTER;
            } else {
                return TYPE_ITEM;
            }
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerViewHolder holder = null;
            if (viewType == TYPE_ITEM) {
                holder = RecyclerViewHolder.createViewHolder(parent.getContext(), parent, R.layout.list_item_member_all);
                bindItemViewClickListener(holder);
            } else if (viewType == TYPE_FOOTER) {
                holder = RecyclerViewHolder.createViewHolder(parent.getContext(), parent, R.layout.grid_item_footer_subcategory);
            }
            return holder;
        }

        @Override
        public void bindDataToItemView(RecyclerViewHolder holder, int position) {
            if (holder.getItemViewType() == TYPE_ITEM) {
                holder.setText(R.id.txt_label, mDatas.get(position).getName());
                holder.setText(R.id.txt_month_cost, MathCompute.roundHalfUp_scale2(mDatas.get(position).getMonthCost()));
                holder.setText(R.id.txt_all_cost, MathCompute.roundHalfUp_scale2(mDatas.get(position).getAllCost()));
                holder.setText(R.id.txt_money, mDatas.get(position).getMoney());

            } else if (holder.getItemViewType() == TYPE_FOOTER) {
                switch (mLoadStatus) {
                    case FLAG_PULL_UP:
                        holder.getConvertView().setVisibility(View.VISIBLE);
                        holder.setVisible(R.id.pbLoad, false);//隐藏前面的环形的加载进度条
                        holder.setText(R.id.tvLoadText, "上拉加载更多");
                        break;
                    case FLAG_LOADING:
                        holder.getConvertView().setVisibility(View.VISIBLE);
                        holder.setVisible(R.id.pbLoad, true);//显示前面的环形的加载进度条
                        holder.setText(R.id.tvLoadText, "正在加载中");
                        break;
                    case FLAG_END:
                        holder.getConvertView().setVisibility(View.VISIBLE);
                        holder.setVisible(R.id.pbLoad, false);
                        holder.setText(R.id.tvLoadText, "当前已是全部信息");//已经到底了
                        break;
                    case FLAG_NETWORK_EEROR:
                        holder.getConvertView().setVisibility(View.VISIBLE);
                        holder.setVisible(R.id.pbLoad, false);
                        holder.setText(R.id.tvLoadText, "网络出错");
                        break;
                    case FLAG_HIDDEN:
                        //隐藏footerView
                        holder.getConvertView().setVisibility(View.GONE);
                        break;
                }
            }
        }

        @Override
        public int getItemCount() {
            return mDatas.size() + 1;
        }

        public void setLoadStatus(int status) {
            mLoadStatus = status;
            //notifyDataSetChanged();
            notifyItemChanged(getItemCount() - 1);
        }
    }
}
