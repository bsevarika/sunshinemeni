package com.example.android.sunshine.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Bojan on 10.10.2015.
 */

public class FeedReaderDbHelper extends SQLiteOpenHelper {

    // Database helper instance
    private static FeedReaderDbHelper _instance;

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE + TEXT_TYPE +
     // Any other options for the CREATE command
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "FeedReader.db";

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


public void addContentToDb(){
    String[] data =  FetchWeatherTask.bazaArray;
    Log.v(FeedReaderDbHelper.class + "Srbija", data[8]);
   // FetchWeatherTask weatherTask = new FetchWeatherTask();
   // weatherTask.execute("");


   // FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(getContent());
// Gets the data repository in write mode
    SQLiteDatabase db = this.getWritableDatabase();

// Create a new map of values, where column names are the keys
    ContentValues values = new ContentValues();

    values.put("entryid", 2);
    values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, data[8]);
    values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUBTITLE, "srbija");
    Log.v(FeedReaderDbHelper.class + "", data[8]);
    // Insert the new row, returning the primary key value of the new row
    long newRowId;
    newRowId = db.insert(
            FeedReaderContract.FeedEntry.TABLE_NAME,
            null,
            values);

}


    /**
     * @param context
     * @return get databasehelper instance
     */
    public static FeedReaderDbHelper getInstance(Context context) {
        if (null == _instance) {
            _instance = new FeedReaderDbHelper(context);
        }
        return _instance;
    }
}