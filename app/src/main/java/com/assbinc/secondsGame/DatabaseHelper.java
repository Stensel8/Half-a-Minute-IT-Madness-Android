package com.assbinc.secondsGame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME="secondsGame.db";

    public static final String TABLE_USER ="user";
    public static final String COL_ID ="ID";
    public static final String COL_USERNAME ="username";
    public static final String COL_PWD ="password";

    public static final String TABLE_HSCORE ="highScore";
    public static final String COL_HSCORE_ID ="ID";
    public static final String COL_USERNAME_HSCORE ="HScoreUsername";
    public static final String COL_SCORE_EASY ="easyScore";
    public static final String COL_SCORE_MEDIUM ="mediumScore";
    public static final String COL_SCORE_HARD ="hardScore";
    public static final String COL_GAME_HSCCORE ="HScoreChosenGame";

    public static final String TABLE_PROFILE ="profile";
    public static final String COL_PROFILE_ID ="ID";
    public static final String COL_USERNAME_PROFILE ="username";
    public static final String COL_SCORE_PROFILE ="profileScore";

    public static final String TABLE_FRIEND ="hardHighScore";
    public static final String COL_FRIEND_ID ="ID";
    public static final String COL_FRIEND_USERNAME ="username";
    public static final String COL_FRIENDS ="myFriends";

    public DatabaseHelper( Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE user (ID Integer PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_USER);
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
        String[] columns = {COL_ID};
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_USERNAME + "=?" + " and " + COL_PWD + "=?";
        String[] selectionArgs = { username, password };
        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs,null, null,null);
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
        String query = "SELECT" + COL_ID + " FROM " + TABLE_USER + " WHERE " + COL_USERNAME + " = '" + name + "'";
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public boolean checkMultipleUsername(String name){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COL_USERNAME + " FROM " + TABLE_USER + " WHERE " + COL_USERNAME + " = '" + name + "'";
        Cursor data = db.rawQuery(query,null);
        int count = data.getCount();

        if(count > 0)
            return true;
        else
            return false;

    }
}
