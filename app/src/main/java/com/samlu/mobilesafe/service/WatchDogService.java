package com.samlu.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.samlu.mobilesafe.activity.EnterPwdActivity;
import com.samlu.mobilesafe.db.dao.AppLockDao;

import java.security.KeyStore;
import java.util.List;

/**
 * Created by sam lu on 2019/11/15.
 */
public class WatchDogService extends Service{
    private boolean isWatch;
    private AppLockDao mDao;
    private List<String> mPackagenameList;
    private InnerReceiver mReceiver;
    private String mSkipPackageName;
    private MyContentObserver mContentObserver;

    @Override
    public void onCreate() {
        mDao = AppLockDao.getInstance(this);
        //看门狗的死循环，让其时刻检测现在开启的应用，是否为程序中要去拦截的应用
        isWatch = true;
        watch();

        //注册广播接收器，解锁完成发送广播，不在监测这个应用
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SKIP");
        mReceiver = new InnerReceiver();
        registerReceiver(mReceiver,filter);

        //注册内容观察者，观察数据库变化，若数据库有插入或删除，则需要让集合重新获取一次数据
        mContentObserver = new MyContentObserver(new Handler());
        getContentResolver().registerContentObserver(Uri.parse("content://applock/change"),
                true,mContentObserver);
        super.onCreate();
    }

    private void watch() {
        //在子线程中，开启一个可控的死循环
        new Thread(){
            @Override
            public void run() {
                mPackagenameList = mDao.findAll();
                while (isWatch){
                    //监测正在开启的应用，使用任务栈
                    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    //获取正在开启应用的任务栈
                    List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
                    //获取最新挂到后台的一个任务栈
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                    //获取栈顶的activity,获取此activity所在应用的包名
                    String packageName = runningTaskInfo.topActivity.getPackageName();
                    //拿包名与已加锁的包名集合做比较
                    if (mPackagenameList.contains(packageName)){
                        if (!packageName.equals(mSkipPackageName)){
                            //弹出拦截界面
                            Intent intent = new Intent(getApplicationContext(),EnterPwdActivity.class);
                            //因为在服务中开启一个新activity,可能这个activity没有任务栈存放，所有使用下面的代码，创建一个任务栈
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("packagename",packageName);
                            startActivity(intent);
                        }
                    }
                    //睡眠一下,避免子线程长期占用CPU
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

        class InnerReceiver extends BroadcastReceiver{

            @Override
            public void onReceive(Context context, Intent intent) {
                //获取广播传递的包名，略过此包名的监测
                mSkipPackageName = intent.getStringExtra("packagename");
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }


    class MyContentObserver extends ContentObserver{

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        /**数据库发生改变调用的方法，重新获取包名所在集合的数据
        *@param
        *@return
        */
        @Override
        public void onChange(boolean selfChange) {
            new Thread(){
                @Override
                public void run() {
                    mPackagenameList = mDao.findAll();
                }
            }.start();
            super.onChange(selfChange);
        }
    }
}
