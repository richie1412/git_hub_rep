/**
 * 
 */
package com.wxxr.callhelper.qg.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.wxxr.callhelper.qg.adapter.IObservableListDataProvider;
import com.wxxr.callhelper.qg.bean.CallAudioTrack;

/**
 * @author neillin
 *
 */
public interface ICallRecorderService {
	public static final int CALL_TYPE_OUTGOING = 0;
	public static final int CALL_TYPE_INCOMING = 1;
	

	boolean isCallRecorderEnabled();
	boolean isAutoRecording();
	void setCallRecorderEnabled(boolean bool);
	void setAutoRecording(boolean bool);
	
	IMediaRecording startCallRecord(String phoneNumber, int type) throws IOException;
	
	IObservableListDataProvider<CallAudioTrack> getListDataProvider();
	
	boolean removeAudioTrack(CallAudioTrack track);
	
	public void refreshTracks();
	
	public File generateOutputFile(String phoneNumber, int callType, Date timestamp);
}
