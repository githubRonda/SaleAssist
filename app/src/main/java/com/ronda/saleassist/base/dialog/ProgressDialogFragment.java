package com.ronda.saleassist.base.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.ronda.saleassist.base.BaseDialogFragment;


/**
 * 自定义进度dialog
 */
public class ProgressDialogFragment extends DialogFragment {

    private String  mMessage;
    private boolean mIsCancelable;

    private DialogFactory.DialogActionListener mActionListener;

    public void setActionListener(DialogFactory.DialogActionListener actionListener) {
        mActionListener = actionListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        mMessage = bundle.getString("msg");
        mIsCancelable = bundle.getBoolean("cancelable");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(mMessage);
        setCancelable(mIsCancelable);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public static ProgressDialogFragment newInstance(String message, boolean cancelable) {
        ProgressDialogFragment dialog = new ProgressDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("msg", message);
        bundle.putBoolean("cancelable", cancelable);
        dialog.setArguments(bundle);
        return dialog;
    }

}
