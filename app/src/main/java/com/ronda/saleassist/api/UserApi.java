package com.ronda.saleassist.api;

import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.ronda.saleassist.api.volley.GsonRequest;
import com.ronda.saleassist.api.volley.VolleyUtil;
import com.ronda.saleassist.base.MyApplication;
import com.ronda.saleassist.base.SPHelper;
import com.ronda.saleassist.bean.BaseBean;
import com.ronda.saleassist.bean.LoginBean;
import com.ronda.saleassist.utils.NetUtils;
import com.ronda.saleassist.utils.ToastUtils;
import com.socks.library.KLog;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/03/11
 * Version: v1.0
 */

public class UserApi {

    private static final String OFFICIAL_SERVER = "http://ceshi.edianlai.com";
    public static String BASE_SERVER = OFFICIAL_SERVER;


    private static final String TAG = UserApi.class.getSimpleName();


    //1. 获取短信验证码
    private static final String CODE_URI = "/market/api/code_get";

    //2. 用户注册
    private static final String REGIST_URI = "/market/api/reg";

    //3. 用户登录
    private static final String LOGIN_URI = "/market/api/login";

    //4. 用户登出
    private static final String LOGOUT_URI = "/market/api/logout";


    /**
     * 用户登录（密码验证码登录和token登录）
     * 使用了GsonRequest
     *
     * @param tag           请求标识. 便于撤销的时候使用
     * @param mobile        手机号
     * @param password      密码
     * @param code          短信验证码
     * @param token         登录token
     * @param listener
     * @param errorListener
     */
    public static void login(final String tag, final String mobile, final String password, final String code, final String token, Response.Listener<LoginBean> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        GsonRequest<LoginBean> gsonRequest = new GsonRequest<LoginBean>(Request.Method.POST, OFFICIAL_SERVER + LOGIN_URI, LoginBean.class, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile", mobile); //RequestParams的add和put最主要的区别：add()方法可以给同一个键添加多个值，而put()方法键名若相同的话，后面的会覆盖前面的
                params.put("password", password);
                params.put("code", code);
                params.put("token", token);

                MapRemoveNullUtil.removeNullAndEmptyValue(params);

                KLog.w(tag + "--> " + new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(gsonRequest, tag);

    }


    /**
     * 获取短信验证码
     * 为了方便粘贴复制，就直接使用StringRequest了
     *
     * @param tag
     * @param mobile
     * @param type          1:注册， 2：登录
     * @param listener
     * @param errorListener
     */
    public static void getVerifyCode(final String tag, final String mobile, final int type, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + CODE_URI, listener, errorListener) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile", mobile);
                params.put("type", type + "");

                MapRemoveNullUtil.removeNullAndEmptyValue(params);

                KLog.w(tag + "--> " + new Gson().toJson(params));
                return params;
            }
        };

        VolleyUtil.getInstance().addToRequestQueue(request, tag);
    }



    public static void regist(final String tag, final String mobile, final String password, final String nickname, final String code, Response.Listener<String> listener, Response.ErrorListener errorListener) {

        if (!NetUtils.isConnected(MyApplication.getInstance())) {
            ToastUtils.showToast("无网络连接");
            return;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, OFFICIAL_SERVER + REGIST_URI, listener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile", mobile);
                params.put("password", password);
                params.put("nickname", nickname);
                params.put("code", code);

                KLog.w(tag + "--> " + new Gson().toJson(params));
                return params;
            }
        };
        VolleyUtil.getInstance().addToRequestQueue(strReq, tag);
    }

}
