package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.engine.SmsBackUP;

import java.io.File;

/**
 * Created by sam lu on 2019/11/3.
 */
public class AToolActivity extends Activity{

    private static final String TAG = "AToolActivity";
    private TextView tv_query_phone_address;
    private TextView tv_sms_backup;
    private TextView tv_common_number_query;
    private TextView tv_app_lock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool);
        Log.i(TAG,"进入高级工具");
        //电话归属地查询的方法
        initPhoneAddress();
        //短信备份方法
        initSmsBackup();
        initCommonNumberQuery();
        initAppLock();
    }

    private void initAppLock() {
        tv_app_lock = findViewById(R.id.tv_app_lock);
        tv_app_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AppLockActivity.class));
            }
        });
    }

    private void initCommonNumberQuery() {
        tv_common_number_query = findViewById(R.id.tv_common_number_query);
        tv_common_number_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CommonNumberQueryActivity.class));
            }
        });
    }

    private void initSmsBackup() {
        tv_sms_backup = findViewById(R.id.tv_sms_backup);
        tv_sms_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSmsBackUpDialog();
            }
        });
    }

    private void showSmsBackUpDialog() {
        //创建带进度条的对话框
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("短信备份");
        //指定进度条样式:水平进度条
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        final ProgressBar pb_bar = findViewById(R.id.pb_bar);
        //场景：由于产品经理不确定是用ProgressBar还是progressDialog，所以使用回调。
        //backup()方法里要用到这两个控件的地方是设置进度条最大值和进度的更新，所以把这两个地方抽出来，放在callBack接口里
        //等确定了要用哪个控件，就实现这两个方法
        new Thread(){
            @Override
            public void run() {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"sms.xml";
                //SmsBackUP.backup(getApplicationContext(),path,progressDialog);
                //progressDialog.dismiss();
                SmsBackUP.backup(getApplicationContext(), path, new SmsBackUP.callBack() {
                    @Override
                    public void setMax(int max) {
                        //A.确定使用ProgressBar了
                        pb_bar.setMax(max);
                        //B.确定使用ProgressDialog了
                        progressDialog.setMax(max);
                    }
                    @Override
                    public void setProgress(int progress) {
                        //A.确定使用ProgressBar了
                        pb_bar.setProgress(progress);
                        //B.确定使用ProgressDialog了
                        progressDialog.setProgress(progress);
                    }
                });
                progressDialog.dismiss();
            }
        }.start();

    }

    private void initPhoneAddress() {
        tv_query_phone_address = findViewById(R.id.tv_query_phone_address);
        tv_query_phone_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),QueryAddressActivity.class));
            }
        });
    }
}
