package com.samlu.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**创建数据库
 * Created by sam lu on 2019/11/5.
 */

public class AppLockOpenHelper extends SQLiteOpenHelper {

    public AppLockOpenHelper(Context context) {
        super(context, "applock.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库表
        db.execSQL("create table applock " +
                "(_id integer primary key autoincrement ,package varchar(50))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
