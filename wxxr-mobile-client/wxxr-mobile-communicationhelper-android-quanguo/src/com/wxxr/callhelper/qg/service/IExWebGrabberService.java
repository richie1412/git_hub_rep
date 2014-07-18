/**
 * 
 */
package com.wxxr.callhelper.qg.service;

import com.wxxr.mobile.web.grabber.api.IWebGrabberService;

/**
 * @author neillin
 *
 */
public interface IExWebGrabberService extends IWebGrabberService {
	boolean grabWebPage(String htmlUrl, String tmpDir, String dir);
}
