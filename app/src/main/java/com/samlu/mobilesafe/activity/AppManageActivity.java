package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.db.domin.AppInfo;
import com.samlu.mobilesafe.engine.AppInfoProvider;

import java.util.List;

/**
 * Created by sam lu on 2019/11/6.
 */
public class AppManageActivity extends Activity{

    private ListView lv_app_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        initTitle();
        initListView();
    }

    private void initListView() {
        lv_app_list = findViewById(R.id.lv_app_list);

        List<AppInfo> appInfoList = AppInfoProvider.getAppInfoList(this);
    }

    private void initTitle() {
        //获取内部存储可用大小
        //内部存储的路径
        String path = Environment.getDataDirectory().getAbsolutePath();
        //获取sd卡可用大小
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //获取以上两个路径的可用大小,单位为bytes
        long space = getAvailableSpace(path);
        long sdSpace = getAvailableSpace(sdPath);
        //将结果格式化
        String strSpace = Formatter.formatFileSize(this, space);
        String strSdSpace = Formatter.formatFileSize(this,sdSpace);

        TextView tv_memory = findViewById(R.id.tv_memory);
        TextView tv_sd_memory = findViewById(R.id.tv_sd_memory);

        tv_memory.setText("磁盘可用："+strSpace);
        tv_sd_memory.setText("sd卡可用："+strSdSpace);
    }

    /**返回可用磁盘大小
    *@param
    *@return 返回值用long类型，因为int的最大值换算成2G
    */
    private long getAvailableSpace(String path) {
        StatFs statFs = new StatFs(path);
        //获取可用区块的个数
        long availableBlocks = statFs.getAvailableBlocksLong();
        //获取区块的大小
        long blockSize = statFs.getBlockSizeLong();
        //区块大小*可用区块个数 = 可用空间大小
        return availableBlocks*blockSize;
    }
}
