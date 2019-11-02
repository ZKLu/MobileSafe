package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.utils.ConstantValue;
import com.samlu.mobilesafe.utils.SpUtil;
import com.samlu.mobilesafe.utils.ToastUtil;

/**
 * Created by sam lu on 2019/11/1.
 */
public class Setup4Activity extends BaseSetupActivity{

    private CheckBox cb_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        initUI();
    }

    private void initUI() {
        cb_box = findViewById(R.id.cb_box);
        //是否选中状态的显示
        boolean open_security = SpUtil.getBoolean(this,ConstantValue.OPEN_SECURITY,false);
        //根据状态修改checkbox后面的文字
        if (open_security){
            cb_box.setChecked(open_security);
            cb_box.setText("安全设置已开启");
        }else {
            cb_box.setChecked(open_security);
            cb_box.setText("安全设置已关闭");
        }
        //点击过程中，checkbox状态的切换
        cb_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //isChecked点击后的状态
                SpUtil.putBoolean(getApplicationContext(),ConstantValue.OPEN_SECURITY,isChecked);
                //根据状态，去修改显示的文字
                if (isChecked){
                    cb_box.setText("安全设置已开启");
                }else {
                    cb_box.setText("安全设置已关闭");
                }
            }
        });
    }

    @Override
    protected void showPrePage() {
        Intent intent = new Intent(getApplicationContext() ,Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }

    @Override
    protected void showNextPage() {
        boolean open_security = SpUtil.getBoolean(this,ConstantValue.OPEN_SECURITY,false);
        if (open_security){
            Intent intent = new Intent(getApplicationContext() ,SetupOverActivity.class);
            SpUtil.putBoolean(this, ConstantValue.SETUP_OVER,true);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
        }else {
            ToastUtil.show(getApplicationContext(),"请开启防盗保护");
        }
    }

    @Override
    public void prePage(View view) {
        super.prePage(view);
    }

    @Override
    public void nextPage(View view) {
        super.nextPage(view);
    }
}
