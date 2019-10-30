package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by sam lu on 2019/10/30.
 */
public class TestActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setText("testactivity");
        setContentView(textView);
    }
}
