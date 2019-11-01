package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.samlu.mobilesafe.R;

/**
 * Created by sam lu on 2019/11/1.
 */
public class Setup3Activity extends Activity{

    private EditText et_phone_number;
    private Button bt_select_number;

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null){
            //返回到当前界面，接收结果
            String phone = data.getStringExtra("phone");
            //将特殊字符过滤(将中划线转换成空字符串)
            phone = phone.replace("-","").replace(" ","").trim();
            et_phone_number.setText(phone);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void nextPage(View view){
        Intent intent = new Intent(getApplicationContext() ,Setup4Activity.class);
        startActivity(intent);
    }
    public  void prePage(View view){
        Intent intent = new Intent(getApplicationContext() ,Setup2Activity.class);
        startActivity(intent);
    }
}
