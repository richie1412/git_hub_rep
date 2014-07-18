/**
 * 
 */
package com.wxxr.callhelper.qg;

import java.io.IOException;

/**
 * @author wangxuyang
 *
 */
public interface ContentCleanStategy {
	boolean isCleanable(CleanContext context);
	void executeClean(CleanContext context) throws IOException;
}
