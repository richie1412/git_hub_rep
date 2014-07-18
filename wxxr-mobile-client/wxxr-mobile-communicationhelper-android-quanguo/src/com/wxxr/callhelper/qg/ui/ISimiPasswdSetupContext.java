/**
 * 
 */
package com.wxxr.callhelper.qg.ui;

import android.app.Activity;

/**
 * @author neillin
 *
 */
public interface ISimiPasswdSetupContext {
	boolean updatePassword(String newPasswd);
	void doPhase1();
	void doPhase2(String newPasswd);
	void showMessageBox(String message);
	void cancelPasswdSetup();
	Activity getActivity();
}
