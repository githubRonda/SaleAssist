package com.ronda.saleassist.base.dialog;


import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;




/**
 * Created by ronda on 2016/2/3.
 * 对话框工厂
 */
public class DialogFactory {

    /**
     * 进度条的tag
     */
    private static final String DIALOG_PROGRESS_TAG = "progress";

    private static final String DIALOG_CONFIRM_TAG = "confirm";
    private static final String DIALOG_LIST_TAG    = "list";

    private FragmentManager mFragmentManager;


    public DialogFactory(FragmentManager fragmentManager){
        this.mFragmentManager = fragmentManager;
    }

    /**
     * @param title 对话框title
     * @param message
     * @param cancelable
     * @param listener
     */
    public void showConfirmDialog(String title, String message, boolean cancelable, int btnCount, DialogActionListener listener){

        FragmentTransaction ft = mFragmentManager.beginTransaction();
        Fragment fragment = mFragmentManager.findFragmentByTag(DIALOG_CONFIRM_TAG);
        if (null != fragment) {
            ft.remove(fragment);
        }
        ConfirmDialogFragment df = ConfirmDialogFragment.newInstance(title, message, cancelable, btnCount);
        df.show(mFragmentManager,DIALOG_CONFIRM_TAG);
        df.setActionListener(listener);
        mFragmentManager.executePendingTransactions();
    }




    /**
     * 显示列表对话框
     * @param items
     * @param cancelable
     */
    public void showListDialog(String[] items,boolean cancelable,DialogActionListener listener){
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        Fragment fragment = mFragmentManager.findFragmentByTag(DIALOG_LIST_TAG);
        if (null != fragment) {
            ft.remove(fragment);
        }
        ListDialogFragment df = ListDialogFragment.newInstance(items,cancelable);
        df.show(mFragmentManager,DIALOG_LIST_TAG);
        df.setActionListener(listener);
        mFragmentManager.executePendingTransactions();
    }


    /**
     * @param message 进度条显示的信息
     * @param cancelable 点击空白处是否可以取消
     */
    public void showProgressDialog(String message, boolean cancelable){
        if(mFragmentManager != null){

            /**
             * 为了不重复显示dialog，在显示对话框之前移除正在显示的对话框。
             */
           FragmentTransaction ft = mFragmentManager.beginTransaction();
           Fragment fragment = mFragmentManager.findFragmentByTag(DIALOG_PROGRESS_TAG);
            if (null != fragment) {
                ft.remove(fragment).commit();
            }

            ProgressDialogFragment progressDialogFragment = ProgressDialogFragment.newInstance(message, cancelable);
            progressDialogFragment.show(mFragmentManager, DIALOG_PROGRESS_TAG);

            mFragmentManager.executePendingTransactions();
        }
    }

    /**
     * 取消进度条
     */
    public void dissProgressDialog(){
       Fragment fragment = mFragmentManager.findFragmentByTag(DIALOG_PROGRESS_TAG);
        if (null != fragment) {
            ((ProgressDialogFragment)fragment).dismiss();
            mFragmentManager.beginTransaction().remove(fragment).commit();
        }
    }

    public interface DialogActionListener {
        void onDialogClick(DialogInterface dialog, int which);
    }

}
