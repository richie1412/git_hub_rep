/**
 * 
 */
package com.wxxr.callhelper.qg.service;

import java.util.regex.Pattern;

import com.wxxr.mobile.web.grabber.api.IGrabberServiceContext;
import com.wxxr.mobile.web.grabber.common.AbstractPageGrabbingTask;
import com.wxxr.mobile.web.grabber.model.WebURL;

/**
 * @author wangyan
 *
 */
public class PageGrabbingTask extends AbstractPageGrabbingTask {

	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|pdf))$");

	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		if (FILTERS.matcher(href).matches()) {
			return true;
		}
		return false;
	}


	



	/* (non-Javadoc)
	 * @see com.wxxr.mobile.web.grabber.common.AbstractPageGrabbingTask#init(com.wxxr.mobile.web.grabber.api.IGrabberServiceContext, java.lang.String, java.lang.Object)
	 */
	@Override
	public void init(IGrabberServiceContext context, String url,
			Object customData) {
	super.init(context, url, customData);
	}

}
