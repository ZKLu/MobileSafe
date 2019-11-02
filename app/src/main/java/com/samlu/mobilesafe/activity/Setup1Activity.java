package com.samlu.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.samlu.mobilesafe.R;

/**
 * Created by sam lu on 2019/10/30.
 */
public class Setup1Activity extends BaseSetupActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }


    @Override
    protected void showPrePage() {
        //在第一页，没有上一页，空实现
    }

    @Override
    protected void showNextPage() {
        Intent intent = new Intent(getApplicationContext(),Setup2Activity.class);
        startActivity(intent);
        finish();

        //开启平移动画
        overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);
    }

    @Override
    public void nextPage(View view) {
        super.nextPage(view);
    }
}
