package com.wxxr.test.business;

import java.util.LinkedList;

public interface IBusinessManager {

	/**
	 * 业务名称
	 * @param name
	 * @return
	 */
	public String[] getBusinessNames(String name);
	
	/**
	 * 业务标识
	 * @param context
	 * @param icon
	 * @return
	 */
	public LinkedList<Integer> getBusinessIcon(String name);
	
	/**
	 * 业务代码
	 * @param name
	 * @return
	 */
	public String[] getBusinessCodes(String name);
}
