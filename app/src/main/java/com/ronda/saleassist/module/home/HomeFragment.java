package com.ronda.saleassist.module.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ronda.saleassist.R;
import com.ronda.saleassist.base.BaseFragment;
import com.ronda.saleassist.module.baserecyclerviewadapter.PullRefreshActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import xyz.zpayh.adapter.BaseAdapter;
import xyz.zpayh.adapter.BaseViewHolder;
import xyz.zpayh.adapter.OnItemClickListener;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/03/12
 * Version: v1.0
 */

public class HomeFragment extends BaseFragment {

//    @BindView(R.id.tv_label)
//    TextView mTvLabel;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;


    private MyAdapter mAdapter;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main_home, container, false);
    }

    @Override
    public void init(View view) {
//        mTvLabel.setText(mArgument);



        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new MyAdapter();



        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull View view, int adapterPosition) {
                startActivity(new Intent(getActivity(), mAdapter.getData(adapterPosition).getClazz()));
            }
        });


        loadData();
    }

    private void loadData() {
        List<IntentBean> data = new ArrayList<>();
        //data.add(new IntentBean("Volley", VolleyUsageActivity.class));
        data.add(new IntentBean("BaseRecyclerViewAdapter-->PullRefresh", PullRefreshActivity.class));


        mAdapter.setData(data);
    }

    public static HomeFragment newInstance(String arg) {
        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, arg);
        fragment.setArguments(bundle);
        return fragment;
    }

    class MyAdapter extends BaseAdapter<IntentBean>{

        @Override
        public int getLayoutRes(int index) {
            return android.R.layout.simple_list_item_1;
        }

        @Override
        public void convert(BaseViewHolder holder, IntentBean data, int index) {
            holder.setText(android.R.id.text1, data.getMsg());
        }

        @Override
        public void bind(BaseViewHolder holder, int layoutRes) {
            holder.itemView.setClickable(true);
        }
    }


    public static class IntentBean {
        public String          msg;
        public Class<Activity> clazz;

        public IntentBean() {
        }

        public IntentBean(String msg, Class clazz) {
            this.msg = msg;
            this.clazz = clazz;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public Class getClazz() {
            return clazz;
        }

        public void setClazz(Class clazz) {
            this.clazz = clazz;
        }
    }

}
