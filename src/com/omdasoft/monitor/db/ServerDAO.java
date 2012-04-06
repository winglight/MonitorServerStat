package com.omdasoft.monitor.db;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class ServerDAO {

	private SQLiteDatabase db;
	private final Context context;

	private static ServerDAO instance;
	private ServerDBOpenHelper sdbHelper;

	private ServerDAO(Context c) {
		this.context = c;
		this.sdbHelper = new ServerDBOpenHelper(this.context);
	}

	public void close() {
		db.close();
	}

	public void open() throws SQLiteException {
		try {
			db = sdbHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			Log.v("Open database exception caught", ex.getMessage());
			db = sdbHelper.getReadableDatabase();
		}
	}

	public static ServerDAO getInstance(Context c) {
		if (instance == null) {
			instance = new ServerDAO(c);
		}
		return instance;
	}

	public Cursor getAllServer() {
		Cursor c = db.query(ServerDBOpenHelper.SERVER_TABLE_NAME, null, null,
				null, null, null, null);
				
		return c;
	}

	public int getMaxId() {
		return 0;
	}

	public long insert(ServerModel sm) {

		try{
			ContentValues newServerValue = new ContentValues();
			newServerValue.put(ServerDBOpenHelper.SERVER_URL, sm.getUrl());
			newServerValue.put(ServerDBOpenHelper.IS_MONITORED, true);
			newServerValue.put(ServerDBOpenHelper.VERIFY_TITLE, sm.getVerifyTitle());
			newServerValue.put(ServerDBOpenHelper.MINUTES, sm.getMinutes());
			newServerValue.put(ServerDBOpenHelper.CHECK_LIST, sm.getCheckList());
			newServerValue.put(ServerDBOpenHelper.USER_ID, sm.getUserId());
			newServerValue.put(ServerDBOpenHelper.PASSWORD, sm.getPassword());
			return db.insert(ServerDBOpenHelper.SERVER_TABLE_NAME, null, newServerValue);
			} catch(SQLiteException ex) {
				Log.v("Insert into database exception caught",
						ex.getMessage());
				return -1;
			}
	}
	
	public long update(ServerModel sm) {

		try{
			ContentValues newServerValue = new ContentValues();
//			newServerValue.put("_id", sm.getId());
			newServerValue.put(ServerDBOpenHelper.SERVER_URL, sm.getUrl());
			newServerValue.put(ServerDBOpenHelper.IS_MONITORED, sm.getMonitored());
			newServerValue.put(ServerDBOpenHelper.VERIFY_TITLE, sm.getVerifyTitle());
			newServerValue.put(ServerDBOpenHelper.MINUTES, sm.getMinutes());
			newServerValue.put(ServerDBOpenHelper.CHECK_LIST, sm.getCheckList());
			newServerValue.put(ServerDBOpenHelper.USER_ID, sm.getUserId());
			newServerValue.put(ServerDBOpenHelper.PASSWORD, sm.getPassword());
			return db.update(ServerDBOpenHelper.SERVER_TABLE_NAME, newServerValue, "_id=" + sm.getId(), null);
			} catch(SQLiteException ex) {
				Log.v("update database exception caught",
						ex.getMessage());
				return -1;
			}
	}
	
	public long delete(long id) {
		try{
			return db.delete(ServerDBOpenHelper.SERVER_TABLE_NAME, "_id=" + id, null);
			} catch(SQLiteException ex) {
				Log.v("delete database exception caught",
						ex.getMessage());
				return -1;
			}
	}
}
