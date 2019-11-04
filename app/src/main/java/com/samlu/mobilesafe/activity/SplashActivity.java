package com.samlu.mobilesafe.activity;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.common.Callback;
import org.xutils.http.*;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.utils.ConstantValue;
import com.samlu.mobilesafe.utils.SpUtil;
import com.samlu.mobilesafe.utils.StreamUtil;
import com.samlu.mobilesafe.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class SplashActivity extends AppCompatActivity {

    private String mVersionDes;
    private String mDownloadUrl;
    private TextView tv_version_name;
    private int localVersionCode;
    protected static final String TAG ="SplashActivity";

    /**
     * 更新新版本的状态码
     */
    private static final int UPDATE_VERSION = 100;
    /**
     * 进入应用程序主界面的状态码
     */
    private static final int ENTER_HOME = 101;
    /**
     * url地址出错的状态码
     */
    private static final int URL_ERROR = 102;
    /**
     * IO出错的状态码
     */
    private static final int IO_ERROR = 103;
    /**
     * JSON解析出错的状态码
     */
    private static final int JSON_ERROR = 104;
    //用于弹出对话框
    private Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_VERSION:
                    //弹出对话框，提示用户更新
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    //进入应用程序主界面
                    enterHome();
                    break;
                //出现以下三种异常都要使用户可以进入主界面继续使用功能
                case URL_ERROR:
                    //弹出对话框，提示用户更新
                    ToastUtil.show(getApplicationContext(),"url异常");
                    enterHome();
                    break;
                case IO_ERROR:
                    //弹出对话框，提示用户更新
                    ToastUtil.show(getApplicationContext(),"读取异常");
                    enterHome();
                    break;
                case JSON_ERROR:
                    //弹出对话框，提示用户更新
                    ToastUtil.show(getApplicationContext(),"JSON解析异常");
                    enterHome();
                    break;
            }
        }
    };
    private RelativeLayout rl;


    /**
     * 弹出对话框，提示用户更新
    *@param:
    *@return:
    */
    protected void showUpdateDialog() {
        //对话框是依赖于activity存在的，所以要用this作为参数
        //getApplicationContext()得到的是一个全文的上下文环境，不能指定某一个上下文环境
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("版本更新");
        //设置描述内容
        builder.setMessage(mVersionDes);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下载apk,断点续传
                downloadApk();
            }
        });

        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取消对话框，进入主界面
                enterHome();

            }
        });

        //点击取消事件
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //即使用户店家取消，也让用户进入主界面
                enterHome();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    protected void downloadApk() {

        //判断sd卡是否可用
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED));{
        //2获取sd卡的路径
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
        //3发送请求获取apk,并且放置到指定路径
        RequestParams params = new RequestParams(mDownloadUrl);
        params.setSaveFilePath(path);

        x.http().get(params, new Callback.ProgressCallback<File>() {

            @Override
            public void onSuccess(File result) {
                Log.i(TAG,"下载成功");
                installAPK(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                Log.i(TAG,"下载失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                Log.i(TAG,"下载完成");
            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }


            @Override
            /**
             *@param:total 下载apk大小 current 已经下载的大小 isDownloading 是否正在下载
             *@return:void
             */
            public void onLoading(long total, long current, boolean isDownloading) {
                Log.i(TAG, "current："+ current +"，total："+total);
                Log.i(TAG,"下载中");
            }
        });
    }
    }

    protected void installAPK(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        //startActivity(intent);
        //如果已下载安装包，不想安装，点击取消安装，也可以进入主界面。
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 进入应用程序的主界面
     */

    protected void enterHome() {

        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        //开启新的页面后，将splahactivity关闭，因为导航界面只可见一次
        finish();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除当前activity的标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        x.Ext.init(getApplication());
        x.Ext.setDebug(false);


        //初始化UI
        initUI();
        initData();
        //初始化动画
        initAnimation();
        //初始化数据库
        initDB();
    }

    private void initDB() {
        //归属地属地库拷贝过程
        initAddressDB("address.db");
    }

    /**
     * 拷贝数据库至files文件夹下
    *@param dbName 数据库名称
    *@return
    */
    private void initAddressDB(String dbName) {
        //在files文件夹下创建dbName数据库文件过程
        File files = getFilesDir();
        File file = new File(files, dbName);
        if (file.exists()){
            return;
        }
        InputStream open = null;
        FileOutputStream fos = null;
        //输入流读取/assets下的文件
        try {
            open = getAssets().open(dbName);
            //将读取的内容写入到指定文件夹中的文件中去
            fos = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int temp = -1;
            while ((temp = open.read(bytes))!=-1){
                fos.write(bytes,0,temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (open != null && fos != null){
                try {
                    open.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * 添加淡入动画效果
    *@param:
    *@return:
    */
    private void initAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setDuration(3000);
        rl.startAnimation(alphaAnimation);
    }

    /**
    * 获取数据方法
    * */
    private void initData() {
        //1、应用版本名称
        tv_version_name.setText(getVersionName());
        //检测是否用更新（本地版本号与服务器版本号比对），如果有，提示用户更新
        //2、获取本地版本号
        localVersionCode = getVersionCode();
        //3、获取服务器版本号（客户端发请求，服务端给响应）,用json
        //json中内容包括：
        /*更新版本的版本名称
        * 新版本的描述信息
        * 服务器版本号
        * 新版本apk的下载地址
        * json内容如下
        * {
        *   versionName:"2.0",
        *   versionDes:"2.0版本发布了，好帅",
        *   versionCode:"2",
        *   downloadUrl:"http://www.sdasd.com/sss.apk"
        * }
        * */
        if (SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE,false)){
        checkVersion();
        }else {
            //直接进入主界面
            //发送消息4s后去处理ENTER_HOME状态码指向的处理
            mHandler.sendEmptyMessageDelayed(ENTER_HOME,4000);
        }
    }

    /**
    * 检查版本号
    * */
    private void checkVersion() {
        new Thread(){
            @Override
            public void run() {
                //发送请求获取数据,参数为请求json的连接地址
                //http://10.18.56.99:90/update.json 测试阶段不是最优
                //仅限于模拟器访问电脑服务器
                Message msg = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {
                    //1、封装url地址
                    URL url = new URL("http://10.18.56.99:90/update.json");
                    //2、开启一个连接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //3、设置常见请求参数
                    //请求超时
                    connection.setConnectTimeout(2000);
                    //读取超时，已跟服务器连上，不能读取数据
                    connection.setReadTimeout(2000);
                    //默认是get请求
                    connection.setRequestMethod("GET");
                    //4、获取响应码
                    if (connection.getResponseCode()==200){
                        //5、以流的形式，将数据获取下来
                        InputStream is = connection.getInputStream();
                        //6、将流转换成字符串（工具类封装）
                        String json = StreamUtil.streamToString(is);
                        //7、json解析
                        JSONObject jsonObject = new JSONObject(json);
                        String versionName = jsonObject.getString("versionName");
                        mVersionDes = jsonObject.getString("versionDes");
                        String versionCode = jsonObject.getString("versionCode");
                        mDownloadUrl = jsonObject.getString("downloadUrl");
                        //8、对比版本号（服务器版本号>本地版本号，提示用户更新）
                        if(localVersionCode<Integer.parseInt(versionCode)){
                            //提示用户更新,弹出对话框,要用到消息机制
                            msg.what = UPDATE_VERSION;
                        }else {
                            //进入应用程序主界面
                            msg.what = ENTER_HOME;
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    msg.what = URL_ERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = IO_ERROR;
                } catch (JSONException e){
                    e.printStackTrace();
                    msg.what = JSON_ERROR;
                }finally {
                    //指定睡眠时间，请求网络的时长超过4s则不做处理
                    //请求网络的时长小于4s,强制让其睡满4s
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime<4000){
                        try {
                            Thread.sleep(4000-(endTime - startTime));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);

                }
            }
        }.start();
      /*  new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }});*/
    }

    /**
    * 返回版本号
    * @return 非0，则代表获取成功
    * */
    private int getVersionCode() {
        PackageManager pm = getPackageManager();
        //2、从包管理者对象中，获取指定包名的基本信息，传0代表获取基本信息
        try {
            PackageInfo packageInfo =  pm.getPackageInfo(getPackageName(),0);
            //3、获取版本号
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
    * 获取版本名称：清单名称中
    * @return 应用版本名称 返回null代表异常
    * */
    private String getVersionName() {
        //1、包管理者对象PackManager
        PackageManager pm = getPackageManager();
        //2、从包管理者对象中，获取指定包名的基本信息，传0代表获取基本信息
        try {
            PackageInfo packageInfo =  pm.getPackageInfo(getPackageName(),0);
            //3、获取版本名称
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
    * 初始化UI方法
    * */
    private void initUI() {
        rl = (RelativeLayout) findViewById(R.id.rl_root);
        tv_version_name = (TextView) findViewById(R.id.tv_version_name);
    }
}
