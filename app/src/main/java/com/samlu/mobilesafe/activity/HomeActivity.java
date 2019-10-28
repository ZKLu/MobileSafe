package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.samlu.mobilesafe.R;

/**
 * Created by sam lu on 2019/10/24.
 */
public class HomeActivity extends Activity{
    public static final String TAG = "HomeActivity";
    private GridView gv_home;
    private String[] mTitleStr;
    private int[] mDrawableIDs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initUI();
        //初始化数据
        initData();
    }

    /**
     * 准备数据,包含文字和图片
     *@param:
     *@return:
     */
    private void initData() {
        mTitleStr = new String[]{"手机防盗","通信卫士","软件管理",
        "进程管理","流量统计","手机杀毒","缓存清理",
        "高级工具","设置中心"};
        mDrawableIDs = new int[]{R.drawable.home_safe,R.drawable.home_callmsgsafe,
                R.drawable.home_apps, R.drawable.home_taskmanager,R.drawable.home_netmanager,
                R.drawable.home_trojan, R.drawable.home_sysoptimize,R.drawable.home_tools,
                R.drawable.home_settings};
        //九宫格控件设置数据适配器
        gv_home.setAdapter(new MyAdapter());
    }


    private void initUI() {
        gv_home = findViewById(R.id.gv_home);
        
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            //返回条目的总数
            return mTitleStr.length;
        }

        @Override
        public Object getItem(int position) {
            return mTitleStr[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(),R.layout.gridview_item,null);
            ImageView iv_icon = view.findViewById(R.id.iv_icon);
            TextView tv_title = view.findViewById(R.id.tv_title);
            tv_title.setText(mTitleStr[position]);;
            iv_icon.setBackgroundResource(mDrawableIDs[position]);
            return view;
        }
    }
}
