package com.ronda.saleassist.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.ronda.saleassist.R;
import com.ronda.saleassist.base.BaseActivty;
import com.socks.library.KLog;

public class SellManageActivity extends BaseActivty {

    private WindowManager mWM;
    private View mToastView;
    private TextView mTvWeight;
    private TextView mTvLabel;
    private int measuredWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_manage);

        mWM = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);


        mTvLabel = (TextView) findViewById(R.id.tv_label);


        initToast();
//        mTvLabel.post(new Runnable() {
//            @Override
//            public void run() {
//                initToast();
//            }
//        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mToastView != null) {
            mWM.removeView(mToastView);
            mToastView = null;
        }
    }

    private void initToast() {
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

        mToastView = LayoutInflater.from(mContext).inflate(R.layout.toast_address, null);
        mTvWeight = (TextView) mToastView.findViewById(R.id.tv_weight);
        mToastView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        measuredWidth = mToastView.getMeasuredWidth();
        mWM.addView(mToastView, params);
    }


    public void show(View view) {
        if (mToastView.getTranslationX() >= 0) { //完全展开
            return;
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(mToastView, "translationX", mToastView.getTranslationX(), 0f);
        animator.setDuration(1000);
        animator.start();

        KLog.d("show=====");
    }

    public void hide(View view) {
        if (mToastView.getTranslationX() <= -measuredWidth) { //完全收缩
            return;
        }

        ObjectAnimator animator = ObjectAnimator.ofFloat(mToastView, "translationX", mToastView.getTranslationX(), -measuredWidth);
        animator.setDuration(1000);
        animator.start();

        KLog.d("hide-----");
    }


    /**
     * 自定义归属地浮窗显示
     */
    private void showToast(String text) {
        if (mToastView == null) {
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            //params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.width = 155;
            params.format = PixelFormat.TRANSLUCENT;
            params.windowAnimations = android.R.style.Animation_Toast;
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
//            params.type = WindowManager.LayoutParams.TYPE_PHONE;
            params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            params.gravity = Gravity.LEFT + Gravity.TOP;
            params.setTitle("Toast");

            params.x = -200;
            params.y = 100;

            mToastView = LayoutInflater.from(mContext).inflate(R.layout.toast_address, null);
            mTvWeight = (TextView) mToastView.findViewById(R.id.tv_weight);

            mToastView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

//            params.x = -mToastView.getMeasuredWidth();
//            params.y = 50;

            mToastView.post(new Runnable() {
                @Override
                public void run() {
//                    mWM.addView(mToastView, params);
                }
            });

//            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.push_left_in);
//            Animation animation = new TranslateAnimation(0, 0, 100, 0);
//            animation.setDuration(2000);


//            Animation animation = new TranslateAnimation(0, 200,0 , 0);
//            animation.setDuration(2000);
//            animation.setFillAfter(true);
//            mToastView.startAnimation(animation);


        }

        float translationX = mToastView.getTranslationX();

        if (translationX == 0) { // 完全展开

        }

        int measuredWidth = mToastView.getMeasuredWidth();
        KLog.i("translationX: " + translationX + ", measuredWidth: " + measuredWidth);

//        ObjectAnimator animator = ObjectAnimator.ofFloat(mToastView, "translationX", -measuredWidth, 0f);
        ObjectAnimator animator = ObjectAnimator.ofFloat(mToastView, "translationX", 0f, measuredWidth);
        animator.setDuration(1000);
        animator.start();

        mTvWeight.setText(text);
    }


    private boolean isOpened = false;
    private boolean isClosed = false;

    private void hideToast() {
        if (mToastView != null) {
//            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.push_left_out);

//            Animation animation = new TranslateAnimation(200, 0, 0, 0);
//            animation.setFillAfter(true);
//            animation.setDuration(2000);
//            mToastView.startAnimation(animation);
            float translationX = mToastView.getTranslationX();

            int measuredWidth = mToastView.getMeasuredWidth();
            KLog.i("translationX: " + translationX + ", measuredWidth: " + measuredWidth);


            ObjectAnimator animator = ObjectAnimator.ofFloat(mToastView, "translationX", 0f, -measuredWidth);
            animator.setDuration(1000);
            animator.start();

//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mWM.removeViewImmediate(mToastView);
//                    mToastView = null;
//                }
//            }, 1000);

        }
    }
}
