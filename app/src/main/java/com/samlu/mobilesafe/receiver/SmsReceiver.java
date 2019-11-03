package com.samlu.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.service.LocationService;
import com.samlu.mobilesafe.utils.ConstantValue;
import com.samlu.mobilesafe.utils.SpUtil;

/**
 * Created by sam lu on 2019/11/3.
 */

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //判断是否开启防盗广播
        boolean open_security = SpUtil.getBoolean(context, ConstantValue.OPEN_SECURITY,false);
        if (open_security){
            //获取短信内容
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            //循环遍历短信数组
            for (Object object :objects){
                //获取短信对象
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
                //获取短信对象的基本信息
                String OriginatingAddress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();
                //判断是否包含播放音乐的关键字
                if (messageBody.contains("#*alarm*#")){
                    //准备音乐
                    MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                    player.setLooping(true);
                    player.start();
                }
                if (messageBody.contains("#*location*#")) {
                    //开启获取定位服务
                    context.startService(new Intent(context, LocationService.class)) ;
                }
            }
        }
    }
}
