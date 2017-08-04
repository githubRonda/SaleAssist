package com.ronda.saleassist.adapter;

import android.support.annotation.LayoutRes;

import com.ronda.saleassist.R;
import com.ronda.saleassist.local.sqlite.table.Goods;

import xyz.zpayh.adapter.BaseAdapter;
import xyz.zpayh.adapter.BaseViewHolder;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/03/12
 * Version: v1.0
 * <p>
 * <p>
 * <p>
 * mAdapter = new HeadAndFootAdapter();
 * mAdapter.setData(mData);
 * mAdapter.addHeadLayout(R.layout.item_head);
 * mAdapter.addFootLayout(R.layout.item_foot);
 */

public class HeadAndFootAdapter extends BaseAdapter<Goods> {
    @Override
    public int getLayoutRes(int index) {
        return R.layout.item_text;
    }

    @Override
    public void convert(BaseViewHolder holder, Goods data, int index) {
        //holder.setText(R.id.iv, data.getName());
        holder.setText(R.id.tv_name, data.getName());
        holder.setText(R.id.tv_price, data.getPrice());
    }

    @Override
    public void bind(BaseViewHolder holder, int layoutRes) {
        holder.setChecked(R.id.item_cart, true);
    }

    @Override
    public void convertHead(BaseViewHolder holder, @LayoutRes int headLayout, int index) {
        holder.setText(R.id.tv_head, "这是头部" + (index + 1));
    }

    @Override
    public void convertFoot(BaseViewHolder holder, @LayoutRes int footLayout, int index) {
        holder.setText(R.id.tv_foot, "这是尾部" + (index + 1));
    }
}
