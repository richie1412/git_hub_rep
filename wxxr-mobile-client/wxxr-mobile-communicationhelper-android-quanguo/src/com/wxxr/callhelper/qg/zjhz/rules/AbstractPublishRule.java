/*
 * @(#)AbstractActiveRule.java	 2006-1-21
 *
 * Copyright 2004-2005 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */
package com.wxxr.callhelper.qg.zjhz.rules;

import java.util.List;

import com.wxxr.callhelper.qg.zjhz.bean.StatisticsInfo;
import com.wxxr.callhelper.qg.zjhz.bean.UserAttributes;
import com.wxxr.mobile.rule.impl.AbstractActiveRule;
import com.wxxr.mobile.rule.impl.RuleHelper;

/**
 * @author neillin
 *
 */
public abstract class AbstractPublishRule extends AbstractActiveRule  {
	
    protected StatisticsInfo getUserStatisticsInfo(List data){
    	return (StatisticsInfo)RuleHelper.getObject(data, StatisticsInfo.class);
    }
    
    protected UserAttributes getUserAttributes(List data){
    	return (UserAttributes)RuleHelper.getObject(data, UserAttributes.class);   	
    }
}
