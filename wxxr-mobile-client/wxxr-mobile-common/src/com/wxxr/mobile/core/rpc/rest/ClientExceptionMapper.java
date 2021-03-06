package com.wxxr.mobile.core.rpc.rest;
/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jul 28, 2012
 */
public interface ClientExceptionMapper<E extends Throwable>
{
      RuntimeException toException(E exception);
}