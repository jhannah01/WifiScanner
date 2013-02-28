package com.blueodin.wifiscanner.adapters;

import java.text.DateFormat;
import java.util.Date;

import com.blueodin.wifiscanner.R;
import com.blueodin.wifiscanner.data.ProviderContract;
import com.blueodin.wifiscanner.data.ScanResult;
import com.blueodin.wifiscanner.data.DBHelper;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ResultsAdapter extends SimpleCursorAdapter {
	private final static String[] mProjection = new String[] {
		DBHelper.ResultsColumns.BSSID,
		DBHelper.ResultsColumns.SSID,
		DBHelper.ResultsColumns.LEVEL,
		DBHelper.ResultsColumns.TIMESTAMP,
		DBHelper.ResultsColumns.LATITUDE,
		DBHelper.ResultsColumns.LONGITUDE,
		DBHelper.ResultsColumns.ACCURACY,
		DBHelper.ResultsColumns.ALTITUDE
	};

	private static int[] mOutProjection = new int[] { 
		R.id.textBSSID,
		R.id.textSSID, 
		R.id.textLevel, 
		R.id.textTimestamp, 
		R.id.textLocation,
		0,
		0,
		R.id.textAltitude
	};
	
	private SimpleCursorAdapter.ViewBinder mViewBinder = new SimpleCursorAdapter.ViewBinder() {

		@Override
		public boolean setViewValue(View view, Cursor cursor,
				int columnIndex) {
			double longitude, latitude;
			int accuracy;
			int altitude;
			switch (view.getId()) {
			case 0:
				((TextView)view).setText(cursor.getCount());
				return true;
			case R.id.textTimestamp:
				((TextView) view).setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(new Date(cursor.getLong(columnIndex))));
				return true;
			case R.id.textLevel:
				((TextView) view).setText(String.format("%d dBm", cursor.getInt(columnIndex)));
				return true;
			case R.id.textRecords:
				((TextView) view).setText("#" + cursor.getInt(columnIndex));
				return true;
			case R.id.textLocation:
				latitude = cursor.getDouble(cursor.getColumnIndex(DBHelper.ResultsColumns.LATITUDE));
				longitude = cursor.getDouble(cursor.getColumnIndex(DBHelper.ResultsColumns.LONGITUDE));
				accuracy = cursor.getInt(cursor.getColumnIndex(DBHelper.ResultsColumns.ACCURACY));
				((TextView) view).setText(((longitude != 0) && (latitude != 0)) ? String.format("%f,%f (+/- %d meters)", latitude, longitude, accuracy) : "N/A");
				return true;
			case R.id.textAltitude:
				altitude = cursor.getInt(columnIndex);
				((TextView) view).setText((altitude != 0) ? altitude + " meters" : "N/A");
				return true;
			}
			return false;
		}
	};

	public ResultsAdapter(Context context) {
		super(context, R.layout.result_list_row, null, mProjection, mOutProjection, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		setViewBinder(mViewBinder);
	}

	public ScanResult getResult(int position) {
		Cursor data = (Cursor)getItem(position);

		data.moveToFirst();
		
		return new ScanResult(data);
	}
}