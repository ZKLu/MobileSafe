package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.db.domin.ProcessInfo;
import com.samlu.mobilesafe.engine.ProcessInfoProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sam lu on 2019/11/10.
 */
public class ProcessManageActivity extends Activity implements View.OnClickListener{

    private TextView tv_process_count,tv_memory_info;
    private ListView lv_process_list;
    private Button bt_select_all,bt_reverse,bt_clean,bt_setting;
    private int mProcessCount;
    private List<ProcessInfo> mProcessInfo;
    private ArrayList<ProcessInfo> mSystemList,mCustomerList;
    private CheckBox cb_box;
    private MyAdapter mAdapter;
    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //使用数据适配器
            mAdapter = new MyAdapter();
            lv_process_list.setAdapter(mAdapter);
            if (tv_des != null && mCustomerList != null){
                tv_des.setText("用户进程("+mCustomerList.size()+")");
            }
        }
    };
    private TextView tv_des;


    class MyAdapter extends BaseAdapter {
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
            return mProcessInfo.size() +2;
        }

        @Override
        public ProcessInfo getItem(int position) {
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
                    holder.tv_title_des.setText("用户进程（"+mCustomerList.size()+"）");
                }else {
                    holder.tv_title_des.setText("系统进程（"+mSystemList.size()+"）");
                }
                return convertView;
            }else{
                //图文item
                ViewHolder holder = new ViewHolder();
                //判断convertView是否为空
                if (convertView == null){
                    convertView = View.inflate(getApplicationContext(),R.layout.listview_process_item,null);
                    //获取控件，赋值给ViewHolder
                    holder.iv_icon = convertView.findViewById(R.id.iv_icon);
                    holder.tv_app_name = convertView.findViewById(R.id.tv_app_name);
                    holder.tv_memory_info = convertView.findViewById(R.id.tv_memory_info);
                    holder.cb_box = convertView.findViewById(R.id.cb_box);
                    //holde放置在convertView上
                    convertView.setTag(holder);
                }else {
                    holder = (ViewHolder) convertView.getTag();
                }
                //获取holder中的控件，赋值
                holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
                holder.tv_app_name.setText(getItem(position).name);
                String strSize = Formatter.formatFileSize(getApplicationContext(),getItem(position).memSize);
                holder.tv_memory_info.setText(strSize);
                //本应用不能被选中，讲checkbox隐藏
                if (getItem(position).packageName.equals(getPackageName())){
                    holder.cb_box.setVisibility(View.GONE);
                }else {
                    holder.cb_box.setVisibility(View.VISIBLE);
                }
                holder.cb_box.setChecked(getItem(position).isCheck);
                return convertView;
            }
        }
    }
    static class ViewHolder{
        TextView tv_memory_info;
        ImageView iv_icon;
        TextView tv_app_name;
        CheckBox cb_box;
    }
    static class ViewTitleHolder{
        TextView tv_title_des;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manage);

        initUI();
        initTitleData();
        initListData();
    }

    private void initListData() {
       getData();
    }

    private void getData(){
        new Thread(){
            @Override
            public void run() {
                mProcessInfo = ProcessInfoProvider.getProcessInfo(getApplicationContext());
                mSystemList = new ArrayList<ProcessInfo>();
                mCustomerList = new ArrayList<ProcessInfo>();
                //分开系统应用和用户应用
                for (ProcessInfo info : mProcessInfo){
                    if(info.isSystem){
                        mSystemList.add(info);
                    }else {
                        mCustomerList.add(info);
                    }
                }
                mhandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initTitleData() {
        mProcessCount = ProcessInfoProvider.getProcssCount(this);
        tv_process_count.setText("进程总数："+mProcessCount);

        //获取可用内存大小，并且格式化
        long availableSpace = ProcessInfoProvider.getAvailableSpace(this);
        String strAvailebleSpace = Formatter.formatFileSize(this, availableSpace);

        //获取总内存大小，并且格式化(文件大小转换，第二个参数不断处除1024，直到小于900)
        long totalleSpace = ProcessInfoProvider.getTotalleSpace(this);
        String strTotalSpace = Formatter.formatFileSize(this, totalleSpace);

        tv_memory_info.setText("剩余/总共："+strAvailebleSpace+"/"+strAvailebleSpace);
    }

    private void initUI() {
        tv_process_count = findViewById(R.id.tv_process_count);
        tv_memory_info = findViewById(R.id.tv_memory_info);
        lv_process_list = findViewById(R.id.lv_process_list);
        bt_select_all = findViewById(R.id.bt_select_all);
        bt_reverse = findViewById(R.id.bt_reverse);
        bt_clean = findViewById(R.id.bt_clean);
        bt_setting = findViewById(R.id.bt_setting);

        bt_select_all.setOnClickListener(this);
        bt_reverse.setOnClickListener(this);
        bt_clean.setOnClickListener(this);
        bt_setting.setOnClickListener(this);
        tv_des = findViewById(R.id.tv_des);
        lv_process_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //滚动过程中调用的方法
                if (mCustomerList != null && mSystemList != null) {
                    if (firstVisibleItem >= mCustomerList.size() + 1) {
                        //滚动到系统应用
                        tv_des.setText("系统进程（"+mSystemList.size()+"）");
                    } else {
                        //滚动到用户应用
                        tv_des.setText("用户进程（"+mCustomerList.size()+"）");
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
