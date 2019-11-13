package com.samlu.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.engine.ProcessInfoProvider;
import com.samlu.mobilesafe.receiver.MyAppWidgetProvider;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sam lu on 2019/11/13.
 */
public class UpdateWidgetService extends Service{

    private Timer mTimer;
    private InnerReceiver mInnerReceiver;

    @Override
    public void onCreate() {
        //管理进程总数和可用内存的更新
        startTimer();

        //锁屏时不用5s更新一次进程数和可用内存数
        //解锁时重新运行startTimer()方法
        //注册解锁、锁屏的广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        mInnerReceiver = new InnerReceiver();
        registerReceiver(mInnerReceiver,filter);
        super.onCreate();
    }

    private class InnerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                //开启定时更新任务
                startTimer();
            }else {
                //关闭定时更新任务
                cancelTimerTask();
            }
        }
    }

    private void cancelTimerTask() {
        //取消定时任务
        if (mTimer != null){
            mTimer.cancel();
            mTimer = null ;
        }
    }

    private void startTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //UI定时刷新
                updateAppWidget();
            }
        }, 0, 5000);
    }

    private void updateAppWidget() {
        //获取widget对象
        AppWidgetManager aWM = AppWidgetManager.getInstance(this);
        //获取小工具布局转换成的view对象
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.example_appwidget);
        //给view对象里的控件赋值
        remoteViews.setTextViewText(R.id.tv_process_count,"进程总数："+ ProcessInfoProvider.getProcssCount(this));
        String strAvaiSpace = Formatter.formatFileSize(this, ProcessInfoProvider.getAvailableSpace(this));
        remoteViews.setTextViewText(R.id.tv_process_memory,"可用内存："+strAvaiSpace);

        //点击小工具进入应用
        Intent intent = new Intent("android.intent.action.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        //跳转到一个activity
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_root,pendingIntent);

        //通过pendingIntent发送广播，在广播接收者中杀死进程
        Intent broadcastIntent = new Intent("android.intent.action.KILL_BACKGROUND_PROCESS");
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_clear,broadcast);

        ComponentName componentName = new ComponentName(this, MyAppWidgetProvider.class);
        //更新小工具
        aWM.updateAppWidget(componentName,remoteViews);
    }

    @Override
    public void onDestroy() {
        if (mInnerReceiver != null) {
            unregisterReceiver(mInnerReceiver);
        }
        cancelTimerTask();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
