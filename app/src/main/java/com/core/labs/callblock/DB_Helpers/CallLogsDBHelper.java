package com.core.labs.callblock.DB_Helpers;

/**
 * Database Helper Class for Call Logs
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CallLogsDBHelper extends SQLiteOpenHelper {

    //Define your class variables here
    private static final String DATABASE_NAME = "call_logs_db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "DB Helper";
    private static final String CALL_LOGS_LIMIT = "20";

    //Database Queries
    private static final String CREATE_TABLE = "CREATE TABLE " + CallLogsContract.CallLogsEntry.TABLE_NAME
            + "(" + CallLogsContract.CallLogsEntry.CALL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CallLogsContract.CallLogsEntry.CONTACT_NAME + " text,"
            + CallLogsContract.CallLogsEntry.CONTACT_NUMBER + " text,"
            + CallLogsContract.CallLogsEntry.DATE + " text);";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + CallLogsContract.CallLogsEntry.TABLE_NAME;

    //Create the constructor to create the DB
    public CallLogsDBHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        Log.d(TAG,"Database created");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
        Log.d(TAG,"Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE);
        onCreate(sqLiteDatabase);
    }

    //Method to add a Call Log to the SQLite Database
    public void addCallLog(String name,String number,String date,SQLiteDatabase sqLiteDatabase) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CallLogsContract.CallLogsEntry.CONTACT_NAME,name);
        contentValues.put(CallLogsContract.CallLogsEntry.CONTACT_NUMBER,number);
        contentValues.put(CallLogsContract.CallLogsEntry.DATE,date);

        sqLiteDatabase.insert(CallLogsContract.CallLogsEntry.TABLE_NAME,null,contentValues);
        Log.d(TAG,"Call Log Inserted");
        sqLiteDatabase.close();
    }

    //Method to return all the Call Logs
    public Cursor showCallLogs(SQLiteDatabase sqLiteDatabase) {
        String[] projections = {CallLogsContract.CallLogsEntry.CALL_ID, CallLogsContract.CallLogsEntry.CONTACT_NAME,
                CallLogsContract.CallLogsEntry.CONTACT_NUMBER, CallLogsContract.CallLogsEntry.DATE};
        return sqLiteDatabase.query(CallLogsContract.CallLogsEntry.TABLE_NAME,projections,null,null,null,null, CallLogsContract.CallLogsEntry.CALL_ID + " DESC",CALL_LOGS_LIMIT);
    }
}
