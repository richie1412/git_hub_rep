/**
 * 
 */
package com.wxxr.callhelper.qg.zjhz.rules;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.wxxr.callhelper.qg.zjhz.bean.RuleResults;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.phone.helper.workstation.SSHXMessageBO;

/**
 * @author wangyan
 *
 */
public class StartDateRule extends BaseZJHZPublishRule{
    private static final Trace log = Trace.register(PriorityRule.class);

	@Override
	protected void doEvaluate(List data) {
		RuleResults results = checkResultset(data);
		if ((results.getResults() == null) || (results.getResults().isEmpty())) {
			if (log.isDebugEnabled()) {
				log.debug("Result list is empty, no farther processing neccessary!");
			}
			return;
		}
		List<SSHXMessageBO> messages = results.getResults();
		for(Iterator<SSHXMessageBO> iterator=messages.iterator();iterator.hasNext();){
			SSHXMessageBO sshxMessageBO=iterator.next();
			if(sshxMessageBO.getStartDate()!=null && sshxMessageBO.getStartDate().compareTo(new Date())>0){
				iterator.remove();
			}
		}
	}

}
