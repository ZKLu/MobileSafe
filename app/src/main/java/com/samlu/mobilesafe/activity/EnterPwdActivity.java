package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.utils.ToastUtil;

/**
 * Created by sam lu on 2019/11/15.
 */
public class EnterPwdActivity extends Activity{

    private String packagename;
    private TextView tv_app_name;
    private ImageView iv_app_icon;
    private EditText et_pwd;
    private Button bt_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pwd);
        //获取传递过来的包名
        packagename = getIntent().getStringExtra("packagename");
        initUI();
        initData();
    }

    private void initData() {
        //通过包名，获取拦截的应用和图标
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packagename, 0);
            Drawable icon = applicationInfo.loadIcon(pm);
            iv_app_icon.setBackgroundDrawable(icon);
            tv_app_name.setText(applicationInfo.loadLabel(pm).toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = et_pwd.getText().toString();
                if (!TextUtils.isEmpty(pwd)){
                    if (pwd.equals("")){
                        //密码正确，进入应用，告知看门狗不用再监听这个应用
                        Intent intent = new Intent("android.intent.action.SKIP");
                        intent.putExtra("packagename",packagename);
                        sendBroadcast(intent);
                        finish();
                    }else {
                        ToastUtil.show(getApplicationContext(),"密码错误");
                    }
                }else {
                    ToastUtil.show(getApplicationContext(),"请输入密码");
                }
            }
        });
    }

    private void initUI() {
        tv_app_name = findViewById(R.id.tv_app_name);
        iv_app_icon = findViewById(R.id.iv_app_icon);
        
        et_pwd = findViewById(R.id.et_pwd);
        bt_submit = findViewById(R.id.bt_submit);
    }
}
