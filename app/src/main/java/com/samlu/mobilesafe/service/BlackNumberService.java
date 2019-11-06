package com.samlu.mobilesafe.service;

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
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.ITelephony;

import com.samlu.mobilesafe.db.dao.BlackNumberDao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by sam lu on 2019/11/6.
 */
public class BlackNumberService extends Service{

    private InnerSmsReceiver mInnerSmsReceiver;
    private BlackNumberDao mDao ;
    private TelephonyManager mTM;
    private MyPhoneStateListener mPhoneStateListener;
    private MyContentObserver mContentObserver;

    @Override
    public void onCreate() {
        mDao = BlackNumberDao.getInstance(this);

        //拦截短信
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        //设置优先级，比系统应用短信还高，在这里拦截收到短信的广播，短信收不到广播
        intentFilter.setPriority(1000);
        mInnerSmsReceiver = new InnerSmsReceiver();
        registerReceiver(mInnerSmsReceiver,intentFilter);

        //监听电话状态
        //电话管理者对象
        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //监听电话状态
        mPhoneStateListener = new MyPhoneStateListener();
        mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (mInnerSmsReceiver !=null){
            unregisterReceiver(mInnerSmsReceiver);
        }
        if (mContentObserver != null){
            getContentResolver().unregisterContentObserver(mContentObserver);
        }
        if (mPhoneStateListener != null){
            mTM.listen(mPhoneStateListener,PhoneStateListener.LISTEN_NONE);
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class MyPhoneStateListener extends PhoneStateListener{
        //手动重写，电话状态发生改变会触发的方法
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_IDLE:
                    //空闲状态
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机状态
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //响铃状态，识别电话号码，如果跟黑名单的匹配，挂断电话
                    endCall(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private void endCall(String phone) {
        int mode = mDao.getMode(phone);
        if (mode ==2 || mode ==3){
            //拦截电话
            //ServiceManager类对开发者隐藏，不能直接调用，需要反射调用
            //ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELECOM_SERVICE));
            try {
                //步骤1、获取ServiceManager的字节码文件
                Class<?> clazz = Class.forName("android.os.ServiceManager");
                //步骤2、获取方法
                Method method = clazz.getMethod("getService", String.class);
                //步骤3、反射调用此方法
                IBinder iBinder = (IBinder) method.invoke(null, Context.TELECOM_SERVICE);
                //步骤4、获取aidl文件对象方法
                ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
                //步骤5、调用在aidl中隐藏的endCall();
                iTelephony.endCall();
            } catch (Exception e) {
                    e.printStackTrace();
            }

            //删除被拦截电话号码的通信记录
            //通过内容观察者，观察数据库变化
            mContentObserver = new MyContentObserver(new Handler(), phone);
            getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"),
                    true,mContentObserver);
        }
    }

    private class InnerSmsReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取短信内容，获取发送短信的号码，如果号码在黑名单中，且拦截模式也为短信（1），拦截短信
            //获取短信内容
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            //循环遍历短信数组
            for (Object object : objects) {
                //获取短信对象
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
                //获取短信对象的基本信息
                String OriginatingAddress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();

                int mode = mDao.getMode(OriginatingAddress);
                if (mode ==1 || mode ==3){
                    //拦截短信
                    abortBroadcast();
                }
            }
        }
    }

    private class MyContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        private String phone;
        public MyContentObserver(Handler handler,String phone) {
            super(handler);
            this.phone = phone;
        }
        //数据库中指定的表发生改变时会调用的方法
        @Override
        public void onChange(boolean selfChange) {
            //插入一条数据后再进行删除
            getContentResolver().delete(Uri.parse("content://call_log/calls"),
                    "number=?",new String[]{phone});
            super.onChange(selfChange);
        }
    }
}
