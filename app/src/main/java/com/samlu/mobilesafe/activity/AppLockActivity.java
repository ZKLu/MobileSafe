package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.db.dao.AppLockDao;
import com.samlu.mobilesafe.db.domin.AppInfo;
import com.samlu.mobilesafe.engine.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sam lu on 2019/11/13.
 */
public class AppLockActivity extends Activity{

    private Button bt_lock;
    private Button bt_unlock;
    private LinearLayout ll_lock;
    private LinearLayout ll_unlock;
    private TextView tv_lock;
    private TextView tv_unlock;
    private ListView lv_lock;
    private ListView lv_unlock;
    private List<AppInfo> mAppInfoList;
    private List<AppInfo> mLockList,mUnlockList;
    private AppLockDao mDao;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //接收到信息，填充已加锁和未加锁的数据适配器
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_app_lock);
        initUI();
        initData();
    }

    /**区分已加锁和未加锁应用的集合
    *@param 
    *@return 
    */
    private void initData() {
        new Thread(){
            @Override
            public void run() {
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                //区分已加锁应用和未加锁应用
                mLockList = new ArrayList<>();
                mUnlockList = new ArrayList<>();
                //获取数据中已加锁应用的包名的集合
                mDao = AppLockDao.getInstance(getApplicationContext());
                List<String> lockPackageList = mDao.findAll();
                for (AppInfo appInfo:mAppInfoList){
                    if(lockPackageList.contains(appInfo.packageName)){
                        mLockList.add(appInfo);
                    }else {
                        mUnlockList.add(appInfo);
                    }
                }
                //告知主线程，可以使用准备好的数据
            }
        }.start();

    }

    private void initUI() {
        bt_lock = findViewById(R.id.bt_lock);
        bt_unlock = findViewById(R.id.bt_unlock);

        ll_lock = findViewById(R.id.ll_lock);
        ll_unlock = findViewById(R.id.ll_unlock);

        tv_lock = findViewById(R.id.tv_lock);
        tv_unlock = findViewById(R.id.tv_lock);

        lv_lock = findViewById(R.id.lv_lock);
        lv_unlock = findViewById(R.id.lv_unlock);
    }
}
