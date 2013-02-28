package com.blueodin.wifiscanner.data;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

public class ScanResult implements Parcelable, Comparable<ScanRun> {
	public final String bssid;
	public final String ssid;
	public final int level;
	public final int frequency;
	public final String capabilities;
	public final double longitude;
	public final double latitude;
	public final int altitude;
	public final int accuracy;
	public long timestamp;
	public long run_id;
	
	private ScanRun mScanRun = null;
	
	public ScanResult(String bssid, String ssid, int level, int frequency, String capabilities, double longitude, double latitude, int altitude, int accuracy, long timestamp, long run_id) {
		this.bssid = bssid;
		this.ssid = ssid;
		this.level = level;
		this.frequency = frequency;
		this.capabilities = capabilities;
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
		this.accuracy = accuracy;
		this.timestamp = timestamp;
		this.run_id = run_id;
	}
	
	public ScanResult(Parcel src) {
		this(src.readString(), src.readString(), src.readInt(), src.readInt(), src.readString(), src.readDouble(), src.readDouble(), src.readInt(), src.readInt(), src.readLong(), src.readLong());
	}
	
	public ScanResult(Cursor data) {
		this(data.getString(data.getColumnIndex(DBHelper.ResultsColumns.BSSID)),
				data.getString(data.getColumnIndex(DBHelper.ResultsColumns.SSID)),
				data.getInt(data.getColumnIndex(DBHelper.ResultsColumns.LEVEL)),
				data.getInt(data.getColumnIndex(DBHelper.ResultsColumns.FREQUENCY)),
				data.getString(data.getColumnIndex(DBHelper.ResultsColumns.CAPABILITIES)),
				data.getDouble(data.getColumnIndex(DBHelper.ResultsColumns.LONGITUDE)),
				data.getDouble(data.getColumnIndex(DBHelper.ResultsColumns.LATITUDE)),
				data.getInt(data.getColumnIndex(DBHelper.ResultsColumns.ALTITUDE)),
				data.getInt(data.getColumnIndex(DBHelper.ResultsColumns.ACCURACY)),
				data.getLong(data.getColumnIndex(DBHelper.ResultsColumns.TIMESTAMP)),
				data.getLong(data.getColumnIndex(DBHelper.ResultsColumns.RUN_ID)));
	}
	
	public static List<ScanResult> getArray(Cursor data) {
		List<ScanResult> results = new ArrayList<ScanResult>();
		
		if(data == null)
			return results;
		
		while(!data.isAfterLast()) {
			results.add(new ScanResult(data));
			data.moveToNext();
		}
		
		return results;
	}
	
	public ContentValues getValues() {
		ContentValues values = new ContentValues();
		
		values.put(DBHelper.ResultsColumns.BSSID, bssid);
		values.put(DBHelper.ResultsColumns.SSID, ssid);
		values.put(DBHelper.ResultsColumns.LEVEL, level);
		values.put(DBHelper.ResultsColumns.FREQUENCY, frequency);
		values.put(DBHelper.ResultsColumns.CAPABILITIES, capabilities);
		values.put(DBHelper.ResultsColumns.LONGITUDE, longitude);
		values.put(DBHelper.ResultsColumns.LATITUDE, latitude);
		values.put(DBHelper.ResultsColumns.ACCURACY, accuracy);
		values.put(DBHelper.ResultsColumns.TIMESTAMP, timestamp);
		values.put(DBHelper.ResultsColumns.RUN_ID, run_id);
		
		return values;
	}
	
	@Override
	public int compareTo(ScanRun another) {
		return 0;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(bssid);
		dest.writeString(ssid);
		dest.writeInt(level);
		dest.writeInt(frequency);
		dest.writeString(capabilities);
		dest.writeDouble(longitude);
		dest.writeDouble(latitude);
		dest.writeInt(altitude);
		dest.writeInt(accuracy);
		dest.writeLong(timestamp);
		dest.writeLong(run_id);
	}

	@SuppressLint("DefaultLocale")
	@Override
	public String toString() {
		return String.format("[%s] %s (%s) %.02f [%d MHz] @ %d,%d alt: %d", getRelativeTimestamp(), ssid, bssid, level, frequency, latitude, longitude, altitude);
	}
		
	public String getRelativeTimestamp() {
		return DateUtils.getRelativeTimeSpanString(timestamp).toString();
	}
	
	public Location getLocation() {
		return new Location(longitude, latitude, altitude, accuracy);
	}
	
	public static ScanRun getById(ContentResolver resolver, long id) {
		Cursor data = resolver.query(ProviderContract.Runs.CONTENT_URI, null, DBHelper.ResultsColumns._ID + " = " + id, null, null);
		
		if(data.getCount() != 1)
			return null;
		
		return new ScanRun(data);
	}
	
	public static ScanResult newEntry(android.net.wifi.ScanResult result, long run_id, ContentResolver resolver) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.ResultsColumns.BSSID, result.BSSID);
		values.put(DBHelper.ResultsColumns.SSID, result.SSID);
		values.put(DBHelper.ResultsColumns.LEVEL, result.level);
		values.put(DBHelper.ResultsColumns.FREQUENCY, result.frequency);
		values.put(DBHelper.ResultsColumns.CAPABILITIES, result.capabilities);
		values.put(DBHelper.ResultsColumns.TIMESTAMP, System.currentTimeMillis());
		values.put(DBHelper.ResultsColumns.RUN_ID, run_id);
		return newEntry(resolver, values);
	}
	
	public static ScanResult newEntry(ContentResolver resolver, ContentValues values) {
		Uri resultUri = resolver.insert(ProviderContract.Results.CONTENT_URI, values);
		
		Cursor data = resolver.query(resultUri, null, null, null, null);
		if((data.getCount() < 1) || !data.moveToFirst())
			return null;
		
		ScanResult result = new ScanResult(data);

		data.close();
		
		return result;
	}
}
