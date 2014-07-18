/**
 * 
 */
package com.wxxr.callhelper.qg;

import android.content.Context;


/**
 * @author neillin
 *
 */
public interface ITaskHandler {
	Object doExecute(Context context, Object... args);
}
