package com.blueodin.wifiscanner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.blueodin.wifiscanner.RunsListFragment.RunListAdapter;
import com.blueodin.wifiscanner.data.DBHelper;
import com.blueodin.wifiscanner.data.ProviderContract;
import com.blueodin.wifiscanner.data.ScanRun;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class RunsListFragment extends ListFragment implements LoaderCallbacks<Cursor> {
	public interface IClickHandler {
		public void onListItemClick(ScanRun scanRun);
	}

	public static class RunListAdapter extends SimpleCursorAdapter {
		private class ViewBinder implements SimpleCursorAdapter.ViewBinder {
			@Override
			public boolean setViewValue(View view, Cursor data, int columnIndex) {
				long timestamp;
				String description;
				
				switch(view.getId()) {
				case R.id.text_runlist_start_time:
					timestamp = data.getLong(columnIndex);
					((TextView)view).setText("From: " + ((timestamp > 0) ? SimpleDateFormat.getDateTimeInstance().format(new Date(timestamp)) : "N/A"));
					return true;
				case R.id.text_runlist_end_time:
					timestamp = data.getLong(columnIndex);
					((TextView)view).setText("To: " + ((timestamp > 0) ? SimpleDateFormat.getDateTimeInstance().format(new Date(timestamp)) : "N/A"));
					return true;
				case R.id.text_runlist_description:
					description = data.getString(data.getColumnIndex(DBHelper.RunsColumns.DESCRIPTION));
					((TextView)view).setText("Description: " + (TextUtils.isEmpty(description) ? "None" : description));
					return true;
				}
				
				return false;
			}
			
		}
		
		private ViewBinder mViewBinder = new ViewBinder();
		
		public RunListAdapter(Context context) {
			super(context, R.layout.run_list_row, null, new String[] {
					DBHelper.RunsColumns.START_TIMESTAMP,
					DBHelper.RunsColumns.END_TIMESTAMP,
					DBHelper.RunsColumns.DESCRIPTION }, new int[] {
					R.id.text_runlist_start_time,
					R.id.text_runlist_end_time,
					R.id.text_runlist_description },
					SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			setViewBinder(mViewBinder);
		}
		
		@Override
		public ScanRun getItem(int position) {
			Cursor data = (Cursor)super.getItem(position);
			
			if((data == null) || data.isAfterLast())
				return null;
			
			return new ScanRun(data);
		}
	}

	private static final int LOADER_ID = 0x01;
	private RunListAdapter mListAdapter;
	private List<IClickHandler> mOnClickHandlers = new ArrayList<IClickHandler>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mListAdapter = new RunListAdapter(getActivity());
		getLoaderManager().initLoader(LOADER_ID, null, this);
		setListAdapter(mListAdapter);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arguments) {
		return new CursorLoader(getActivity(), ProviderContract.Runs.CONTENT_URI, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mListAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mListAdapter.swapCursor(null);		
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if(mOnClickHandlers.size() > 0) {
			for(IClickHandler clickHandler : mOnClickHandlers)
				clickHandler.onListItemClick((ScanRun) mListAdapter.getItem(position));
		} else
			super.onListItemClick(l, v, position, id);
	}	
	
	public void registerOnClickHandler(IClickHandler clickHandler) {
		mOnClickHandlers.add(clickHandler);
	}
	
	public void unregisterOnClickHandler(IClickHandler clickHandler) {
		mOnClickHandlers.remove(clickHandler);
	}
}
