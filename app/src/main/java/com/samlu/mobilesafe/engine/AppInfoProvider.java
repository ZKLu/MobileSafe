package com.samlu.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.samlu.mobilesafe.db.domin.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sam lu on 2019/11/6.
 */

public class AppInfoProvider {
    /**获取安装在手机上应用相关信息集合
    *@param
    *@return 包含了安装在手机上应用相关信息的集合
    */
    public static List<AppInfo> getAppInfoList(Context ctx){
        List<AppInfo> appInfos = new ArrayList<>();
        //获取包的管理者对象
        PackageManager manager = ctx.getPackageManager();
        //获取安装在手机上的应用集合
        List<PackageInfo> installedPackages= manager.getInstalledPackages(0);
        for (PackageInfo packageInfo : installedPackages){
            AppInfo appInfo = new AppInfo();
            appInfo.packageName = packageInfo.packageName;
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            //applicationInfo.uid 每个应用在手机里的唯一性标识
            appInfo.name = applicationInfo.loadLabel(manager).toString()+applicationInfo.uid;
            appInfo.icon = applicationInfo.loadIcon(manager);
            //判断是否系统应用
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM){
                appInfo.isSystem = true;
            }else {
                appInfo.isSystem = false;
            }
            if ((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE){
                appInfo.isSdCard = true;
            }else {
                appInfo.isSdCard = false;
            }
            appInfos.add(appInfo);
        }
        return appInfos;
    }
}
