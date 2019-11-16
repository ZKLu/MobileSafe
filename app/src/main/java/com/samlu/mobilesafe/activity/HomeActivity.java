package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.utils.ConstantValue;
import com.samlu.mobilesafe.utils.Md5Util;
import com.samlu.mobilesafe.utils.SpUtil;
import com.samlu.mobilesafe.utils.ToastUtil;

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
        //注册九宫格单个条目的点击事件
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        //开启对话框
                        showDialog();
                        break;
                    case 1:
                        startActivity(new Intent(getApplicationContext(),BlackNumberActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(getApplicationContext(), AppManageActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(getApplicationContext(),ProcessManageActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(getApplicationContext(),AntiVirusActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(getApplicationContext(),AToolActivity.class));
                        break;
                    case 8:
                        Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void showDialog() {
        //判断本地是否有密码
        String pwd = SpUtil.getString(this, ConstantValue.MOBILE_SAFE_PWD,"");
        if (TextUtils.isEmpty(pwd)){
            //1、初始设置密码对话框
            showPwdDialog();
        }else {
            //2、确认密码对话框
            showConfirmDialog();
        }
    }
/**
 * 确认密码对话框
*@param
*@return
*/
    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        //自定义对话框界面
        final View view = View.inflate(this,R.layout.dialog_confirm_pwd,null);
        dialog.setView(view,0,0,0,0);//其余5个参数是内边距
        dialog.show();

        Button bt_submit = view.findViewById(R.id.bt_submit);
        Button bt_cancel = view.findViewById(R.id.bt_cancel);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText et_confirm_pwd = view.findViewById(R.id.et_confirm_pwd);
                String confirmPwd = et_confirm_pwd.getText().toString();

                if ( !confirmPwd.isEmpty()){
                    String MD5_pwd = Md5Util.encoder(confirmPwd);
                    String pwd = SpUtil.getString(getApplicationContext(),ConstantValue.MOBILE_SAFE_PWD,"");
                    if (MD5_pwd.equals(pwd)){
                        //进入手机防盗模块
                        Intent intent = new Intent(getApplicationContext(),SetupOverActivity.class);
                        startActivity(intent);
                        //跳转到新Activity后，dialog会隐藏
                        dialog.dismiss();
                    }
                    else{
                        ToastUtil.show(getApplicationContext(),"密码不匹配");
                    }
                }
                else{
                    ToastUtil.show(getApplicationContext(),"密码不能为空");
                }
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
 * 自定义设置密码对话框
*@param
*@return
*/
    private void showPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        //自定义对话框界面
        final View view = View.inflate(this,R.layout.dialog_set_pwd,null);
        dialog.setView(view,0,0,0,0); //其余5个参数是内边距
        dialog.show();

        Button bt_submit = view.findViewById(R.id.bt_submit);
        Button bt_cancel = view.findViewById(R.id.bt_cancel);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_set_pwd = view.findViewById(R.id.et_set_pwd);
                EditText et_confirm_pwd = view.findViewById(R.id.et_confirm_pwd);

                String pwd = et_set_pwd.getText().toString();
                String confirmPwd = et_confirm_pwd.getText().toString();

                if (!pwd.isEmpty() && !confirmPwd.isEmpty()){
                    if (pwd.equals(confirmPwd)){
                        //进入手机防盗模块
                        Intent intent = new Intent(getApplicationContext(),SetupOverActivity.class);
                        startActivity(intent);
                        //跳转到新Activity后，dialog会隐藏
                        dialog.dismiss();

                        SpUtil.putString(getApplicationContext(),ConstantValue.MOBILE_SAFE_PWD,
                                Md5Util.encoder(pwd));
                    }
                    else{
                        ToastUtil.show(getApplicationContext(),"密码不匹配");
                    }

                }
                else{
                    ToastUtil.show(getApplicationContext(),"密码不能为空");
                }
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
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
