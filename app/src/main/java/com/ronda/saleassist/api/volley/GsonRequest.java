package com.ronda.saleassist.api.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

/**
 * Created by ronda on 17-8-1/01.
 * <p>
 * 自定义的GsonRequest, 把请求返回的数据直接解析成JavaBean对象
 * 参考StringRequest源码实现
 */

public class GsonRequest<T> extends Request<T> {

    //请求成功时回调（在构造器中初始化）
    private final Response.Listener<T> mListener;
    private Gson     mGson;
    private Class<T> mClazz;

    //两个有参构造器
    public GsonRequest(String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(Method.GET, url, clazz, listener, errorListener);
    }

    public GsonRequest(int method, String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);

        mGson = new Gson();
//        mGson = GsonUtil.getGson(); // 封装后的Gson, 可以把null转换成""
        mClazz = clazz;
        mListener = listener;
    }

    // 对服务器响应的数据进行解析，其中数据是以字节的形式存放在NetworkResponse的data变量中的，
    // 这里将数据取出解析成一个String，然后再使用Gson转成JavaBean对象，最后传入Response的success()方法中
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            return Response.success(mGson.fromJson(jsonString, mClazz), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    //将JavaBean对象进行回调
    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }
}
