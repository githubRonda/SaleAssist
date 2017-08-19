package com.ronda.saleassist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: Ronda(1575558177@qq.com)
 * Date: 2016/10/09
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {
    protected final List<T> mDatas;
    protected       int                     mLayoutId;
    protected OnItemClickListener     mClickListener;
    protected OnItemLongClickListener mLongClickListener;

    //这个Adapter的构造方法没有 layoutId 参数，表明该Adapter中是多布局, 需要拓展这个类，复写onCreateViewHolder()方法
    public BaseRecyclerViewAdapter(List<T> list) {
        mDatas = (list != null) ? list : new ArrayList<T>();
    }

    public BaseRecyclerViewAdapter(List<T> list, int layoutId) {
        this(list);
        mLayoutId = layoutId;
        //mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //因为这里可以使用parent.getContext()来获取上下文，所以本类就不需要 mContext 和 mInflater 成员变量了。本身 mContext 的作用就是加载布局的时候使用
        RecyclerViewHolder holder = RecyclerViewHolder.createViewHolder(parent.getContext(), parent, mLayoutId);
        bindItemViewClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        //T item = getItem(position); //对于有 footer 的列表来说，最后一个 itemView 的 position 要比 mData.size() 大1， 这里就会出现越界异常
        //bindDataToItemView(holder, item, position);// 对于常规的来说（footer）这里传入三个参数感觉比较全面
        bindDataToItemView(holder, position);// 为了更加通用，这里去掉了 item 这个参数
    }

    protected void bindItemViewClickListener(final RecyclerViewHolder holder) {
        if (mClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //其实这里的 v 和 itemView 是一样的
                    mClickListener.onItemClick(holder.itemView, holder, holder.getLayoutPosition());//这里的itemView 的 position 可以自己获取，不需要传入
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
        return mDatas.size();
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public T getItem(int position) {
        return mDatas.get(position);
    }

    public void add(int pos, T item) {
        mDatas.add(pos, item);
        notifyItemInserted(pos);
    }

    public void add(T[] items){
        mDatas.addAll(Arrays.asList(items));
        notifyDataSetChanged();
    }

    public void add(List<T> items){
        mDatas.addAll(items);
        notifyDataSetChanged();
    }

    public void remove(int pos) {
        mDatas.remove(pos);
        notifyItemRemoved(pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mLongClickListener = listener;
    }

    abstract public void bindDataToItemView(RecyclerViewHolder holder, int position);

    public interface OnItemClickListener {
        void onItemClick(View itemView, RecyclerViewHolder holder, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View itemView, RecyclerViewHolder holder, int position);
    }
}
