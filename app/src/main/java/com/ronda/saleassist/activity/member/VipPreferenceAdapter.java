package com.ronda.saleassist.activity.member;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.simple2.BaseRecyclerViewAdapter;
import com.ronda.saleassist.adapter.simple2.RecyclerViewHolder;
import com.ronda.saleassist.bean.PreferenceBean;

import java.util.List;


/**
 * author: Ronda(1575558177@qq.com)
 * Date: 2016/11/18
 * Version: v1.0
 */

public class VipPreferenceAdapter extends BaseRecyclerViewAdapter<PreferenceBean> {

    private OnDeleteBtnClickListener mOnDeleteBtnClickListener;

    public VipPreferenceAdapter(List<PreferenceBean> list) {
        super(list, R.layout.list_item_preference);
    }

    @Override
    public void bindDataToItemView(final RecyclerViewHolder holder, final int position) {
        PreferenceBean bean = mDatas.get(position);
        String msg = "";
        switch (bean.getCosttype()){
            case "1":
                msg = "消费满"+bean.getMin()+"元, 减"+bean.getCommission()+"元";
                break;
            case "2":
                msg = "消费满"+bean.getScore()+"元，送1积分";
                break;
            case "3":
                msg = "折扣为："+bean.getCost();
                break;
            case "4":
                msg = "充值满"+bean.getMin()+"元, 送"+bean.getGift()+"元";
                break;
        }

        holder.setText(R.id.tv_name, mDatas.get(position).getName());
        holder.setText(R.id.tv_show_preference, msg);

        if (mOnDeleteBtnClickListener != null) {
            holder.getView(R.id.img_del).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnDeleteBtnClickListener.onDeleteBtnClick(holder.getConvertView(), holder, position);
                }
            });
        }
    }


    public void setOnDeleteBtnClickListener(OnDeleteBtnClickListener onDeleteBtnClickListener) {
        mOnDeleteBtnClickListener = onDeleteBtnClickListener;
    }

    public interface OnDeleteBtnClickListener {
        void onDeleteBtnClick(View itemView, RecyclerView.ViewHolder holder, int position);
    }
}
