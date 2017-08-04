package com.ronda.saleassist.module.baserecyclerviewadapter;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ronda.saleassist.R;
import com.socks.library.KLog;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 加载更多 是当绘制该条目的时候，就调用该事件
 */

public class PullRefreshActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_refresh);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));// 网格布局
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnLoadMoreListener(this, mRecyclerView);
        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT); //设置新条目加载进来的动画方式

        View headView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, (ViewGroup) mRecyclerView.getParent(), false);
        ((TextView) headView.findViewById(android.R.id.text1)).setText("我是头部");
        mAdapter.addHeaderView(headView);


        //两种设置的点击事件均可（给RecyclerView设置 或者 给Adapter设置）
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(PullRefreshActivity.this, "mRecyclerView 点击了Position： " + position, Toast.LENGTH_SHORT).show();
            }
        });

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(PullRefreshActivity.this, "mAdapter 点击了Position： " + position, Toast.LENGTH_SHORT).show();
            }
        });


        mAdapter.addData(Arrays.asList("data 1", "data 2", "data 3", "data 4", "data 5", "data 6", "data 7", "data 8", "data 9", "data 10"));

    }

    //SwipeRefreshLayout中的OnRefreshListener
    @Override
    public void onRefresh() {

        KLog.i("onRefresh");

        Toast.makeText(this, "onRefresh", Toast.LENGTH_SHORT).show();
        mAdapter.setEnableLoadMore(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.setNewData(Arrays.asList("new 1", "new 2", "new 3","new 4", "new 5", "new 6","new 7", "new 8", "new 9","new 10", "new 11", "new 12", "new 13", "new 14"));

                mSwipeRefreshLayout.setRefreshing(false);
                mAdapter.setEnableLoadMore(true);
            }
        }, 2000);
    }

    //Adapter中的OnLoadMoreListener
    int i = 0;

    @Override
    public void onLoadMoreRequested() {

        KLog.i("onLoadMoreRequested");

        Toast.makeText(this, "onLoadMoreRequested", Toast.LENGTH_SHORT).show();

        mSwipeRefreshLayout.setEnabled(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.addData(Arrays.asList("add 1", "add 2", "add 3", "add 4", "add 5", "add 6", "add 7", "add 8", "add 9", "add 10"));
                mAdapter.loadMoreComplete();

                mSwipeRefreshLayout.setEnabled(true);
            }
        }, 2000);

/*
//        if (mAdapter.getData().size() < 5) {
            mAdapter.loadMoreEnd(false); //true is gone,false is visible. 此时不能在上拉（一般用于数据不足一屏时，禁止上拉的情况。或者就不需要上拉加载更多的功能）
//        } else
        {
            if (i == 0) {
                mAdapter.addData(Arrays.asList("add 1", "add 2", "add 3", "add 4"));
            } else if (i == 1) {
                mAdapter.loadMoreFail();
            } else if (i == 2) {
                mAdapter.loadMoreComplete();
            } else {
                i = 0;
            }

            mSwipeRefreshLayout.setEnabled(true);
            i++;
        }
//        mAdapter.loadMoreEnd(false);
//        mAdapter.loadMoreEnd(true);*/


    }


    class MyAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public MyAdapter() {
            super(android.R.layout.simple_list_item_1);
        }

        @Override
        protected void convert(BaseViewHolder holder, String item) {

            holder.setText(android.R.id.text1, item);
        }
    }
}
