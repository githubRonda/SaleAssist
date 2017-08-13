package com.ronda.saleassist.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;

import com.ronda.saleassist.api.UserApi;
import com.ronda.saleassist.base.MyApplication;
import com.ronda.saleassist.base.SPHelper;
import com.ronda.saleassist.db.OrderBean;
import com.ronda.saleassist.db.dao.OrderDao;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2016/11/24
 * Version: v1.0
 */

public class UploadOrderService extends Service {

    private Thread mThread;
    private OrderDao mOrderDao;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mThread = new Thread(new MyRunnable());
        mOrderDao = new OrderDao(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mThread.isAlive()) {
            mThread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThread.interrupt();
    }

    class MyRunnable implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(1000); // 启动延迟，必须要设置启动延迟，否则当sqlite数据库中的订单数据不为空时，第一次进入程序，页面中的数据是加载不出来的，不知道为什么

                while (!Thread.currentThread().isInterrupted()) {
                    OrderBean bean = mOrderDao.queryFirst();

                    Log.i("TAG_ORDER", null == bean ? null + "" : bean.toString());

                    if (bean != null) {

                        if ("-1".equals(bean.shopid)) {//当shopid是-1时表示是买家端下的新订单，只需要打印，不需要上传
                            /*if (SPHelper.getSettingPrintNewOrder() && mBluetooth.isConnected()) { //SPHelper.getSettingPrintNewOrder()
                                mBluetooth.startPrintBill(bean.printhexdata);
                            }*/
                            mOrderDao.delete(bean);
                        } else {
                            uploadOrder_cash(bean);
                        }

                    } else {
                        stopSelf(); //停止订单上传的服务
                    }
                    Thread.sleep(2000); // 间隔周期
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    //现金结算时的订单上传
    private void uploadOrder_cash(final OrderBean orderBean) {

        //访问后台，上传订单
        /*UserApi.uploadOrder(orderBean.shopid, orderBean.data, orderBean.total, orderBean.paycode, orderBean.method, orderBean.customer, new UserApi.VolleyStrHandler() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MyApplication.getInstance(), "订单上传失败（支付失败）", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String responseStr) {
                KLog.json(responseStr);
                try {
                    JSONObject response = new JSONObject(responseStr);
                    int status = response.getInt("status");
                    String msg = response.getString("msg");
                    if (status == -10) { //提示店铺不存在。当前账号在别处登录
                        //mOrderDao.delete(orderBean);
                        stopSelf();//停止订单上传的服务
                    } else {
                        Toast.makeText(MyApplication.getInstance(), msg, Toast.LENGTH_SHORT).show();
                    }

                    if (status > 0) {
                        if (SPHelper.getSettingPrintBill() && mBluetooth.isConnected()) {
                            mBluetooth.startPrintBill(orderBean.printhexdata);
                        }
                        mOrderDao.delete(orderBean);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MyApplication.getInstance(), "数据解析有误", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }
}
