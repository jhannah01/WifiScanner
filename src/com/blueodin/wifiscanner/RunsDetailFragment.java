package com.blueodin.wifiscanner;

import com.blueodin.wifiscanner.adapters.ResultsAdapter;
import com.blueodin.wifiscanner.data.DBHelper;
import com.blueodin.wifiscanner.data.ProviderContract;
import com.blueodin.wifiscanner.data.ScanRun;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class RunsDetailFragment extends Fragment implements LoaderCallbacks<Cursor> {
	public static final String ARG_SCAN_RUN = "arg_scan_run";
	
	private static final int LOADER_ID = 0x02;
	private ScanRun mScanRun = null;
	private ResultsAdapter mResultsAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle args = getArguments();
		if((args != null) && args.containsKey(ARG_SCAN_RUN))
			mScanRun = args.getParcelable(ARG_SCAN_RUN);
		
		getLoaderManager().initLoader(LOADER_ID, null, this);
		
		mResultsAdapter = new ResultsAdapter(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if(mScanRun == null)
			return super.onCreateView(inflater, container, savedInstanceState);
		
		View rootView = inflater.inflate(R.layout.fragment_runs_detail, container, false);
		
		((TextView)rootView.findViewById(R.id.text_runsdetail_start_time)).setText("From: " + mScanRun.getStartTimestamp(getActivity()));
		((TextView)rootView.findViewById(R.id.text_runsdetail_start_location)).setText(mScanRun.getStartLocation().toString());
		((TextView)rootView.findViewById(R.id.text_runsdetail_end_time)).setText("To: " + mScanRun.getEndTimestamp(getActivity()));
		((TextView)rootView.findViewById(R.id.text_runsdetail_end_location)).setText(mScanRun.getEndLocation().toString());
		((TextView)rootView.findViewById(R.id.text_runsdetail_description)).setText("Description: " + (TextUtils.isEmpty(mScanRun.description) ? "N/A" : mScanRun.description));
		
		ListView listResults = ((ListView)rootView.findViewById(R.id.list_runsdetail_results));
		listResults.setAdapter(mResultsAdapter);
		
		return rootView;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arguments) {
		String selection = ((mScanRun == null) ? "" : DBHelper.ResultsColumns.RUN_ID + " = " + mScanRun._id);
		return (new CursorLoader(getActivity(), ProviderContract.Results.CONTENT_URI, null, selection, null, null));
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mResultsAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mResultsAdapter.swapCursor(null);
	}

}
