package com.blueodin.wifiscanner.data;

import java.util.TreeMap;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class ContentProvider extends android.content.ContentProvider {
	public static final TreeMap<String,String> RUNS_PROJECTION = new TreeMap<String,String>();
	public static final TreeMap<String,String> RESULTS_PROJECTION = new TreeMap<String,String>();
	
	private static final UriMatcher mUriMatcher;
	private static final int URIMATCH_RUNS = 0x01;
	private static final int URIMATCH_RUN_BY_ID = 0x02;
	private static final int URIMATCH_RESULTS = 0x03;
	private static final int URIMATCH_RESULT_BY_ID = 0x04;
	
	private DBHelper mDbHelper;
	
	static {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		
		mUriMatcher.addURI(ProviderContract.AUTHORITY, ProviderContract.Runs.PATH, URIMATCH_RUNS);
		mUriMatcher.addURI(ProviderContract.AUTHORITY, ProviderContract.Runs.PATH_BY_ID + "#", URIMATCH_RUN_BY_ID);
		mUriMatcher.addURI(ProviderContract.AUTHORITY, ProviderContract.Results.PATH, URIMATCH_RESULTS);
		mUriMatcher.addURI(ProviderContract.AUTHORITY, ProviderContract.Results.PATH_BY_ID + "#", URIMATCH_RESULT_BY_ID);
		
		RESULTS_PROJECTION.put(DBHelper.ResultsColumns._ID, DBHelper.ResultsColumns._ID);
		RESULTS_PROJECTION.put(DBHelper.ResultsColumns.BSSID, DBHelper.ResultsColumns.BSSID);
		RESULTS_PROJECTION.put(DBHelper.ResultsColumns.SSID, DBHelper.ResultsColumns.SSID);
		RESULTS_PROJECTION.put(DBHelper.ResultsColumns.LEVEL, DBHelper.ResultsColumns.LEVEL);
		RESULTS_PROJECTION.put(DBHelper.ResultsColumns.FREQUENCY, DBHelper.ResultsColumns.FREQUENCY);
		RESULTS_PROJECTION.put(DBHelper.ResultsColumns.CAPABILITIES, DBHelper.ResultsColumns.CAPABILITIES);
		RESULTS_PROJECTION.put(DBHelper.ResultsColumns.LONGITUDE, DBHelper.ResultsColumns.LONGITUDE);
		RESULTS_PROJECTION.put(DBHelper.ResultsColumns.LATITUDE, DBHelper.ResultsColumns.LATITUDE);
		RESULTS_PROJECTION.put(DBHelper.ResultsColumns.ALTITUDE, DBHelper.ResultsColumns.ALTITUDE);
		RESULTS_PROJECTION.put(DBHelper.ResultsColumns.ACCURACY, DBHelper.ResultsColumns.ACCURACY);
		RESULTS_PROJECTION.put(DBHelper.ResultsColumns.TIMESTAMP, DBHelper.ResultsColumns.TIMESTAMP);
		RESULTS_PROJECTION.put(DBHelper.ResultsColumns.RUN_ID, DBHelper.ResultsColumns.RUN_ID);
		
		RUNS_PROJECTION.put(DBHelper.RunsColumns._ID, DBHelper.RunsColumns._ID);
		RUNS_PROJECTION.put(DBHelper.RunsColumns.START_TIMESTAMP, DBHelper.RunsColumns.START_TIMESTAMP);
		RUNS_PROJECTION.put(DBHelper.RunsColumns.END_TIMESTAMP, DBHelper.RunsColumns.END_TIMESTAMP);
		RUNS_PROJECTION.put(DBHelper.RunsColumns.START_LONGITUDE, DBHelper.RunsColumns.START_LONGITUDE);
		RUNS_PROJECTION.put(DBHelper.RunsColumns.START_LATITUDE, DBHelper.RunsColumns.START_LATITUDE);
		RUNS_PROJECTION.put(DBHelper.RunsColumns.END_LONGITUDE, DBHelper.RunsColumns.END_LONGITUDE);
		RUNS_PROJECTION.put(DBHelper.RunsColumns.END_LATITUDE, DBHelper.RunsColumns.END_LATITUDE);
		RUNS_PROJECTION.put(DBHelper.RunsColumns.DESCRIPTION, DBHelper.RunsColumns.DESCRIPTION);
	}
	
	public ContentProvider() { }

	@Override
	public boolean onCreate() {
		mDbHelper = new DBHelper(getContext());
		return true;
	}

	@Override
	public void shutdown() {
		super.shutdown();
		mDbHelper.close();
	}
	
	@Override
	public String getType(Uri uri) {
		switch (mUriMatcher.match(uri)) {
		case URIMATCH_RUNS:
			return ProviderContract.Runs.CONTENT_TYPE;
		case URIMATCH_RUN_BY_ID:
			return ProviderContract.Runs.CONTENT_ITEM_TYPE;
		case URIMATCH_RESULTS:
			return ProviderContract.Results.CONTENT_TYPE;
		case URIMATCH_RESULT_BY_ID:
			return ProviderContract.Results.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String orderBy = null;
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		switch(mUriMatcher.match(uri)) {
		case URIMATCH_RUN_BY_ID:
			queryBuilder.appendWhere(DBHelper.RunsColumns._ID + " = " + uri.getPathSegments().get(1));
		case URIMATCH_RUNS:
			queryBuilder.setTables(DBHelper.TABLE_RUNS);
			queryBuilder.setProjectionMap(RUNS_PROJECTION);
			orderBy = ProviderContract.Runs.DEFAULT_ORDER_BY;
			break;
		case URIMATCH_RESULT_BY_ID:
			queryBuilder.appendWhere(DBHelper.ResultsColumns._ID + " = " + uri.getPathSegments().get(1));
		case URIMATCH_RESULTS:
			queryBuilder.setTables(DBHelper.TABLE_RESULTS);
			queryBuilder.setProjectionMap(RESULTS_PROJECTION);
			orderBy = ProviderContract.Results.DEFAULT_ORDER_BY;
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		if(!TextUtils.isEmpty(sortOrder))
			orderBy = sortOrder;
		
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor c = queryBuilder.query(db, projection, selection, selectionArgs, null, null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		
		return c;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// Implement this to handle requests to delete one or more rows.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri baseUri;
		String tableName;
		
		switch(mUriMatcher.match(uri)) {
		case URIMATCH_RUNS:
			baseUri = ProviderContract.Runs.CONTENT_ID_URI_BASE;
			tableName = DBHelper.TABLE_RUNS;
			break;
		case URIMATCH_RESULTS:
			baseUri = ProviderContract.Results.CONTENT_ID_URI_BASE;
			tableName = DBHelper.TABLE_RESULTS;
			break;
		default:
			throw new IllegalArgumentException("Invalid URI: " + uri);
		}
		
		if (values == null)
			values = new ContentValues();
		
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		
		long rowId = db.insert(tableName, null, values);
		
		if(rowId < 1)
			throw new SQLException("Failed to insert row for URI: " + uri);
		
		Uri insertedUri = ContentUris.withAppendedId(baseUri, rowId);
		
		getContext().getContentResolver().notifyChange(insertedUri, null);

		return insertedUri;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
