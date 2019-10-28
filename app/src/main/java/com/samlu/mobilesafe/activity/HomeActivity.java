package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import com.samlu.mobilesafe.R;

/**
 * Created by sam lu on 2019/10/24.
 */
public class HomeActivity extends Activity{
    public static final String TAG = "HomeActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

    }
}
