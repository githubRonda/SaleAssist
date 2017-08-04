package com.ronda.saleassist.local.sqlite;

import android.database.sqlite.SQLiteDatabase;

import com.ronda.saleassist.base.MyApplication;
import com.ronda.saleassist.local.sqlite.table.DaoMaster;
import com.ronda.saleassist.local.sqlite.table.DaoSession;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/03/12
 * Version: v1.0
 */

public class GreenDaoHelper {
    private static DaoMaster.DevOpenHelper mHelper;
    private static SQLiteDatabase          db;
    private static DaoMaster               mDaoMaster;
    private static DaoSession              mDaoSession;

    /**
     * 初始化greenDao，这个操作建议在Application初始化的时候添加；
     */
    public static void initDatabase() {
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(MyApplication.getInstance(), "cache-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public static DaoSession getDaoSession() {
        return mDaoSession;
    }

    public static SQLiteDatabase getDb() {
        return db;
    }
}
