package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.samlu.mobilesafe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sam lu on 2019/11/1.
 */
public class ContactListActivity extends Activity{

    private MyAdapter myadapter;
    private ListView lv_contact;
    private List<HashMap<String,String>> contactList = new ArrayList<HashMap<String,String>>();

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            myadapter = new MyAdapter();
            lv_contact.setAdapter(myadapter);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        initUI();
        initData();
    }
    /**获取系统联系人列表
    *@param
    *@return
    */
    private void initData() {
        //因为读取系统联系人可能是耗时操作，需要放置到子线程中处理
        new Thread(){
            @Override
            public void run() {
                ContentResolver contentResolver = getContentResolver();
                //查询系统联系人数据库
                Cursor cursor = contentResolver.query(
                        Uri.parse("content://com.android.contacts/raw_contacts"),
                        new String[]{"contact_id"},null,null,null);
                //ListView作为成员变量，为避免里面有数据,出现数据重复的问题，在使用之前清空以下
                contactList.clear();
                while (cursor.moveToNext()){
                    String id = cursor.getString(0);
                    //根据用户唯一性id，查询data表和mimetype表生成的视图，获取data以及mimetype字段
                    Cursor indexCursor = contentResolver.query(
                            Uri.parse("content://com.android.contacts/data"),
                            new String[]{"data1","mimetype"},"raw_contact_id = ?",
                            new String[]{id},null);
                    HashMap<String,String> haspMap = new HashMap<>();
                    while (indexCursor.moveToNext()){
                        String data = indexCursor.getString(0);
                        String type = indexCursor.getString(1);

                        if (type.equals("vnd.android.cursor.item/phone_v2")){
                            if (!TextUtils.isEmpty(data)){
                                haspMap.put("phone",data);
                            }
                        }else if(type.equals("vnd.android.cursor.item/name")){
                            if (!TextUtils.isEmpty(data)) {
                                haspMap.put("name", data);
                            }
                        }
                    }
                    indexCursor.close();
                    contactList.add(haspMap);
                }
                cursor.close();

                //因为这是子线程，不能更新UI，所以用handler
                //发送一个空消息，告诉主线程数据集合可以使用
                mHandler.sendEmptyMessage(0);
            }
        }.start();

    }

    private void initUI() {
        lv_contact = findViewById(R.id.lv_contact);
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (myadapter != null){
                    HashMap<String,String> hashMap =myadapter.getItem(position);
                    String phone = hashMap.get("phone");
                    //电话号码需要给第三导航界面使用
                    Intent intent =new Intent();
                    intent.putExtra("phone",phone);
                    setResult(0,intent);

                    finish();
                }
            }
        });
    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return contactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(),R.layout.listview_contact_item,null);
            TextView tv_name = view.findViewById(R.id.tv_name);
            TextView tv_phone = view.findViewById(R.id.tv_phone);
            tv_name.setText(getItem(position).get("name"));
            tv_phone.setText(getItem(position).get("phone"));
            return view;
        }
    }
}
