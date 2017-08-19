package com.ronda.saleassist.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ronda.saleassist.api.volley.VolleyUtil;
import com.ronda.saleassist.base.dialog.DialogFactory;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by ronda on 2016/02/15.
 */
public abstract class BaseFragment extends Fragment {

    public static final String ARGUMENT = "argument";
    protected String TAG = this.getClass().getSimpleName();;
    protected String mArgument;

    protected Context mContext;
    protected View    mRootView;

    protected DialogFactory mDialogFactory;

    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mArgument = bundle.getString(ARGUMENT);
        }

        mDialogFactory = new DialogFactory(getActivity().getSupportFragmentManager()); // getChildFragmentManager() 这个是v4包下的
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        VolleyUtil.getInstance().cancelPendingRequests(TAG);
    }
}
