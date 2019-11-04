package com.samlu.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.samlu.mobilesafe.R;

/**
 * Created by sam lu on 2019/10/29.
 */

public class SettingClickView extends RelativeLayout {

    private static final String TAG ="SettingItemView" ;
    private TextView tv_des;
    private TextView tv_title;


    public SettingClickView(Context context) {
        this(context,null);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //xml->view 将设置界面的一个条目转换成view对象，直接添加到了当前SettingItemView对应的view中
        View.inflate(context, R.layout.setting_click_view,this);

        //自定义组合控件的标题描述
        tv_title = findViewById(R.id.tv_title);
        tv_des = findViewById(R.id.tv_des);

    }

    /**设置标题内容
    *@param title
    *@return
    */
    public void setTitle(String title){
        tv_title.setText(title);
    }
    /**设置描述内容
    *@param 
    *@return 
    */
    public void setDes(String des){
        tv_des.setText(des);
    }
}
