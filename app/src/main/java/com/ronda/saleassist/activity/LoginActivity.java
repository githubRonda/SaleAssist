package com.ronda.saleassist.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.ronda.saleassist.R;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.api.volley.GsonUtil;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.base.AppManager;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.bean.BaseBean;
import com.ronda.saleassist.bean.LoginBean;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.utils.MD5Utils;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivty {
    @BindView(R.id.et_phone)
    EditText mEtPhone;
    @BindView(R.id.et_password)
    EditText mEtPassword;

    @BindView(R.id.et_code)
    EditText mEtCode;
    @BindView(R.id.btn_code)
    Button mBtnCode;


    @BindView(R.id.input_layout_pwd)
    TextInputLayout mInputLayoutPwd;
    @BindView(R.id.layout_code)
    LinearLayout mLayoutCode;
    @BindView(R.id.layout_use_msg)
    LinearLayout mLayoutUseMsg;
    @BindView(R.id.layout_use_pwd)
    LinearLayout mLayoutUsePwd;


    private boolean isUseMsg = false; //表示是不是用短信登录
    private ProgressDialog pd; //登录时的进度对话框


    private Handler mHandler = new Handler();
    private Runnable mRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initToolbar("登录 ", false);

        initView();
    }

    private void initView() {
        setMsgViewVisible(false);

        String savePhone = SPUtils.getString(AppConst.MOBILE, "");
        mEtPhone.setText(savePhone);
        //mEtPhone.setSelection(savePhone.length());//光标移到最后
        //若保存的有手机号，则光标直接定位到密码框
        if (!TextUtils.isEmpty(savePhone)){
            mEtPassword.requestFocus();
        }
    }

    @OnClick({R.id.btn_use_msg, R.id.btn_use_pwd, R.id.btn_login, R.id.btn_code, R.id.btn_regist})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_use_msg:
                setMsgViewVisible(true);
                break;
            case R.id.btn_use_pwd:
                setMsgViewVisible(false);
                break;
            case R.id.btn_code:
                getVerifyCode();
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_regist:
                jump(RegistActivity.class);
                break;
        }
    }


    /**
     * 是否隐藏短信登录的相关View
     *
     * @param visible
     */
    private void setMsgViewVisible(boolean visible) {
        if (visible) {
            isUseMsg = true;
            mLayoutCode.setVisibility(View.VISIBLE);
            mLayoutUsePwd.setVisibility(View.VISIBLE);

            mInputLayoutPwd.setVisibility(View.GONE);
            mLayoutUseMsg.setVisibility(View.GONE);
            mEtPassword.setText("");
        } else {
            isUseMsg = false;
            mLayoutCode.setVisibility(View.GONE);
            mLayoutUsePwd.setVisibility(View.GONE);
            mEtCode.setText("");

            mInputLayoutPwd.setVisibility(View.VISIBLE);
            mLayoutUseMsg.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 登录过程中的进度对话框
     *
     * @param visible
     */
    private void setProgressDialogVisible(boolean visible) {
        if (visible) {
            if (pd == null) {
                pd = new ProgressDialog(mContext);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setMessage("正在登录...");
                //pd.setCancelable(false); //设置进度对话框不能用回退按钮关闭
                pd.setCanceledOnTouchOutside(false);
            }
            pd.show();
        } else {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
        }
    }


    /**
     * 改变获取短信验证码Button上的文字
     */
    private void changeText() {
        mRunnable = new Runnable() {
            private int i = 60;

            @Override
            public void run() {
                --i;
                if (i > 0) {
                    mBtnCode.setText("重新发送（" + i + "）");
                    mHandler.postDelayed(this, 1000);
                } else {
                    mBtnCode.setText("重新发送");
                    mBtnCode.setEnabled(true);
                    mHandler.removeCallbacks(this);
                }
            }
        };
        mHandler.post(mRunnable);
    }



    //======================服务器相关=========================

    /**
     * 登录
     */
    private void login() {

        //关闭软键盘
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }


        final String phone = mEtPhone.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();
        String code = mEtCode.getText().toString().trim();



        //表单校验
        if (phone.isEmpty() || phone.length() != 11) {
            ToastUtils.showToast(R.string.phone_error);
            return;
        }
        if (!isUseMsg && password.isEmpty()) {
            ToastUtils.showToast(R.string.password_not_empty);
            return;
        }
        if (isUseMsg && code.isEmpty()) {
            ToastUtils.showToast(R.string.verify_not_empty);
            return;
        }

        if (!TextUtils.isEmpty(password)){ // 说明是密码登录。 当验证码登录的时候, 密码为空字符串,不需要md5加密
            password = MD5Utils.getMD5_32(password);// 空字符串也会被md5加密
        }

        setProgressDialogVisible(true);

        // 登录（包括密码和验证码登录）
        UserApi.login(TAG, phone, password, code, null,
                new Response.Listener<LoginBean>() {
                    @Override
                    public void onResponse(LoginBean response) {
                        setProgressDialogVisible(false);
                        KLog.json(new Gson().toJson(response));

                        if (response.getStatus() != 1) {
                            ToastUtils.showToast(getApplicationContext(), response.getMsg());
                            return;
                        }

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

                        // 结束当前Activity 并 跳转
                        AppManager.getInstance().finishAllActivity();
                        jump(MainActivity.class);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setProgressDialogVisible(false);
                        ToastUtils.showToast(R.string.no_respnose);
                    }
                });
    }

    /**
     * 获取验证码
     */
    private void getVerifyCode() {
        String phone = mEtPhone.getText().toString().trim();

        if (phone.length() != 11) {
            Toast.makeText(this, R.string.phone_error, Toast.LENGTH_SHORT).show();
            return;
        }

        changeText(); // 改变Btn上的文字
        mBtnCode.setEnabled(false);

        UserApi.getVerifyCode(TAG, phone, 2, //  1:注册， 2：登录
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        BaseBean bean = GsonUtil.getGson().fromJson(response, BaseBean.class);
                        ToastUtils.showToast(bean.getMsg());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.showToast(R.string.no_respnose);
                    }
                });
    }

}
