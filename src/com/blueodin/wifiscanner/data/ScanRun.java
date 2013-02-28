package com.blueodin.wifiscanner.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

public class ScanRun implements Parcelable, Comparable<ScanRun> {
	public long _id;
	public long start_timestamp;
	public long end_timestamp;
	public double start_longitude;
	public double start_latitude;
	public double end_longitude;
	public double end_latitude;
	public String description;
	
	public ScanRun(long id, long start_timestamp, long end_timestamp, double start_longitude, double start_latitude, double end_longitude, double end_latitude, String description) {
		this._id = id;
		this.start_timestamp = start_timestamp;
		this.end_timestamp = end_timestamp;
		this.start_longitude = start_longitude;
		this.start_latitude = start_latitude;
		this.end_longitude = end_longitude;
		this.end_latitude = end_latitude;
		this.description = description;
	}
	
	public ScanRun(Parcel src) {
		this._id = src.readLong();
		this.start_timestamp = src.readLong();
		this.end_timestamp = src.readLong();
		this.start_longitude = src.readDouble();
		this.start_latitude = src.readDouble();
		this.end_longitude = src.readDouble();
		this.end_latitude = src.readDouble();
		this.description = src.readString();
	}
	
	public static List<ScanRun> getArray(Cursor data) {
		List<ScanRun> results = new ArrayList<ScanRun>();
		
		if(data == null || !data.moveToFirst())
			return results;
		
		while(!data.isAfterLast()) {
			results.add(new ScanRun(data));
			data.moveToNext();
		}
		
		return results;
	}
	
