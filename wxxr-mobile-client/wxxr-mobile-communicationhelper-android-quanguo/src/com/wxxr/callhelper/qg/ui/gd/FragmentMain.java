package com.wxxr.callhelper.qg.ui.gd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wxxr.callhelper.qg.ActivityID;
import com.wxxr.callhelper.qg.IPrivateSMService;
import com.wxxr.callhelper.qg.IUserUsageDataRecorder;
import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.ComSecretaryBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.db.dao.ComSecretaryDao;
import com.wxxr.callhelper.qg.db.dao.IComeHelperDao;
import com.wxxr.callhelper.qg.db.dao.NoticeDao;
import com.wxxr.callhelper.qg.service.IClientCustomService;
import com.wxxr.callhelper.qg.ui.CSMainAcitivity;
import com.wxxr.callhelper.qg.ui.CallRecordActivity;
import com.wxxr.callhelper.qg.ui.HomeActivity;
import com.wxxr.callhelper.qg.ui.LouHuaMainActivity;
import com.wxxr.callhelper.qg.ui.PrivateZooLocationActivity;
import com.wxxr.callhelper.qg.ui.QGBusinessQueryManagerActivity;
import com.wxxr.callhelper.qg.ui.SiMiSuoHomePasswordActivity;
import com.wxxr.callhelper.qg.ui.SiMiSuoThirdPassword;
import com.wxxr.callhelper.qg.ui.SmsHuiZhiMainActivity;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.callhelper.qg.widget.MarqueeText;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.phone.helper.bo.NotifyMessage;

public class FragmentMain extends Fragment implements OnClickListener {

	private static final Trace log = Trace.register(FragmentMain.class);
	private static final int SET_PASS = 2013;
	NotifyMessage bean = null;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		// getService(IMoblieBusiness.class).getDuanXinHuiZhi();
		//
//		bean = NoticeDao.getInstance(getActivity())
//				.gefetchHomeNoticeDataByType( AppUtils.getService(IClientCustomService.class)
//						.getProviceCode());

//		if (bean!=null&&bean.getId() != null) {
//			emptynotice.setVisibility(View.GONE);
//			noticediv.setVisibility(View.VISIBLE);
//			gd_homenotice_text.setText(bean.getTitle() + ":"
//					+ bean.getContent());
//		} else {
			emptynotice.setVisibility(View.VISIBLE);
	//	}

		int size = AppUtils.getFramework().getService(IPrivateSMService.class)
				.getAllUnreadSize();
		TextView v = (TextView) mainview.findViewById(R.id.temp_simi_new);

		if (size != 0) {
			v.setVisibility(View.VISIBLE);
			v.setText("" + size);
		} else {
			v.setVisibility(View.GONE);
		}
		((TextView) mainview.findViewById(R.id.home_location_text))
				.setText(Tools.getProviceName());
		IComeHelperDao<ComSecretaryBean> dao = ComSecretaryDao.getInstance(getActivity());
		int count = dao.getUnhappenItemCount();
		if (count > 0) {
			xiaomishu_new.setVisibility(View.VISIBLE);
			xiaomishu_new.setText(count + "");
		} else {
			xiaomishu_new.setVisibility(View.INVISIBLE);
		}
		View mobliecustom = mainview.findViewById(R.id.mobliecustom);
		boolean ishot = Tools.isHotCity(this.getResources());

