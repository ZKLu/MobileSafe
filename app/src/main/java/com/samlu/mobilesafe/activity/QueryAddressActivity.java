package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.EditText;

import com.samlu.mobilesafe.R;

/**
 * Created by sam lu on 2019/11/3.
 */
public class QueryAddressActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_address);

        initUI();
    }

    private void initUI() {
        EditText et_phone = findViewById(R.id.et_phone);
        String phone = et_phone.getText().toString();
        Button bt_query = findViewById(R.id.bt_query);
    }
}
