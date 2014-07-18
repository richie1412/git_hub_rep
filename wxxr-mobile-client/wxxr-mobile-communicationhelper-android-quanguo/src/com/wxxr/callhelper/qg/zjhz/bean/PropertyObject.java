/*
 * @(#)PropertyObject.java	 2005-11-25
 *
 * Copyright 2004-2005 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */
package com.wxxr.callhelper.qg.zjhz.bean;

public class PropertyObject  {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long owner;

    /**
     * This constructor is created for TreeCache using only,
     * Do not use this constructor from your code
     *
     */
    public PropertyObject() {
		super();
	}

	public PropertyObject(Long key,Long user) {
        if(user == null){
            throw new IllegalArgumentException();
        }
        this.owner = user;
    }

    /**
     * @return Returns the owner.
     */
    public Long getOwner() {
        return owner;
    }

}
