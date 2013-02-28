package com.blueodin.wifiscanner.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_FILENAME = "results.db";
	private static final int DATABASE_VERSION = 2;
		
	public static class RunsColumns implements BaseColumns {
		public static final String START_TIMESTAMP = "start_timestamp";
		public static final String END_TIMESTAMP = "end_timestamp";
		public static final String START_LONGITUDE = "start_longitude";
		public static final String START_LATITUDE = "start_latitude";
		public static final String END_LONGITUDE = "end_longitude";
		public static final String END_LATITUDE = "end_latitude";
		public static final String DESCRIPTION = "description";
	}
	
	public static class ResultsColumns implements BaseColumns {
		public static final String BSSID = "bssid";
		public static final String SSID = "ssid";
		public static final String LEVEL = "level";
		public static final String FREQUENCY = "frequency";
		public static final String CAPABILITIES = "capabilities";
		public static final String LONGITUDE = "longitude";
		public static final String LATITUDE = "latutide";
		public static final String ALTITUDE = "altitude";
		public static final String ACCURACY = "accuracy";
		public static final String TIMESTAMP = "timestamp";
		public static final String RUN_ID = "run_id";
	}
	
	// Runs SQL statements
	public static final String TABLE_RUNS = "runs";
	private static final String _SQL_CREATE_RUNS_STATEMENT = 
			"CREATE TABLE IF NOT EXISTS " + TABLE_RUNS + " (" +
					RunsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					RunsColumns.START_TIMESTAMP + " INTEGER, " + 
					RunsColumns.END_TIMESTAMP + " INTEGER, " +
					RunsColumns.START_LONGITUDE + " REAL, " +
					RunsColumns.START_LATITUDE + " REAL, " +
					RunsColumns.END_LONGITUDE + " REAL, " +
					RunsColumns.END_LATITUDE + " REAL, " +
					RunsColumns.DESCRIPTION + " TEXT);";
	private static final String _SQL_DROP_RUNS_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_RUNS + ";";
	
	// Results SQL statements
	public static final String TABLE_RESULTS = "results";
	private static final String _SQL_CREATE_RESULTS_STATEMENT =
			"CREATE TABLE IF NOT EXISTS " + TABLE_RESULTS + " (" +
					ResultsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					ResultsColumns.BSSID + " TEXT NOT NULL, " +
					ResultsColumns.SSID + " TEXT, " + 
					ResultsColumns.LEVEL + " INTEGER, " +
					ResultsColumns.FREQUENCY + " INTEGER, " +
					ResultsColumns.CAPABILITIES + " TEXT, " +
					ResultsColumns.LONGITUDE + " REAL, " + 
					ResultsColumns.LATITUDE + " REAL, " + 
					ResultsColumns.ALTITUDE + " REAL, " +
					ResultsColumns.ACCURACY + " INTEGER, " +
					ResultsColumns.TIMESTAMP + " INTEGER, " +
					ResultsColumns.RUN_ID + " INTEGER);";
	private static final String _SQL_DROP_RESULTS_STATEMENT = "DROP TABLE IF EXISTS " + TABLE_RESULTS + ";";
	
	
	private static final String[] _SQL_CREATE_STATEMENTS = new String[] {
		_SQL_CREATE_RUNS_STATEMENT,
		_SQL_CREATE_RESULTS_STATEMENT
	};

	private static final String[] _SQL_DROP_STATEMENTS = new String[] {
		_SQL_DROP_RESULTS_STATEMENT,
		_SQL_DROP_RUNS_STATEMENT
	};
	
	public DBHelper(Context context) {
		 super(context, DATABASE_FILENAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		for(String sql : _SQL_CREATE_STATEMENTS)
    		db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    	for(String sql : _SQL_DROP_STATEMENTS)
    		db.execSQL(sql);
    	
    	onCreate(db);
    }
}
