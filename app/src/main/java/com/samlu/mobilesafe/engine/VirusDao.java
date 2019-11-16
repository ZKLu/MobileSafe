package com.samlu.mobilesafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sam lu on 2019/11/3.
 */

public class VirusDao {
    public static String path ="data/data/com.samlue.mobilesafe/files/antivirus.db";
    public static List<String> getVirusList(){
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("datable", new String[]{"md5"}, null, null, null, null, null);
        List<String> virusList = new ArrayList<>();
        while (cursor.moveToNext()){
            virusList.add(cursor.getString(0)) ;
        }
        cursor.close();
        db.close();
        return virusList;
    }
}
