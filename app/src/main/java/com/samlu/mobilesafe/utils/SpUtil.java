package com.samlu.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by sam lu on 2019/10/29.
 */

public class SpUtil {

    private static SharedPreferences sharedPreferences;

    //写
    /**
     * 写入布尔类型的变量到sharepreference
    *@param context 上下文环境
     * @param key  存储节点名称
     * @param value  存储节点的值
    *@return:
    */
    public static  void putBoolean(Context context,String key,boolean value){
        //getSharedPreferences(存储节点文件名称,操作模式)
        if (sharedPreferences ==null) {
            sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putBoolean(key,value).commit();
    }
    //读
    /**
     *从sharepreference读取布尔类型的值
    *@param context 上下文环境
     * @param key  存储节点名称
     * @param defValue  没有此节点默认值
    *@return 默认值或此节点读取到的结果
    */
    public static boolean getBoolean(Context context,String key,boolean defValue){
        if (sharedPreferences ==null){
            sharedPreferences = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        return sharedPreferences.getBoolean(key,defValue);
    }

    //写
    /**
     * 写入String类型的变量到sharepreference
     *@param context 上下文环境
     * @param key  存储节点名称
     * @param value  存储节点的值
     *@return:
     */
    public static  void putString(Context context,String key,String value){
        //getSharedPreferences(存储节点文件名称,操作模式)
        if (sharedPreferences ==null) {
            sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putString(key,value).commit();
    }
    //读
    /**
     *从sharepreference读取String类型的值
     *@param context 上下文环境
     * @param key  存储节点名称
     * @param defValue  没有此节点默认值
     *@return 默认值或此节点读取到的结果
     */
    public static String getString(Context context,String key,String defValue){
        if (sharedPreferences ==null){
            sharedPreferences = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        return sharedPreferences.getString(key,defValue);
    }

    /**获取Toast的样式颜色的索引值
    *@param
    *@return
    */
    public static int getInt(Context ctx,String key,int defValue){
        if (sharedPreferences == null){
            sharedPreferences = ctx.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        return sharedPreferences.getInt(key,defValue);
    }

    /**写入Toast的样式颜色的索引值
    *@param
    *@return
    */
    public static void putInt(Context ctx, String key, int value) {
        if (sharedPreferences == null){
            sharedPreferences = ctx.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putInt(key, value).commit();
    }
    /**
     * 从sharepreference中移除节点
    *@param context 上下文环境
    *@param key 需要移除的节点
    */
    public static void remove(Context context, String key) {
        if (sharedPreferences ==null) {
            sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().remove(key).commit();
    }


}
