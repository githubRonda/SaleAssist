package com.ronda.saleassist.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.ronda.saleassist.R;
import com.ronda.saleassist.api.volley.VolleyUtil;
import com.ronda.saleassist.base.dialog.DialogFactory;
import com.ronda.saleassist.local.preference.SPUtils;

/**
 * Activity的基类，方便进行后来的开发
 * Created by HandsomeDragon_Wu on 2016-01-15.
 */
public abstract class BaseActivty extends AppCompatActivity {

    protected String TAG;
    //    private Unbinder unbinder;
    protected Toolbar mToolbar;
    protected Context mContext;

    protected DialogFactory mDialogFactory; // 确定框、进度框、和列表框

    protected String token = SPUtils.getString(AppConst.TOKEN, "");
    protected String shopId = SPUtils.getString(AppConst.CUR_SHOP_ID, "");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        TAG = this.getClass().getSimpleName();

        AppManager.getInstance().addActivity(this);
        AppManager.logActivityStack();

        mDialogFactory = new DialogFactory(getSupportFragmentManager());

//        setContentView(getContentLayoutResId());
//        unbinder = ButterKnife.bind(this);
//        init(savedInstanceState);


    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.gc();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.gc();
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.gc();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().removeActivity(this);

        AppManager.logActivityStack();
        System.gc();

        VolleyUtil.getInstance().cancelPendingRequests(TAG);
    }


    /**
     * 初始化Toolbar
     *
     * @param title
     * @param homeAsUpEnabled
     */
    protected void initToolbar(String title, boolean homeAsUpEnabled) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar == null)
            throw new RuntimeException("no toolbar is found");

        TextView tv_title = (TextView) mToolbar.findViewById(R.id.tv_title);
        tv_title.setText(title);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (homeAsUpEnabled) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    protected void initToolbar(@StringRes int resTitle, boolean homeAsUpEnabled) {
        initToolbar(getResources().getString(resTitle), homeAsUpEnabled);
    }

    protected void setToolbarTitle(String title) {
        TextView tv_title = (TextView) mToolbar.findViewById(R.id.tv_title);
        tv_title.setText(title);
    }

    protected void setToolbarTitle(@StringRes int resId) {
        TextView tv_title = (TextView) mToolbar.findViewById(R.id.tv_title);
        tv_title.setText(resId);
    }

    /**
     * 添加 Fragment . 无回退栈
     *
     * @param containerViewId
     * @param fragment
     */
    protected void addFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(containerViewId, fragment);
        fragmentTransaction.commit();
    }

    /**
     * 添加 Fragment. 有回退栈
     *
     * @param containerViewId
     * @param fragment
     */
    protected void addFragment(int containerViewId, Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // 设置tag，不然下面 findFragmentByTag(tag)找不到
        fragmentTransaction.add(containerViewId, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    /**
     * 在指定容器中，隐藏一个Fragment，显示另一个Fragment
     *
     * @param containerViewId
     * @param from
     * @param to
     */
    protected void showFragment(int containerViewId, Fragment from, Fragment to) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        if (!to.isAdded()) {    // 先判断是否被add过
            transaction.hide(from).add(containerViewId, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
        }
    }

    /**
     * 替换 Fragment. 有回退栈
     *
     * @param containerViewId
     * @param fragment
     */
    protected void replaceFragment(int containerViewId, Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * 替换 Fragment. 有回退栈
     *
     * @param containerViewId
     * @param fragment
     */
    protected void replaceFragment(int containerViewId, Fragment fragment, String tag) {
        if (getSupportFragmentManager().findFragmentByTag(tag) == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            // 设置tag
            fragmentTransaction.replace(containerViewId, fragment, tag);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // 这里要设置tag，上面也要设置tag
            fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.commit();
        } else {
            // 存在则弹出在它上面的所有fragment，并显示对应fragment
            getSupportFragmentManager().popBackStack(tag, 0);
        }
    }

    /**
     * 替换 Fragment. 无回退栈
     *
     * @param containerViewId
     * @param fragment
     */
    protected void replaceFragmentWithoutBackStack(int containerViewId, Fragment fragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment).commit();
    }


    // Activity跳转
    protected void jump(Class<? extends Activity> clz) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        startActivity(intent);
    }


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            finish();
//        }
//        return super.onKeyDown(keyCode, event);
//    }


}

