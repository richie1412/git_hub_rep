package com.wxxr.callhelper.qg.ui;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.adapter.CallRecordAdapter;
import com.wxxr.callhelper.qg.adapter.ICallActivityContext;
import com.wxxr.callhelper.qg.bean.CallAudioTrack;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.controller.MediaPlayProgress;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.service.ICallRecorderService;
import com.wxxr.callhelper.qg.ui.gd.GDCallRecordSettingActivity;
import com.wxxr.callhelper.qg.ui.gd.GDItemLongListDialog;
import com.wxxr.callhelper.qg.utils.ManagerSP;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.core.log.api.Trace;

public class CallRecordActivity extends BaseActivity {
	private ListView lv_callrecord;
	private LinearLayout ll_callrecord_empty, ll_callrecord_all;
	private TextView tv_titlebar_name, tv_callrecord_time, gd_tv_callrecord_current_position;
	private ImageView iv_titlebar_set, iv_guide_callrecorder;
	private SeekBar sb_callrecord;
	private CallRecordAdapter adapter;
	private PopupWindow pop;
	private MediaPlayProgress sbController;
	private MediaPlayer mPlayer = new MediaPlayer();

	private static final Trace log = Trace.register(CallRecordActivity.class);
	// private Map<Integer, Boolean> maps;
	private int longPosition;
	private int guide_callrecorder;
	private ManagerSP sp;
	// private boolean isRefresh;
	// private LinearLayout ll_callrecorder_guide, ;
	private LinearLayout ll_titlebar_setting;
	private Intent intent = null;
	
