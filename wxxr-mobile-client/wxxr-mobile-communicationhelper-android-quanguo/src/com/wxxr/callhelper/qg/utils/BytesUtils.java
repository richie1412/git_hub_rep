/**
 * 
 */
package com.wxxr.callhelper.qg.utils;

/**
 * @author wangyan
 *
 */
public class BytesUtils {
	// long类型转成byte数组
		public static byte[] longToByte(long number) {
			long temp = number;
			byte[] b = new byte[8];
			for (int i = 0; i < b.length; i++) {
				b[i] = new Long(temp & 0xff).byteValue();//
				temp = temp >> 8; // 向右移8位
			}
			return b;
		}
		    
		// byte数组转成long
		public static long byteToLong(byte[] b) {
			long s = 0;
			long s0 = b[0] & 0xff;// 最低位
			long s1 = b[1] & 0xff;
			long s2 = b[2] & 0xff;
			long s3 = b[3] & 0xff;
			long s4 = b[4] & 0xff;// 最低位
			long s5 = b[5] & 0xff;
			long s6 = b[6] & 0xff;
			long s7 = b[7] & 0xff;

			// s0不变
			s1 <<= 8;
			s2 <<= 16;
			s3 <<= 24;
			s4 <<= 8 * 4;
			s5 <<= 8 * 5;
			s6 <<= 8 * 6;
			s7 <<= 8 * 7;
			s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
			return s;
		}
}
