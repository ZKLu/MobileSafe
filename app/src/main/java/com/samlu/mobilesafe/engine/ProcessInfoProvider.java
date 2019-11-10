package com.samlu.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Debug;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.db.domin.ProcessInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.ActivityManager.*;

/**
 * Created by sam lu on 2019/11/10.
 */

public class ProcessInfoProvider {
    private FileReader fileReader;

    //获取进程总数
    public static int getProcssCount(Context ctx){
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //获取正在运行进程的集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        return runningAppProcesses.size();
    }


    /**获取剩余内存大小
    *@param
    *@return 返回可用的内存数，单位是bytes
    */
    public static long getAvailableSpace(Context ctx){
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //构建存储可用内存的对象
        MemoryInfo memoryInfo = new MemoryInfo();
        //给指定memoryInfo对象赋值
        am.getMemoryInfo(memoryInfo);
        //获取memoryInfo中可用内存大小
        return memoryInfo.availMem;
    }

    /**返回设备的内存书
    *@param
    *@return
    */
    public static long getTotalleSpace(Context ctx){
        FileReader fileReader =null;
        BufferedReader bufferedReader =null;
        //方法一
       /* ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //构建存储可用内存的对象
        MemoryInfo memoryInfo = new MemoryInfo();
        //给指定memoryInfo对象赋值
        am.getMemoryInfo(memoryInfo);
        //获取memoryInfo中可用内存大小
        return memoryInfo.totalMem;*/
        //方法二
        //设备的配置信息都写入一个文件/proc/meminfo中，读取文件获得信息
        try {
            fileReader =  new FileReader("proc/meminfo");
            bufferedReader = new BufferedReader(fileReader);
            String lintOne = bufferedReader.readLine();
            //将字符串转换成字符的数组
            char[] charArray = lintOne.toCharArray();
            //循环遍历每一个字符，如果字符的ascii码在0~9的区域内，说明此字符有效
            StringBuffer stringBuffer = new StringBuffer();
            for (char c:charArray){
                if (c>='0' && c<='9'){
                    stringBuffer.append(c);
                }
            }
            return Long.parseLong(stringBuffer.toString())*1024;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (fileReader !=null && bufferedReader !=null){
                try {
                    fileReader.close();
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return 0;
    }

    /**
    *@param
    *@return 当前手机正在运行的进程信息列表
    */
    public static List<ProcessInfo> getProcessInfo(Context ctx){
        List<ProcessInfo> processInfoList = new ArrayList<ProcessInfo>();
        //获取进程相关信息
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //获取正在运行的进程的集合
        List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        PackageManager pm = ctx.getPackageManager();
        //循环集合，获取进程相关信息
        for (RunningAppProcessInfo info:runningAppProcesses){
            ProcessInfo processInfo = new ProcessInfo();
            processInfo.packageName = info.processName;
            //数组中索引位置为0的对象，为当i前进程的内存信息的对象
            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
            Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
            //获取已使用内存的大小
            processInfo.memSize = memoryInfo.getTotalPrivateDirty()*1024;

            try {
                //获取应用的名称
                ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.packageName, 0);
                processInfo.name = applicationInfo.loadLabel(pm).toString();
                //获取应用图标
                processInfo.icon = applicationInfo.loadIcon(pm);
                //判断是否为系统进程
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM){
                    processInfo.isSystem = true;
                }else {
                    processInfo.isSystem = false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                //找不到应用名，很大概率为系统应用
                processInfo.name = info.processName;
                processInfo.icon = ctx.getResources().getDrawable(R.mipmap.ic_launcher);
                processInfo.isSystem = true;
                e.printStackTrace();
            }
        processInfoList.add(processInfo);
        }
        return processInfoList;
    }
}
