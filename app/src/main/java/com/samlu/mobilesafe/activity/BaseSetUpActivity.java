package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.samlu.mobilesafe.R;

/**
 * Created by sam lu on 2019/11/2.
 */
public abstract class BaseSetupActivity extends Activity{

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gestureDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //监听手势的移动
                if ((e1.getX()-e2.getX())>0){
                    //又右向左，移动到下一页
                    //调用子类的下一页方法，抽象方法
                    //在第一个界面上使用，跳转到第二个页面
                    //在第二个界面上使用，跳转到第三个页面
                    showNextPage();
                }
                if ((e1.getX()-e2.getX())<0){
                    //由左向右，移动到上一页
                    //调用子类的上一页方法
                    //在第一个界面上使用，无响应
                    //在第二个界面上使用，跳转到第一个页面
                    //在第三个界面上使用，跳转到第二个页面
                    showPrePage();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }


    public void nextPage(View view){
        //调用抽象方法
        showNextPage();
    }
    public void prePage(View view){
        showPrePage();
    }

    //上一页的抽象方法，由子类决定跳转到哪一页
    protected abstract void showPrePage();
    //下一页的抽象方法，由子类决定跳转到哪一页
    protected abstract void showNextPage() ;

    //监听屏幕上响应的事件类型（按下，移动（多次），抬起）
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //通过手势处理类，接受多种类型的事件，用作处理
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


}
