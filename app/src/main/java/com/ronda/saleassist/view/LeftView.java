package com.ronda.saleassist.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.ronda.saleassist.R;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/08/16
 * Version: v1.0
 */

public class LeftView {

    private Context mContext;

    private WindowManager mWM;
    private View mLeftView;
    private TextView mTvName, mTvWeight, mTvPrice, mTvTotalCount, mTvTotal;
    private int mLeftViewWidth;


    public LeftView(Context context) {
        mContext = context;

        initLeftView();
    }

    /**
     * 自定义浮窗显示
     */
    private void initLeftView() {
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.windowAnimations = android.R.style.Animation_Toast;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.gravity = Gravity.LEFT + Gravity.CENTER_VERTICAL;
        params.setTitle("Toast");

        mLeftView = LayoutInflater.from(mContext).inflate(R.layout.toast_left_view, null);
        mTvName = (TextView) mLeftView.findViewById(R.id.tv_name);
        mTvWeight = (TextView) mLeftView.findViewById(R.id.tv_weight);
        mTvPrice = (TextView) mLeftView.findViewById(R.id.tv_price);
        mTvTotal = (TextView) mLeftView.findViewById(R.id.tv_subtotal_total);
        mTvTotalCount = (TextView) mLeftView.findViewById(R.id.tv_total_count);

        mLeftView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mLeftViewWidth = mLeftView.getMeasuredWidth();
        mWM.addView(mLeftView, params);
    }

    public void setData(String name, String price, String total) {
        mTvName.setText(name);
        mTvPrice.setText(price);
        mTvTotal.setText(total);
    }


    public void setWeight(String weight) {
        mTvWeight.setText(weight);
    }

    public void setTotal(String total) {
        mTvTotal.setText(total);
    }


    public void showLeftView() {

        if (mLeftView.getTranslationX() >= 0) { //完全展开
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(mLeftView, "translationX", mLeftView.getTranslationX(), 0f);
        animator.setDuration(800);
        animator.start();
    }

    public void hideLeftView() {
        if (mLeftView.getTranslationX() <= -mLeftViewWidth) { //完全收缩
            return;
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(mLeftView, "translationX", mLeftView.getTranslationX(), -mLeftViewWidth);
        animator.setDuration(800);
        animator.start();
    }


}
