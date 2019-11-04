package com.samlu.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by sam lu on 2019/11/4.
 */

public class ServiceUtil {

    private static ActivityManager mAM;

    /**判断服务是否在运行
     * @param ctx 上下文环境
    *@param serviceName 判断是否正在运行的服务
    *@return true 运行 false 没有运行
    */
    public static boolean isRunning(Context ctx,String serviceName){
        //获取activityManager管理者对象，可以去获取当前手机正在运行的所有服务
        mAM = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //获取手机中正在运行的服务集合
        List<ActivityManager.RunningServiceInfo> runningServices = mAM.getRunningServices(100);
        //遍历获取所有服务的集合，拿到每一个服务的名称，和传递进来的类名做比较，如果相同，则说明服务正在运行
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices){
            if (serviceName.equals(runningServiceInfo.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}
