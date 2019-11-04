package com.samlu.mobilesafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.engine.AddressDao;
import com.samlu.mobilesafe.utils.ConstantValue;
import com.samlu.mobilesafe.utils.SpUtil;

/**
 * Created by sam lu on 2019/11/4.
 */
public class AddressService extends Service{

    private TelephonyManager mTM;
    private MyPhoneStateListener mPhoneStateListener;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private View mViewToast;
    private WindowManager mWM;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            tv_toast.setText(mAddress);
        }
    };
    private String mAddress;
    private TextView tv_toast;
    private int[] mDrawableIds;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //第一次开启服务后，需要去管理Toast的显示
        //电话状态的监听（服务开启的时候需要监听，服务关闭的时候不需要监听）
        //电话管理者对象
        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //监听电话状态
        mPhoneStateListener = new MyPhoneStateListener();
        mTM.listen(mPhoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);
        //获取窗体对象
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        //取消对电话状态的监听（开启服务的时候监听电话的对象）
        if (mTM != null && mPhoneStateListener !=null){
            mTM.listen(mPhoneStateListener,PhoneStateListener.LISTEN_NONE);
        }
        super.onDestroy();
    }

    class MyPhoneStateListener extends PhoneStateListener{
        //手动重写，电话状态发生改变会触发的方法

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_IDLE:
                    //空闲状态,移除Toast
                    //挂断电话时，从窗体上移除Toast
                    if (mWM !=null && mViewToast != null){
                        mWM.removeView(mViewToast);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机状态
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //响铃状态，展示Toast
                    showToast(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private void showToast(String incomingNumber) {

        //自定义Toast，去Toast的构造方法寻找
        final WindowManager.LayoutParams params = mParams;

        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;

        params.format = PixelFormat.TRANSLUCENT;

        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        //定义Toast的位置
        params.gravity = Gravity.LEFT + Gravity.TOP;
        //定义Toast的显示效果,需要将Toast挂载到windowManager窗体上
        mViewToast = View.inflate(this, R.layout.toast_view, null);
        tv_toast = mViewToast.findViewById(R.id.tv_toast);

        //从SharePreference中获取色值文字的索引，匹配图片
        //"透明","橙色","蓝色","灰色","绿色"
        mDrawableIds = new int[]{R.drawable.call_locate_white,R.drawable.call_locate_orange,
        R.drawable.call_locate_blue,R.drawable.call_locate_gray,
        R.drawable.call_locate_green};
        int toastStyleIndex = SpUtil.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE,0);
        tv_toast.setBackgroundResource(mDrawableIds[toastStyleIndex]);

        //需要添加权限android.permission.SYSTEM_ALERT_WINDOW
        mWM.addView(mViewToast,mParams);

        //获取了来电号码，需要做来电号码归属地查询
        query(incomingNumber);

    }
    private void query(final String inComingPhone){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAddress = AddressDao.getAddress(inComingPhone);
                mHandler.sendEmptyMessage(0);
            }
        });
    }
}
