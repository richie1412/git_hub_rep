/*
 * @(#)PriorityRule.java	 2006-2-5
 *
 * Copyright 2004-2005 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */
package com.wxxr.callhelper.qg.zjhz.rules;

import java.util.Comparator;

import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.phone.helper.workstation.SSHXMessageBO;

/**
 * 
 * @author neillin
 * 消息排序规则，消息列表到达此规则后会根据消息结构中的优先级有高到底依次对消息进行排序,只根据消息的优先权进行排序
 */
public class PriorityOnlyRule extends PriorityRule {

	private static final Trace log = Trace.register(PriorityOnlyRule.class);
	protected Comparator qComparator = new Comparator() {
		public boolean equals(Object o) {
			if (!(o instanceof Comparator))
				return false;
			else
				return true;
		}

		@Override
		public int compare(Object lhs, Object rhs) {
			SSHXMessageBO msg0 = (SSHXMessageBO) lhs;
			SSHXMessageBO msg1 = (SSHXMessageBO) rhs;
			int val = msg0.getPriority() - msg1.getPriority();
			if (val == 0) {
				long adsId0 = msg0.getAdsId()== null ? 0 : msg0.getAdsId();
				long adsId1 = msg1.getAdsId()== null ? 0 : msg1.getAdsId();
				val = (int) (adsId0 - adsId1);
			}
			return val;
		}
	};

	public PriorityOnlyRule() {
		super();
	}

	protected Comparator getComparator() {
		return qComparator;
	}

}
