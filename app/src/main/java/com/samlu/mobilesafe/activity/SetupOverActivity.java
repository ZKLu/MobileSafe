package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.utils.ConstantValue;
import com.samlu.mobilesafe.utils.SpUtil;

/**
 * 设置完成界面
 * Created by sam lu on 2019/10/30.
 */
public class SetupOverActivity extends Activity{

    private TextView tv_phone;
    private TextView tv_reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //是否完成导航界面的设置
        boolean setup_over = SpUtil.getBoolean(this, ConstantValue.SETUP_OVER,false);
        if (setup_over){
            //跳转到设置完成功能列表界面
            setContentView(R.layout.activity_setup_over);
            initUI();
        }else{
            //跳转到导航界面的第一页
            Intent intent = new Intent( this,Setup1Activity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initUI() {
        tv_phone = findViewById(R.id.tv_phone);
        String phone = SpUtil.getString(this,ConstantValue.CONTACT_PHONE,"");
        tv_phone.setText(phone);

        tv_reset = findViewById(R.id.tv_reset);
        tv_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Setup1Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
