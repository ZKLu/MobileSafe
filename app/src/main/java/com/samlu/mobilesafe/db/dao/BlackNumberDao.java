package com.samlu.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.samlu.mobilesafe.db.BlackNumberOpenHelper;
import com.samlu.mobilesafe.db.domin.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sam lu on 2019/11/5.
 */

public class BlackNumberDao {

    private final BlackNumberOpenHelper blackNumberOpenHelper;

    //BlackNumberDao单例模式
    //1、私有化构造方法
    private BlackNumberDao(Context context){
        //创建数据库以及表
        blackNumberOpenHelper = new BlackNumberOpenHelper(context);
    }
    //2、声明一个私有的静态当前类对象
    private static BlackNumberDao blackNumberDao =null;
    //3、提供一个静态方法，如果当前类为空，创建一个空的
    public static BlackNumberDao getInstance(Context context){
        if (blackNumberDao == null){
            blackNumberDao = new BlackNumberDao(context);
        }
        return blackNumberDao;
    }

    /**
    *@param phone 拦截电话号码
     *            @param mode 拦截类型（1短信 2电话 3以上两种）
    *@return
    */
    public void insert(String phone , String mode){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone",phone);
        values.put("mode",mode);
        db.insert("blacknumber",null,values);
        db.close();
    }

    /**从数据库中删除一条电话号码
    *@param phone 要删除的电话号码
    *@return
    */
    public void delete(String phone){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        db.delete("blacknumber","phone = ?",new String[]{phone});
        db.close();
    }


    public void update(String phone ,String mode){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode",mode);
        db.update("blacknumber",values,"phone = ?",new String[]{phone});
        db.close();
    }

    /**返回数据库中所有的号码和类型
    *@param
    *@return
    */
    public List<BlackNumberInfo> findAll(){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        Cursor cursor= db.query("blacknumber",new String[]{"phone,mode"},null,null,null,null,"_id desc");
        List<BlackNumberInfo> blackNumberList = new ArrayList<>();
        while (cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.phone = cursor.getString(0);
            blackNumberInfo.mode = cursor.getString(1);
            blackNumberList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();

        return blackNumberList;
    }
}
