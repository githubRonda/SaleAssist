package com.ronda.saleassist.activity.setting;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.ronda.saleassist.R;
import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.base.BaseActivty;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

public class UserSuggestionActivity extends BaseActivty implements View.OnClickListener {


    private EditText etUser;
    private EditText etMsg;

    private Button btnCommit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_suggestion);

        initToolbar("用户反馈", true);

        initView();
    }

    private void initView() {
        etUser = (EditText) findViewById(R.id.et_user);
        etMsg = (EditText) findViewById(R.id.et_msg);
        btnCommit = (Button) findViewById(R.id.btn_commit);

        btnCommit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_commit) {

            String user = etUser.getText().toString().trim();
            String msg = etMsg.getText().toString().trim();

            uploadSugesstion(user, msg);

        }
    }

    private void uploadSugesstion(String user, String msg) {
        System.out.println("user:" + user + ", msg:" + msg);

        if (TextUtils.isEmpty(msg)){
            ToastUtils.showToast("内容不能为空:)");
            return;
        }

        UserApi.uploadSuggestion(TAG, user, "app:" + getVersion(), msg,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseStr) {
                        KLog.json(responseStr);
                        try {
                            JSONObject response = new JSONObject(responseStr);
                            int status = response.getInt("status");
                            String msg = response.getString("msg");

                            Toast.makeText(UserSuggestionActivity.this, msg, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ToastUtils.showToast(R.string.no_respnose);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtils.showToast(R.string.no_respnose);
                    }
                });
    }

    // 获取当前版本的版本号
    private String getVersion() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "版本号未知";
        }
    }
}
