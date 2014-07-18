/*
 * @(#)BaseBJHZPublishRule.java	 2006-1-21
 *
 * Copyright 2004-2005 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */
package com.wxxr.callhelper.qg.zjhz.rules;

import java.util.List;

import com.wxxr.callhelper.qg.zjhz.bean.RuleResults;
import com.wxxr.callhelper.qg.zjhz.bean.ZJHZSMProcessingContext;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.rule.impl.RuleHelper;
import com.wxxr.phone.helper.workstation.SSHXMessageBO;

/**
 * @author neillin
 *
 */
public abstract class BaseZJHZPublishRule extends AbstractPublishRule {


	private static final Trace log = Trace.register(BaseZJHZPublishRule.class);
	

    protected ZJHZSMProcessingContext getContext(List data) {
        return (ZJHZSMProcessingContext)RuleHelper.getObject(data,ZJHZSMProcessingContext.class);
    }


    public final void evaluate(List data) {

       doEvaluate(data);
		
    }
    
    protected abstract void doEvaluate(List data);
    

    protected RuleResults checkResultset(List data) {
        RuleResults results = (RuleResults)RuleHelper.getObject(data,RuleResults.class);
        if(results == null){
            results = new RuleResults();
            results.setResults(RuleHelper.getObjects(data,SSHXMessageBO.class));
            data.add(results);
        }
        return results;
    }

}
