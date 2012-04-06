package com.omdasoft.monitor.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ServerDBOpenHelper extends SQLiteOpenHelper {
	
	public  static final int DATABASE_VERSION = 3;
    public  static final String SERVER_TABLE_NAME = "server";
	public  static final String SERVER_URL = "URL";
	public  static final String IS_MONITORED = "IS_MONITORED";
	public  static final String MINUTES = "MINUTES";
	public  static final String VERIFY_TITLE = "VERIFY_TITLE";
	public  static final String CHECK_LIST = "CHECK_LIST";
	public  static final String USER_ID = "USER_ID";
	public  static final String PASSWORD = "PASSWORD";
    public  static final String SERVER_TABLE_CREATE =
                "CREATE TABLE " + SERVER_TABLE_NAME + " (" +
                "_id integer primary key autoincrement," +
                SERVER_URL + " TEXT, " +
                IS_MONITORED + " INT, " +
                MINUTES + " LONG,  " +
                VERIFY_TITLE + " TEXT, " +
                USER_ID + " TEXT, " +
                PASSWORD + " TEXT, " +
    CHECK_LIST + " TEXT);";
	public static final String DATABASE_NAME = "servers";

    ServerDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SERVER_TABLE_CREATE);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		android.util.Log.w("Constants", "Upgrading database, which will destroy all old	data");
				db.execSQL("DROP TABLE IF EXISTS " + SERVER_TABLE_NAME);
				onCreate(db);
		
	}

}
