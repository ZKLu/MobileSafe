package com.samlu.mobilesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.samlu.mobilesafe.service.UpdateWidgetService;

/**
 * Created by sam lu on 2019/11/13.
 */

public class MyAppWidgetProvider extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        //创建第一个小工具调用的方法
        //开启服务
        context.startService(new Intent(context,UpdateWidgetService.class));
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //创建多一个小工具调用的方法。创建第一个小工具也会调用这个方法
        context.startService(new Intent(context,UpdateWidgetService.class));
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        //当小工具宽高发生改变时调用的方法。创建小工具也会调用这个方法
        context.startService(new Intent(context,UpdateWidgetService.class));
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        //删除小工具时调用的方法

        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        //删除最后一个小工具时调用的方法
        //关闭服务
        context.stopService(new Intent(context,UpdateWidgetService.class));
        super.onDisabled(context);
    }
}
