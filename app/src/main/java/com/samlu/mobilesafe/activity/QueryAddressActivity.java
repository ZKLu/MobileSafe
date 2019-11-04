package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.engine.AddressDao;

/**
 * Created by sam lu on 2019/11/3.
 */
public class QueryAddressActivity extends Activity{

    private EditText et_phone;
    private Button bt_query;
    private TextView tv_query_result;
    private String mAddress;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            tv_query_result.setText(mAddress);
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_address);

        initUI();
    }

    private void initUI() {
        tv_query_result = findViewById(R.id.tv_query_result);
        et_phone = findViewById(R.id.et_phone);

        bt_query = findViewById(R.id.bt_query);
        bt_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_phone.getText().toString();
                if (!TextUtils.isEmpty(phone)){
                    query(phone);
                }else{
                    //抖动输入框
                    Animation shake = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.shake);
                    et_phone.startAnimation(shake);
                }
            }
        });
        //实时查询（监听输入框文本的变化）
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = et_phone.getText().toString();
                query(phone);
            }
        });
    }
    /**耗时操作
    *@param phone 要查询的电话号
    *@return
    */
    private void query(final String phone) {
        new Thread(){
            @Override
            public void run() {
                mAddress = AddressDao.getAddress(phone);
                //消息机制，告诉主线程更新UI
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }
}