	private ICallActivityContext ctx = new ICallActivityContext() {

		@Override
		public MediaPlayProgress playAudio(CallAudioTrack track) {
			try {
				playAudioTrack(track);
				return sbController;
			} catch (Throwable e) {
				log.error("Failed to play audio track :" + track.getRecordId(),
						e);
				return null;
			}
		}

		@Override
		public Activity getActivity() {
			return CallRecordActivity.this;
		}

		@Override
		public synchronized MediaPlayProgress getTrackPlayProgress(
				String recordId) {
			if ((sbController != null)
					&& sbController.getPlayingTrack().getRecordId()
							.equals(recordId)) {
				return sbController;
			}
			return null;
		}

		@Override
		public boolean stopPlay(String recordId) {
			if ((sbController != null)
					&& sbController.getPlayingTrack().getRecordId()
							.equals(recordId)) {
				sbController.cancle();
				sbController = null;
				adapter.notifyDataSetChanged();
				return true;
			}
			return false;
		}
	};
	private RelativeLayout rl_sb_callrecord;
	private ImageView gd_iv_seekbar_btn;
	private LinearLayout gd_ll_menu_space;
	private TextView gd_tv_menu_all, gd_tv_menu_cancel, gd_tv_menu_del;
	private CallRecordActivity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.callrecord);

		if(null == activity){
			activity = this;
		}
		
		findView();
		((com.wxxr.callhelper.qg.widget.BottomTabBar)findViewById(R.id.home_bottom_tabbar)).setActivtiy(this,0);
		processLogic();

	}

	private void setMenu() {
		View view = View.inflate(this, R.layout.menu, null);
		pop = getMenu(this, view);
		pop.showAtLocation(findViewById(R.id.rl_menu), Gravity.BOTTOM, 0, 0);
		view.findViewById(R.id.ll_all_choose).setOnClickListener(this);
		view.findViewById(R.id.ll_delete).setOnClickListener(this);
		view.findViewById(R.id.ll_cancle).setOnClickListener(this);
		gd_tv_menu_all = (TextView) view.findViewById(R.id.gd_tv_menu_all);
		gd_tv_menu_cancel = (TextView) view.findViewById(R.id.gd_tv_menu_cancel);
		gd_tv_menu_del = (TextView) view.findViewById(R.id.gd_tv_menu_del);
	}

	/**
	 * 单选 &全选状态
	 */
	public void pressAll(){
		gd_tv_menu_all.setTextColor(getResources().getColor(R.color.gd_titlebar_text));
		gd_tv_menu_cancel.setTextColor(getResources().getColor(R.color.gd_titlebar_text));
		gd_tv_menu_del.setTextColor(getResources().getColor(R.color.gd_titlebar_text));
	}
	
	/**
	 * 初始化&取消全选状态
	 */
	public void pressCancelAll(){
		gd_tv_menu_all.setTextColor(getResources().getColor(R.color.gd_titlebar_text));
		gd_tv_menu_cancel.setTextColor(getResources().getColor(R.color.gd_item_eighty));
		gd_tv_menu_del.setTextColor(getResources().getColor(R.color.gd_item_eighty));
	}
	
	public void findView() {

		// isRefresh = false;

		sp = ManagerSP.getInstance();
		guide_callrecorder = sp.get(Constant.GUIDE_CALLRECORDER, 0);

		tv_titlebar_name = (TextView) findViewById(R.id.tv_titlebar_name);
		findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(this);
		findViewById(R.id.gd_iv_titlebar_right).setOnClickListener(this);
		
//		iv_titlebar_set = (ImageView) findViewById(R.id.iv_titlebar_set);
//		ll_titlebar_setting = (LinearLayout) findViewById(R.id.ll_titlebar_setting);

		lv_callrecord = (ListView) findViewById(R.id.lv_callrecord);
		ll_callrecord_empty = (LinearLayout) findViewById(R.id.ll_callrecord_empty);

		ll_callrecord_all = (LinearLayout) findViewById(R.id.ll_callrecord_all);

		tv_callrecord_time = (TextView) findViewById(R.id.tv_callrecord_time);
		gd_tv_callrecord_current_position = (TextView) findViewById(R.id.gd_tv_callrecord_current_position);

		sb_callrecord = (SeekBar) findViewById(R.id.sb_callrecord);
		
		rl_sb_callrecord = (RelativeLayout) findViewById(R.id.rl_sb_callrecord);
		
		gd_iv_seekbar_btn = (ImageView) findViewById(R.id.gd_iv_seekbar_btn);
		
		gd_ll_menu_space = (LinearLayout) findViewById(R.id.gd_ll_menu_space);
		
	}
	
	public void processLogic() {

		// if (guide_callrecorder == 0) {
		// ll_callrecorder_guide.setVisibility(View.VISIBLE);
		// iv_guide_callrecorder.setBackgroundResource(R.drawable.callrecorder_guide_bg);
		// }

		// lists = new ArrayList<CallRecordBean>();
		// maps = new HashMap<Integer, Boolean>();

		tv_titlebar_name.setText("通话录音");
//		ll_titlebar_setting.setVisibility(View.VISIBLE);
//		iv_titlebar_set.setOnClickListener(this);
		adapter = new CallRecordAdapter(ctx, rl_sb_callrecord, activity, getService(
				ICallRecorderService.class).getListDataProvider());
		lv_callrecord.setAdapter(adapter);
		
		lv_callrecord.setOnItemLongClickListener(new MyOnItemLongClickListener());
		
		gd_iv_seekbar_btn.setOnClickListener(this);
		
		mPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// tv_callrecord_time.setText("00''00");
				if (sbController != null) {
					sbController.cancle();
					sbController = null;
					adapter.notifyDataSetChanged();
					hideSeekbar();
				}
			}
		});

		sb_callrecord.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_UP) {

					if (mPlayer.isPlaying()) {
						return false;
					}
				}
				return true;

			}
		});

		sb_callrecord.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				seekBar.setMax(mPlayer.getDuration());
				mPlayer.seekTo(seekBar.getProgress());
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
			}
		});
		
	}

	protected synchronized void playAudioTrack(CallAudioTrack track)
			throws IOException {
		if (sbController != null) {
			if (sbController.getPlayingTrack().getRecordId()
					.equals(track.getRecordId())) {
				return;
			} else {
				sbController.cancle();
				sbController = null;
			}
		}
		sbController = new MediaPlayProgress(this, mPlayer, this.sb_callrecord,
				track, tv_callrecord_time, gd_tv_callrecord_current_position);
		sbController.play();
		adapter.notifyDataSetChanged();
	}
	
	private class MyOnItemLongClickListener implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {

			longPosition = position;
			CallAudioTrack track = (CallAudioTrack) adapter.getItem(position);
			intent = new Intent(CallRecordActivity.this, GDItemLongListDialog.class);
			intent.putExtra("phoneNumber", track.getPhoneNumber());
			int month=track.getTimestamp().getMonth();
			intent.putExtra("smsmonth",month>9 ? ""+month:"0"+month );
			intent.putExtra(Constant.DIALOG_KEY, Constant.LONG_DELETE_RECORD);
			intent.putExtra(Constant.DIALOG_CONTENT, "删除联系人通话录音记录？");
			startActivityForResult(intent, position);
			view.setBackgroundColor(getResources().getColor(R.color.gd_callrecord_long_press));

			return false;
		}

	}
	
	/**
	 * 显示进度框
	 */
	public void showSeekbar(){
		((com.wxxr.callhelper.qg.widget.BottomTabBar)findViewById(R.id.home_bottom_tabbar)).setVisibility(View.GONE);
		Tools.pushIn(getApplicationContext(), rl_sb_callrecord);
		rl_sb_callrecord.setVisibility(View.VISIBLE);
		gd_iv_seekbar_btn.setBackgroundResource(R.drawable.gd_seekbar_pause);
	}
	
	/**
	 * 隐藏进度框
	 */
	public void hideSeekbar(){
		((com.wxxr.callhelper.qg.widget.BottomTabBar)findViewById(R.id.home_bottom_tabbar)).setVisibility(View.VISIBLE);
		Tools.pushOut(getApplicationContext(), rl_sb_callrecord);
		rl_sb_callrecord.setVisibility(View.GONE);
	}
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {

//		case R.id.iv_titlebar_set:
//			if (guide_callrecorder == 0) {
//				sp.update(Constant.GUIDE_CALLRECORDER, 1);
//				// ll_callrecorder_guide.setVisibility(View.GONE);
//			}
//			intent = new Intent(this, ConfirmDialogActivity.class);
//			intent.putExtra(Constant.DIALOG_KEY,
//					Constant.CALLRECORDER_SETTING_DIALOG);
//			startActivity(intent);
//			break;

		case R.id.gd_iv_titlebar_left:
			finish();
			break;
			
		case R.id.gd_iv_titlebar_right:
			startActivity(new Intent(this, GDCallRecordSettingActivity.class));
			break;
		
		case R.id.ll_all_choose:
			adapter.selectAll();
			// showMenu();
			break;

		case R.id.ll_delete:
			if (isDelete()) {
				intent = new Intent(this, ConfirmDialogActivity.class);
				intent.putExtra(Constant.DIALOG_KEY, Constant.DELETE_RECORD);
				intent.putExtra(Constant.DIALOG_CONTENT, "删除联系人通话录音记录？");
				startActivityForResult(intent, 100);
			}
			break;

		case R.id.ll_cancle:
			adapter.selectNone();
			break;
			
		case R.id.gd_iv_seekbar_btn:
			if(mPlayer.isPlaying()){
				mPlayer.pause();
				sbController.interruptThread();
				gd_iv_seekbar_btn.setBackgroundResource(R.drawable.gd_seekbar_play);
			}else{
				mPlayer.start();
				sbController.startUpdate();
				gd_iv_seekbar_btn.setBackgroundResource(R.drawable.gd_seekbar_pause);
			}
			break;

		}
	}

	private boolean isDelete() {
		return adapter.getSelected().isEmpty() == false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(null != sbController){
			hideSeekbar();
			this.sbController.cancle();
			this.sbController = null;
		}
		
		switch (resultCode) {

		case Constant.DELETE_RECORD:
			ArrayList<CallAudioTrack> tracks = new ArrayList<CallAudioTrack>();
			for (Integer key : adapter.getSelected()) {
				tracks.add((CallAudioTrack) adapter.getItem(key));
			}
			adapter.selectNone();
			for (CallAudioTrack trc : tracks) {
				deleteTrack(trc);
			}
			break;

		case Constant.LONG_DELETE_RECORD:
			deleteTrack(longPosition);
			break;

		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * @param key
	 */
	protected void deleteTrack(Integer key) {
		CallAudioTrack track = (CallAudioTrack) adapter.getItem(key);
		deleteTrack(track);
	}

	/**
	 * @param track
	 */
	protected void deleteTrack(CallAudioTrack track) {
		if (track != null) {
			ctx.stopPlay(track.getRecordId());
			getService(ICallRecorderService.class).removeAudioTrack(track);
		}
	}

	@Override
	protected void onResume() {
		// if (isRefresh) {
		// refresh();
		// } else {
		// isRefresh = true;
		// }
		//清空头像集合
		adapter.clearPortraitMaps();
		adapter.notifyDataSetChanged();
		if (adapter.getCount() == 0) {
			ll_callrecord_empty.setVisibility(View.VISIBLE);
			ll_callrecord_all.setVisibility(View.GONE);
		}
		if (pop != null && pop.isShowing()) {
			hideMenu();
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (pop != null && pop.isShowing()) {
			pop.dismiss();
			pop = null;
		}
		if (this.mPlayer != null) {
			this.mPlayer.release();
			this.mPlayer = null;
		}
		super.onDestroy();
	}

	private void showMenu() {
		adapter.setChecked(true);
		adapter.notifyDataSetChanged();
		setMenu();
		gd_ll_menu_space.setVisibility(View.VISIBLE);
		((com.wxxr.callhelper.qg.widget.BottomTabBar)findViewById(R.id.home_bottom_tabbar)).setVisibility(View.GONE);
	}

	private void hideMenu() {
		if(pop != null && pop.isShowing()){
			adapter.setChecked(false);
			adapter.notifyDataSetChanged();
			pop.dismiss();
			pop = null;
			gd_ll_menu_space.setVisibility(View.GONE);
			adapter.selectNone();
			((com.wxxr.callhelper.qg.widget.BottomTabBar)findViewById(R.id.home_bottom_tabbar)).setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onStop() {
		if (this.sbController != null) {
			this.sbController.cancle();
			this.sbController = null;
		}
		
		hideSeekbar();
		
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (adapter.getCount() > 0) {
				if (pop == null && rl_sb_callrecord.getVisibility() == View.GONE) {
					showMenu();
				} else {
					hideMenu();
				}
			}
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (pop != null && pop.isShowing()) {
				hideMenu();
				return true;
			} else if(rl_sb_callrecord.getVisibility() == View.VISIBLE){
				if (this.sbController != null) {
					this.sbController.cancle();
					this.sbController = null;
				}
				hideSeekbar();
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		return false;
	}
}
