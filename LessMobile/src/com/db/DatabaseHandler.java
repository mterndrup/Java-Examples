package com.db;

import java.util.Vector;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.beans.Intervals;
import com.lessutility.MainActivity;
import com.lessutility.Tabs;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "stored";

	// Contacts table name
	private static final String TABLE_CONTACTS = "user";
	private static final String TABLE_INTERVALS = "intervals";

	// Contacts Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "token";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT" + ")";
		String CREATE_LOCAL_DATA = "CREATE TABLE " + TABLE_INTERVALS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + "a FLOAT," + "b FLOAT,"
				+ "c FLOAT," + "d FLOAT," + "e FLOAT," + "f FLOAT,"
				+ "g FLOAT," + "h FLOAT," + "i FLOAT," + "j FLOAT,"
				+ "k FLOAT," + "l FLOAT," + "m FLOAT," + "n FLOAT,"
				+ "o FLOAT," + "p FLOAT," + "q FLOAT," + "r FLOAT,"
				+ "s FLOAT," + "t FLOAT," + "u FLOAT," + "v FLOAT," + "w FLOAT,"
				+ "x FLOAT" + ")";
		db.execSQL(CREATE_CONTACTS_TABLE);
		db.execSQL(CREATE_LOCAL_DATA);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_INTERVALS);

		// Create tables again
		onCreate(db);
	}

	public void removeAll() {
		// db.delete(String tableName, String whereClause, String[] whereArgs);
		// If whereClause is null, it will delete all rows.
		try {
			MainActivity.result = null;
			MainActivity.access_token = null;
			Tabs.dailyData = null;
			Tabs.dailyJSON = null;
			Tabs.cycleData = null;
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(TABLE_CONTACTS, null, null);
			db.delete(TABLE_INTERVALS, null, null);
		} catch (Exception e) {

		}
	}

	public void removeDailyIntervals() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_INTERVALS, null, null);
	}

	public void addContact(String token) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, token);

		// Inserting Row
		db.insert(TABLE_CONTACTS, null, values);
		db.close(); // Closing database connection
	}

	public void addDailyIntervals(Vector<Intervals> intervals) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			removeDailyIntervals();
			ContentValues values = new ContentValues();
			values.put("a", intervals.elementAt(0).getCost());
			values.put("b", intervals.elementAt(1).getCost());
			values.put("c", intervals.elementAt(2).getCost());
			values.put("d", intervals.elementAt(3).getCost());
			values.put("e", intervals.elementAt(4).getCost());
			values.put("f", intervals.elementAt(5).getCost());
			values.put("g", intervals.elementAt(6).getCost());
			values.put("h", intervals.elementAt(7).getCost());
			values.put("i", intervals.elementAt(8).getCost());
			values.put("j", intervals.elementAt(9).getCost());
			values.put("k", intervals.elementAt(10).getCost());
			values.put("l", intervals.elementAt(11).getCost());
			values.put("m", intervals.elementAt(12).getCost());
			values.put("n", intervals.elementAt(13).getCost());
			values.put("o", intervals.elementAt(14).getCost());
			values.put("p", intervals.elementAt(15).getCost());
			values.put("q", intervals.elementAt(16).getCost());
			values.put("r", intervals.elementAt(17).getCost());
			values.put("s", intervals.elementAt(18).getCost());
			values.put("t", intervals.elementAt(19).getCost());
			values.put("u", intervals.elementAt(20).getCost());
			values.put("v", intervals.elementAt(21).getCost());
			values.put("w", intervals.elementAt(22).getCost());
			values.put("x", intervals.elementAt(23).getCost());
			// Inserting Row
			db.insert(TABLE_INTERVALS, null, values);
			db.close(); // Closing database connection
		} catch (Exception e) {

		}
	}

	public int getContactsCount() {
		int count = -1;
		try {
			String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(countQuery, null);
			count = cursor.getCount();
			cursor.close();
		} catch (Exception e) {

		}
		// return count
		return count;
	}

	public String getToken() {
		String token = "";
		// Select All Query
		String selectQuery = "SELECT  " + KEY_NAME + " FROM " + TABLE_CONTACTS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				token = cursor.getString(0);
			} while (cursor.moveToNext());
		}

		// return contact list
		return token;
	}
}