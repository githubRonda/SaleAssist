package com.ronda.saleassist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.simple.ViewHolder;
import com.ronda.saleassist.bean.PankuBean;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by lrd on 0020,2016/9/20.
 */
public class PankuBarAdapter extends BaseAdapter {
    protected Context         mContext;
    protected List<PankuBean> mDatas;
    protected LayoutInflater  mInflater;
    protected int             layoutId;


    public PankuBarAdapter(Context context, List<PankuBean> datas, int layoutId) {
        mContext = context;
        mDatas = datas;
        this.layoutId = layoutId;
        mInflater = LayoutInflater.from(context);

    }


    @Override
    public int getCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size();
    }
    @Override
    public PankuBean getItem(int position) {
        return mDatas.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mContext, convertView, parent,
                layoutId, position);
        convert(holder, getItem(position));

        return holder.getConvertView();
    }
    /**
     * 控件赋值
     *
     * @param holder
     * @param bean
     */
    public void convert(ViewHolder holder, PankuBean  bean){
        holder.setText(R.id.tv_name, bean.getName());
        holder.setText(R.id.tv_stock, bean.getStock());

        //把小数的字符串形式转成百分比的字符串形式
        BigDecimal b = new BigDecimal(Double.parseDouble(bean.getPercent())*100);
        String percent_t = b.setScale(1, BigDecimal.ROUND_HALF_UP).toString()+"%";//四舍五入

        holder.setText(R.id.tv_percent, percent_t);
    }
}