		if (ishot) {
			mobliecustom.setVisibility(View.VISIBLE);
		} else {
			mobliecustom.setVisibility(View.GONE);
		}

	}

	public void onPause() {
		super.onPause();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

	}

	View mainview;
	private MarqueeText gd_homenotice_text;
	private View noticediv;
	private View emptynotice;
	private Intent intent;
	private TextView xiaomishu_new;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mainview = inflater.inflate(R.layout.gd_frament_main, container, false);
		mainview.findViewById(R.id.gd_callnotice).setOnClickListener(this);
		mainview.findViewById(R.id.gd_smsnotice).setOnClickListener(this);
		mainview.findViewById(R.id.gd_simisuo).setOnClickListener(this);
		mainview.findViewById(R.id.gd_callrecord).setOnClickListener(this);
		mainview.findViewById(R.id.gd_xiaomishu).setOnClickListener(this);
		mainview.findViewById(R.id.home_loaction_div).setOnClickListener(this);
		mainview.findViewById(R.id.home_loaction_img).setOnClickListener(this);
		mainview.findViewById(R.id.home_location_text).setOnClickListener(this);

		mainview.findViewById(R.id.gd_mobilebusyness_check).setOnClickListener(
				this);
		mainview.findViewById(R.id.gd_mobilebusyness_process)
				.setOnClickListener(this);
		mainview.findViewById(R.id.gd_guishudi).setOnClickListener(this);
		mainview.findViewById(R.id.gd_homenotice_btn).setOnClickListener(this);
		mainview.findViewById(R.id.gd_homenotice_text).setOnClickListener(this);
		emptynotice = mainview.findViewById(R.id.empty_notice);
		xiaomishu_new = (TextView) mainview
				.findViewById(R.id.temp_xiaomishu_new);
		gd_homenotice_text = (MarqueeText) mainview
				.findViewById(R.id.gd_homenotice_text);
		noticediv = mainview.findViewById(R.id.notice_div);

		return mainview;
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		ActivityID actId = null;
		switch (v.getId()) {

			case R.id.gd_homenotice_btn :
				actId = ActivityID.CLOSENOTICE;
				noticediv.setVisibility(View.GONE);
				emptynotice.setVisibility(View.VISIBLE);
				if (bean != null) {
					NoticeDao.getInstance(this.getActivity()).setColseByUser(
							bean.getId(), AppUtils.getService(IClientCustomService.class)
							.getProviceCode());
				}

				break;

			case R.id.gd_homenotice_text :
				actId = ActivityID.LOOKNOTICE;
				Tools.processGonggao(this.getActivity(), bean);
				break;

			case R.id.gd_callnotice :
				actId = ActivityID.MISSCALL;

				// 来电提醒
				startActivity(new Intent(getActivity(),
						LouHuaMainActivity.class));

				break;

			case R.id.gd_smsnotice :
				actId = ActivityID.DXHZ;
				// 短信回执
				startActivity(new Intent(getActivity(),
						SmsHuiZhiMainActivity.class));
				break;

			case R.id.gd_simisuo :
				actId = ActivityID.PRIVATESM;
				// 私密锁
				IPrivateSMService service = getService(IPrivateSMService.class);
				try {

					boolean authen = service.isPasswordSetup();
					if (authen && (service.getBindedEmail() != null)
							&& (service.getBindedEmail().length() > 0)) {

						Intent intent = new Intent(getActivity(),
								SiMiSuoThirdPassword.class);
						startActivity(intent);

					} else if (service.getBindedEmail() == null
							|| service.getBindedEmail().length() == 0) {

						Intent intent = new Intent(getActivity(),
								GDSimsuoBindEMailActivity.class);
						startActivity(intent);

					} else {
						// 绑定了邮箱，而没有设置密码的情况
						Intent intent = new Intent(getActivity(),
								SiMiSuoHomePasswordActivity.class);
						startActivityForResult(intent, SET_PASS);
					}

				} catch (Throwable t) {
					log.warn("Failed to init private number", t);
					if (log.isDebugEnabled()) {
						// showMessageBox("私密锁初始化失败，原因：[" +
						// t.getLocalizedMessage()
						// + "]");
					} else {
						// showMessageBox("私密锁初始化失败，请稍后再试...");
					}
				}

				break;

			case R.id.gd_callrecord :
				actId = ActivityID.RECORDER;
				// 通话录音
				startActivity(new Intent(getActivity(),
						CallRecordActivity.class));

				break;

			case R.id.gd_guishudi :
				actId = ActivityID.NUMBERHOME;
				// 号码归属地
				startActivity(new Intent(getActivity(),
						GDGuishuidiQueryAcitity.class));

				break;

			case R.id.gd_mobilebusyness_check :
				actId = ActivityID.YEWUCHAXUN;
				// startActivity(new Intent(getActivity(),
				// GDMobileQueryActivity.class));
				intent = new Intent(getActivity(),
						QGBusinessQueryManagerActivity.class);
				intent.putExtra(Constant.QG_BUSINESS_TYPE, "查询");
				startActivity(intent);
				break;

			case R.id.gd_mobilebusyness_process :
				actId = ActivityID.YEWUCHAXUN;
				// startActivity(new Intent(getActivity(),
				// GDMobileQueryActivity.class));
				intent = new Intent(getActivity(),
						QGBusinessQueryManagerActivity.class);
				intent.putExtra(Constant.QG_BUSINESS_TYPE, "办理");
				startActivity(intent);
				break;

			case R.id.gd_xiaomishu :
				startActivity(new Intent(getActivity(), CSMainAcitivity.class));

				break;

			case R.id.home_loaction_div :
			case R.id.home_loaction_img :
			case R.id.home_location_text :
				Intent t = new Intent(getActivity(),
						PrivateZooLocationActivity.class);
				startActivity(t);

				break;

		}

		if ((actId != null) && getService(IUserUsageDataRecorder.class) != null) {
			getService(IUserUsageDataRecorder.class).doRecord(actId.ordinal());
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (SET_PASS == requestCode) {
			if (resultCode == Activity.RESULT_OK) {
				startActivity(new Intent(getActivity(),
						GDSimiContactsActvity.class));
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private <T> T getService(Class<T> class1) {

		return ((HomeActivity) getActivity()).getService(class1);
	}
}