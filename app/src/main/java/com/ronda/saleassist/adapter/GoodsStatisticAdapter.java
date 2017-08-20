package com.ronda.saleassist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ronda.saleassist.R;
import com.ronda.saleassist.bean.GoodsStatisticBean;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lrd on 0016,2016/9/16.
 */
public class GoodsStatisticAdapter extends RecyclerView.Adapter<GoodsStatisticAdapter.ViewHolder> {

    private List<GoodsStatisticBean> mDatas;
    private LayoutInflater mInflater;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public GoodsStatisticAdapter(Context context, ArrayList<GoodsStatisticBean> datas) {
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
        holder.tvGoods.setText(mDatas.get(position).getGoodsName());
        holder.tvWeight.setText(mDatas.get(position).getGoodsNum());
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

    public void addData(GoodsStatisticBean data){
        mDatas.add(data);
        notifyDataSetChanged();
    }

    public void addData(int position, GoodsStatisticBean data){
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
        private TextView tvGoods;
        private TextView tvWeight;
        private TextView tvAmount;

        public ViewHolder(View itemView) {
            super(itemView);

            tvGoods = (TextView) itemView.findViewById(R.id.tv_category);
            tvWeight = (TextView) itemView.findViewById(R.id.tv_weight);
            tvAmount = (TextView) itemView.findViewById(R.id.tv_amount);

        }
    }
}
