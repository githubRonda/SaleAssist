package com.ronda.saleassist.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ronda.saleassist.R;
import com.ronda.saleassist.base.BaseFragment;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/08/04
 * Version: v1.0
 */

public class CartFragment extends BaseFragment {
    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main_cart, container, false);
    }

    @Override
    public void init(View view) {

    }
}
