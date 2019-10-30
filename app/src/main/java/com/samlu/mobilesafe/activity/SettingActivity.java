package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.utils.ConstantValue;
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
