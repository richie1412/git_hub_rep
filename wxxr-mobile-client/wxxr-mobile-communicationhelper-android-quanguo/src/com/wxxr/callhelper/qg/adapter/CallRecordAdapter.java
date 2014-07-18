package com.wxxr.callhelper.qg.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.CallAudioTrack;
import com.wxxr.callhelper.qg.controller.MediaPlayProgress;
import com.wxxr.callhelper.qg.ui.CallRecordActivity;
import com.wxxr.callhelper.qg.utils.GDItemPortrait;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.core.log.api.Trace;

public class CallRecordAdapter extends BaseAdapter implements IDataChangedListener {
	private static final Trace log = Trace.register(CallRecordAdapter.class);
	
	private IObservableListDataProvider<CallAudioTrack> provider;
//	private TextView mTv;
//	private SeekBar sb;
//	private MediaPlayProgress sbController;
//	private MediaPlayer player = new MediaPlayer();
	private SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
	String nowyear=(new java.util.Date().getYear()+1900)+"";
	
   
	private ICallActivityContext activityCtx;
//	private CallAudioTrack curr_bean;// 当前播放的记录

//	private String duration;
	private boolean isMenu;
	private List<Integer> selectedItems = new ArrayList<Integer>();
	private RelativeLayout rl_sb_callrecord;

	private GDItemPortrait gd_portrait;
//	private int current;
	private CallRecordActivity mActivity;

	public CallRecordAdapter(ICallActivityContext ctx, RelativeLayout rl_sb_callrecord, CallRecordActivity activity, IObservableListDataProvider<CallAudioTrack> prov) {
//		,TextView mTv, SeekBar sb) {
		this.provider = prov;
		this.activityCtx = ctx;
		this.provider.onDataChanged(this);
//		this.mTv = mTv;
//		this.sb = sb;
		this.isMenu = false;
		this.rl_sb_callrecord = rl_sb_callrecord;
//		this.current = -1;
		this.mActivity = activity;
		
		gd_portrait = GDItemPortrait.getInstance(ctx.getActivity());
	}

	@Override
	public int getCount() {
		return this.provider.getItemCounts();
	}

	@Override
	public Object getItem(int position) {
		return this.provider.getItem(position);
	}

//
//	private void setPlayer(MediaPlayer player) {
//		this.player = player;
//	}
//
//	public MediaPlayer getPlayer() {
//		return player;
//	}

	public void setChecked(boolean isMenu) {
		this.isMenu = isMenu;
	}

//	public Map<Integer, Boolean> getMaps() {
//		return maps;
//	}
//
//	public void setMaps(Map<Integer, Boolean> maps) {
//		this.maps = maps;
//	}

//	public void setPosition(int current) {
//		this.current = current;
//	}
//
//	public int getPosition() {
//		return current;
//	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewControl control;
		CallAudioTrack crBean = this.provider.getItem(position);

		if (convertView == null) {
			view = View.inflate(activityCtx.getActivity(), R.layout.callrecord_item, null);
		} else {
			view = convertView;
		}
		// 关联view
//		crBean.setReferView(view);
		control = (ViewControl) view.getTag();
		if (control == null) {
			control = new ViewControl();
			control.position = position;
			control.tv_user = (TextView) view.findViewById(R.id.tv_username);
//			control.tv_phoneNum = (TextView) view.findViewById(R.id.tv_phone);
			control.tv_date = (TextView) view.findViewById(R.id.tv_date);
			control.tv_duration = (TextView) view.findViewById(R.id.tv_duration);

			control.iv_callrecord_state = (ImageView) view.findViewById(R.id.iv_callrecord);

			control.iv_callrecord_player = (ImageView) view
					.findViewById(R.id.iv_callrecord_player);

			control.cb_callrecord = (CheckBox) view.findViewById(R.id.cb_callrecord);
			
			control.ll_callrecorder_item = (LinearLayout) view.findViewById(R.id.ll_callrecorder_item);
			
			//头像
			control.gd_iv_portrait = (ImageView) view.findViewById(R.id.gd_iv_callrecord_portrait);
			control.gd_rl_portrait = (RelativeLayout) view.findViewById(R.id.gd_rl_callrecord_portrait);
			control.gd_tv_portrait = (TextView) view.findViewById(R.id.gd_tv_callrecord_portrait_text);
			
			final ViewControl vc = control;
			
//			control.cb_callrecord.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					CheckBox cb = (CheckBox)v;
//					if (cb.isChecked()) {
//						mActivity.pressAll();
//						if(!selectedItems.contains(vc.position)){
//							selectedItems.add(vc.position);
//						}
////						vc.ll_callrecorder_item.setBackgroundColor(activityCtx.getActivity().getResources().getColor(R.color.gd_callrecord_long_press));
//					} else {
//						selectedItems.remove((Object)vc.position);
////						vc.ll_callrecorder_item.setBackgroundColor(activityCtx.getActivity().getResources().getColor(R.color.white));
//						if(selectedItems.isEmpty()){
//							mActivity.pressCancelAll();
//						}
//					}
//				}
//			});
			
			control.cb_callrecord.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						mActivity.pressAll();
						if(!selectedItems.contains(vc.position)){
							selectedItems.add(vc.position);
						}
					} else {
						selectedItems.remove((Object)vc.position);
						if(selectedItems.isEmpty()){
							mActivity.pressCancelAll();
						}
					}
				}
			});
			
			control.iv_callrecord_player.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
