package com.samlu.mobilesafe.engine;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by sam lu on 2019/11/6.
 */

public class SmsBackUP {
    private static int index = 0;

    /**
    *@param path 备份文件的路径
    *@return 
    */
    public static void backup(Context ctx, String path, callBack callBack){
        FileOutputStream fos = null;
        Cursor cursor = null;
        try {
            //获取备份短信写入的文件
            File file = new File(path);
            //获取内容解析器
            cursor = ctx.getContentResolver().query(Uri.parse("content://sms/"),
                    new String[]{"address","date","type","body"},null,null,null);
            fos = new FileOutputStream(file);
            //序列化数据库中读取的数据，防止到xml中
            XmlSerializer newSerializer = Xml.newSerializer();
            //为xml做初始设置
            newSerializer.setOutput(fos,"utf-8");
            newSerializer.startDocument("utf-8",true);
            newSerializer.startTag(null,"smss");
            //备份短信总数
            callBack.setMax(cursor.getCount());

            //读取数据库中的每一行数据，写入到xml中
            while (cursor.moveToNext()){
                newSerializer.startTag(null,"sms");
                newSerializer.startTag(null,"address");
                newSerializer.text(cursor.getString(0));
                newSerializer.endTag(null,"address");

                newSerializer.startTag(null,"date");
                newSerializer.text(cursor.getString(1));
                newSerializer.endTag(null,"date");

                newSerializer.startTag(null,"type");
                newSerializer.text(cursor.getString(2));
                newSerializer.endTag(null,"type");

                newSerializer.startTag(null,"body");
                newSerializer.text(cursor.getString(3));
                newSerializer.endTag(null,"body");

                newSerializer.endTag(null,"sms");

                //每循环一次，更新进度条
                index++;
                Thread.sleep(500);
                //ProgressDialog可以在子线程中更新进度条
                callBack.setProgress(index);
            }
            newSerializer.endTag(null,"smss");
            newSerializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (fos != null && cursor != null){
                cursor.close();
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    public interface callBack{
        public void setMax(int max);
        public void setProgress(int progress);
    }
}
