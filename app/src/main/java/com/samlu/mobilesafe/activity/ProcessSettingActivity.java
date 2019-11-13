package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.service.LockScreenService;
import com.samlu.mobilesafe.utils.ConstantValue;
import com.samlu.mobilesafe.utils.ServiceUtil;
import com.samlu.mobilesafe.utils.SpUtil;

/**
 * Created by sam lu on 2019/11/13.
 */
public class ProcessSettingActivity extends Activity{

    private CheckBox cb_cb_show_system_process;
    private CheckBox cb_lock_clean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);
        initSystemProcessShow();
        initLockScreenClean();
    }

    /**锁屏清理
    *@param
    *@return
    */
    private void initLockScreenClean() {
        cb_lock_clean = findViewById(R.id.cb_lock_clean);
        //根据服务是否开启，决定checkbox是否选中
        boolean isRunning = ServiceUtil.isRunning(this, "com.samlu.mobilesafe.service.LockScreenService");
        if (isRunning){
            cb_lock_clean.setText("锁屏清理已开启");
        }else {
            cb_lock_clean.setText("锁屏清理已关闭");
        }
        cb_lock_clean.setChecked(isRunning);
        //对选中状态进行监听
        cb_lock_clean.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cb_lock_clean.setText("锁屏清理已开启");
                    startService(new Intent(getApplicationContext(),LockScreenService.class));
                }else {
                    cb_lock_clean.setText("锁屏清理已关闭");
                    stopService(new Intent(getApplicationContext(),LockScreenService.class));
                }
            }
        });
    }

    private void initSystemProcessShow() {
        cb_cb_show_system_process = findViewById(R.id.cb_show_system_process);
        boolean show_system_process = SpUtil.getBoolean(this,ConstantValue.SHOW_SYSTEM,false);
        if (show_system_process){
            cb_cb_show_system_process.setText("显示系统进程");
            cb_cb_show_system_process.setChecked(true);
        }else {
            cb_cb_show_system_process.setText("隐藏系统进程");
            cb_cb_show_system_process.setChecked(false);
        }
        //对选中状态进行监听
        cb_cb_show_system_process.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cb_cb_show_system_process.setText("显示系统进程");
                }else {
                    cb_cb_show_system_process.setText("隐藏系统进程");
                }
                SpUtil.putBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM,isChecked);
            }
        });
    }
}
