package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
    private MyAdapter mUnLockAdatper,mLockAdatper;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //接收到信息，填充已加锁和未加锁的数据适配器
            mLockAdatper = new MyAdapter(true);
            lv_lock.setAdapter(mLockAdatper);

            mUnLockAdatper = new MyAdapter(false);
            lv_lock.setAdapter(mUnLockAdatper);
        }
    };
    private TranslateAnimation mTranslateAnimation;

    class MyAdapter extends BaseAdapter{
        private final boolean isLock;

        /**
        *@param isLock 用于区分已加锁和未加锁应用的标识
        *@return
        */
        public MyAdapter(boolean isLock){
            this.isLock = isLock;
        }

        @Override
        public int getCount() {

            if (isLock){
                tv_lock.setText("已加锁应用("+mLockList.size()+")");
                return mLockList.size();
            }else {
                tv_unlock.setText("未加锁应用("+mUnlockList.size()+")");
                return mUnlockList.size();
            }
        }

        @Override
        public AppInfo getItem(int position) {
            if (isLock){
                return mLockList.get(position);
            }else {
                return mUnlockList.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null){
                convertView = View.inflate(getApplicationContext(),R.layout.listview_islock_item,null);
                holder.iv_icon = convertView.findViewById(R.id.iv_lock);
                holder.tv_name = convertView.findViewById(R.id.tv_name);
                holder.iv_lock = convertView.findViewById(R.id.iv_lock);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            final AppInfo appInfo = getItem(position);
            final View animationView = convertView;
            holder.iv_icon.setBackgroundDrawable(appInfo.icon);
            holder.tv_name.setText(appInfo.name);
            if (isLock){
                holder.iv_lock.setBackgroundResource(R.drawable.lock);
            }else {
                holder.iv_lock.setBackgroundResource(R.drawable.unlock);
            }
            holder.iv_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animationView.setAnimation(mTranslateAnimation);
                    //对动画进行事件监听，动画执行完，才移除集合中的数据，操作数据库，刷新界面
                    mTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (isLock){

                                mLockList.remove(appInfo);
                                mUnlockList.add(appInfo);
                                mDao.delete(appInfo.packageName);
                                mLockAdatper.notifyDataSetChanged();
                            }else {
                                mLockList.add(appInfo);
                                mUnlockList.remove(appInfo);
                                mDao.insert(appInfo.packageName);
                                mUnLockAdatper.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
            });
            return convertView;
        }
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_lock;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        initUI();
        initData();
        initAnimation();
    }

    /**初始化平移动画
    *@param
    *@return
    */
    private void initAnimation() {
        mTranslateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        mTranslateAnimation.setDuration(500);
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
                mHandler.sendEmptyMessage(0);
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

        bt_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //已加锁列表显示，未加锁列表隐藏
                ll_lock.setVisibility(View.VISIBLE);
                ll_unlock.setVisibility(View.GONE);
                bt_unlock.setBackgroundResource(R.drawable.tab_left_default);
                bt_lock.setBackgroundResource(R.drawable.tab_right_pressed);
            }
        });

        bt_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_lock.setVisibility(View.GONE);
                ll_unlock.setVisibility(View.VISIBLE);
                bt_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
                bt_lock.setBackgroundResource(R.drawable.tab_right_default);
            }
        });
    }
}
