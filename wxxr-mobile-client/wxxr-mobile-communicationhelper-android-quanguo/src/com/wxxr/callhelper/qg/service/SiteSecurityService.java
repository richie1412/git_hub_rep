/**
 * 
 */
package com.wxxr.callhelper.qg.service;

import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;

import com.wxxr.mobile.core.security.api.ISiteSecurityService;
import com.wxxr.mobile.web.grabber.common.AbstractGrabberModule;

/**
 * @author wangyan
 *
 */
public class SiteSecurityService extends AbstractGrabberModule implements ISiteSecurityService {

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.security.api.ISiteSecurityService#getHostnameVerifier()
	 */
	@Override
	public HostnameVerifier getHostnameVerifier() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.security.api.ISiteSecurityService#getTrustKeyStore()
	 */
	@Override
	public KeyStore getTrustKeyStore() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.security.api.ISiteSecurityService#getSiteKeyStore()
	 */
	@Override
	public KeyStore getSiteKeyStore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initServiceDependency() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void startService() {
		context.registerService(ISiteSecurityService.class, this);
	}

	@Override
	protected void stopService() {
		context.unregisterService(ISiteSecurityService.class, this);
	}

}
