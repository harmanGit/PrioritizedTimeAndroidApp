package com.cs345.harmanjeetdhillon.prioritizedtime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Set;
import java.util.TreeSet;

/**
 * Class is used to create, update, and remove items from a database. Class creates a database
 * in which stores whitelisted apps and score history.
 *
 * @author harmanjeet dhillon
 */

public class DatabaseAid extends SQLiteOpenHelper {
    //database the app uses to store content
    public static final String DATABASE_NAME = "prioritizeTable.db";
    public static final String TABLE_NAME = "sourceData";//table name
    public static final String Col_1 = "appAllowed";//whitelisted apps
    public static final String Col_2 = "scoreAdd";//add score used in the user history view
    public static final String Col_3 = "scoreSub";//sub score used in the user history view

    public DatabaseAid(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //creating the table
        db.execSQL("create table " + TABLE_NAME + "(" + Col_1 + " VARCHAR," +
                " " + Col_2 + " INTEGER, " + Col_3 + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        //dropping the table if it already exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    /**
     * Method adds apps that need to be whitelisted to the database.
     * @param appName string representing the app name
     * @return boolean true if the app was added and a false if it wasn't
     */
    public boolean insertApp(String appName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Col_1, appName); //adding the appName to the content value
        //adding app name to the user name
        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1)
            return false;
        else
            return true;
    }

    /**
     * Method is used to delete a whitelisted app from the database
     * @param appName string name of the app to delete
     * @return boolean returns true if the app was deleted else false
     */
    public boolean deleteApp(String appName) {
        SQLiteDatabase db = this.getWritableDatabase();
        //deleting the app from the database
        long result = db.delete(TABLE_NAME, Col_1 + "= ?", new String[] {appName});
        if (result == -1)
            return false;
        else
            return true;

    }

    /**
     * Method adds a score into the database, its a boolean(addScore) is true then score is added
     * to the scoreAdd column and if the boolean(addScore) is false then the score gets added to the
     * scoreSub column
     * @param addScore boolean used to determine if score goes into the add column or sub column
     * @param score int representing the score to be added into the database
     * @return boolean true if the score was added, else false if it wasn't
     */
    public boolean insertScore(boolean addScore, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        //checking to see what column to add the score too
        if (addScore) {
            cv.put(Col_2, score);
        } else {
            cv.put(Col_3, score);
        }
        //inserting into the database
        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1)
            return false;
        else
            return true;
    }

    /**
     * Method is used return a set of strings representing the list of apps in the whitelisted
     * apps column. This method is used in other classes like in the AppSelecter and the
     * MainActivity
     * @return set of strings representing the list of apps in the whitelisted
     * apps column
     */
    public Set<String> isAppWhitelist() {
        Set<String> returnSet = new TreeSet<String>();

        Cursor cursor = dataDumb();
        //returning a empty string is the database is empty
        if (cursor.getCount() == 0) {
            System.out.println("NO DATA FOUND");
            return returnSet;
        }
        //getting all the apps from the database and adding them into the returnSet
        while (cursor.moveToNext()) {
            if(cursor.getString(1)!= null)
                returnSet.add(cursor.getString(1));
        }
        return returnSet;
    }

    /**
     * This method returns a cursor, which can be used to get all data from the database from
     * any column.
     * @return a cursor which allows all data in the database to be viewed
     */
    public Cursor dataDumb() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor returnCursor = db.rawQuery("select * from " + TABLE_NAME, null);
        return returnCursor;
    }

    /**
     * This method wipes all the data in the database.
     */
    public void wipeHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }

}
