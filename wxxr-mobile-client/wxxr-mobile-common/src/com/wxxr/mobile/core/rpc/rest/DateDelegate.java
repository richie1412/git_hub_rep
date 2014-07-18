package com.wxxr.mobile.core.rpc.rest;
import javax.ws.rs.ext.RuntimeDelegate;

import com.wxxr.mobile.core.util.DateUtil;

import java.util.Date;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1.1 $
 */
public class DateDelegate implements RuntimeDelegate.HeaderDelegate<Date>
{
   @Override
   public Date fromString(String value)
   {
      if (value == null) throw new IllegalArgumentException("param was null");
      return DateUtil.parseDate(value);
   }

   @Override
   public String toString(Date value)
   {
      if (value == null) throw new IllegalArgumentException("param was null");
      return DateUtil.formatDate(value);
   }
}