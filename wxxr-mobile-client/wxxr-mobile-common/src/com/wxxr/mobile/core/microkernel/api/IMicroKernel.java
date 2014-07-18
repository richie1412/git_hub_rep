/*
 * @(#)IMicroKernal.java May 18, 2011 Copyright 2004-2011 WXXR Network
 * Technology Co. Ltd. All rights reserved. WXXR PROPRIETARY/CONFIDENTIAL.
 */

package com.wxxr.mobile.core.microkernel.api;

public interface IMicroKernel<C extends IKernelContext, T extends IKernelModule<C>> {

   public void registerKernelModule(T module);

   public void unregisterKernelModule(T module);

}