	public static ScanRun newEntry(ContentResolver resolver) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.RunsColumns.START_TIMESTAMP, System.currentTimeMillis());
		return newEntry(resolver, values);
	}
	
	public static ScanRun newEntry(ContentResolver resolver, double start_longitude, double start_latitude) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.RunsColumns.START_TIMESTAMP, System.currentTimeMillis());
		values.put(DBHelper.RunsColumns.START_LONGITUDE, start_longitude);
		values.put(DBHelper.RunsColumns.START_LATITUDE, start_latitude);
		return newEntry(resolver, values);
	}
	
	public static ScanRun newEntry(ContentResolver resolver, ContentValues values) {
		Uri resultUri = resolver.insert(ProviderContract.Runs.CONTENT_URI, values);
		
		Cursor data = resolver.query(resultUri, null, null, null, null);
		if((data.getCount() < 1) || !data.moveToFirst())
			return null;
		
		ScanRun result = new ScanRun(data);
		data.close();
		
		return result;
	}
	
	public ScanRun(Cursor data) {
		this(data.getLong(data.getColumnIndex(DBHelper.RunsColumns._ID)),
			data.getLong(data.getColumnIndex(DBHelper.RunsColumns.START_TIMESTAMP)),
			data.getLong(data.getColumnIndex(DBHelper.RunsColumns.END_TIMESTAMP)),
			data.getDouble(data.getColumnIndex(DBHelper.RunsColumns.START_LONGITUDE)),
			data.getDouble(data.getColumnIndex(DBHelper.RunsColumns.START_LATITUDE)),
			data.getDouble(data.getColumnIndex(DBHelper.RunsColumns.END_LONGITUDE)),
			data.getDouble(data.getColumnIndex(DBHelper.RunsColumns.END_LATITUDE)),
			data.getString(data.getColumnIndex(DBHelper.RunsColumns.DESCRIPTION)));
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(_id);
		dest.writeLong(start_timestamp);
		dest.writeLong(end_timestamp);
		dest.writeDouble(start_longitude);
		dest.writeDouble(start_latitude);
		dest.writeDouble(end_longitude);
		dest.writeDouble(end_latitude);
		dest.writeString(description);
	}
	
	public ContentValues getValues() {
		ContentValues values = new ContentValues();
		
		values.put(DBHelper.RunsColumns.START_TIMESTAMP, start_timestamp);
		values.put(DBHelper.RunsColumns.END_TIMESTAMP, end_timestamp);
		values.put(DBHelper.RunsColumns.START_LONGITUDE, start_longitude);
		values.put(DBHelper.RunsColumns.START_LATITUDE, start_latitude);
		values.put(DBHelper.RunsColumns.END_LONGITUDE, end_longitude);
		values.put(DBHelper.RunsColumns.END_LATITUDE, end_latitude);
		values.put(DBHelper.RunsColumns.DESCRIPTION, description);
		
		return values;
	}

	@Override
	public int compareTo(ScanRun another) {
		long lidx = this.end_timestamp - this.start_timestamp;
		long ridx = another.end_timestamp - another.start_timestamp;
		if(lidx == ridx)
			return 0;
		
		if(lidx < ridx)
			return -1;
		
		return 1;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof ScanRun) {
			ScanRun obj = (ScanRun)o;
			return ((this.start_timestamp == obj.start_timestamp) && (this.end_timestamp == obj.end_timestamp));
		}

		return super.equals(o);
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	public String toString() {
		return String.format("Run from %s to %s (%s)", DateUtils.getRelativeTimeSpanString(this.start_timestamp), DateUtils.getRelativeTimeSpanString(this.end_timestamp), this.description);
	}
	
	@SuppressLint("DefaultLocale")
	public String repr() {
		return String.format("ScanRun(start_timestamp=%s,end_timestamp=%s,start=(%d,%d),end=(%d,%d),description=%s)", 
				DateUtils.getRelativeTimeSpanString(this.start_timestamp),
				DateUtils.getRelativeTimeSpanString(this.end_timestamp),
				this.start_latitude, this.start_longitude,
				this.end_latitude, this.end_longitude,
				this.description);
	}
	
	public String getRelativeStartTimestamp() {
		if(this.start_timestamp < 0)
			return "N/A";
		
		return DateUtils.getRelativeTimeSpanString(this.start_timestamp).toString();
	}
	
	public String getRelativeEndTimestamp() {
		if(this.end_timestamp < 0)
			return "N/A";
		
		return DateUtils.getRelativeTimeSpanString(this.end_timestamp).toString();
	}
	
	public String getStartTimestamp(Context context) {
		if(this.start_timestamp < 0)
			return "N/A";
		
		return DateUtils.formatDateTime(context, this.start_timestamp, DateUtils.FORMAT_ABBREV_ALL);
	}
	
	public String getEndTimestamp(Context context) {
		if(this.end_timestamp < 0)
			return "N/A";
		
		return DateUtils.formatDateTime(context, this.end_timestamp, DateUtils.FORMAT_ABBREV_ALL);
	}
	
	public Location getStartLocation() {
		return new Location(start_longitude, start_latitude);
	}
	
	public Location getEndLocation() {
		return new Location(end_longitude, end_latitude);
	}
	
	public static ScanRun getById(ContentResolver resolver, int id) {
		Cursor data = resolver.query(ProviderContract.Runs.CONTENT_URI, null, DBHelper.RunsColumns._ID + " = " + id, null, null);
		
		if(data.getCount() != 1)
			return null;
		
		return new ScanRun(data);
	}
	
	public boolean update(ContentResolver resolver) {
		int result = resolver.update(ProviderContract.Runs.uriById(_id), getValues(), DBHelper.RunsColumns._ID + " = " + _id, null);
		
		if(result > 0)
			return true;
		
		return false;
	}
	
	public ScanRun insert(ContentResolver resolver) {
		Uri resultUri = resolver.insert(ProviderContract.Runs.CONTENT_URI, getValues());
		
		Cursor data = resolver.query(resultUri, null, null, null, null);
		if((data.getCount() < 1) || !data.moveToFirst())
			return null;
		
		ScanRun run = new ScanRun(data);
		
		data.close();
		
		return run;
	}
}
