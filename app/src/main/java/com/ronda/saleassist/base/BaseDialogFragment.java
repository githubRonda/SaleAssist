package com.ronda.saleassist.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/03/08
 * Version: v1.0
 */

public abstract class BaseDialogFragment extends DialogFragment {

    public static final String ARGUMENT = "argument";

    protected String mArgument;

    protected Context mContext;
    protected View    mRootView;

    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mArgument = bundle.getString(ARGUMENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);

        mRootView = createView(inflater, container, savedInstanceState);
        this.mContext = getActivity();
        unbinder = ButterKnife.bind(this, mRootView);
        init(mRootView);
        return mRootView;
    }

    public abstract View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public abstract void init(View view);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 设置某个View 下的最终的子节点是否可用
     * @param rootView
     * @param enable
     */
    protected void setAllViewEnable(View rootView, boolean enable) {

        if (rootView instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) rootView).getChildCount(); i++) {
                View v = ((ViewGroup) rootView).getChildAt(i);
                setAllViewEnable(v, enable);
            }
        } else {
            rootView.setEnabled(enable);
        }
    }
}
