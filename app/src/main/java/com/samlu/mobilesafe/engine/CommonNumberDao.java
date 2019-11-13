package com.samlu.mobilesafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sam lu on 2019/11/3.
 */

public class CommonNumberDao {

    //指定访问数据库的路径
    public static String path ="data/data/com.samlue.mobilesafe/files/commonnum.db";
    private List<Group> list;

    public List<Group> getGroup(){
        SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = database.query("classlist", new String[]{"name", "idx"}, null, null, null, null, null);
        list = new ArrayList<>();
        while (cursor.moveToNext()){
            Group group = new Group();
            group.name = cursor.getString(0);
            group.idx = cursor.getString(1);
            group.childList = getChild(group.idx);
            list.add(group);
        }
        cursor.close();
        database.close();
        return list;
    }
    //获取每个组中节点的值
    public List<Child> getChild(String idx){
        SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = database.rawQuery("select * from table" + idx + ";", null);
        List<Child> childList = new ArrayList<>();
        while (cursor.moveToNext()){
            Child child =new Child();
            child._id = cursor.getString(0);
            child.name = cursor.getString(1);
            child.number = cursor.getString(2);
            childList.add(child);
        }
        cursor.close();
        database.close();
        return childList;
    }

    public class  Group{
        public String name;
        public String idx;
        public List<Child> childList;
    }
    public class Child{
        public String name;
        public String _id;
        public String number;
    }
}
