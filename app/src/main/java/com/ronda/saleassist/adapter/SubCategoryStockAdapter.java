package com.ronda.saleassist.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ronda.saleassist.R;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.bean.SubCategory;

import java.util.ArrayList;



public class SubCategoryStockAdapter extends RecyclerView.Adapter<SubCategoryStockAdapter.ViewHolder> {

    private Context                context;
    private LayoutInflater         layoutInflater;
    private ArrayList<SubCategory> listData;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);

        void onItemLongClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void setOnListener(final RecyclerView.ViewHolder holder) {
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int layoutPosition = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, layoutPosition);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int layoutPosition = holder.getLayoutPosition();
                    onItemClickListener.onItemLongClick(holder.itemView, layoutPosition);
                    return false;
                }
            });
        }
    }

    public SubCategoryStockAdapter(Context context, ArrayList<SubCategory> listData) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.listData = listData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_subcategory1, null);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.text_subcategory = (TextView) view.findViewById(R.id.text_subcategory);
        viewHolder.img_subcategory = (ImageView) view.findViewById(R.id.img_subcategory);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder.getLayoutPosition() < listData.size()) {
            //substring(str.indexOf("/")+1, str.length())
            String unit = listData.get(position).getUnit();
            holder.text_subcategory.setText("菜名："+listData.get(position).getName()+"\n库存："+listData.get(position).getStock()+ unit.substring(unit.indexOf("/")+1, unit.length()));
            //holder.img_subcategory.setImageResource(R.mipmap.ic_launcher);
            Glide.with(context)
                    //.load(new File(listData.get(position).getImg()))
                    .load(UserApi.BASE_SERVER+listData.get(position).getPicurl())
                    .into(holder.img_subcategory);
        } else {
            holder.text_subcategory.setText("添加菜品");
            holder.img_subcategory.setImageResource(R.drawable.img_add_subcategory);
        }
        setOnListener(holder);
    }

    //这里返回的数据 +1 表示最后一位要自定义为添加的选项
    @Override
    public int getItemCount() {
        return listData.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_subcategory;
        ImageView img_subcategory;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
