package com.samlu.mobilesafe.db.domin;

import android.graphics.drawable.Drawable;

/**
 * Created by sam lu on 2019/11/10.
 */

public class ProcessInfo {
    public String name;//应用名称

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMemSize() {
        return memSize;
    }

    public void setMemSize(long memSize) {
        this.memSize = memSize;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable icon;//应用图标
    public long memSize;//应用已使用的内存书
    public boolean isCheck;
    public boolean isSystem;
    public String packageName; //如果服务没有名称，则将其所在应用的包名作为名称

}
