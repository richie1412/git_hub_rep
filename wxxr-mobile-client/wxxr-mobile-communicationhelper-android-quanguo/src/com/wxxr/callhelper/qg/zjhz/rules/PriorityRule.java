/*
 * @(#)PriorityRule.java	 2006-2-5
 *
 * Copyright 2004-2005 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */
package com.wxxr.callhelper.qg.zjhz.rules;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.wxxr.callhelper.qg.zjhz.bean.CellarPhoneUser;
import com.wxxr.callhelper.qg.zjhz.bean.RuleResults;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.rule.impl.RuleHelper;
import com.wxxr.phone.helper.workstation.SSHXMessageBO;

/**
 * 消息排序规则，消息列表到达此规则后会根据消息结构中的优先级有高到底依次对消息进行排序
 * @author neillin
 *
 */
public class PriorityRule extends BaseZJHZPublishRule {
    
    private static final Trace log = Trace.register(PriorityRule.class);
    private final int INDEX = 121;
    protected Comparator qComparator = new Comparator() {
        public boolean equals(Object o) {
        	if(!(o instanceof Comparator))
                return false;
            else 
            	return true;

        }

        public int compare(Object arg0, Object arg1) {
            SSHXMessageBO msg0 = (SSHXMessageBO) arg0;
            SSHXMessageBO msg1 =(SSHXMessageBO) arg1;
            int val = msg0.getPriority() - msg1.getPriority();
            if(val == 0){
                long time0 = msg0.getStartDate().getTime();
                long time1 = msg1.getStartDate().getTime();         
                val = (int)(time0 - time1);
            }
            return val;
        }
    };
    
    public PriorityRule() {
        super();
    }

    public void doEvaluate(List data) {
        RuleResults results = checkResultset(data);
        CellarPhoneUser user = (CellarPhoneUser)RuleHelper.getObject(data,CellarPhoneUser.class);
        if((results.getResults()== null)||(results.getResults().isEmpty())){
        	if(log.isDebugEnabled()){
        		log.debug("Result list is empty, no farther processing neccessary!");
        	}
            return;
        }


        List<SSHXMessageBO> messages = results.getResults();
       
        Collections.sort(messages,getComparator());
        results.setResults(messages);
        
        // end debug output
    }
    
    protected Comparator getComparator() {
    	return qComparator;
    }

}
