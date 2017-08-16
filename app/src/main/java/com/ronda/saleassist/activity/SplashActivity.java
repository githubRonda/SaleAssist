package com.ronda.saleassist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.ronda.saleassist.R;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.base.AppManager;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.bean.LoginBean;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.utils.ToastUtils;
import com.ronda.saleassist.utils.VersionUtils;
import com.socks.library.KLog;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
启动界面的目的是：跳转Activity到登录界面(LoginActivity)，还是内容界面(MainActivity)
在启动界面这段时间内具体要做的事情：
1.检验token登录是否成功

若网络请求的时间超过了3s，则此时就直接跳转；否则继续等待，直到3s钟后跳转Activity
*/
public class SplashActivity extends BaseActivty {

    @BindView(R.id.tv_version)
    TextView mTvVersion;
    @BindView(R.id.fl_root)
    FrameLayout mFlRoot;

    private boolean isSuccessOfToken = false; // 标识token登录是否成功

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        if (!this.isTaskRoot()) { //判断该Activity是不是任务空间的源Activity，“非”也就是说是被系统重新实例化出来
            finish();
            return;
        }

        mTvVersion.setText("版本：" + VersionUtils.getVersionName(this));

        loginOfToken();

        //渐变的动画效果
        AlphaAnimation anim = new AlphaAnimation(0.3f, 1.0f);
        anim.setDuration(2000);
        mFlRoot.startAnimation(anim);
    }


    private void goNextActivity() {
        Intent intent;
        if (isSuccessOfToken) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }


    //使用手机号 + token
    private void loginOfToken() {

        final long startTime = System.currentTimeMillis();
        String mobile = SPUtils.getString(AppConst.MOBILE, "");
        String token = SPUtils.getString(AppConst.TOKEN, "");

        UserApi.login(TAG, mobile, null, null, token,
                new Response.Listener<LoginBean>() {
                    @Override
                    public void onResponse(final LoginBean response) {
                        //KLog.json(new Gson().toJson(response));

                        if (response.getStatus() != 1) {
                            isSuccessOfToken = false;

                        } else {
                            isSuccessOfToken = true;

                            //先清空
                            //SPUtils.clear();//其他信息不能清空（进制，GoodsStyle）
                            SPUtils.remove(AppConst.TOKEN);
                            SPUtils.remove(AppConst.MOBILE);
                            SPUtils.remove(AppConst.NICK_NAME);
                            SPUtils.remove(AppConst.USER_ID);
                            SPUtils.remove(AppConst.LOGIN_SHOP_LIST);
                            SPUtils.remove(AppConst.CUR_SHOP_ID);
                            SPUtils.remove(AppConst.CUR_SHOP_NAME);
                            SPUtils.remove(AppConst.SUPPORT_ALIPAY);
                            SPUtils.remove(AppConst.SUPPORT_WECHATPAY);

                            //保存信息到SharedPreference中
                            SPUtils.putString(AppConst.TOKEN, response.getData().getToken());
                            SPUtils.putString(AppConst.MOBILE, response.getData().getMobile());
                            SPUtils.putString(AppConst.NICK_NAME, response.getData().getMobile());
                            SPUtils.putString(AppConst.USER_ID, response.getData().getMobile());
                            SPUtils.putList(AppConst.LOGIN_SHOP_LIST, response.getData().getShopinfo());
                            SPUtils.putString(AppConst.CUR_SHOP_ID, response.getData().getShopinfo().get(0).getShopid()); // 默认当前店铺为第一个店铺
                            SPUtils.putString(AppConst.CUR_SHOP_NAME, response.getData().getShopinfo().get(0).getShopname());
                            SPUtils.putBoolean(AppConst.SUPPORT_ALIPAY, response.getData().getShopinfo().get(0).getAlipay_check() == 1); //等于1说明开通
                            SPUtils.putBoolean(AppConst.SUPPORT_WECHATPAY, response.getData().getShopinfo().get(0).getWechatpay_check() == 1);
                        }

                        // 若网络请求的时间超过了3s，则此时就直接跳转；否则继续等待，直到3s钟后跳转
                        long waitTime = 3000 - (System.currentTimeMillis() - startTime);
                        waitTime = waitTime < 0 ? 0 : waitTime;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showToast(response.getMsg());
                                goNextActivity();
                            }
                        }, waitTime);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.showToast(R.string.no_respnose);
                        //若网络错误则直接跳转至登录界面
                        isSuccessOfToken = false;
                        goNextActivity();
                    }
                });
    }
}
