package com.samlu.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.samlu.mobilesafe.db.AppLockOpenHelper;
import com.samlu.mobilesafe.db.BlackNumberOpenHelper;
import com.samlu.mobilesafe.db.domin.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sam lu on 2019/11/5.
 */

public class AppLockDao {

    private final AppLockOpenHelper appLockOpenHelper;
    private final Context context;


    //BlackNumberDao单例模式
    //1、私有化构造方法
    private AppLockDao(Context context){
        this.context = context;
        //创建数据库以及表
        appLockOpenHelper = new AppLockOpenHelper(context);

    }
    //2、声明一个私有的静态当前类对象
    private static AppLockDao appLockDao =null;
    //3、提供一个静态方法，如果当前类为空，创建一个空的
    public static AppLockDao getInstance(Context context){
        if (appLockDao == null){
            appLockDao = new AppLockDao(context);
        }
        return appLockDao;
    }

    public void insert(String packageName){
        SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("package",packageName);
        db.insert("applock",null,contentValues);
        context.getContentResolver().notifyChange(Uri.parse("content://applock/change"),null);
        db.close();
    }
    public void delete(String packageName){
        SQLiteDatabase db =appLockOpenHelper.getWritableDatabase();
        db.delete("applock","package =?",new String[]{packageName});
        db.close();
        context.getContentResolver().notifyChange(Uri.parse("content://applock/change"),null);
    }

    public List<String> findAll(){
        SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("applock", new String[]{"package"}, null, null, null, null, null);
        List<String> packageList = new ArrayList<>();
        while (cursor.moveToNext()){
            packageList.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return packageList;
    }
}
