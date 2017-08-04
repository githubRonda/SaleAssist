package com.ronda.saleassist.base.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ronda.saleassist.base.BaseDialogFragment;


/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/03/22
 * Version: v1.0
 */

/**
 * DialogFragment 之所以在屏幕旋转之后不会消失，原因就在于销毁后又重绘了一遍。而 Dialog 和 AlertDialog 在屏幕旋转后就会消失，并且还会输出Log错误信息（不一定会导致程序崩溃）
 * <p>
 * 当配置发生变化时，eg：屏幕旋转等
 * 当前显示的 DialogFragment 会被销毁掉，onDestroy() 和 onDetach()都会执行，然后又新建一个 DialogFragment 对象，重新触发 onCreate() 生命周期等方法。其实本质上就是Activity因屏幕旋转而销毁重建导致其内部的所有View都要销毁重建。
 * <p>
 * 上面这个Fragment的特性和Activity的是一样的，屏幕旋转后，都会销毁当前的，然后重新创建一个。
 * <p>
 * 解决方法：
 * -- 1) 我们可以在DialogFragment时，把所需的数据存储在Fragment的arguments中，然后每次在 onCreate() 中获取参数，并初始化成员变量
 * -- 2) 常规方法：恢复现场法：使用 onSaveInstanceState() 和 onCreate() 把数据保存在 Bundle 中. Fragment中无 onRestoreInstanceState()方法 (写在这里，我怀疑这种方法和第一种本质上是一样的，因为argument也是Bundle类型)
 * -- 3) Manifest中为宿主Activity配置旋转后不销毁Activity：android:configChanges="orientation|keyboardHidden|keyboard|screenLayout|screenSize" （布局不变时，用这种方法比较简单）
 * -- 4) Manifest中为宿主Activity配置禁止旋转Activity：android:screenOrientation="portrait"
 */
public class ConfirmDialogFragment extends BaseDialogFragment {

    private static final String EXTRA_DIALOG_TITLE_KEY     = "extra_dialog_title_key";
    private static final String EXTRA_DIALOG_MESSAGE_KEY   = "extra_dialog_message_key";
    private static final String EXTRA_DIALOG_CANELABLE_KEY = "extra_dialog_cancelable";
    private static final String EXTRA_DIALOG_BTN_COUNT     = "extra_dialog_btn_count"; // btn的个数


    protected boolean mIsCancelable;
    protected String  mTitle;
    protected String  mMessage;
    private   int     btnCount;


    private DialogFactory.DialogActionListener mActionListener;

    public void setActionListener(DialogFactory.DialogActionListener actionListener) {
        mActionListener = actionListener;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mTitle = args.getString(EXTRA_DIALOG_TITLE_KEY);
        mMessage = args.getString(EXTRA_DIALOG_MESSAGE_KEY);
        mIsCancelable = args.getBoolean(EXTRA_DIALOG_CANELABLE_KEY);

        btnCount = args.getInt(EXTRA_DIALOG_BTN_COUNT);

    }


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void init(View view) {}

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (null != mActionListener) {
                    mActionListener.onDialogClick(dialog, which);
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(mTitle == null ? "" : mTitle)//getString(R.string.app_name)
                .setMessage(mMessage == null ? " " : mMessage);


        if (btnCount == 3) {
            builder.setNegativeButton("否", listener)
                    .setPositiveButton("是", listener)
                    .setNeutralButton("取消", listener);
        } else if (btnCount == 2) {
            builder.setNegativeButton("取消", listener)
                    .setPositiveButton("确定", listener);
        } else if (btnCount == 1) {
            builder.setPositiveButton("确定", listener);
        }

        setCancelable(mIsCancelable); // 在 onCreateDialog 中设置才有效
        return builder.create();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //setCancelable(mIsCancelable); // 在 onViewCreated 中设置是没有效果
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public static ConfirmDialogFragment newInstance(String title, String message, boolean cancelable, int btnCount) {
        ConfirmDialogFragment instance = new ConfirmDialogFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_DIALOG_TITLE_KEY, title);
        args.putString(EXTRA_DIALOG_MESSAGE_KEY, message);
        args.putBoolean(EXTRA_DIALOG_CANELABLE_KEY, cancelable);
        args.putInt(EXTRA_DIALOG_BTN_COUNT, btnCount);
        instance.setArguments(args);
        return instance;
    }

}
