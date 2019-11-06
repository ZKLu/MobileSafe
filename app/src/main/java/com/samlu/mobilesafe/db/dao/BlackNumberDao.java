package com.samlu.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.samlu.mobilesafe.db.BlackNumberOpenHelper;
import com.samlu.mobilesafe.db.domin.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by sam lu on 2019/11/5.
 */

public class BlackNumberDao {

    private final BlackNumberOpenHelper blackNumberOpenHelper;
    private int count;

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

    /**每次返回index后的20条数据
    *@param
    *@return 
    */
    public List<BlackNumberInfo> find(int index){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        Cursor cursor= db.rawQuery("select phone,mode from blacknumber order by _id desc limit ?,20",
                new String[]{index+""});
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

    /**
    *@param
    *@return 返回数据库中条目的总数
    */
    public int getCount(){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        Cursor cursor= db.rawQuery("select count(*) from blacknumber;",null);
        int count = 0;
        while (cursor.moveToNext()){
            count =  cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    /**根据电话号码，查找拦截模式
    *@param phone 要查询拦截模式的电话号码
    *@return 拦截模式 1：短信 2：电话 3：所有 4：没有电话
    */
    public int getMode(String phone){
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select mode from blacknumber where phone=;",new String[]{phone});
        int mode = 0;
        while (cursor.moveToNext()){
            mode = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return mode;
    }
}
