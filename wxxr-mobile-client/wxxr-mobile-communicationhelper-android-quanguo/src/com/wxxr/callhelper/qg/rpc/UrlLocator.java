/**
 * 
 */
package com.wxxr.callhelper.qg.rpc;

import com.wxxr.javax.xml.bind.annotation.XmlRootElement;

/**
 * @author neillin
 *
 */
@XmlRootElement(name = "UrlLocator")
public class UrlLocator {
	private String homePageUrl;
	private String dailyNewsUrl;
	private String restServiceUrl;
	private String sshxRestUrl;
	/**
	 * @return the homePageUrl
	 */
	public String getHomePageUrl() {
		return homePageUrl;
	}
	/**
	 * @return the dailyNewsUrl
	 */
	public String getDailyNewsUrl() {
		return dailyNewsUrl;
	}
	/**
	 * @return the restServiceUrl
	 */
	public String getRestServiceUrl() {
		return restServiceUrl;
	}
	/**
	 * @param homePageUrl the homePageUrl to set
	 */
	public void setHomePageUrl(String homePageUrl) {
		this.homePageUrl = homePageUrl;
	}
	/**
	 * @param dailyNewsUrl the dailyNewsUrl to set
	 */
	public void setDailyNewsUrl(String dailyNewsUrl) {
		this.dailyNewsUrl = dailyNewsUrl;
	}
	/**
	 * @param restServiceUrl the restServiceUrl to set
	 */
	public void setRestServiceUrl(String restServiceUrl) {
		this.restServiceUrl = restServiceUrl;
	}
	public String getSshxRestUrl() {
		return sshxRestUrl;
	}
	public void setSshxRestUrl(String sshxRestUrl) {
		this.sshxRestUrl = sshxRestUrl;
	}
	
	
}
