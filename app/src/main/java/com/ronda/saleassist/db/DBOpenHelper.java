package com.ronda.saleassist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 刘荣达 on 0020,2016/6/20.
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "User.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "t_order";
    private static final String CREATE_TABLE = "create table if not exists "+TABLE_NAME+" " +
            "(_id integer primary key autoincrement, " +
            "usermobile text, " +
            "shopid text, " +
            "total text, " +
            "paycode text, " +
            "method text, " +
            "customer text, " +
            "data TEXT, " +
            "printhexdata text)";

    public DBOpenHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context, DB_NAME, null, DB_VERSION);
    }

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("drop table if exists "+TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}