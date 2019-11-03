package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.TextView;

import com.samlu.mobilesafe.R;

/**
 * Created by sam lu on 2019/11/3.
 */
public class AToolActivity extends Activity{

    private TextView tv_query_phone_address;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_atool);
        
        //电话归属地查询的方法
        initPhoneAddress();
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