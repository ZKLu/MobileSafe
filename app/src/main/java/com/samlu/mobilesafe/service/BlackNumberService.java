package com.samlu.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.samlu.mobilesafe.db.dao.BlackNumberDao;

/**
 * Created by sam lu on 2019/11/6.
 */
public class BlackNumberService extends Service{

    private InnerSmsReceiver mInnerSmsReceiver;
    private BlackNumberDao mDao;
    private TelephonyManager mTM;
    private MyPhoneStateListener mPhoneStateListener;

    @Override
    public void onCreate() {
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

                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
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

                mDao = BlackNumberDao.getInstance(context);
                int mode = mDao.getMode(OriginatingAddress);
                if (mode ==1 || mode ==3){
                    //拦截短信
                    abortBroadcast();
                }
            }
        }
    }
}
