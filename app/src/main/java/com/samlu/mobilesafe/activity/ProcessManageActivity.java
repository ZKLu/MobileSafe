package com.samlu.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.samlu.mobilesafe.R;
import com.samlu.mobilesafe.engine.ProcessInfoProvider;

/**
 * Created by sam lu on 2019/11/10.
 */
public class ProcessManageActivity extends Activity implements View.OnClickListener{

    private TextView tv_process_count,tv_memory_info;
    private ListView lv_process_list;
    private Button bt_select_all,bt_reverse,bt_clean,bt_setting;
    private int mProcessCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manage);

        initUI();
        initTitleData();
    }

    private void initTitleData() {
        mProcessCount = ProcessInfoProvider.getProcssCount(this);
        tv_process_count.setText("进程总数："+mProcessCount);

        //获取可用内存大小，并且格式化
        long availableSpace = ProcessInfoProvider.getAvailableSpace(this);
        String strAvailebleSpace = Formatter.formatFileSize(this, availableSpace);

        //获取总内存大小，并且格式化
        long totalleSpace = ProcessInfoProvider.getTotalleSpace(this);
        String strTotalSpace = Formatter.formatFileSize(this, totalleSpace);

        tv_memory_info.setText("剩余/总共："+strAvailebleSpace+"/"+strAvailebleSpace);
    }

    private void initUI() {
        tv_process_count = findViewById(R.id.tv_process_count);
        tv_memory_info = findViewById(R.id.tv_memory_info);
        lv_process_list = findViewById(R.id.lv_process_list);
        bt_select_all = findViewById(R.id.bt_select_all);
        bt_reverse = findViewById(R.id.bt_reverse);
        bt_clean = findViewById(R.id.bt_clean);
        bt_setting = findViewById(R.id.bt_setting);

        bt_select_all.setOnClickListener(this);
        bt_reverse.setOnClickListener(this);
        bt_clean.setOnClickListener(this);
        bt_setting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
