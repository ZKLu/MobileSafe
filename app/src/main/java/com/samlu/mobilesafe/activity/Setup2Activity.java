package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Toast;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.utils.ConstantValue;
import com.samlu.mobilesafe.utils.SpUtil;
import com.samlu.mobilesafe.utils.ToastUtil;
import com.samlu.mobilesafe.view.SettingItemView;

/**
 * Created by sam lu on 2019/11/1.
 */
public class Setup2Activity extends Activity{

    private SettingItemView siv_sim_bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        initUI();

    }

    private void initUI() {
        siv_sim_bound = findViewById(R.id.siv_sim_bound);
        //若之前已绑定sim卡，读取已有的绑定状态,判断标准是sim卡序列号是否保存在sharepreference中
        String sim_number = SpUtil.getString(this, ConstantValue.SIM_NUMBER,"");
        //判断序列卡号为空
        if (TextUtils.isEmpty(sim_number)){
            siv_sim_bound.setCheck(false);

        }else {
            siv_sim_bound.setCheck(true);

        }
        siv_sim_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取原有的状态
                boolean isCheck = siv_sim_bound.isCheck();
                //将原有状态取反，设置给当前条目
                siv_sim_bound.setCheck(!isCheck);

                if (!isCheck){
                    //存储在sp中
                    TelephonyManager manager = (TelephonyManager)
                            getSystemService(Context.TELEPHONY_SERVICE);
                    String simSerialNumber = manager.getSimSerialNumber();
                    SpUtil.putString(getApplicationContext(),ConstantValue.SIM_NUMBER,simSerialNumber);
                }else{
                    //将存储序列卡号的节点从sp中删除
                    SpUtil.remove(getApplicationContext(),ConstantValue.SIM_NUMBER);
                }
            }
        });
    }

    public void nextPage(View view){
        String serialNumber= SpUtil.getString(this,ConstantValue.SIM_NUMBER,"");
        if (!TextUtils.isEmpty(serialNumber)) {
            Intent intent = new Intent(getApplicationContext(),Setup3Activity.class);
            startActivity(intent);
            finish();
        }else {
            ToastUtil.show(this,"请绑定SIM卡");
        }


    }
    public void prePage(View view){
        Intent intent = new Intent(getApplicationContext(),Setup1Activity.class);
        startActivity(intent);
        finish();
    }
}
