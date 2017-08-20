package com.ronda.saleassist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ronda.saleassist.R;
import com.ronda.saleassist.bean.GoodsDetailBean;

import java.util.List;

/**
 * 订单详细记录的适配器
 * Created by lrd on 0021,2016/8/21.
 */
public class GoodsDetailAdapter extends RecyclerView.Adapter<GoodsDetailAdapter.ViewHolder> {

    private List<GoodsDetailBean> mDatas;
    private LayoutInflater mInflater;

    private OnItemClickListener mOnItemClickListener;

    public GoodsDetailAdapter(Context context, List<GoodsDetailBean> datas){
        mInflater = LayoutInflater.from(context);
        mDatas = datas;
    }

    @Override
    public GoodsDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(mInflater.inflate(R.layout.card_good_detail, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(GoodsDetailAdapter.ViewHolder holder, int position) {


        holder.tvGoodsName.setText(mDatas.get(position).getGoodsName());
        holder.tvPrice.setText(mDatas.get(position).getPrice());
        holder.tvDiscount.setText(mDatas.get(position).getDiscount());
        holder.tvNumber.setText(mDatas.get(position).getGoodsNumer());
        holder.tvGoodsCost.setText(mDatas.get(position).getGoodsCost());
        holder.tvOrderNo.setText(mDatas.get(position).getOrderNo());
        holder.tvOrderCost.setText(mDatas.get(position).getOrderCost());
        holder.tvPayMethod.setText(mDatas.get(position).getPayMethod());
        holder.tvOperator.setText(mDatas.get(position).getOperaterNickname());

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    public void addData(GoodsDetailBean data){
        mDatas.add(data);
        notifyDataSetChanged();
    }

    public void addData(int position, GoodsDetailBean data){
        mDatas.add(position, data);
        notifyItemInserted(position);
    }

    public void removeData(int position){
        mDatas.remove(position);
        notifyItemRemoved(position);
    }


    public interface OnItemClickListener{
        void OnItemClick();
        void onItemLongClick();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvGoodsName;
        private TextView tvPrice;
        private TextView tvDiscount;
        private TextView tvNumber;
        private TextView tvGoodsCost;
        private TextView tvOrderNo;
        private TextView tvOrderCost;

        private TextView tvPayMethod;
        private TextView tvOperator;


        public ViewHolder(View itemView) {
            super(itemView);

            tvGoodsName = (TextView) itemView.findViewById(R.id.tv_goods_name);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            tvDiscount = (TextView) itemView.findViewById(R.id.tv_discount);
            tvNumber = (TextView) itemView.findViewById(R.id.tv_number);
            tvGoodsCost = (TextView) itemView.findViewById(R.id.tv_goods_cost);
            tvOrderNo = (TextView) itemView.findViewById(R.id.tv_order_no);
            tvOrderCost = (TextView) itemView.findViewById(R.id.tv_order_cost);
            tvPayMethod = (TextView) itemView.findViewById(R.id.tv_pay_method);
            tvOperator = (TextView) itemView.findViewById(R.id.tv_operater);
        }
    }
}
