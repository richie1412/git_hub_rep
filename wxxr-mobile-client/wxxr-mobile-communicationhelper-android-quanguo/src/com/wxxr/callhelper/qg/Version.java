/*
 * @(#)Version.java	 2005-8-16
 *
 * Copyright 2004-2005 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */
package com.wxxr.callhelper.qg;

public class Version {
    
    private static final String buildDate="201309181509";
    private static final String buildNumber="wangyan201309181509";
    private static final String versionNumber="2.0";
    private static final String versionName="ComHelper_gd";

    public Version() {
        super();
    }

    /**
     * @return Returns the buildDate.
     */
    public static String getBuildDate() {
        return buildDate;
    }

    /**
     * @return Returns the buildNumber.
     */
    public static String getBuildNumber() {
        return buildNumber;
    }

    /**
     * @return Returns the versionName.
     */
    public static String getVersionName() {
        return versionName;
    }

    /**
     * @return Returns the versionNumber.
     */
    public static String getVersionNumber() {
        return versionNumber;
    }

}
