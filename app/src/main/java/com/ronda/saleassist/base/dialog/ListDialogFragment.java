package com.ronda.saleassist.base.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.ronda.saleassist.base.BaseDialogFragment;


/**
 * 列表dialog
 */
public class ListDialogFragment extends BaseDialogFragment {

    private   String[] mItemContents;
    protected boolean  mIsCancelable;

    private DialogFactory.DialogActionListener mActionListener;

    public void setActionListener(DialogFactory.DialogActionListener actionListener) {
        mActionListener = actionListener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle args = getArguments();
        mItemContents = args.getStringArray("items");
        mIsCancelable = args.getBoolean("cancelable");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setItems(mItemContents, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mActionListener != null) {
                            mActionListener.onDialogClick(dialog, which);
                        }
                    }
                });

        setCancelable(mIsCancelable);

        return builder.create();
    }


    //纯粹向父类中的onCreateView()中添加取消标题栏的代码
    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void init(View view) {

    }


    public static ListDialogFragment newInstance(String[] itemContents, boolean cancelable) {

        ListDialogFragment dialog = new ListDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArray("items", itemContents);
        bundle.putBoolean("cancelable", cancelable);
        dialog.setArguments(bundle);
        return dialog;
    }
}
