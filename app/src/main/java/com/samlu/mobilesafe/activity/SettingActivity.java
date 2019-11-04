package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.service.AddressService;
import com.samlu.mobilesafe.utils.ConstantValue;
import com.samlu.mobilesafe.utils.ServiceUtil;
import com.samlu.mobilesafe.utils.SpUtil;
import com.samlu.mobilesafe.view.SettingItemView;

/**
 * Created by sam lu on 2019/10/29.
 */
public class SettingActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initUpdate();
        initAddress();
    }
    /**是否显示电话号码归属地
    *@param
    *@return
    */
    private void initAddress() {
        //不能用sp去记录是否开启，因为当手机内存不够，会杀死服务，这就导致状态是打开，但是服务已经被杀死。
        boolean isRunning = ServiceUtil.isRunning(this, "com.samlu.mobilesafe.service.AddressService");
        final SettingItemView siv_address = findViewById(R.id.siv_address);
        siv_address.setCheck(isRunning);
        siv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回点击前的状态
                boolean isCheck = siv_address.isCheck();
                //点击后的状态就是点击前的状态取反
                siv_address.setCheck(!isCheck);
                if (!isCheck){
                    //开启服务，管理Toast
                    startService(new Intent(getApplicationContext(),AddressService.class));
                }else {
                    //关闭服务，不需要Toast
                    stopService(new Intent(getApplicationContext(),AddressService.class));
                }
            }
        });
    }

    private void initUpdate() {
        //获取已有的开关状态，用作显示
        boolean open_update = SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE,false);
        final SettingItemView siv_update = findViewById(R.id.siv_update);
        //是否选中，根据上一次存储的结果去决定
        siv_update.setCheck(open_update);
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取反操作
                boolean ischeck = siv_update.isCheck();
                siv_update.setCheck(!ischeck);
                //将取反后的状态存储到相应sp中
                SpUtil.putBoolean(getApplicationContext(),ConstantValue.OPEN_UPDATE,!ischeck);
            }
        });

    }
}
