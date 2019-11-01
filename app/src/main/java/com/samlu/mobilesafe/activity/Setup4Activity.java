package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.utils.ConstantValue;
import com.samlu.mobilesafe.utils.SpUtil;

/**
 * Created by sam lu on 2019/11/1.
 */
public class Setup4Activity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
    }

    public void nextPage(View view){
        Intent intent = new Intent(getApplicationContext() ,SetupOverActivity.class);
        SpUtil.putBoolean(this, ConstantValue.SETUP_OVER,true);
        startActivity(intent);
        finish();
    }
    public  void prePage(View view){
        Intent intent = new Intent(getApplicationContext() ,Setup3Activity.class);
        startActivity(intent);
    }
}
