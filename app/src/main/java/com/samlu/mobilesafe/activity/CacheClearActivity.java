package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.samlu.mobilesafe.R;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

/**
 * Created by sam lu on 2019/11/17.
 */
public class CacheClearActivity extends Activity{

    private static final int UPDATE_CACHE_APP = 100;
    private static final int CHECK_CACHE_APP = 101;
    private static final int CHECK_FINISH = 102;
    private Button bt_clean;
    private ProgressBar pb_bar;
    private TextView tv_name;
    private LinearLayout ll_add_text;
    private PackageManager mPM;
    private int index = 0;
    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_CACHE_APP:
                    View view = View.inflate(getApplicationContext(), R.layout.linearlayout_cache_item, null);
                    ImageView iv_icon = view.findViewById(R.id.iv_icon);
                    TextView tv_app_name = view.findViewById(R.id.tv_app_name);
                    TextView tv_memory_info = view.findViewById(R.id.tv_memory_info);
                    ImageView iv_delete = view.findViewById(R.id.iv_delete);
                    CacheInfo info = (CacheInfo) msg.obj;
                    iv_icon.setBackgroundDrawable(info.icon);
                    tv_name.setText(info.name);
                    tv_memory_info.setText(Formatter.formatFileSize(getApplicationContext(),info.cacheSize));
                    ll_add_text.addView(view,0);
                    break;
                case CHECK_CACHE_APP:
                    tv_name.setText("正在扫描："+msg.obj.toString());
                    break;
                case CHECK_FINISH:
                    tv_name.setText("扫描结束");
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_cache);
        initUI();
        initData();
    }

    private void initData() {
        new Thread(){
            @Override
            public void run() {
                mPM = getPackageManager();
                List<PackageInfo> installedPackagesList = mPM.getInstalledPackages(0);
                pb_bar.setMax(installedPackagesList.size());
                //遍历应用列表，获取有缓存的应用的信息
                for (PackageInfo info : installedPackagesList){
                    String packageName = info.packageName;
                    getPackageCache(packageName);

                    try {
                        Thread.sleep(100 + new Random().nextInt(50));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    index++;
                    pb_bar.setProgress(index);

                    //每循环一次，将检测应用的名称发送给主线程
                    Message msg = Message.obtain();
                    msg.what = CHECK_CACHE_APP;
                    try {
                        msg.obj = getPackageManager().getApplicationInfo(packageName,0).loadLabel(getPackageManager()).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    mhandler.sendMessage(msg);
                }
                Message msg = Message.obtain();
                msg.what = CHECK_FINISH;
                mhandler.sendMessage(msg);
            }
        }.start();

    }

    class CacheInfo{
        public String name;
        public Drawable icon;
        public String packageName;
        public long cacheSize;
    }

    /**获取应用的缓存
    *@param
    *@return
    */
    private void getPackageCache(String packageName) {
        IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
            @Override
            public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                long cacheSize = pStats.cacheSize;
                //判断缓存大小是否大于0，因为有的应用没有缓存
                if (cacheSize > 0){
                    //告知主线程更新UI
                    Message msg = Message.obtain();
                    msg.what = UPDATE_CACHE_APP;
                    CacheInfo info = new CacheInfo();
                    info.cacheSize = cacheSize;
                    info.packageName = pStats.packageName;
                    try {
                        info.name  = mPM.getApplicationInfo(pStats.packageName,0).loadLabel(mPM).toString();
                        info.icon = mPM.getApplicationInfo(pStats.packageName,0).loadIcon(mPM);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    msg.obj = info;
                    mhandler.sendMessage(msg);
                }
            }
        };
        try {
            Class<?> clazz = Class.forName("android.content.pm.PackageManager");
            Method method = clazz.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            method.invoke(mPM,packageName,mStatsObserver);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        bt_clean = findViewById(R.id.bt_clean);
        pb_bar = findViewById(R.id.pb_bar);
        tv_name = findViewById(R.id.tv_name);
        ll_add_text = findViewById(R.id.ll_add_text);

        bt_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class<?> clazz = null;
                try {
                    clazz = Class.forName("android.content.pm.PackageManager");
                    Method method = clazz.getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
                    method.invoke(mPM,Long.MAX_VALUE,mStatsObserver);
                } catch (Exception e) {
                    e.printStackTrace();
                }



            }
        });
    }
}
