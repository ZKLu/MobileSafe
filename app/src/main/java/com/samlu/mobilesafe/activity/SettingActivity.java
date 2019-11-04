package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.service.AddressService;
import com.samlu.mobilesafe.utils.ConstantValue;
import com.samlu.mobilesafe.utils.ServiceUtil;
import com.samlu.mobilesafe.utils.SpUtil;
import com.samlu.mobilesafe.view.SettingClickView;
import com.samlu.mobilesafe.view.SettingItemView;

/**
 * Created by sam lu on 2019/10/29.
 */
public class SettingActivity extends Activity{

    private String[] mToastStyleDes;
    private int mToastStyle;
    private SettingClickView scv_toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initUpdate();
        initAddress();
        initToastStyle();
    }

    private void initToastStyle() {
        scv_toast = findViewById(R.id.scv_toast_style);
        scv_toast.setTitle("设置归属地显示风格");
        //创建描述文字所在的String类型数组
        mToastStyleDes = new String[]{"透明","橙色","蓝色","灰色","绿色"};
        mToastStyle = SpUtil.getInt(this, ConstantValue.TOAST_STYLE,0);
        scv_toast.setDes(mToastStyleDes[mToastStyle]);
        scv_toast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToastStyleDialog();
            }
        });
    }

    /**
     * 创建选择Toast样式的对话框
    *@param
    *@return
    */
    private void showToastStyleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择归属地样式");
        //选择单个条目时间监听
        builder.setSingleChoiceItems(mToastStyleDes, mToastStyle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {//选中的索引值
                SpUtil.putInt(getApplicationContext(), ConstantValue.TOAST_STYLE,which);
                dialog.dismiss();
                scv_toast.setDes(mToastStyleDes[which]);
            }
        });
        //消极按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
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
