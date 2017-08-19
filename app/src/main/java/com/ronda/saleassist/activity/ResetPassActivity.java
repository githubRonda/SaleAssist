package com.ronda.saleassist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.ronda.saleassist.R;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.api.volley.VolleyUtil;
import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.base.SPHelper;
import com.ronda.saleassist.local.preference.SPUtils;
import com.ronda.saleassist.utils.MD5Utils;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 修改密码
 */
public class ResetPassActivity extends BaseActivty implements View.OnClickListener {


    private EditText etOriginPass;
    private EditText etNewPass;
    private EditText etConfirmPass;

    private Button btnGetCode; //获取临时密码
    private Button btnCommit; //提交


    private String token = SPUtils.getString(AppConst.TOKEN, "");
    private String shopId = SPUtils.getString(AppConst.CUR_SHOP_ID, "");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        initToolbar("修改密码", true);

        initView();
    }

    private void initView() {
        etOriginPass = (EditText) findViewById(R.id.et_origin_pass);
        etNewPass = (EditText) findViewById(R.id.et_new_pass);
        etConfirmPass = (EditText) findViewById(R.id.et_confirm_pass);
        btnGetCode = (Button) findViewById(R.id.btn_get_code);
        btnCommit = (Button) findViewById(R.id.btn_commit);

        btnGetCode.setOnClickListener(this);
        btnCommit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_get_code:
                resetPass("","");//当不传入新密码时，系统将以手机短信的形式通知用户新的随机密码
                break;
            case R.id.btn_commit:
                //表单校验
                String origin_pass = etOriginPass.getText().toString().trim();
                String new_pass = etNewPass.getText().toString().trim();
                String confirm_pass = etConfirmPass.getText().toString().trim();
                
                if (origin_pass.isEmpty()){
                    etOriginPass.setError("原始密码不能为空");
                    return;
                }
                if (new_pass.isEmpty()){
                    etNewPass.setError("新密码不能为空");
                    return;
                }
                if (!new_pass.equals(confirm_pass)){
                    etNewPass.setError("两次密码输入不一致");
                    etConfirmPass.setError("两次密码输入不一致");
                    return ;
                }
                resetPass(MD5Utils.getMD5_32(origin_pass), MD5Utils.getMD5_32(new_pass));
                break;
        }
    }

    private void resetPass(String password, String newpassword){

        UserApi.resetPassword(TAG, token, shopId, password, newpassword,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        KLog.json(responseStr);

                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");

                            Toast.makeText(ResetPassActivity.this, msg, Toast.LENGTH_SHORT).show();

                            if (status == 1){
                                Intent intent = new Intent(ResetPassActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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
