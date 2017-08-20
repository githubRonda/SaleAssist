package com.ronda.saleassist.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import com.ronda.saleassist.R;
import com.ronda.saleassist.adapter.PankuBarAdapter;
import com.ronda.saleassist.base.BaseFragment;
import com.ronda.saleassist.bean.PankuBean;

import java.util.ArrayList;

/**
 * Created by lrd on 0030,2016/9/30.
 */
public class PankuBargoodsFragment extends BaseFragment {

    private ArrayList<PankuBean> mDatas;//上半部的RecyclerView 称重类的数据
    private PankuBarAdapter      mAdapter; //上半部的RecyclerView的适配器

    private PankuWeightFragment pankuWeightFragment;

    private ListView listView;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_panku_bargoods, container, false);
    }

    @Override
    public void init(View view) {

        listView = (ListView) view.findViewById(R.id.list_view);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        pankuWeightFragment = (PankuWeightFragment) fm.findFragmentByTag("weight");

        mDatas = pankuWeightFragment.getBargoodsDatas();

        mAdapter = new PankuBarAdapter(getActivity(), mDatas, R.layout.item_panku_bar);

        listView = (ListView) view.findViewById(R.id.list_view);
        listView.setAdapter(mAdapter);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false){
            mAdapter.notifyDataSetChanged();
        }
    }
}
