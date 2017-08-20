package com.ronda.saleassist.adapter;

import android.view.View;

import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.simple2.BaseRecyclerViewAdapter;
import com.ronda.saleassist.adapter.simple2.RecyclerViewHolder;
import com.ronda.saleassist.bean.Category;
import com.ronda.saleassist.fragment.WeightFragment;

import java.util.List;


/**
 * User: Ronda(1575558177@qq.com)
 * Date: 2016/10/15
 */
public class CategoryAdapter extends BaseRecyclerViewAdapter<Category> {

    private static int SELECT_POSITION = 0;// 记录主分类选中了哪一项

//    private static boolean hiddenLast = false;//用于批量修改单价时，对于称重类的分类列表不显示最后一项（添加分类）

    public CategoryAdapter(List<Category> list) {
        super(list, R.layout.list_item_category); //这个参数直接写在这里比较清楚
    }

    @Override
    public void bindDataToItemView(RecyclerViewHolder holder, int position) {
        if (holder.getLayoutPosition() < mDatas.size()) {
            holder.setText(R.id.text_category, mDatas.get(position).getName());
            holder.setBackgroundRes(R.id.text_category, R.color.menu_background_normal);
            if (holder.getLayoutPosition() == WeightFragment.SELECTPOSITION) {
//            if (holder.getLayoutPosition() == SELECT_POSITION) {
                holder.setBackgroundRes(R.id.text_category, R.color.menu_background_selcted);
            }
        } else {
            holder.setText(R.id.text_category, "添加分类");
//            if (hiddenLast){
//                holder.getConvertView().setVisibility(View.GONE);
//            }
        }
    }

    protected void bindItemViewClickListener(final RecyclerViewHolder holder) {

        if (mClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 排除点击添加分类时也给 SELECT_POSITION 赋值的情况
                    if (holder.getLayoutPosition() != getItemCount() - 1) {
                        SELECT_POSITION = holder.getLayoutPosition();
                    }
                    mClickListener.onItemClick(holder.itemView, holder, holder.getLayoutPosition());
                }
            });
        }
        if (mLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mLongClickListener.onItemLongClick(holder.itemView, holder, holder.getLayoutPosition());
                    return true;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mDatas.size() + 1;
    }

//    public void hideLastItem(){
//        hiddenLast = true;
//        notifyItemChanged(getItemCount()-1);
//    }
}
