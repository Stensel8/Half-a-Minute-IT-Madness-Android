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

    //adding users to the db
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

    //check if the user exists so we can log in
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

//    public Cursor getItemId(String name){
//        SQLiteDatabase db = this.getWritableDatabase();
//        String query = "SELECT" + COL_ID + " FROM " + TABLE_USER + " WHERE " + COL_USERNAME + " = '" + name + "'";
//        Cursor data = db.rawQuery(query,null);
//        return data;
//    }

    //check if the username is already taken
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

    //initialize the profile with its username and score
    public void initProfile(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USERNAME_PROFILE,username);
        contentValues.put(COL_SCORE_PROFILE,0);
        long result = db.insert(TABLE_PROFILE,null,contentValues);
    }

    //updates the score in the Profile table
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

    //adds the score to the High-score table
    public boolean addScore(String username, String difficulty, String chosenGame, int score){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USERNAME_HSCORE,username);
        contentValues.put(COL_HSCORE_DIFFICULTY,difficulty);
        contentValues.put(COL_chosenG_HSCCORE,chosenGame);
        contentValues.put(COL_SCORE,score);
        long result;

        //we don't save the score value if it is equal to 0
        if(score != 0)
            result = db.insert(TABLE_HSCORE,null,contentValues);
        else
            result = 1;

        if(result==-1){
            return false;
        }else{
            return true;
        }
    }

    //gets the 5 best scores of the High-score table
    public Cursor getHScore(String difficulty, String chosenGame){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL_USERNAME_HSCORE + ", " + COL_SCORE + " FROM " + TABLE_HSCORE + " WHERE " + COL_HSCORE_DIFFICULTY + " = '" + difficulty + "' AND " + COL_chosenG_HSCCORE + " = '" + chosenGame + "' ORDER BY " + COL_SCORE + " DESC LIMIT 5";
        Cursor res = db.rawQuery(query,null);

        return res;
    }

    //search all the users where the username contains the String "friendsUsername"
    public Cursor searchFriend(String username, String friendsUsername){
        SQLiteDatabase db = this.getWritableDatabase();
        //It will not select the logged user
        String query = "SELECT " + COL_USERNAME + " FROM " + TABLE_USER + " WHERE " + COL_USERNAME + " != '" + username + "' AND " + COL_USERNAME + " LIKE '%" + friendsUsername +"%'";
        Cursor res = db.rawQuery(query,null);

        return res;
    }


    public boolean addFriend(String username, String friendsUsername) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_FRIEND_USERNAME,username);
        contentValues.put(COL_FRIENDS,friendsUsername);

        long result1 = db.insert(TABLE_FRIEND,null,contentValues);

        contentValues.put(COL_FRIEND_USERNAME,friendsUsername);
        contentValues.put(COL_FRIENDS,username);
        long result2 = db.insert(TABLE_FRIEND,null,contentValues);

        if(result1==-1 || result2==-1){
            return false;
        }else{
            return true;
        }
    }

    //check if 2 users are friends
    public boolean checkFriend(String username, String friendsUsername){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COL_FRIENDS + " FROM " + TABLE_FRIEND + " WHERE (" + COL_FRIEND_USERNAME + " = '" + username + "' AND " + COL_FRIENDS + " = '" + friendsUsername + "') OR (" + COL_FRIENDS + " = '" + username + "' AND " + COL_FRIEND_USERNAME + " = '" + friendsUsername + "')";
        Cursor data = db.rawQuery(query,null);
        int count = data.getCount();

        if(count > 0)
            return true;
        else
            return false;
    }

    //returns all the friends of a user
    public Cursor showFriends(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL_FRIENDS + " FROM " + TABLE_FRIEND  + " WHERE " + COL_FRIEND_USERNAME + " = '" + username + "'";
        Cursor res = db.rawQuery(query,null);

        return res;
    }
}
