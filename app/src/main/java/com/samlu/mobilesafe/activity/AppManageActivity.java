package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.db.domin.AppInfo;
import com.samlu.mobilesafe.engine.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sam lu on 2019/11/6.
 */
public class AppManageActivity extends Activity{

    private ListView lv_app_list;
    private List<AppInfo> mAppInfoList;
    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //使用数据适配器
            mAdapter = new MyAdapter();
            lv_app_list.setAdapter(mAdapter);
        }
    };
    private MyAdapter mAdapter;
    private List<AppInfo> mSystemList;
    private List<AppInfo> mCustomerList;
    private TextView tv_title_type;

    class MyAdapter extends BaseAdapter{
        //在ListView中添加多一种条目的类型，现在条目类型总数为2
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount()+ 1;
        }

        //区分position指向的item类型，是图片+文字还是纯文字
        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mCustomerList.size()+1 ) {
                //纯文本类型
                //0是纯文本的状态码
                return 0;
            }else {
                return 1;
                //图文类型
                //1是图文的状态码
            }
        }

        @Override
        public int getCount() {
            //还有两条灰色item，显示用户应用和系统应用
            return mAppInfoList.size() +2;
        }

        @Override
        public AppInfo getItem(int position) {
            if (position == 0 || position == mCustomerList.size()+1 ){
                //纯文本条目不能使用mAppInfoList的数据，这里返回Null
                return null;
            }else {
                if (position < mCustomerList.size() +1){
                    return mCustomerList.get(position - 1);
                }else {
                    return mSystemList.get(position - mCustomerList.size() - 2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //判断当前Position指向的条目类型的状态码
            int itemViewType = getItemViewType(position);
            if (itemViewType == 0){
                ViewTitleHolder holder ;
                //纯文字
                if (convertView == null){
                    convertView = View.inflate(getApplicationContext(),R.layout.list_app_title_item,null);
                    holder = new ViewTitleHolder();
                    holder.tv_title_des= convertView.findViewById(R.id.tv_title_des);
                    convertView.setTag(holder);
                }else {
                    holder = (ViewTitleHolder) convertView.getTag();
                }
                if (position ==0){
                    holder.tv_title_des.setText("用户应用（"+mCustomerList.size()+"）");
                }else {
                    holder.tv_title_des.setText("系统应用（"+mSystemList.size()+"）");
                }
                return convertView;
            }else{
                //图文item
                ViewHolder holder = new ViewHolder();
                //判断convertView是否为空
                if (convertView == null){
                    convertView = View.inflate(getApplicationContext(),R.layout.list_app_item,null);
                    //获取控件，赋值给ViewHolder
                    holder.iv_icon = convertView.findViewById(R.id.iv_icon);
                    holder.tv_app_location = convertView.findViewById(R.id.tv_app_location);
                    holder.tv_app_name = convertView.findViewById(R.id.tv_app_name);
                    //holde放置在convertView上
                    convertView.setTag(holder);
                }else {
                    holder = (ViewHolder) convertView.getTag();
                }
                //获取holder中的控件，赋值
                holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
                holder.tv_app_name.setText(getItem(position).name);
                if (getItem(position).isSdCard){
                    holder.tv_app_location.setText("sd卡应用");
                }else{
                    holder.tv_app_location.setText("内存应用");
                }
                return convertView;
            }
        }
    }
    static class ViewHolder{
        TextView tv_app_name;
        ImageView iv_icon;
        TextView tv_app_location;
    }
    static class ViewTitleHolder{
        TextView tv_title_des;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        initTitle();
        initListView();
    }

    private void initListView() {
        lv_app_list = findViewById(R.id.lv_app_list);
        tv_title_type = findViewById(R.id.tv_title_type);
        lv_app_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //滚动过程中调用的方法
                if (mCustomerList != null && mSystemList != null) {
                    if (firstVisibleItem >= mCustomerList.size() + 1) {
                        //滚动到系统应用
                        tv_title_type.setText("系统应用（"+mSystemList.size()+"）");
                    } else {
                        //滚动到用户应用
                        tv_title_type.setText("用户应用（"+mCustomerList.size()+"）");
                    }
                }
            }
        });

        new Thread(){
            @Override
            public void run() {
                mCustomerList = new ArrayList<AppInfo>();
                mSystemList = new ArrayList<AppInfo>();
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                //分开系统应用和用户应用
                for (AppInfo appInfo : mAppInfoList){
                    if(appInfo.isSystem){
                        mSystemList.add(appInfo);
                    }else {
                        mCustomerList.add(appInfo);
                    }
                }
                mhandler.sendEmptyMessage(0);
            }
        }.start();

    }

    private void initTitle() {
        //获取内部存储可用大小
        //内部存储的路径
        String path = Environment.getDataDirectory().getAbsolutePath();
        //获取sd卡可用大小
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //获取以上两个路径的可用大小,单位为bytes
        long space = getAvailableSpace(path);
        long sdSpace = getAvailableSpace(sdPath);
        //将结果格式化
        String strSpace = Formatter.formatFileSize(this, space);
        String strSdSpace = Formatter.formatFileSize(this,sdSpace);

        TextView tv_memory = findViewById(R.id.tv_memory);
        TextView tv_sd_memory = findViewById(R.id.tv_sd_memory);

        tv_memory.setText("磁盘可用："+strSpace);
        tv_sd_memory.setText("sd卡可用："+strSdSpace);
    }

    /**返回可用磁盘大小
    *@param
    *@return 返回值用long类型，因为int的最大值换算成2G
    */
    private long getAvailableSpace(String path) {
        StatFs statFs = new StatFs(path);
        //获取可用区块的个数
        long availableBlocks = statFs.getAvailableBlocksLong();
        //获取区块的大小
        long blockSize = statFs.getBlockSizeLong();
        //区块大小*可用区块个数 = 可用空间大小
        return availableBlocks*blockSize;
    }
}
