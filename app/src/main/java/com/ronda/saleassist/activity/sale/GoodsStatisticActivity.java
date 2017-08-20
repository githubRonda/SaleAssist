package com.ronda.saleassist.activity.sale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.GoodsStatisticAdapter;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.bean.GoodsStatisticBean;

import java.util.ArrayList;

public class GoodsStatisticActivity extends BaseActivty {

    private RecyclerView rvGoods;
    private ArrayList<GoodsStatisticBean> mDatas;
    private GoodsStatisticAdapter mAdapter;


    private RecyclerView.LayoutManager mLinearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_statistic);

        mDatas = (ArrayList<GoodsStatisticBean>) getIntent().getSerializableExtra("datas");

        //标题栏设置
        initToolbar("商品统计", true);

        rvGoods = (RecyclerView) findViewById(R.id.rv_goods);

        mAdapter = new GoodsStatisticAdapter(this, mDatas);
        rvGoods.setAdapter(mAdapter);
        mLinearLayoutManager = new LinearLayoutManager(this);
        rvGoods.setLayoutManager(mLinearLayoutManager);
        rvGoods.setItemAnimator(new DefaultItemAnimator());


        final int day = getIntent().getIntExtra("day", 1);
//        String categoryId = getIntent().getStringExtra("categoryId");
        final String endtime = getIntent().getStringExtra("entime");
//        System.out.println("day:"+day+", categoryid:"+categoryId+", endtime:"+endtime);

        mAdapter.setOnItemClickListener(new GoodsStatisticAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(GoodsStatisticActivity.this, GoodsDetailActivity.class);
                intent.putExtra("day", day);
                intent.putExtra("goodid", mDatas.get(position).getGoodId());
                intent.putExtra("endtime", endtime);

                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }
}
