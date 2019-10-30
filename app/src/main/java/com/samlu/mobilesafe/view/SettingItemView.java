package com.samlu.mobilesafe.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.utils.ConstantValue;

/**
 * Created by sam lu on 2019/10/29.
 */

public class SettingItemView extends RelativeLayout {

    private static final String TAG ="SettingItemView" ;
    private CheckBox cb_box;
    private TextView tv_des;
    private String mDeson;
    private String mDesoff;
    private String mDestitle;
    private static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";

    public SettingItemView(Context context) {
        this(context,null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //xml->view 将设置界面的一个条目转换成view对象，直接添加到了当前SettingItemView对应的view中
        View.inflate(context, R.layout.setting_item_view,this);
        /*上下两个方法是实现相同功能
        View view = View.inflate(context, R.layout.setting_item_view, null);
        this.addView(view);*/

        //自定义组合控件的标题描述
        TextView tv_title = findViewById(R.id.tv_title);
        tv_des = findViewById(R.id.tv_des);
        cb_box = findViewById(R.id.cb_box);

        //获取自定义以及原生属性的操作，从AttributeSet获取
        initAttrs(attrs);

        tv_title.setText(mDestitle);
    }

    /**
    *@param attrs 构造方法中维护好的属性集合
    *@return 返回属性集合中自定义属性值
    */
    private void initAttrs(AttributeSet attrs) {
        //通过名空间+属性值名称获取属性值
        mDeson = attrs.getAttributeValue(NAMESPACE,"deson");
        mDesoff = attrs.getAttributeValue(NAMESPACE,"desoff");
        mDestitle = attrs.getAttributeValue(NAMESPACE,"destitle");

        }

    /**
     * 判断是否开启的方法
    *@param:
    *@return:返回当前SettingItemView是否选中状态，true开启 false关闭
    */
    public boolean isCheck(){
            //由checkbox的选中结果，决定当前条目是否开启
            return cb_box.isChecked();
    }

    /**
     *@param: isCheck 是否作为开启的变量，由点击过程中去做传递
     *@return:
     */
    public void setCheck(boolean isCheck){

        cb_box.setChecked(isCheck);
        if (isCheck){
            tv_des.setText(mDeson);
        }else {
            tv_des.setText(mDesoff);
        }
    }
}
