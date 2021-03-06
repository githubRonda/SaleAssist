package com.ronda.saleassist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ronda.saleassist.R;
import com.ronda.saleassist.bean.CategoryStatisticBean;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lrd on 0016,2016/9/16.
 */
public class CategoryStatisticAdapter extends RecyclerView.Adapter<CategoryStatisticAdapter.ViewHolder> {

    private List<CategoryStatisticBean> mDatas;
    private LayoutInflater mInflater;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setmOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public CategoryStatisticAdapter(Context context, ArrayList<CategoryStatisticBean> datas) {
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(mInflater.inflate(R.layout.item_statistic_category, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvCategory.setText(mDatas.get(position).getCategoryName());
        holder.tvWeight.setText(mDatas.get(position).getGoodnum());
        holder.tvAmount.setText(mDatas.get(position).getIncome());
        
        setOnListener(holder);
    }

    private void setOnListener(final ViewHolder holder) {
        if (mOnItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void addData(CategoryStatisticBean data){
        mDatas.add(data);
        notifyDataSetChanged();
    }

    public void addData(int position, CategoryStatisticBean data){
        mDatas.add(position, data);
        notifyItemInserted(position);
    }

    public void removeData(int position){
        mDatas.remove(position);
        notifyItemRemoved(position);
    }


    public void clearData(){
        mDatas.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategory;
        private TextView tvWeight;
        private TextView tvAmount;

        public ViewHolder(View itemView) {
            super(itemView);

            tvCategory = (TextView) itemView.findViewById(R.id.tv_category);
            tvWeight = (TextView) itemView.findViewById(R.id.tv_weight);
            tvAmount = (TextView) itemView.findViewById(R.id.tv_amount);

        }
    }
}
