package com.wxxr.callhelper.qg.zjhz.bean;

import java.io.Serializable;


/**
 *The executing of TimerJob is triggerred by a Timer, the main purpose of
 *TimerJob here is to periodically evaluate the state of a cellar user.
 */
public interface TimerJob extends Serializable{
 
    Object getJobId();
}
 
