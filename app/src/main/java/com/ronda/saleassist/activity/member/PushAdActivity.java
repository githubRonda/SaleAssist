package com.ronda.saleassist.activity.member;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.simple2.BaseRecyclerViewAdapter;
import com.ronda.saleassist.adapter.simple2.RecyclerViewHolder;
import com.ronda.saleassist.adapter.divider.DividerItemDecoration;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.bean.AdvertisementBean;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class PushAdActivity extends BaseActivty {

    private int pageCount = 0; //分页加载的页码,从0开始，与CategoryFragment中是不同的
    private int pageSize = 30; //每页的大小

    private String ing = "";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private PushAdAdapter mPushAdAdapter;
    private List<AdvertisementBean> mDatas = new ArrayList<>();

    private String token = SPUtils.getString(AppConst.TOKEN, "");
    private String shopId = SPUtils.getString(AppConst.CUR_SHOP_ID, "");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_ad);

        initToolbar("推送优惠", true);

        initView();

        initEvent();

        loadAdData(0);

    }

    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()));
        mSwipeRefreshLayout.setRefreshing(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mPushAdAdapter = new PushAdAdapter(mDatas);
        mRecyclerView.setAdapter(mPushAdAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    private void initEvent() {
        // 下拉刷新事件
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadAdData(0);
            }
        });


        //设置上拉加载更多
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int firstVisibleItem;
            private int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE && firstVisibleItem != 0 && lastVisibleItem + 1 == mPushAdAdapter.getItemCount()) {//数据为空的时候，也不会执行.当有数据且滑倒底部时才执行
                    //设置正在加载中
                    mPushAdAdapter.setLoadStatus(PushAdAdapter.FLAG_LOADING);
                    loadAdData(pageCount + 1);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstVisibleItem = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                lastVisibleItem = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_ad, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                showAddDialog();
                break;
            case R.id.all:
                ing = "";
                loadAdData(0);
                break;
            case R.id.duringtime:
                ing ="1";
                loadAdData(0);
                break;
        }
        return true;
    }

    //显示发布的优惠的对话框
    private void showAddDialog() {
        final AlertDialog addDialog = new AlertDialog.Builder(this).create();
        addDialog.show();

        addDialog.getWindow().setContentView(R.layout.dialog_push_ad);
        addDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        addDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        addDialog.setCanceledOnTouchOutside(false);

        final EditText et_title = (EditText) addDialog.getWindow().findViewById(R.id.et_title);
        final EditText et_content = (EditText) addDialog.getWindow().findViewById(R.id.et_content);
        final EditText et_start_time = (EditText) addDialog.getWindow().findViewById(R.id.et_start_time);
        final EditText et_end_time = (EditText) addDialog.getWindow().findViewById(R.id.et_end_time);
        final Button btn_cancel = (Button) addDialog.getWindow().findViewById(R.id.btn_cancel);
        Button btn_confirm = (Button) addDialog.getWindow().findViewById(R.id.btn_confirm);

        View.OnClickListener datetimeClickListener = new View.OnClickListener() {

            private String datetime = "";

            @Override
            public void onClick(final View view) {
                final AlertDialog datetimeDialog = new AlertDialog.Builder(PushAdActivity.this).create();
                datetimeDialog.show();

                datetimeDialog.getWindow().setContentView(R.layout.dialog_datetime);

                final DatePicker datePicker = (DatePicker) datetimeDialog.getWindow().findViewById(R.id.date_picker);
                final TimePicker timePicker = (TimePicker) datetimeDialog.getWindow().findViewById(R.id.time_picker);
                Button btn_confirm_t = (Button) datetimeDialog.getWindow().findViewById(R.id.btn_confirm);

                timePicker.setIs24HourView(true);
                datePicker.setMinDate(System.currentTimeMillis() - 1000);

                final Calendar c = Calendar.getInstance();

                c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), 0);

                datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        int t = monthOfYear + 1;//monthOfYear表示的是0-11
                        String month = t < 10 ? "0" + t : t + "";
                        String day = dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth + "";

                        //得到时间
                        String time = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
                        datetime = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + " " + time;

                        c.set(year, monthOfYear, dayOfMonth);
                    }
                });
                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        String date = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();
                        datetime = date + " " + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();
                        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
                    }

                });

                btn_confirm_t.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((EditText) view).setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTimeInMillis()));
                        datetimeDialog.dismiss();
                    }
                });
            }
        };

        et_start_time.setOnClickListener(datetimeClickListener);
        et_end_time.setOnClickListener(datetimeClickListener);


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog.dismiss();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = et_title.getText().toString().trim();
                String content = et_content.getText().toString().trim();
                String begintime = et_start_time.getText().toString();
                String endtime = et_end_time.getText().toString();

                if (title.isEmpty()) {
                    Toast.makeText(PushAdActivity.this, "标题不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (content.isEmpty()) {
                    Toast.makeText(PushAdActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean b = getQueryDays(begintime, endtime);
                if (!b) {
                    Toast.makeText(PushAdActivity.this, "日期输入有误", Toast.LENGTH_SHORT).show();
                    return;
                }

                //上传数据至后台
                UserApi.pushAd(TAG, token, shopId, title, content, begintime, endtime,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String responseStr) {
                                KLog.json(responseStr);

                                try {
                                    JSONObject response = new JSONObject(responseStr);
                                    int status = response.getInt("status");
                                    String msg = response.getString("msg");

                                    Toast.makeText(PushAdActivity.this, msg, Toast.LENGTH_SHORT).show();

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

                addDialog.dismiss();
            }
        });


    }

    // 获取后台广告信息
    private void loadAdData(final int page) {
        mSwipeRefreshLayout.setRefreshing(true);

        UserApi.getPushAd(TAG, token, shopId, page, pageSize, ing,
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
                                Toast.makeText(PushAdActivity.this, "当前已是全部信息", Toast.LENGTH_SHORT).show();
                                mPushAdAdapter.setLoadStatus(PushAdAdapter.FLAG_END);
                            } else {
                                Toast.makeText(PushAdActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }

                            if (status == 1) {
                                JSONArray data = response.getJSONArray("data");

                                Gson gson = new Gson();
                                List<AdvertisementBean> ads = gson.fromJson(data.toString(), new TypeToken<List<AdvertisementBean>>() {
                                }.getType());


                                mDatas.addAll(ads);
//                        mPushAdAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            mPushAdAdapter.notifyDataSetChanged();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mPushAdAdapter.setLoadStatus(PushAdAdapter.FLAG_HIDDEN);
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


    //用于比较两个日期：startDatetime 要不能大于 endDatetime
    private boolean getQueryDays(String startDatetime, String endDatetime) {
        SimpleDateFormat smdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date start = smdf.parse(startDatetime);
            Date end = smdf.parse(endDatetime);

//            KLog.e("start: "+start.getTime()+", end: "+end.getTime() );
            long t = (int) ((end.getTime() - start.getTime()) / (60 * 1000));//到分就可以了，不必要到毫秒级

            if (t <= 0) return false;
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    class PushAdAdapter extends BaseRecyclerViewAdapter<AdvertisementBean> {

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

        public PushAdAdapter(List<AdvertisementBean> list) {
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
                holder = RecyclerViewHolder.createViewHolder(parent.getContext(), parent, R.layout.list_item_push_ad);
            } else if (viewType == TYPE_FOOTER) {
                holder = RecyclerViewHolder.createViewHolder(parent.getContext(), parent, R.layout.grid_item_footer_subcategory);
            }
            return holder;
        }

        @Override
        public void bindDataToItemView(RecyclerViewHolder holder, int position) {
            if (holder.getItemViewType() == TYPE_ITEM) {
                holder.setText(R.id.tv_title, mDatas.get(position).getTitle());
                holder.setText(R.id.tv_content, mDatas.get(position).getContent());
                holder.setText(R.id.tv_begin_time, mDatas.get(position).getBegintime());
                holder.setText(R.id.tv_end_time, mDatas.get(position).getEndtime());
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
                        holder.setText(R.id.tvLoadText, "当前已是全部货物");//已经到底了
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
