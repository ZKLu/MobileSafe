package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;

import com.samlu.mobilesafe.R;

/**
 * Created by sam lu on 2019/11/17.
 */
public class TrafficActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);
        //获取移动网络下载流量(R :下载流量)
        long mobileRxBytes = TrafficStats.getMobileRxBytes();
        //获取总流量(t：总流量，移动网络的上传+下载)
        long mobileTxBytes = TrafficStats.getMobileTxBytes();

        //下载流量总和(移动网络+wifi)
        long totalRxBytes = TrafficStats.getTotalRxBytes();
        //总流量 移动网络+WiFi的上传+下载
        long totalTxBytes = TrafficStats.getTotalTxBytes();
    }
}
