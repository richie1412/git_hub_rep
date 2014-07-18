/*
 * @(#)FNV32Hash.java	 2013-6-19
 *
 * Copyright 2004-2013 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */

package com.wxxr.callhelper.qg.utils;

import java.math.BigInteger;

/**
 * @class desc A FNV32Hash.
 * 
 * @author taozhicheng
 * @version $Revision: 1.1 $
 * @created time 2013-6-19
 */
public class FNV32Hash {
   private static final long OFFSET_BASIS = 2166136261L; // 32λoffset basis
   private static final long PRIME = 16777619; // 32λprime

   private static final BigInteger PRIME32 = new BigInteger("01000193", 16);
 
   public static long hash1(byte[] src) {   
      long hash = OFFSET_BASIS;   
      for (byte b : src) {   
         hash *= PRIME; 
         hash ^= b;   
      }   
      return hash;   
  }
   
   public static long rehash1(byte[] src) {   
      long hash = OFFSET_BASIS;   
      for (byte b : src) {   
         hash *= PRIME32.longValue(); 
         hash ^= b;   
      }   
      return hash;   
  }
   
   public static long hash1a(byte[] src) {   
      long hash = OFFSET_BASIS;   
      for (byte b : src) {   
          hash ^= b;   
          hash *= PRIME;   
      }   
      return hash;   
  }   
}
