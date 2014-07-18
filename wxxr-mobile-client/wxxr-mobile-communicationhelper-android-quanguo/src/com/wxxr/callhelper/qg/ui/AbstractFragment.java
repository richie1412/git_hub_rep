package com.wxxr.callhelper.qg.ui;

import com.wxxr.mobile.core.log.api.Trace;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public abstract class AbstractFragment extends Fragment{
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		if(getLogger().isDebugEnabled()){
			getLogger().debug("onActivityCreated ...");
		}

		super.onActivityCreated(savedInstanceState);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		if(getLogger().isDebugEnabled()){
			getLogger().debug("onAttach ...");
		}

		super.onAttach(activity);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(getLogger().isDebugEnabled()){
			getLogger().debug("onCreate ...");
		}

		super.onCreate(savedInstanceState);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(getLogger().isDebugEnabled()){
			getLogger().debug("onCreateView ...");
		}

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		if(getLogger().isDebugEnabled()){
			getLogger().debug("onDestroy ...");
		}

		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		if(getLogger().isDebugEnabled()){
			getLogger().debug("onDestroyView ...");
		}

		super.onDestroyView();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDetach()
	 */
	@Override
	public void onDetach() {
		if(getLogger().isDebugEnabled()){
			getLogger().debug("onDetach ...");
		}

		super.onDetach();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onInflate(android.app.Activity, android.util.AttributeSet, android.os.Bundle)
	 */
	@Override
	public void onInflate(Activity activity, AttributeSet attrs,
			Bundle savedInstanceState) {
		if(getLogger().isDebugEnabled()){
			getLogger().debug("onInflate ...");
		}

		super.onInflate(activity, attrs, savedInstanceState);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		if(getLogger().isDebugEnabled()){
			getLogger().debug("onPause ...");
		}

		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		if(getLogger().isDebugEnabled()){
			getLogger().debug("onResume ...");
		}

		super.onResume();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		if(getLogger().isDebugEnabled()){
			getLogger().debug("onSaveInstanceState ...");
		}

		super.onSaveInstanceState(outState);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		if(getLogger().isDebugEnabled()){
			getLogger().debug("onStart ...");
		}

		super.onStart();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		if(getLogger().isDebugEnabled()){
			getLogger().debug("onStop ...");
		}

		super.onStop();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		if(getLogger().isDebugEnabled()){
			getLogger().debug("onViewCreated ...");
		}

		super.onViewCreated(view, savedInstanceState);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onViewStateRestored(android.os.Bundle)
	 */
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		if(getLogger().isDebugEnabled()){
			getLogger().debug("onViewStateRestored ...");
		}

		super.onViewStateRestored(savedInstanceState);
	}

	protected abstract Trace getLogger();
	
	public boolean onBackPressed() {
		return false;
	}
	
}