package com.ronda.saleassist.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ronda.saleassist.base.AppConst;
import com.ronda.saleassist.db.DBOpenHelper;
import com.ronda.saleassist.db.OrderBean;
import com.ronda.saleassist.local.preference.SPUtils;
import com.socks.library.KLog;

/**
 * 对于暂存的订单的各种数据库的操作
 * <p>
 * Author: Ronda(1575558177@qq.com)
 * Date: 2016/11/24
 * Version: v1.0
 */

public class OrderDao {
    private SQLiteDatabase db;
    private DBOpenHelper mDBOpenHelper;

    public OrderDao(Context context) {
        mDBOpenHelper = new DBOpenHelper(context);

        db = mDBOpenHelper.getWritableDatabase();
    }

    public void close() {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    /******* 以下是对数据库的增删改查  ********/

    // 添加
    public void add(OrderBean orderBean) {
        db.beginTransaction();
        try{
            db.execSQL("INSERT INTO t_order VALUES(null, ?, ?, ?, ?, ?, ?, ?, ?)",
                    new Object[]{orderBean.usermobile, orderBean.shopid, orderBean.total, orderBean.paycode, orderBean.method, orderBean.customer, orderBean.data});
            db.setTransactionSuccessful();
            KLog.i("OrderDao --> add --> setTransactionSuccessful");
        }
        finally {
            db.endTransaction();
            KLog.i("OrderDao --> add --> endTransaction");
        }
    }

    // 获取第一行数据
    public OrderBean queryFirst(){
        Cursor c = db.rawQuery("SELECT * FROM t_order WHERE usermobile = ?", new String[]{SPUtils.getString(AppConst.MOBILE, "")});
        if (c.getCount() == 0){
            c.close();//必须要释放Cursor资源
            return null;
        }
        OrderBean orderBean;
        try{
            c.moveToFirst();
            orderBean = new OrderBean(
                    c.getInt(c.getColumnIndex("_id")),
                    c.getString(c.getColumnIndex("usermobile")),
                    c.getString(c.getColumnIndex("shopid")),
                    c.getString(c.getColumnIndex("total")),
                    c.getString(c.getColumnIndex("paycode")),
                    c.getString(c.getColumnIndex("method")),
                    c.getString(c.getColumnIndex("customer")),
                    c.getString(c.getColumnIndex("data"))
            );
        }
        finally {
            c.close();
        }
        return orderBean;
    }


    //删除
    public void delete(OrderBean orderBean){
        db.delete("t_order", "_id = ?", new String[]{String.valueOf(orderBean._id)});
    }

}
