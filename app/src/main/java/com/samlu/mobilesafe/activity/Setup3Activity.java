package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.utils.ConstantValue;
import com.samlu.mobilesafe.utils.SpUtil;
import com.samlu.mobilesafe.utils.ToastUtil;

/**
 * Created by sam lu on 2019/11/1.
 */
public class Setup3Activity extends Activity{

    private EditText et_phone_number;
    private Button bt_select_number;
    private String mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        initUI();
    }

    private void initUI() {
        et_phone_number = findViewById(R.id.et_phone_number);
        bt_select_number = findViewById(R.id.bt_select_number);
        bt_select_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactListActivity.class);
                startActivityForResult(intent,0);
            }
        });
        //获取联系人，返回该页可以看见电话号码
        String phone = SpUtil.getString(this,ConstantValue.CONTACT_PHONE,"");
        et_phone_number.setText(phone);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null){
            //返回到当前界面，接收结果
            mPhone = data.getStringExtra("phone");
            //将特殊字符过滤(将中划线转换成空字符串)
            mPhone = mPhone.replace("-","").replace(" ","").trim();
            et_phone_number.setText(mPhone);
        }
        //存储联系人至sp中
        SpUtil.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, mPhone);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void nextPage(View view){
        //点击按钮以后，需要获取框中的联系人
        String phone = et_phone_number.getText().toString();

        if (!TextUtils.isEmpty(phone)){
            Intent intent = new Intent(getApplicationContext() ,Setup4Activity.class);
            startActivity(intent);

            finish();

            //如果是输入的电话号码,需要去保存
            SpUtil.putString(getApplicationContext(),ConstantValue.CONTACT_PHONE,phone);
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
        }else{
            ToastUtil.show(getApplicationContext(),"请输入电话号码");
        }
    }
    public  void prePage(View view){
        Intent intent = new Intent(getApplicationContext() ,Setup2Activity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }
}
