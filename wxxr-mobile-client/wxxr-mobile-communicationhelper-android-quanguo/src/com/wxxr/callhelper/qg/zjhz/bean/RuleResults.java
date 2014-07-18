/*
 * @(#)RuleResults.java	 2006-1-21
 *
 * Copyright 2004-2005 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */
package com.wxxr.callhelper.qg.zjhz.bean;

import java.util.List;

import com.wxxr.phone.helper.workstation.SSHXMessageBO;

/**
 * @author neillin
 * 
 */
public class RuleResults {

	private List<SSHXMessageBO> results;

	/**
     * 
     */
	public RuleResults() {
		super();
	}

	/**
	 * @return Returns the results.
	 */
	public List<SSHXMessageBO> getResults() {
		return results;
	}

	/**
	 * @param results
	 *            The results to set.
	 */
	public void setResults(List<SSHXMessageBO> results) {
		if (this.results != results) {
			if (this.results != null) {
				this.results.clear();
			}
			this.results = results;
		}
	}

}
