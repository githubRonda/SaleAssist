package com.ronda.saleassist.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.ronda.saleassist.R;
import com.ronda.saleassist.base.BaseDialogFragment;
import com.ronda.saleassist.local.sqlite.GreenDaoHelper;
import com.ronda.saleassist.local.sqlite.table.CartBeanOrder;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import xyz.zpayh.adapter.BaseAdapter;
import xyz.zpayh.adapter.BaseViewHolder;
import xyz.zpayh.adapter.OnItemClickListener;

/**
 * 取单 对话框
 * <p>
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/04/06
 * Version: v1.0
 */

public class GetOrderDialog extends BaseDialogFragment {


    @BindView(R.id.tv_title)
    TextView     mTvTitle;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;



    private MyAdapter mAdapter;
    private int mCurSelectPosition = 0;


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_get_order, container, false);
    }

    @Override
    public void init(View rootView) {

        mTvTitle.setText("取单");

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new MyAdapter();

        List<CartBeanOrder> list = GreenDaoHelper.getDaoSession().getCartBeanOrderDao().loadAll();
        mAdapter.setData(list);
        mRecyclerView.setAdapter(mAdapter);


        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull View view, int adapterPosition) {
                // 选中或取消选中
                if (mCurSelectPosition != adapterPosition) {
                    mCurSelectPosition = adapterPosition;
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    @OnClick({R.id.btn_cancel, R.id.btn_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_confirm:
                doConfirm();
                break;
        }
    }


    /**
     * 点击确定按钮时
     */
    private void doConfirm() {

        if (mAdapter.getData().isEmpty()){
            Toast.makeText(mContext, "数据为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mCallbackListener!=null){
            GreenDaoHelper.getDaoSession().getCartBeanOrderDao().delete(mAdapter.getData(mCurSelectPosition)); // 取出就从数据库中删除
            mCallbackListener.onCall(mAdapter.getData(mCurSelectPosition).getCartbeans());
        }
        dismiss();
    }


    public static GetOrderDialog newInstance(String arg) {
        GetOrderDialog fragment = new GetOrderDialog();
        Bundle args = new Bundle();
        args.putString(ARGUMENT, arg);
        fragment.setArguments(args);
        return fragment;
    }


    private CallbackListener mCallbackListener;

    public GetOrderDialog setCallbackListener(CallbackListener callbackListener) {
        mCallbackListener = callbackListener;
        return this;
    }

    /**
     * 回调方法
     */
    public interface CallbackListener {
        void onCall(String cartbeans);
    }


    class MyAdapter extends BaseAdapter<CartBeanOrder> {

        @Override
        public int getLayoutRes(int index) {
            return R.layout.item_save_order_list;
        }

        @Override
        public void convert(BaseViewHolder holder, CartBeanOrder data, int index) {

            holder.setText(R.id.tv_order, (index + 1) + "");
            holder.setText(R.id.tv_date, data.getDate());
            holder.setText(R.id.tv_total, data.getTotal());


            if (mCurSelectPosition == index)
                holder.itemView.setBackgroundResource(R.drawable.bg_table_row_select);
            else
                holder.itemView.setBackgroundDrawable(null);
        }

        @Override
        public void bind(BaseViewHolder holder, int layoutRes) {
            holder.setClickable(layoutRes, true);
        }
    }
}
