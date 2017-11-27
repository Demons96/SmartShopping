package com.demon.smartshopping.MyClass;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by gyp19 on 17-4-7.
 * 商品信息数据：commodity
 * 商品清单数据：inventory
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int VERSION = 2;    //数据库版本

    //商品信息数据
    public static final String CREATE_COMMODITY = "create table commodity ("
            + "bar_code text primary key,"   //二维码
            + "name text,"                   //名
            + "price text,"                  //价格
            + "produced_date text,"          //生产日期
            + "expiration_date text,"     //保质期
            + "weight text)";             //重量

    //商品清单数据
    public static final String CREATE_INVENTORY = "create table inventory ("
            + "bar_code test primary key,"
            + "name text,"
            + "price text)";

    private Context mContext;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_COMMODITY);
        db.execSQL(CREATE_INVENTORY);
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists commodity");
        db.execSQL("drop table if exists inventory");
        Toast.makeText(mContext, "drop table succeeded", Toast.LENGTH_SHORT).show();
        onCreate(db);
    }
}
