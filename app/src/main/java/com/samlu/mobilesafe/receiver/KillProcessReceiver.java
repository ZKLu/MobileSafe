package com.samlu.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.samlu.mobilesafe.engine.ProcessInfoProvider;

/**
 * Created by sam lu on 2019/11/13.
 */
public class KillProcessReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //杀死进程
        ProcessInfoProvider.killAllProcess(context);
    }
}
