package com.ronda.saleassist.api.volley;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/03/12
 * Version: v1.0
 */

public class VolleyUtil {

    private static final Object TAG = VolleyUtil.class.getSimpleName();

    private static Context mContext;
    private static  VolleyUtil mVolleyUtil;

    private RequestQueue mRequestQueue;
    private ImageLoader  mImageLoader;

    private VolleyUtil(){}

    public static void init(Context context) {
        mContext = context;
    }

    public static VolleyUtil getInstance(){
        if (mVolleyUtil == null){
            synchronized (VolleyUtil.class){
                if (mVolleyUtil == null){
                    mVolleyUtil = new VolleyUtil();
                }
            }
        }
        return mVolleyUtil;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue, new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
