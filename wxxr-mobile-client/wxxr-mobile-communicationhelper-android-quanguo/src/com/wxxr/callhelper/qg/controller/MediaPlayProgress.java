package com.wxxr.callhelper.qg.controller;

import java.io.FileDescriptor;
import java.io.IOException;

import com.wxxr.callhelper.qg.bean.CallAudioTrack;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.core.log.api.Trace;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.SeekBar;
import android.widget.TextView;

public class MediaPlayProgress {
	private static final Trace log=Trace.register(MediaPlayProgress.class);
	
	private MediaPlayer mPlayer;
	private boolean flag;
	private SeekBar sb;
	private TextView durationView, currentTime;
	private Thread thread;
	private CallAudioTrack track;
	private final Context ctx;

	public MediaPlayProgress(Context context,MediaPlayer player, SeekBar sb,CallAudioTrack tr,TextView view, TextView gd_tv_current_time) {
		this.mPlayer = player;
		this.sb = sb;
		this.flag = true;
		this.track = tr;
		this.durationView = view;
		this.currentTime = gd_tv_current_time;
		this.ctx = context;
	}

	private Handler handler = new Handler() {
		private int position;
		private int progress;

		@Override
		public void handleMessage(Message msg) {
			if (!flag)
				return;
			position = mPlayer.getCurrentPosition();
			currentTime.setText(Tools.toTime(position));
			int duration = mPlayer.getDuration();
			sb.setMax(duration);
			if (duration > 0) {
//				int max = sb.getMax();
//				progress = position * max / duration;
				sb.setProgress(position);
			}
		}
	};

	public void startUpdate() {
		thread = new MyThread();
		thread.start();
	}

	private class MyThread extends Thread {
		@Override
		public void run() {
			while (flag) {

				if (!mPlayer.isPlaying()) {
					break;
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}

				handler.sendEmptyMessage(0);
			}
		}
	}
	
	public void cancle() {
		if(mPlayer != null){
			mPlayer.reset();
			mPlayer = null;
		}
		durationView.setText("00''00''''");
		currentTime.setText("00''00''''");
		flag = false;
		sb.setProgress(0);
		if(thread != null){
			thread.interrupt();
		}
	}
	
	public CallAudioTrack getPlayingTrack() {
		return this.track;
	}
	
	public int getDuration() {
		return this.mPlayer != null ? this.mPlayer.getDuration() : 0;
	}
	
	public int getCurrentPosition() {
		return this.mPlayer != null ? this.mPlayer.getCurrentPosition() : 0;
	}
	
	public void play() throws IOException {
		String filename = track.getDataFile().getCanonicalPath();
		if(log.isDebugEnabled()){
			log.debug("Going to play audio track :"+filename);
		}
		try {
			mPlayer.setDataSource(this.ctx,Uri.fromFile(track.getDataFile()));
			mPlayer.prepare();
			mPlayer.start();
			String duration = Tools.toTime(getDuration());
			durationView.setText(duration);
			startUpdate();
		}catch(IOException e){
			log.warn("Failed to play audio track :"+filename+", file size :"+track.getDataFile().length()+", read :"+track.getDataFile().canRead()+", write :"+track.getDataFile().canWrite());
			throw e;
		}
	}
	
	public void interruptThread(){
		if(thread != null){
			thread.interrupt();
			thread = null;
		}
	}
}
