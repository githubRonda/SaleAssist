package com.ronda.saleassist.module.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ronda.saleassist.base.BaseFragment;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/03/12
 * Version: v1.0
 */

public class MineFragment extends BaseFragment {
    
    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return null;
    }

    @Override
    public void init(View view) {

    }

    public static MineFragment newInstance(String arg){
        MineFragment fragment = new MineFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, arg);
        fragment.setArguments(bundle);
        return fragment;
    }
}
