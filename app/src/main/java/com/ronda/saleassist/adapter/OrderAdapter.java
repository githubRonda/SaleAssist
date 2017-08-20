package com.ronda.saleassist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ronda.saleassist.R;
import com.ronda.saleassist.bean.OrderBean;

import java.util.ArrayList;
import java.util.List;


/**
 * 订单列表的适配器
 * Created by lrd on 0031,2016/7/31.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<OrderBean> mDatas;
    private LayoutInflater mInflater;
    //private int mItemViewLayoutId;//反正这个OrderAdapter也只能供一个RecyclerView使用，所以这里的布局id我就直接写在这个类中，不用当作构造参数传进来了

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mOnItemClickListener = listener;
    }




    public OrderAdapter(Context context, ArrayList<OrderBean> datas) {

        mInflater = LayoutInflater.from(context);
        mDatas = datas;
    }

    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(mInflater.inflate(R.layout.item_order, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(OrderAdapter.ViewHolder holder, int position) {

        holder.tvNo.setText(mDatas.get(position).getNo());
        holder.tvGoodsNum.setText(mDatas.get(position).getBusinessnum());
        holder.tvWeight.setText(mDatas.get(position).getGoodnum());
        holder.tvIncome.setText(mDatas.get(position).getIncome());
        holder.tvDate.setText(mDatas.get(position).getIntime());


        setOnListener(holder);

    }

    private void setOnListener(final RecyclerView.ViewHolder holder) {
        // 如果设置了回调，则设置点击事件
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

    public void addData(OrderBean data){
        mDatas.add(data);
        notifyDataSetChanged();
    }

    public void addData(int position, OrderBean data){
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

        private TextView tvNo;
        private TextView tvGoodsNum;
        private TextView tvWeight;
        private TextView tvIncome;
        private TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);

            tvNo = (TextView) itemView.findViewById(R.id.tv_order_no);
            tvGoodsNum = (TextView) itemView.findViewById(R.id.tv_num_goods);
            tvWeight = (TextView) itemView.findViewById(R.id.tv_total_weight);
            tvIncome = (TextView) itemView.findViewById(R.id.tv_income);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }
}

