package com.samlu.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.samlu.mobilesafe.utils.ConstantValue;
import com.samlu.mobilesafe.utils.SpUtil;

/**
 * Created by sam lu on 2019/11/2.
 */

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String serialNumber = manager.getSimSerialNumber();
        String sim_number = SpUtil.getString(context, ConstantValue.SIM_NUMBER,"");
        if (!sim_number.equals(serialNumber)){
            //4、发送短信给安全号码
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage("5556",null,"sim change!!!",null,null);
        }

    }
}
