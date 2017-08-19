package com.ronda.saleassist.zznew.guazhang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/11/12.
 */

abstract class MyBaseAdapter<Data extends Object> extends BaseAdapter {
    protected List<Data> list;
    protected LayoutInflater inflater;
    protected Context context;
    protected int viewId;
    protected View view;

    public MyBaseAdapter(Context context, List<Data> list) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Data data = list.get(position);
        return getView(data);
    }

    abstract View getView(Data data);

}