//					try {
//						// 当前是否有播放的记录，如果有，应该停止它，同时要改变播放图片
//						// 用crBean来判断点击的是否是同一个，final CallRecordBean crBean =
//						// lists.get(position);
//						// curr_bean.getReferView()存放上一个view
//						if (curr_bean != null && curr_bean != crBean) {
//							curr_bean.setStatus(false);
//							curr_bean.setDurationStatus(false);
//							curr_bean.getReferView().findViewById(R.id.iv_callrecord_player)
//									.setBackgroundResource(R.drawable.play_selector);
//							player.reset();
//							TextView tv_duration = (TextView) curr_bean
//									.getReferView().findViewById(R.id.tv_duration);
//							tv_duration.setVisibility(View.INVISIBLE);
//						}
//						curr_bean = crBean;
//						if (crBean.isStatus()) {
//							player.reset();
//							iv_callrecord_player
//									.setBackgroundResource(R.drawable.play_selector);
//							crBean.setStatus(false);
//							crBean.setDurationStatus(false);
//							mTv.setText("00''00");
//							tv_duration.setVisibility(View.INVISIBLE);
//							setPosition(-1);
//							if(sbController != null){
//								sbController.cancle();
//							}
//
//						} else {
//							crBean.setStatus(true);
//							crBean.setDurationStatus(true);
//							setPlayer(player);
//							setPosition(position);
//							tv_duration.setVisibility(View.VISIBLE);
//							iv_callrecord_player
//									.setBackgroundResource(R.drawable.stop_selector);
//							player.setDataSource(Constant.SDPATH + "/"
//									+ crBean.getFileName());
//							player.prepare();
//							player.start();
//							duration = Tools.toTime(player.getDuration());
//							tv_duration.setText(duration);
//							mTv.setText(duration);
//							sbController = new MediaPlayProgress(player, sb);
//							sbController.startUpdate();
//
//							player.setOnCompletionListener(new OnCompletionListener() {
//
//								@Override
//								public void onCompletion(MediaPlayer mp) {
//									if(getPosition() > -1){
//										lists.get(getPosition()).setStatus(false);
//										lists.get(getPosition()).setDurationStatus(false);
//									}
//									crBean.setStatus(false);
//									crBean.setDurationStatus(false);
//									setPosition(-1);
//									mTv.setText("00''00");
//									player.reset();
//									tv_duration.setVisibility(View.INVISIBLE);
//									iv_callrecord_player
//											.setBackgroundResource(R.drawable.play_selector);
//									if(sbController != null){
//										sbController.cancle();
//									}
//								}
//							});
//						}
//
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
					CallAudioTrack crBean = provider.getItem(vc.position);
					MediaPlayProgress progress = activityCtx.getTrackPlayProgress(crBean.getRecordId());
					if(progress != null){
						if(log.isDebugEnabled()){
							log.debug("Going to stop audio play :"+crBean.getRecordId());
						}
						activityCtx.stopPlay(crBean.getRecordId());
						mActivity.hideSeekbar();
						vc.ll_callrecorder_item.setBackgroundColor(activityCtx.getActivity().getResources().getColor(R.color.white));
					}else {
						if(log.isDebugEnabled()){
							log.debug("Going to play track :"+crBean.getRecordId());
						}
						activityCtx.playAudio(crBean);
						mActivity.showSeekbar();
						vc.ll_callrecorder_item.setBackgroundColor(activityCtx.getActivity().getResources().getColor(R.color.gd_callrecord_long_press));
					}
				}

			});
			view.setTag(control);
		}else{
			control.position = position;
		}

		control.tv_user.setText(crBean.getContactName() == null ? crBean.getPhoneNumber() : crBean.getContactName());
