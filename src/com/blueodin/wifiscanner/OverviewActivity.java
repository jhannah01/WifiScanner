package com.blueodin.wifiscanner;

import java.util.ArrayList;
import java.util.List;

import com.blueodin.wifiscanner.data.DBHelper;
import com.blueodin.wifiscanner.data.ProviderContract;
import com.blueodin.wifiscanner.data.ScanResult;
import com.blueodin.wifiscanner.data.ScanRun;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;

public class OverviewActivity extends FragmentActivity implements
		ActionBar.TabListener {
	
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private static final SparseArray<SectionFragmentBase> mSections = new SparseArray<SectionFragmentBase>();
	
	private static final int SECTION_RUNS = 0;
	private static final int SECTION_NETWORKS = 1;
	private static final int SECTION_STATS = 2;
	private static final String TAG = "OverviewActivity";
	public static final String FLAG_FROM_NOTIFICATION = "from_notification";
	
	static {
		mSections.put(SECTION_RUNS, new RunsSectionFragment());
		mSections.put(SECTION_NETWORKS, new NetworksSectionFragment());
		mSections.put(SECTION_STATS, new StatsSectionFragment());
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_overview);

		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		actionBar.addTab(actionBar.newTab()
				.setText(R.string.title_runs)
				.setTabListener(this));
		
		actionBar.addTab(actionBar.newTab()
				.setText(R.string.title_networks)
				.setTabListener(this));
		
		actionBar.addTab(actionBar.newTab()
				.setText(R.string.title_stats)
				.setTabListener(this));
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_overview, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_settings:
			// TODO: Implement settings
			return false;
		case R.id.menu_exit:
			finish();
			return true;
		case R.id.menu_add_results:
			addResultsToDatabase();
			return true;
		}
		
		return super.onMenuItemSelected(featureId, item);
	}

	private void addResultsToDatabase() {
		ScanResult result;
		ScanRun run;
		ContentValues values;
		
		run = ScanRun.newEntry(getContentResolver(), -75.874802, 39.729865);
		if(run == null) {
			Log.w(TAG, "Unable to insert ScanRun into database");
			return;
		}
		
		values = new ContentValues();
		values.put(DBHelper.ResultsColumns.BSSID, "00:ff:11:33:cc:ff");
		values.put(DBHelper.ResultsColumns.SSID, "wifiAP01");
		values.put(DBHelper.ResultsColumns.LEVEL, -75);
		values.put(DBHelper.ResultsColumns.FREQUENCY, 1250);
		values.put(DBHelper.ResultsColumns.CAPABILITIES, "[WPA2-PSK-TKIP][WPA-PSK][ESS]");
		values.put(DBHelper.ResultsColumns.TIMESTAMP, System.currentTimeMillis()-8000);
		values.put(DBHelper.ResultsColumns.RUN_ID, run._id);
		result = ScanResult.newEntry(getContentResolver(), values);
		
		if(result == null) {
			Log.w(TAG, "Unable to insert ScanResult: " + values);
			return;
		}
		
		values = new ContentValues();
		values.put(DBHelper.ResultsColumns.BSSID, "cc:bb:ff:00:55:11");
		values.put(DBHelper.ResultsColumns.SSID, "testWifi");
		values.put(DBHelper.ResultsColumns.LEVEL, -80);
		values.put(DBHelper.ResultsColumns.FREQUENCY, 1100);
		values.put(DBHelper.ResultsColumns.CAPABILITIES, "[WPA-PSK][ESS]");
		values.put(DBHelper.ResultsColumns.TIMESTAMP, System.currentTimeMillis()-4000);
		values.put(DBHelper.ResultsColumns.RUN_ID, run._id);
		result = ScanResult.newEntry(getContentResolver(), values);
		
		if(result == null) {
			Log.w(TAG, "Unable to insert ScanResult: " + values);
			return;
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, mSections.get(tab.getPosition()))
				.commit();
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
}