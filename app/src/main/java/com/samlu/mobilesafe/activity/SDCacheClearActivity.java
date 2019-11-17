package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by sam lu on 2019/11/17.
 */
public class SDCacheClearActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("SDCacheClearActivity");
        setContentView(tv);
    }
}
