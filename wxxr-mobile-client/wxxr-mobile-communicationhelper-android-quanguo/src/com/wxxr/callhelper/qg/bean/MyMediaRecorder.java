package com.wxxr.callhelper.qg.bean;

import java.io.File;
import java.util.Date;

import com.wxxr.callhelper.qg.service.NewCallRecordService.RecordState;

import android.media.MediaRecorder;

public class MyMediaRecorder extends MediaRecorder {
	private String fileName;
	public Date date;
	private RecordState state;
	public File targetFile;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public RecordState getState() {
		return state;
	}

	public void setState(RecordState state) {
		this.state = state;
	}

}