//		control.tv_phoneNum.setText("(" + crBean.getPhoneNumber() + ")");
		
		
		String recyear=format.format(crBean.getTimestamp());
		if(recyear.startsWith(nowyear)){
			recyear=recyear.substring(5);
		}
		control.tv_date.setText(recyear);

		//加载头像
		
		gd_portrait.setItemPortrait(
				crBean.getContactName(), crBean.getPhoneNumber(), control.gd_rl_portrait, control.gd_tv_portrait, control.gd_iv_portrait);
		
		// 显示checkbox
		if (isMenu) {
			control.iv_callrecord_player.setVisibility(View.GONE);
			control.cb_callrecord.setVisibility(View.VISIBLE);
		} else {
			control.iv_callrecord_player.setVisibility(View.VISIBLE);
			control.cb_callrecord.setVisibility(View.GONE);
		}
		
		if(selectedItems.contains(position)){
//			control.ll_callrecorder_item.setBackgroundColor(activityCtx.getActivity().getResources().getColor(R.color.white));
			control.cb_callrecord.setChecked(true);
		}else{
//			control.ll_callrecorder_item.setBackgroundColor(activityCtx.getActivity().getResources().getColor(R.color.white));
			control.cb_callrecord.setChecked(false);
		}
		
		if (crBean.getCallType() == 1) {
			// 来电话
			control.iv_callrecord_state.setBackgroundResource(R.drawable.coming);
		} else {
			control.iv_callrecord_state.setBackgroundResource(R.drawable.going);
		}
				
		// 播放状态
		MediaPlayProgress progress = activityCtx.getTrackPlayProgress(crBean.getRecordId());
		if (progress != null) {
			control.iv_callrecord_player
					.setBackgroundResource(R.drawable.gd_stop);
			control.tv_duration.setVisibility(View.VISIBLE);
			control.tv_duration.setText("(" + Tools.toTime(progress.getDuration()) + ")");
			control.ll_callrecorder_item.setBackgroundColor(activityCtx.getActivity().getResources().getColor(R.color.gd_callrecord_long_press));
		} else {
			control.iv_callrecord_player
					.setBackgroundResource(R.drawable.gd_play);
			control.tv_duration.setVisibility(View.INVISIBLE);
			control.ll_callrecorder_item.setBackgroundColor(activityCtx.getActivity().getResources().getColor(R.color.white));
		}

		// 时长状态
//		if (crBean.isDurationStatus()) {
//			tv_duration.setVisibility(View.VISIBLE);
//			tv_duration.setText(duration);
//		} else {
//			tv_duration.setVisibility(View.INVISIBLE);
//		}


		return view;
	}

	@Override
	public void dataSetChanged() {
		notifyDataSetChanged();
	}

	@Override
	public void dataItemChanged() {
		
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public void selectNone() {
		this.selectedItems.clear();
		notifyDataSetChanged();
	}
	
	public void selectAll() {
		int cnt = getCount();
		this.selectedItems.clear();
		for(int i=0 ; i < cnt; i++){
			this.selectedItems.add(i);
		}
		notifyDataSetChanged();
	}

	public List<Integer> getSelected() {
		return this.selectedItems.isEmpty() ? Collections.EMPTY_LIST : new ArrayList<Integer>(this.selectedItems);
	}
	
	public void clearPortraitMaps(){
		gd_portrait.clearPortraitMaps();
	}
}

class ViewControl {
	TextView tv_user;
//	TextView tv_phoneNum;
	TextView tv_date;
	ImageView iv_callrecord_state;
	ImageView iv_callrecord_player;
	TextView tv_duration;
	CheckBox cb_callrecord;
	LinearLayout ll_callrecorder_item;
	int position;
	ImageView gd_iv_portrait;
	RelativeLayout gd_rl_portrait;
	TextView gd_tv_portrait;
}
