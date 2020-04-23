package com.assbinc.secondsGame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME="secondsGame.db";
    public static final String TABLE_NAME="user";
    public static final String COL_1="ID";
    public static final String COL_2="username";
    public static final String COL_3="password";

    public DatabaseHelper( Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE user (ID Integer PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long addUser(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        long res = db.insert("user", null, contentValues);
        db.close();

        return res;
    }

    public boolean checkUser(String username, String password){
        String[] columns = { COL_1 };
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_2 + "=?" + " and " + COL_3 + "=?";
        String[] selectionArgs = { username, password };
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs,null, null,null);
        int count = cursor.getCount();

        cursor.close();
        db.close();

        if (count > 0)
            return true;
        else
            return false;
    }

    public Cursor getItemId(String name){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT" + COL_1 + " FROM " + TABLE_NAME + " WHERE " + COL_2 + " = '" + name + "'";
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public boolean checkMultipleUsername(String name){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COL_2 + " FROM " + TABLE_NAME + " WHERE " + COL_2 + " = '" + name + "'";
        Cursor data = db.rawQuery(query,null);
        int count = data.getCount();

        if(count > 0)
            return true;
        else
            return false;

    }
}
