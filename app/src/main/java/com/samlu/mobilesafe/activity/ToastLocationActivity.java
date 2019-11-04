package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.utils.ConstantValue;
import com.samlu.mobilesafe.utils.SpUtil;

/**
 * Created by sam lu on 2019/11/4.
 */
public class ToastLocationActivity extends Activity{

    private ImageView iv_drag;
    private Button bt_top;
    private Button bt_bottom;
    private int mScreenHeight;
    private int mScreenWidth;
    private long[] mHits = new long[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_location);
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mScreenHeight = manager.getDefaultDisplay().getHeight();
        mScreenWidth = manager.getDefaultDisplay().getWidth();
        initUI();
    }

    private void initUI() {
        iv_drag = findViewById(R.id.iv_drag);
        bt_top = findViewById(R.id.bt_top);
        bt_bottom = findViewById(R.id.bt_bottom);

        int locationX = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_X,0);
        int locationY = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_Y,0);
        //父布局是RelativeLayout，所以其所在位置的规则需要有相对布局提供
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //将左上角的坐标作用iv_drag对应的规则参数上
        layoutParams.leftMargin = locationX;
        layoutParams.topMargin = locationY;
        //将以上规则作用在iv_drag
        iv_drag.setLayoutParams(layoutParams);

        if (locationY>mScreenHeight/2){
            bt_bottom.setVisibility(View.INVISIBLE);
            bt_top.setVisibility(View.VISIBLE);
        }else {
            bt_top.setVisibility(View.INVISIBLE);
            bt_bottom.setVisibility(View.VISIBLE);
        }

        //监听某一个控件的拖拽过程（按下，移动，抬起）
        iv_drag.setOnTouchListener(new View.OnTouchListener() {

            private int startY;
            private int startX;

            //对不同的动作做不同的处理
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();

                        int disX =  moveX - startX;
                        int disY = moveY - startY;

                        //getLeft()返回这个view的左边到父view的距离，参照物时左上角
                        int left = iv_drag.getLeft()+disX;//移动后左侧坐标
                        //getTop();返回这个view的上边到父view的距离
                        int top = iv_drag.getTop()+disY;//移动后顶端坐标
                        int right = iv_drag.getRight()+disX;//移动后右侧坐标
                        int bottom = iv_drag.getBottom()+disY;//移动后底部坐标

                        //容错处理(iv_drag不能超出屏幕)
                        if (left<0){
                            return true;
                        }
                        if (right>mScreenWidth){
                            return true;
                        }
                        if (top<0){
                            return true;
                        }
                        //22是通知栏的高度
                        if (bottom>mScreenHeight -22 ){
                            return true;
                        }

                        if (top>mScreenHeight/2){
                            bt_bottom.setVisibility(View.INVISIBLE);
                            bt_top.setVisibility(View.VISIBLE);
                        }else {
                            bt_top.setVisibility(View.INVISIBLE);
                            bt_bottom.setVisibility(View.VISIBLE);
                        }

                        //告知控件，按算出来的坐标去移动
                        iv_drag.layout(left,top,right,bottom);

                        //重置起始坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_X,iv_drag.getLeft());
                        SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_Y,iv_drag.getTop());

                        break;


                }
                //在当前情况下，返回false是不响应移动事件，返回true是响应事件
                //既要响应点击事件，又要响应拖拽事件，需要修改返回结果为false
                return false;
            }
        });

        //双击居中
        iv_drag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits,1,mHits,0,mHits.length-1);
                mHits[mHits.length-1] = SystemClock.uptimeMillis();
                if (mHits[mHits.length-1]-mHits[0] < 500){
                    int left = mScreenWidth/2-iv_drag.getWidth()/2;
                    int top = mScreenHeight/2-iv_drag.getHeight()/2;
                    int right = mScreenWidth/2+iv_drag.getWidth()/2;
                    int bottom = mScreenHeight/2+iv_drag.getHeight()/2;

                    iv_drag.layout(left,top,right,bottom);
                }
            }
        });
    }
}
