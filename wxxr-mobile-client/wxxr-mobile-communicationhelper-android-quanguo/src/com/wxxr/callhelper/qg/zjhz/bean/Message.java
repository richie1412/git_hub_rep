/*
 * $Id: Message.java,v 1.1 2014/02/21 10:05:47 yangrunfei Exp $
 * 
 * Copyright (C) Hygensoft Inc. All rights reserved.
 * 
 * Created on Nov 11, 2003
 */
package com.wxxr.callhelper.qg.zjhz.bean;

import java.io.Serializable;


/**
 * Type description go here
 * 
 * @author Neil Lin
 * @version $Revision: 1.1 $
 *
 */
public interface Message extends Serializable {
	long getMessageID();
	String getMessageType();
}
