package com.ronda.saleassist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.ronda.saleassist.R;
import com.ronda.saleassist.base.MyApplication;
import com.ronda.saleassist.utils.NetUtils;
import com.socks.library.KLog;

/*
启动界面的目的是：跳转Activity到登录界面(LoginActivity)，还是内容界面(MainActivity)
在启动界面这段时间内具体要做的事情：
1.检验token登录是否成功

本来token登录和等待跳转是并行的，后来感觉token登录的返回结果有时延迟有5s左右，甚至更多。
所以现在变成了只要收到了response结果，就立即跳转Activity
 */
public class SplashActivity extends AppCompatActivity {

    private boolean isSuccessOfToken = false; // 标识token登录是否成功


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        KLog.i("isTaskRoot:"+this.isTaskRoot());
        KLog.i("启动界面："+ System.currentTimeMillis());

        if(!this.isTaskRoot()) { //判断该Activity是不是任务空间的源Activity，“非”也就是说是被系统重新实例化出来
            finish();
            return;
        }

        loginOfToken();
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


    private void loginOfToken() {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            Toast.makeText(MyApplication.getInstance(), "无网络连接", Toast.LENGTH_SHORT).show();
            goNextActivity();
            return;
        }

//        UserApi.loginUsingToken(new UserApi.VolleyStrHandler() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(SplashActivity.this, R.string.toast_error_response, Toast.LENGTH_SHORT).show();
//                goNextActivity();
//            }
//
//            @Override
//            public void onResponse(String responseStr) {
//                KLog.json(responseStr);
//
//                try {
//                    JSONObject response = new JSONObject(responseStr);
//                    int status = response.getInt("status");
//                    String msg = response.getString("msg");
//
//                    if (status == 1) {
//                        JSONObject data = response.getJSONObject("data");
//
//
//                        //把后台数据解析成JavaBean
//                        ShopBean shopBean = new Gson().fromJson(data.toString(), new TypeToken<ShopBean>(){}.getType());
//                        String mobile = shopBean.getMobile();
//                        String token = shopBean.getToken();
//                        String nickname = shopBean.getNickname();
//                        String userId = shopBean.getUserId();
//                        List<ShopBean.ShopinfoBean> shopinfoList = shopBean.getShopinfo();
//
//                        //保存信息到SharedPreference中
//                        SPHelper.setLoginInfo(mobile, token, nickname, userId, shopinfoList);
//
//
//                        if (shopinfoList != null && shopinfoList.size()>0) {
//                            App.curShopId = shopinfoList.get(0).getShopid();
//                            App.curShopName = shopinfoList.get(0).getShopname();
//                            App.isSupportAlipay = 1 == (shopinfoList.get(0).getAlipay_check())? true : false;
//                            App.isSupportWechatPay = 1 == (shopinfoList.get(0).getWechatpay_check())? true : false;
//                            App.calculatetype = shopinfoList.get(0).getCalculatetype();
//                        }
//                        App.curNickName = shopBean.getNickname();
//
//                        isSuccessOfToken = true;
//
//                    } else {
//
//                        isSuccessOfToken = false;
//                        //Toast.makeText(SplashActivity.this, msg, Toast.LENGTH_SHORT).show();
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                goNextActivity();
//            }
//        });
    }
}
