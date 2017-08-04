package com.ronda.saleassist.module.home;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.CartAdapter;
import com.ronda.saleassist.base.BaseFragment;
import com.ronda.saleassist.local.sqlite.GreenDaoHelper;
import com.ronda.saleassist.local.sqlite.table.Cart;
import com.ronda.saleassist.local.sqlite.table.CartDao;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/03/12
 * Version: v1.0
 */

public class CartFragment extends BaseFragment {

    @BindView(R.id.tv_label)
    TextView mTvLabel;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private List<Cart>  mData;
    private CartAdapter mAdapter;
    private CartDao mCartDao;
    
    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main_cart, container, false);
    }

    @Override
    public void init(View view) {
        mCartDao = GreenDaoHelper.getDaoSession().getCartDao();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mAdapter = new CartAdapter();
    }



    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            mData = mCartDao.loadAll();

            if (mData == null){
                mData = new ArrayList<>();
            }

            mAdapter.setData(mData);

        }
    }



    public static CartFragment newInstance(String arg){
        CartFragment fragment = new CartFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, arg);
        fragment.setArguments(bundle);
        return fragment;
    }
}
