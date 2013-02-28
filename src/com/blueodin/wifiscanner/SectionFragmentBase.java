package com.blueodin.wifiscanner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class SectionFragmentBase extends Fragment {
	public static final String ARG_SECTION_NUMBER = "arg_section_number";
	
	abstract protected int getLayoutId();
	
	public View buildView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(getLayoutId(), null, false);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return buildView(inflater, container);
	}
}
