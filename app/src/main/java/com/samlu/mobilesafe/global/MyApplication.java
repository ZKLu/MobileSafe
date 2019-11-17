package com.samlu.mobilesafe.global;

import android.app.Application;
import android.os.Environment;

import java.io.Externalizable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by sam lu on 2019/11/17.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //捕获全局异常，可以捕获应用内所有模块的异常
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                //获取到未捕获的异常后，处理的方法
                e.printStackTrace();

                //将捕获到的异常存到sd卡中
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "error.log";
                File file = new File(path);
                try {
                    PrintWriter printWriter = new PrintWriter(file);
                    e.printStackTrace(printWriter);
                    printWriter.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                //将错误日志上传到公司服务器
                //结束应用
                System.exit(0);
            }
        });
    }
}
