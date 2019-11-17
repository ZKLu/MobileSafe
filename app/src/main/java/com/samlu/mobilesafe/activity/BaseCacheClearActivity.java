package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.samlu.mobilesafe.R;

/**
 * Created by sam lu on 2019/11/17.
 */
public class BaseCacheClearActivity extends TabActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_clear_cache);

        //生成选项卡1
        TabHost.TabSpec tab1 = getTabHost().newTabSpec("clear_cache").setIndicator("缓存清理");
        //生成选项卡2
        TabHost.TabSpec tab2 = getTabHost().newTabSpec("sd_clear_cache").setIndicator("sd卡清理");

        //点中选项卡的处理
        tab1.setContent(new Intent(this,CacheClearActivity.class));
        tab2.setContent(new Intent(this,SDCacheClearActivity.class));

        //将两个选项卡维护到host（选项卡宿主）中
        getTabHost().addTab(tab1);
        getTabHost().addTab(tab2);
    }
}
