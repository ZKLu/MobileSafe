package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.engine.VirusDao;
import com.samlu.mobilesafe.utils.Md5Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by sam lu on 2019/11/16.
 */
public class AntiVirusActivity extends Activity{

    private ImageView iv_scanning;
    private TextView tv_name;
    private ProgressBar pb_bar;
    private LinearLayout ll_add_text;
    private int index = 0;
    private static final int SCANNING =100;
    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);
        initUI();
        initAnimation();
        checkVirus();
    }



    private void initUI() {
        iv_scanning = findViewById(R.id.iv_scanning);
        tv_name = findViewById(R.id.tv_name);
        pb_bar = findViewById(R.id.pb_bar);
        ll_add_text = findViewById(R.id.ll_add_text);
    }

    private void initAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        //指定动画一直旋转
        rotateAnimation.setRepeatMode(RotateAnimation.INFINITE);
        //保持动画执行结束后的状态
        rotateAnimation.setFillAfter(true);
    }

    private void checkVirus() {
        new Thread(){
            @Override
            public void run() {
                //获取数据库中病毒的MD5码
                List<String> virusList = VirusDao.getVirusList();
                //获取手机上所有应用的签名文件的MD5码
                PackageManager pm = getPackageManager();
                //PackageManager.GET_SIGNATURES 已安装应用的签名文件
                //PackageManager.GET_UNINSTALLED_PACKAGES 指卸载后残余的文件
                List<PackageInfo> packageInfoList = pm.getInstalledPackages(
                        PackageManager.GET_SIGNATURES + PackageManager.GET_UNINSTALLED_PACKAGES);
                //记录带病毒应用的集合
                List<ScanInfo> VirusScanInfoList = new ArrayList<>();
                //记录所有应用的结合
                List<ScanInfo> scanInfoList = new ArrayList<>();
                pb_bar.setMax(packageInfoList.size());
                for (PackageInfo info :packageInfoList){
                    //获取签名文件的数组
                    Signature[] signatures = info.signatures;
                    Signature signature = signatures[0];
                    String s = signature.toCharsString();
                    String encoder = Md5Util.encoder(s);
                    //比对应用是否为病毒
                    ScanInfo scanInfo = new ScanInfo();
                    if (virusList.contains(encoder)){
                        //记录病毒
                        scanInfo.isVirus = true;
                        VirusScanInfoList.add(scanInfo);
                    }else {
                        scanInfo.isVirus = false;
                    }
                    scanInfo.packageName = info.packageName;
                    scanInfo.name = info.applicationInfo.loadLabel(pm).toString();
                    scanInfoList.add(scanInfo);
                    index++;
                    pb_bar.setProgress(index);

                    try {
                        Thread.sleep(50 + new Random().nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //在子线程中发送消息，告诉主线程更新UI
                    Message msg = Message.obtain();
                    msg.what = SCANNING;
                    msg.obj = scanInfo;
                    mhandler.sendMessage(msg);
                }
            }
        }.start();
    }

    class ScanInfo{
        public boolean isVirus;
        public String packageName;
        public String name;
    }
}
