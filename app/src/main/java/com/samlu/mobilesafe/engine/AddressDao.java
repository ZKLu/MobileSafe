package com.samlu.mobilesafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by sam lu on 2019/11/3.
 */

public class AddressDao  {
    private static final String TAG = "AddressDao";
    //指定访问数据库的路径
    public static String path ="data/data/com.samlue.mobilesafe/files/address.db";
    private static String mAddress ="未知号码";

    /**传递一个电话号码，开启数据库链接，进行访问，返回一个归属地
    *@param phone 查询电话号码
    *@return
    */
    public static String getAddress(String phone){
        mAddress = "未知号码";
        //正则表达式(^1[3-8]\d{9})判断是否满足电话号码
        String regularExpression = "^1[3-8]\\d{9}";
        //开启数据库连接
        SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        if (phone.matches(regularExpression)) {
            String sub_phone = phone.substring(0, 7);
            //数据库查询
            Cursor cursor = database.query("data1", new String[]{"outkey"}, "id = ?", new String[]{sub_phone}, null, null, null);
            if (cursor.moveToNext()) {
                String outkey = cursor.getString(0);
                //通过data1找到的outkey，作为外键在data2找
                Cursor indexCursor = database.query("data2", new String[]{"location"}, "id =?", new String[]{outkey}, null, null, null);
                if (indexCursor.moveToNext()) {
                    //获取查询到的电话归属地
                    mAddress = indexCursor.getString(0);
                    Log.i(TAG, mAddress);
                }
            }else{
                Log.i(TAG,"不符合正则表达式");
                mAddress = "未知号码";
            }
        }else {
            int length = phone.length();
            switch (length){
                case 3:
                    mAddress = "报警电话";
                    break;
                case 4:
                    mAddress ="模拟器";
                    break;
                case 7:
                    mAddress = "本地电话";
                    break;
                case 8:
                    mAddress = "本地电话";
                    break;
                case 11:
                    //3+8:区号+座机号码
                    String area_3 = phone.substring(1,3);
                    Cursor cursor_3 = database.query("data2", new String[]{"location"}, "area = ?", new String[]{area_3}, null, null, null);
                    if (cursor_3.moveToNext()){
                        mAddress = cursor_3.getString(0);
                    }else{
                        mAddress = "未知号码";
                    }
                    break;
                case 12:
                    //4+8:区号+座机号码
                    String area_4 = phone.substring(1,4);
                    Cursor cursor_4 = database.query("data2", new String[]{"location"}, "area = ?", new String[]{area_4}, null, null, null);
                    if (cursor_4.moveToNext()){
                        mAddress = cursor_4.getString(0);
                    }else{
                        mAddress = "未知号码";
                    }
                    break;
            }
        }
        return mAddress;
    }
}
