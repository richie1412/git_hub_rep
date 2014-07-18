/**
 * 
 */
package com.wxxr.callhelper.qg.ui;


import android.support.v4.app.Fragment;

import com.wxxr.callhelper.qg.bean.HtmlMessageBean;


/**
 * @author neillin
 *
 */
public interface ReaderPanelContext {
	void closeFragement(Fragment fragement);
	void onPushMsgClicked(HtmlMessageBean html);
	void showTitle();
	void hideTitle();
}
