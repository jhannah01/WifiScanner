package com.blueodin.wifiscanner;

import com.blueodin.wifiscanner.RunsListFragment.IClickHandler;
import com.blueodin.wifiscanner.data.ScanRun;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RunsSectionFragment extends SectionFragmentBase implements IClickHandler {
	private RunsListFragment mListFragment;
	
	@Override
	protected int getLayoutId() {
		return R.layout.section_runs;
	}

	@Override
	public View buildView(LayoutInflater inflater, ViewGroup container) {
		View rootView = super.buildView(inflater, container);
		
		mListFragment = (RunsListFragment) getFragmentManager().findFragmentById(R.id.list_runs_fragment);
		mListFragment.registerOnClickHandler(this);
		
		return rootView;
	}

	@Override
	public void onListItemClick(ScanRun scanRun) {
		Fragment f = new RunsDetailFragment();
		Bundle args = new Bundle();
		args.putParcelable(RunsDetailFragment.ARG_SCAN_RUN, scanRun);
		f.setArguments(args);
		
		getFragmentManager().beginTransaction()
			.replace(R.id.layout_runs_details, f)
			.commit();
	}
}
