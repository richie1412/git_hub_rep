/**
 * 
 */
package com.wxxr.callhelper.qg.service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.adapter.IDataChangedListener;
import com.wxxr.callhelper.qg.adapter.IObservableListDataProvider;
import com.wxxr.callhelper.qg.bean.CallAudioTrack;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.utils.ManagerSP;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
import com.wxxr.mobile.core.util.FileUtils;
import com.wxxr.mobile.core.util.StringUtils;
import com.wxxr.mobile.preference.api.IPreferenceManager;

/**
 * @author neillin
 *
 */
public class CallRecorderModule extends AbstractModule<ComHelperAppContext>
		implements ICallRecorderService {
	private static final Trace log = Trace.register(CallRecorderModule.class);
	
	private class MediaRecording implements IMediaRecording {
		private MediaRecorder recorder;
		private File outFile, tmpFile;
		private String recordId, phoneNumber;
		private int type;
		private Date timestamp;
		public MediaRecording(String phoneNumber, int type) throws IOException {
			this.recordId = this.outFile.getCanonicalPath();
			this.phoneNumber = phoneNumber;
			this.type = type;
			this.timestamp = new Date();
			this.outFile = generateOutputFile(phoneNumber, type, timestamp);
			this.tmpFile = new File(this.outFile,".tmp");
			this.recorder = new MediaRecorder();
			this.recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
			this.recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			this.recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			this.recorder.setOutputFile(this.tmpFile.getCanonicalPath());
		}
		
		public void startRecording() throws IOException {
			recorder.prepare();
			recorder.start();
		}
		
		@Override
		public String getRecordId() {
			return this.recordId;
		}

		@Override
		public void stopRecording() {
			if (log.isDebugEnabled()) {
				log.debug("------stopRecord--------");
			}
			try {
				recorder.stop();
				this.tmpFile.renameTo(this.outFile);
				CallAudioTrack track = new CallAudioTrack();
				track.setCallType(type);
				track.setDataFile(outFile);
				track.setPhoneNumber(phoneNumber != null ? phoneNumber :"未知");
				track.setTimestamp(timestamp);
				if(phoneNumber != null){
					track.setContactName(Tools.getContactsName(context.getApplication().getAndroidApplication(), phoneNumber));
				}
				synchronized(allTracks){
					allTracks.addFirst(track);
				}
				if(listener != null){
					listener.dataSetChanged();
				}
				if (log.isDebugEnabled()) {
					log.debug("a new call audio track from ["+phoneNumber+"] was recorded and save at : " + this.outFile.getCanonicalPath());
				}
			} catch (Exception e) {
				log.error(" recorder.stop() error!" ,e);
			}
			
		}
		
	}
	private boolean enabled;
	private boolean autoRecording;
	private File privateDir, externalDir;
	private BroadcastReceiver mExternalStorageReceiver;
	private SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
	private LinkedList<CallAudioTrack> allTracks = new LinkedList<CallAudioTrack>();
	private IDataChangedListener listener;
	private IObservableListDataProvider<CallAudioTrack> dataProvider = new IObservableListDataProvider<CallAudioTrack>() {
		
		@Override
		public int getItemCounts() {
			synchronized(allTracks){
				return allTracks.size();
			}
		}
		
		@Override
		public CallAudioTrack getItem(int i) {
			synchronized(allTracks){
				return allTracks.get(i);
			}
		}
		
		@Override
		public void onDataChanged(IDataChangedListener l) {
			listener = l;
		}
	};

	/* (non-Javadoc)
	 * @see com.wxxr.callhelper.service.ICallRecorderService#isCallRecorderEnabled()
	 */
	@Override
	public boolean isCallRecorderEnabled() {
		return enabled;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.callhelper.service.ICallRecorderService#isAutoRecording()
	 */
	@Override
	public boolean isAutoRecording() {
		return autoRecording;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.callhelper.service.ICallRecorderService#setCallRecorderEnabled(boolean)
	 */
	@Override
	public void setCallRecorderEnabled(boolean bool) {
		if(this.enabled != bool){
			this.enabled = bool;
			if(this.enabled){
				ManagerSP.getInstance().update(Constant.CALLRECORDER_OPEN_CLOSE, 0);
			}else{
				ManagerSP.getInstance().update(Constant.CALLRECORDER_OPEN_CLOSE, 1);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.wxxr.callhelper.service.ICallRecorderService#setAutoRecording(boolean)
	 */
	@Override
	public void setAutoRecording(boolean bool) {
		if(this.autoRecording !=  bool){
			this.autoRecording = bool;
			if(this.autoRecording){
				ManagerSP.getInstance().update(Constant.CALLRECORD_SETTING, 1);
			}else{
				ManagerSP.getInstance().update(Constant.CALLRECORD_SETTING, 0);
			}
		}
	}

	/**
	 * 
	 * @param phoneNumber
	 * @param type : 0 -> Outgoging call, 1 -> incoming call
	 * @return
	 */
	@Override
	public IMediaRecording startCallRecord(String phoneNumber, int type) throws IOException{
		return startRecord(phoneNumber, type);
	}


	@Override
	protected void initServiceDependency() {
		addRequiredService(IPreferenceManager.class);
	}

	@Override
	protected void startService() {
		this.enabled = (ManagerSP.getInstance().get(Constant.CALLRECORDER_OPEN_CLOSE, 0) == 0);
		this.autoRecording = (ManagerSP.getInstance().get(Constant.CALLRECORD_SETTING, 0) == 1);
		updateExternalStorageState();
		context.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				moveTracks2ExternalStorage();
				refreshTracks();
				startWatchingExternalStorage();
			}
		}, 100, TimeUnit.MILLISECONDS);
		context.registerService(ICallRecorderService.class, this);
	}

	@Override
	protected void stopService() {
		context.unregisterService(ICallRecorderService.class, this);
		stopWatchingExternalStorage();
	}

   protected boolean updateExternalStorageState() {
	    boolean changed = false;
    	if(this.privateDir == null){
    		this.privateDir = AppUtils.getFramework().getAndroidApplication().getDir("MyCallRecorder", Context.MODE_PRIVATE);
    		if(this.privateDir.exists() == false){
    			this.privateDir.mkdirs();
    		}
    		changed = true;
    	}
        String state = Environment.getExternalStorageState();
        File oldDir = this.externalDir;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            this.externalDir = new File(Environment.getExternalStorageDirectory(),"MyCallRecorder");
            if(this.externalDir.exists() == false){
            	if(!this.externalDir.mkdirs()){
            		log.warn("Failed to create folder :"+this.externalDir.getAbsolutePath());
            		this.externalDir = null;
            	}
            }
            if((externalDir != null)&&(oldDir == null)){
            	changed = true;
            }
        } else {
           this.externalDir = null;
           if(oldDir != null){
           		changed = true;
           }
        }
        return changed;
    }

    protected void startWatchingExternalStorage() {
        mExternalStorageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("test", "Storage: " + intent.getData());
                if(updateExternalStorageState()){
                	CallRecorderModule.this.context.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							refreshTracks();
							if(listener != null){
								listener.dataSetChanged();
							}
						}
					}, 100, TimeUnit.MILLISECONDS);
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        AppUtils.getFramework().getAndroidApplication().registerReceiver(mExternalStorageReceiver, filter);
        updateExternalStorageState();
    }

    protected void stopWatchingExternalStorage() {
    	AppUtils.getFramework().getAndroidApplication().unregisterReceiver(mExternalStorageReceiver);
    }

	protected MediaRecording startRecord(String phoneNumber, int callType) throws IOException{

		if (log.isDebugEnabled()) {
			log.debug("------startRecord--------");
		}
		try {
			MediaRecording recording = new MediaRecording(phoneNumber, callType);
			recording.startRecording();
			return recording;
		} catch (IOException e) {
			log.error(" recorder.startRecord() error!" ,e);
			throw e;
		}

	}

	public File generateOutputFile(String phoneNumber, int callType, Date timestamp){
		String subfix = callType == CALL_TYPE_INCOMING ? "coming" : "going";
		phoneNumber = StringUtils.trimToNull(phoneNumber);
		phoneNumber = phoneNumber == null ? "未知" : phoneNumber;
		File dir = this.externalDir != null ? this.externalDir : this.privateDir;
		String fname = new StringBuffer(format.format(timestamp)).append("_").
				append(phoneNumber).append("_").
				append(subfix).append(".3gp").toString();
		File f = new File(dir,fname);
		if(!f.getParentFile().exists()){
			f.getParentFile().mkdirs();
		}
		return f;
	}
	
	private void populateAudioTracks(File[] files, List<CallAudioTrack> list){
		if(files == null){
			return;
		}
		for (File f : files) {
			CallAudioTrack track = createData(f);
			if(track != null){
				list.add(track);
			}
		}
	}

	protected void moveTracks2ExternalStorage() {
		if(this.externalDir == null){
			return;
		}
		if(this.privateDir != null){
			File[] files = this.privateDir.listFiles();
			if(files != null){
				for (File file : files) {
					File targetFile = new File(this.externalDir,file.getName());
					try {
						FileUtils.copyFile(file, targetFile);
						if(!file.delete()){
							log.warn("Failed to delete audio track :"+file.getAbsolutePath()+" after was copied to external storage");
						}
					} catch (IOException e) {
						log.warn("Failed to copy audio track :"+file.getAbsolutePath()+" to external storage", e);
					}
				}
			}
		}
	}
	public void refreshTracks() {
		synchronized(this.allTracks){
			LinkedList<CallAudioTrack> result = this.allTracks;
			result.clear();
			if(this.privateDir != null){
				populateAudioTracks( this.privateDir.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String filename) {
						return filename.endsWith(".3gp");
					}
				}), result);
			}
			if(this.externalDir != null){
				populateAudioTracks( this.externalDir.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String filename) {
						return filename.endsWith(".3gp");
					}
				}), result);			
			}
	
			Collections.sort(result, new Comparator<CallAudioTrack>() {
	
				@Override
				public int compare(CallAudioTrack c1, CallAudioTrack c2) {
					if (c1 != null && c2 != null) {
						Date l1 = c1.getTimestamp();
						Date l2 = c2.getTimestamp();
						if (l1.after(l2))
							return -1;// 由大到小
						else
							return 1;// 由小到大
					}
					return 0;// 相等
				}
			});
		}
	}

	protected CallAudioTrack createData(File file){
		if(log.isDebugEnabled()){
			log.debug("processing audio file :"+file.getAbsolutePath());
		}
		String infos[] = file.getName().split("_");
		if (infos.length != 3) {
			log.info("invalid audio file name :"+file.getName());
			return null;
		}

		CallAudioTrack crBean = new CallAudioTrack();
		crBean.setPhoneNumber(infos[1]);
		String status = infos[2].substring(0, infos[2].indexOf("."));
		if("未知".equals(infos[1]) == false){
			String userName = Tools.getContactsName(context.getApplication().getAndroidApplication(), infos[1]);
			crBean.setContactName(userName);
		}
		crBean.setDataFile(file);
		crBean.setCallType("coming".equals(status) ? CALL_TYPE_INCOMING : CALL_TYPE_OUTGOING);
		//以前都是按照 MMddHHmmss 格式保存的文件， 跨年有问题， 
		//保存的地方不做修改，只是修改时间戳，不然排序有问题
		try {			
			Date date = format.parse(infos[0]);		
		} catch (Exception e) {
			log.warn("invalid audio file name :"+file.getName()+", missing timestamp !");
			return null;
		}
		
		crBean.setTimestamp(new Date(file.lastModified()));
		return crBean;
	}
	
	@Override
	public IObservableListDataProvider<CallAudioTrack> getListDataProvider() {
		return this.dataProvider;
	}

	@Override
	public boolean removeAudioTrack(CallAudioTrack track) {
		boolean val = false;
		synchronized (allTracks) {
			val = allTracks.remove(track);
		}
		track.getDataFile().delete();
		if(val &&(listener != null)){
			listener.dataSetChanged();
		}
		return val;
	}
}
