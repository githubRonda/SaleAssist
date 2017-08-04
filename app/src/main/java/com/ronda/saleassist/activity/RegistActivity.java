package com.ronda.saleassist.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ronda.saleassist.R;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.api.volley.GsonUtil;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.bean.BaseBean;
import com.ronda.saleassist.local.sqlite.table.User;
import com.ronda.saleassist.utils.MD5Utils;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegistActivity extends BaseActivty {
    @BindView(R.id.et_phone)
    EditText mEtPhone;
    @BindView(R.id.et_password)
    EditText mEtPassword;
    @BindView(R.id.et_confirm_password)
    EditText mEtConfirmPassword;
    @BindView(R.id.et_nickname)
    EditText mEtNickname;
    @BindView(R.id.et_code)
    EditText mEtCode;
    @BindView(R.id.btn_code)
    Button mBtnCode;


    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        ButterKnife.bind(this);

        initToolbar("注册", true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
    }

    @OnClick({R.id.btn_code, R.id.btn_regist})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_code:
                getVerifyCode();
                break;
            case R.id.btn_regist:
                doRegist();
                break;
        }
    }


    /**
     * 改变获取短信验证码上的文字
     */
    private void changeText() {
        mRunnable = new Runnable() {
            private int i = 15;

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

        UserApi.getVerifyCode(TAG, phone, 1, // 1:注册， 2：登录
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


    /**
     * 注册
     */
    private void doRegist() {

        //关闭软键盘
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        //校验
        String phone = mEtPhone.getText().toString().trim();
        String password = mEtPassword.getText().toString().trim();
        String confirmPassword = mEtConfirmPassword.getText().toString().trim();
        String nickname = mEtNickname.getText().toString().trim();
        String code = mEtCode.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, R.string.phone_not_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.password_not_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, R.string.confirm_pwd_not_same, Toast.LENGTH_SHORT).show();
            return;
        }

        //把密码进行md5加密
        password = MD5Utils.getMD5_32(password);

        UserApi.regist(TAG, phone, password, nickname, code,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        BaseBean bean = GsonUtil.getGson().fromJson(response, BaseBean.class);

                        ToastUtils.showToast(bean.getMsg());

                        if (bean.getStatus() == 1){
                            jump(LoginActivity.class);
                            finish();
                        }
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
