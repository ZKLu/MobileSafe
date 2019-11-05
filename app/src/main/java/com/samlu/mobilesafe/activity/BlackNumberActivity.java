package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.db.dao.BlackNumberDao;
import com.samlu.mobilesafe.db.domin.BlackNumberInfo;
import com.samlu.mobilesafe.utils.ToastUtil;

import java.util.List;

/**
 * Created by sam lu on 2019/11/5.
 */
public class BlackNumberActivity extends Activity{

    private ListView lv_blacknumber;
    private MyAdapter myAdapter;
    private BlackNumberDao mDao;
    private List<BlackNumberInfo> mBlackNumberList;
    private int mode =1;
    private Button bt_add;
    private View view;

    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //告知ListView可以去设置数据适配器
            myAdapter = new MyAdapter();
            lv_blacknumber.setAdapter(myAdapter);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacknumber);

        initUI();
        initData();
    }

    private void initData() {
        //获取数据库中所有数据
        new Thread(){
            @Override
            public void run() {
                //获取操作数据库对象
                mDao = BlackNumberDao.getInstance(getApplicationContext());
                //查询所有数据
                mBlackNumberList = mDao.findAll();
                //通过消息机制告诉主线程，可以使用包含数据的集合
                mhandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {
        bt_add = findViewById(R.id.bt_add);
        lv_blacknumber = findViewById(R.id.lv_blacknumber);

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(getApplicationContext(),R.layout.dialog_add_blacknumber,null);

        final EditText et_phone = view.findViewById(R.id.et_phone);
        RadioGroup rg_group = view.findViewById(R.id.rg_group);
        Button bt_submit =  view.findViewById(R.id.bt_submit);
        Button bt_cancel = view.findViewById(R.id.bt_cancel);

        //监听其选中条目的切换过程
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_sms:
                        mode = 1;
                        break;
                    case R.id.rb_phone:
                        mode = 2;
                        break;
                    case R.id.rb_all:
                        mode = 3;
                        break;
                }
            }
        });
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_phone.getText().toString();
                if (TextUtils.isEmpty(phone)){
                    ToastUtil.show(getApplicationContext(),"请输入拦截号码");
                }else {
                    mDao.insert(phone,mode+"");
                    //让数据库和集合保持同步（重新读一遍数据库或向集合中添加一个对象）
                    BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                    blackNumberInfo.phone = phone;
                    blackNumberInfo.mode = mode+"";

                    //将对象插入到集合的头部
                    mBlackNumberList.add(0,blackNumberInfo);
                    //通知数据适配器刷新
                    if (myAdapter != null){
                        myAdapter.notifyDataSetChanged();
                    }
                    dialog.dismiss();
                }
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setView(view);
        dialog.show();
    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mBlackNumberList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBlackNumberList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        //优化ListView的一种方式：复用convertView
        /*if (convertView == null){
               convertView = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item,null);
           }*/
        //优化ListView的一种方式：对findViewById()的次数的优化，使用viewHolder。将findViewById()封装到if(convertView == null)中去执行
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null){
               convertView = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item,null);
           }

            TextView tv_phone = convertView.findViewById(R.id.tv_phone);
            TextView tv_mode = convertView.findViewById(R.id.tv_mode);
            ImageView iv_delete = convertView.findViewById(R.id.iv_delete);

            tv_phone.setText(mBlackNumberList.get(position).phone);
            int mode = Integer.parseInt(mBlackNumberList.get(position).mode);
            switch (mode){
                case  1:
                    tv_mode.setText("拦截短信");
                    break;
                case  2:
                    tv_mode.setText("拦截电话");
                    break;
                case  3:
                    tv_mode.setText("拦截所有");
                    break;
            }
            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //数据库删除数据
                    mDao.delete(mBlackNumberList.get(position).phone);
                    //集合中删除数据，通知数据适配器刷新
                    mBlackNumberList.remove(position);
                    if (myAdapter != null){
                        myAdapter.notifyDataSetChanged();
                    }
                }
            });

            return convertView;
        }
    }

    class ViewHolder{

    }
}
