package com.ronda.saleassist.activity.member;

import android.view.View;
import android.view.ViewGroup;

import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.BaseRecyclerViewAdapter;
import com.ronda.saleassist.adapter.RecyclerViewHolder;
import com.ronda.saleassist.bean.ApplyInfoBean;

import java.util.List;


/**
 * User: Ronda(1575558177@qq.com)
 * Date: 2016/10/24
 */
public class ApplicantListAdapter extends BaseRecyclerViewAdapter<ApplyInfoBean> {

    private OnOperateListner mOnOperateListner;


    public ApplicantListAdapter(List<ApplyInfoBean> list) {
        super(list);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RecyclerViewHolder holder = RecyclerViewHolder.createViewHolder(parent.getContext(), parent, R.layout.list_item_apply_member);
        //bindItemViewClickListener(holder);
        bindHolderViewOperate(holder);
        return holder;
    }

    private void bindHolderViewOperate(final RecyclerViewHolder holder) {
        if (mOnOperateListner != null) {
            holder.setOnClickListener(R.id.btn_agree, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnOperateListner.agree(v, holder.getLayoutPosition());
                }
            });
            holder.setOnClickListener(R.id.btn_refuse, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnOperateListner.refuse(v, holder.getLayoutPosition());
                }
            });

            holder.setOnClickListener(R.id.btn_defriend, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnOperateListner.defriend(v, holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public void bindDataToItemView(RecyclerViewHolder holder, int position) {

        holder.setText(R.id.tv_applyer, mDatas.get(position).getUser());
    }

    public void setOnOperateListner(OnOperateListner onOperateListner) {
        mOnOperateListner = onOperateListner;
    }


    interface OnOperateListner {
        void agree(View v, int position);

        void refuse(View v, int position);

        void defriend(View v, int position);
    }
}
