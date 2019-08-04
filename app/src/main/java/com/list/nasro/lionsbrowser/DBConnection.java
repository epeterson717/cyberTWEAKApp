/*
 Code for Browser Activity modified from "simple_web_browser_app"
 Created by Red Ayoub (redayoub47) on 5/24/18.
 https://github.com/redayoub47/simple_web_browser_app
 */

package com.list.nasro.lionsbrowser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class DBConnection {
    DBinfo my_db;

    public DBConnection(Context context) {
        my_db = new DBinfo(context);
    }

    public WebPage dataInsert(String url) {
        SQLiteDatabase sqLiteDatabase = my_db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBinfo.url, url);
        long id = sqLiteDatabase.insert(DBinfo.TB_NAME, null, contentValues);
        sqLiteDatabase.close();
        return new WebPage((int) id, url);
    }

    public ArrayList<WebPage> getData() {

        SQLiteDatabase sqLiteDatabase = my_db.getReadableDatabase();
        ArrayList<WebPage> arrayList = new ArrayList<>();
        String[] cols = {DBinfo.id, DBinfo.url};
        Cursor cursor = sqLiteDatabase.query(DBinfo.TB_NAME, cols, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String url = cursor.getString(1);
            arrayList.add(new WebPage(id, url));
        }

        sqLiteDatabase.close();

        return arrayList;
    }


    public int updateData(String id, WebPage newInfo) {
        SQLiteDatabase sqLiteDatabase = my_db.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DBinfo.url, newInfo.getUrl());
        String[] whereArgs = {id};
        int count = sqLiteDatabase.update(DBinfo.TB_NAME, contentValues, DBinfo.id + " =?", whereArgs);
        sqLiteDatabase.close();
        return count;
    }

    public int deleteData(String id) {
        SQLiteDatabase sqLiteDatabase = my_db.getReadableDatabase();
        String[] whereArgs = {id};
        int count = sqLiteDatabase.delete(DBinfo.TB_NAME, DBinfo.id + " =?", whereArgs);
        sqLiteDatabase.close();
        return count;
    }


    public void clearData() {
        SQLiteDatabase sqLiteDatabase = my_db.getReadableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + DBinfo.TB_NAME);
        sqLiteDatabase.close();
    }


    static class DBinfo extends SQLiteOpenHelper {
        private static final String DB_NAME = "my_db";
        private static final String TB_NAME = "web_pages";
        private static final int DB_Ver = 2;
        private static final String id = "_ID";
        private static final String url = "url";
        private static final String DROP_TB = "DROP TABLE IF EXISTS " + TB_NAME;
        private static final String CREATE_TB = "CREATE TABLE " + TB_NAME + " ( " +
                id + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                url + " VARCHAR(500) ); ";

        private Context context;

        public DBinfo(Context context) {
            super(context, DB_NAME, null, DB_Ver);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            try {
                sqLiteDatabase.execSQL(CREATE_TB);
            } catch (SQLException e) {
                Log.d("Create Error", e.getMessage());
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVer, int newVer) {
            try {
                sqLiteDatabase.execSQL(DROP_TB);
                onCreate(sqLiteDatabase);

            } catch (SQLException e) {
                Log.d("Drop Error", e.getMessage());
            }

        }
    }
}
