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
    public static final String COL_HSCORE_ID ="HScoreID";
    public static final String COL_USERNAME_HSCORE ="HScoreUsername";
    public static final String COL_HSCORE_DIFFICULTY ="HScoreDifficulty";
    public static final String COL_SCORE ="HScore";
    public static final String COL_chosenG_HSCCORE ="HScoreChosenGame";

    public static final String TABLE_PROFILE ="profile";
    public static final String COL_PROFILE_ID ="profileID";
    public static final String COL_USERNAME_PROFILE ="profileUsername";
    public static final String COL_SCORE_PROFILE ="profileScore";

    public static final String TABLE_FRIEND ="friends";
    public static final String COL_FRIEND_ID ="friendsID";
    public static final String COL_FRIEND_USERNAME ="friendsUername";
    public static final String COL_FRIENDS ="myFriends";

    public DatabaseHelper( Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String tableUser = "CREATE TABLE "+ TABLE_USER + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)";
        String tableHighScore = "CREATE TABLE "+ TABLE_HSCORE + " (HScoreID Integer PRIMARY KEY AUTOINCREMENT, HScoreUsername TEXT, HScoreDifficulty TEXT, HScore Integer, HScoreChosenGame TEXT)";
        String tableProfile = "CREATE TABLE "+ TABLE_PROFILE + " (profileID INTEGER PRIMARY KEY AUTOINCREMENT, profileScore INTEGER, profileUsername TEXT)";
        String tableFriends = "CREATE TABLE "+ TABLE_FRIEND + " (friendsID INTEGER PRIMARY KEY AUTOINCREMENT, friendsUername TEXT, myFriends TEXT)";
        db.execSQL(tableUser);
        db.execSQL(tableHighScore);
        db.execSQL(tableProfile);
        db.execSQL(tableFriends);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_PROFILE);
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_HSCORE);
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_FRIEND);
        onCreate(db);
    }

    public long addUser(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USERNAME, username);
        contentValues.put(COL_PWD, password);
        initProfile(username);
        long res = db.insert(TABLE_USER, null, contentValues);
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
        SQLiteDatabase db = this.getWritableDatabase();
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

    public Cursor displayProfileData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_PROFILE,null);
        return res;
    }

    public void initProfile(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USERNAME_PROFILE,username);
        contentValues.put(COL_SCORE_PROFILE,0);
        long result = db.insert(TABLE_PROFILE,null,contentValues);
    }

    public void updateScoreProfile(String username, int score){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE "+ TABLE_PROFILE + " SET profileScore= '" + score + "' WHERE profileUsername= '"+ username +"'");
        //long result = db.insert(TABLE_PROFILE,null,null);
    }

    public int getProfileScore(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL_SCORE_PROFILE + " FROM " + TABLE_PROFILE + " WHERE " + COL_USERNAME_PROFILE + " = '" + username + "'";
        Cursor data = db.rawQuery(query,null);
        data.moveToFirst();
        return data.getInt(0);
    }

    public boolean addScore(String username, String difficulty, String chosenGame, int score){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USERNAME_HSCORE,username);
        contentValues.put(COL_HSCORE_DIFFICULTY,difficulty);
        contentValues.put(COL_chosenG_HSCCORE,chosenGame);
        contentValues.put(COL_SCORE,score);

        long result = db.insert(TABLE_HSCORE,null,contentValues);

        if(result==-1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getHScore(String difficulty, String chosenGame){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL_USERNAME_HSCORE + ", " + COL_SCORE +" FROM " + TABLE_HSCORE + " WHERE " + COL_HSCORE_DIFFICULTY + " = '" + difficulty + "' AND " + COL_chosenG_HSCCORE + " = '" + chosenGame + "'";
        Cursor res = db.rawQuery(query,null);

        return res;
    }
}
