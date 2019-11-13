package com.samlu.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.samlu.mobilesafe.engine.ProcessInfoProvider;

/**
 * Created by sam lu on 2019/11/13.
 */
public class LockScreenService extends Service{

    private IntentFilter filter;
    private InnerReceiver receiver;

    @Override
    public void onCreate() {
        filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        receiver = new InnerReceiver();
        registerReceiver(receiver,filter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (receiver !=null){
            unregisterReceiver(receiver);
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class InnerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //清理手机正在运行的进程
            ProcessInfoProvider.killAllProcess(context);
        }
    }
}
