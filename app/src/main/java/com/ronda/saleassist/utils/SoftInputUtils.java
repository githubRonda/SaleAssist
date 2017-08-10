package com.ronda.saleassist.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by ronda on 17-8-10/10.
 *
 * 软键盘的工具类
 */

public class SoftInputUtils {
    /**
     * 关闭软键盘
     * 下面这种方法使用 InputMethodManager 来关闭。其实还有一种思路：把当前对话框, 获取焦点的View 隐藏掉, 此时就会因为 EditText 失去焦点而自动隐藏软键盘
     *
     * @param activity
     */
    public static void hide(Activity activity){
        //关闭软键盘
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
