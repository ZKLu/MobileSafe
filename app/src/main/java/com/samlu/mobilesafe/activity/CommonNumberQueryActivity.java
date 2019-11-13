package com.samlu.mobilesafe.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.engine.CommonNumberDao;

import java.util.List;

/**
 * Created by sam lu on 2019/11/13.
 */
public class CommonNumberQueryActivity extends Activity {
    public static final String TAG = "CommonNumberQuery";

    private ExpandableListView elv_number;
    private List<CommonNumberDao.Group> mGroup;
    private MyAdatper myAdatper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_number);
        initUI();
        initData();
    }

    private void initUI() {
        elv_number = findViewById(R.id.elv_number);
    }

    /**给elv准备数据
     *@param
     *@return
     */
    private void initData() {
        CommonNumberDao dao = new CommonNumberDao();
        mGroup = dao.getGroup();
        myAdatper = new MyAdatper();
        elv_number.setAdapter(myAdatper);
        //给elv注册点击事件
        elv_number.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                startCall(myAdatper.getChild(groupPosition, childPosition).number);
                return false;
            }
        });
    }

    private void startCall(String number) {
        //开启系统的打电话应用
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            startActivity(intent);
        }
        startActivity(intent);
    }

    class MyAdatper extends BaseExpandableListAdapter{
        @Override
        public int getGroupCount() {
            //获取组的总数
            return mGroup.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            //获取指定的组的child对象的总数
            return mGroup.get(groupPosition).childList.size();
        }

        @Override
        public CommonNumberDao.Group getGroup(int groupPosition) {
            //获取指定的组的对象
            return mGroup.get(groupPosition);
        }

        @Override
        public CommonNumberDao.Child getChild(int groupPosition, int childPosition) {
            //获取指定的组中child对象
            return mGroup.get(groupPosition).childList.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            //返回child的索引
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            //用来判断ExpandableListView内容id是否有效的(返回true or false)，系统会跟据id来确定当前显示哪条内容，也就是firstVisibleChild的位置。
            //已经写死，不用修改
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getApplicationContext());
            textView.setText("     "+getGroup(groupPosition).name);
            textView.setTextColor(Color.RED);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);//单位DIP等于dp
            return textView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.elv_child_item, null);
            TextView tv_name = view.findViewById(R.id.tv_name);
            TextView tv_number= view.findViewById(R.id.tv_number);
            tv_name.setText(getChild(groupPosition,childPosition).name);
            tv_number.setText(getChild(groupPosition,childPosition).number);
            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            //child是否能被选中，响应点击事件
            return true;
        }
    }
}
