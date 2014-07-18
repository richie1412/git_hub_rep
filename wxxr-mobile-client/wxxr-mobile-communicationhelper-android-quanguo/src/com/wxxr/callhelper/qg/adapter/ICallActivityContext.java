package com.wxxr.callhelper.qg.adapter;

import android.app.Activity;

import com.wxxr.callhelper.qg.bean.CallAudioTrack;
import com.wxxr.callhelper.qg.controller.MediaPlayProgress;

public interface ICallActivityContext {
	/**
	 * return MediaPlayProgress if specific record is in playing, otherwise return null
	 * @param recordId
	 * @return
	 */
	MediaPlayProgress getTrackPlayProgress(String recordId);
	
	/**
	 * return MediaPlayProgress if specific track is in playing or was successfully started playing
	 * @param track
	 * @return
	 */
	MediaPlayProgress playAudio(CallAudioTrack track);
	
	boolean stopPlay(String recordId);
	
	Activity getActivity();
}
