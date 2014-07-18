/**
 * 
 */
package com.wxxr.callhelper.qg.zjhz.rules;

import java.util.List;

import com.wxxr.callhelper.qg.zjhz.bean.RuleResults;
import com.wxxr.callhelper.qg.zjhz.bean.ZJHZMessage;
import com.wxxr.phone.helper.workstation.SSHXMessageBO;

/**
 * @author wangyan
 *
 */
public class ZjhzMesssagePickupRule extends BaseZJHZPublishRule{

	@Override
	protected void doEvaluate(List data) {
        RuleResults results = checkResultset(data);
        List<SSHXMessageBO> messages = results.getResults();
        if(messages!=null && !messages.isEmpty()){
    	    ZJHZMessage zjhzMessage=new ZJHZMessage();
    	    zjhzMessage.setOriginal(messages.get(0));
    	    data.add(zjhzMessage);
        }

	}

}
