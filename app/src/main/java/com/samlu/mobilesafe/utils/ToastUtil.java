package com.samlu.mobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by sam lu on 2019/10/26.
 */

public class ToastUtil {
    /**
    *@param: ctx - 上下文环境
    *@param: msg - 文本
    */
    public static void show(Context ctx,String msg){
        Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show();
    }
}
