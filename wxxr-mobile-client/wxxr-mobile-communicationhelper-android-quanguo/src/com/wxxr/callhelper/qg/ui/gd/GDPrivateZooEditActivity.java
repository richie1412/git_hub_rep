package com.wxxr.callhelper.qg.ui.gd;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.ActivityID;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IUserActivationService;
import com.wxxr.callhelper.qg.IUserUsageDataRecorder;
import com.wxxr.callhelper.qg.bean.ImageHelper;
import com.wxxr.callhelper.qg.bean.UserBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.rpc.UserDetailVO;
import com.wxxr.callhelper.qg.service.IClientCustomService;
import com.wxxr.callhelper.qg.service.IUserHead;
import com.wxxr.callhelper.qg.ui.ConfirmDialogActivity;
import com.wxxr.callhelper.qg.utils.ImageUtils;
import com.wxxr.callhelper.qg.utils.ManagerSP;
import com.wxxr.callhelper.qg.utils.StringUtil;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.callhelper.qg.widget.IImageView;
import com.wxxr.callhelper.qg.widget.pick.ArrayWheelAdapter1;
import com.wxxr.callhelper.qg.widget.pick.China;
import com.wxxr.callhelper.qg.widget.pick.DateForAge;
import com.wxxr.callhelper.qg.widget.pick.OnWheelChangedListener;
import com.wxxr.callhelper.qg.widget.pick.PickedDate;
import com.wxxr.callhelper.qg.widget.pick.PickedRegion;
import com.wxxr.callhelper.qg.widget.pick.WheelView;
import com.wxxr.mobile.android.app.AppUtils;
/**
 * 广东版——个人中心账号信息界面
 * 
 * @author cuizaixi
 * 
 */
public class GDPrivateZooEditActivity extends BaseActivity {
	private ImageContext context = new ImageContext() {
		@Override
		public void setHead(Drawable head) {
			if (head != null) {
				BitmapDrawable drawable = (BitmapDrawable) head;
				Bitmap bitmap = drawable.getBitmap();
				Bitmap rcb = Tools.getRCB(bitmap, Tools.roundPX);
				BitmapDrawable drawable2 = new BitmapDrawable(rcb);
				Bitmap bitmap2 = drawable2.getBitmap();
				iv_icon.setBackgroundDrawable(drawable2);
				btn_unLogin.setVisibility(View.VISIBLE);
				try {
					ImageUtils.saveImage(getApplicationContext(),
							getService(IUserActivationService.class)
									.getCurrentUserId(), bitmap2);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public BaseActivity getActivity() {
			return GDPrivateZooEditActivity.this;
		}
	};
	private LinearLayout pick_mian;
	private PopupWindow agePop;
	private TextView tv_region;
	private WheelView wv_day;
	private WheelView wv_month;
	private WheelView wv_year;
	private LinearLayout age_pick;
	private TextView tv_age;
	private List<String> CachedYears;
	private List<String> months;
	private List<String> days;
	private PickedDate mPickedDate;
	private boolean IsAgePickedShowing;
	private boolean isIconPickedShowing;
	private LayoutInflater mInflater;
	private TextView pick_title;
	private TextView tv_nick;
	private TextView tv_num;
	private TextView tv_gendar;
	private ImageView iv_icon;
	private PopupWindow iconPop;
	private LinearLayout pick_icon_main;
	private LinearLayout ll_main;
	private ImageHelper mImageHelper;
	private Button btn_unLogin;
	private UserDetailVO user;
	private UserBean mCachedUser;
	public final static int REQ_CODE_LOCALE_BG = 202;
	public final static int REQ_CODE_PICTURE_CAMARE = 103;
	public final static int REQ_CODE_PHOTO_CROP = 102;
	public final static String IMAGE_URI = "iamge_uri";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gd_person_center);
		findView();
		processLogic();
	}

	private void findView() {
		// title 界面控件
		TextView tv_mid = (TextView) findViewById(R.id.tv_titlebar_name);
		findViewById(R.id.gd_iv_titlebar_right).setVisibility(View.INVISIBLE);
		findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(this);
		tv_mid.setText("账号信息");
		// 主界面控件
		ll_main = (LinearLayout) findViewById(R.id.ll_main);
		findViewById(R.id.ll_region).setOnClickListener(this);
		findViewById(R.id.ll_age).setOnClickListener(this);
		findViewById(R.id.ll_nick_name).setOnClickListener(this);
		findViewById(R.id.ll_telnum).setOnClickListener(this);
		findViewById(R.id.ll_gendar).setOnClickListener(this);
		btn_unLogin = (Button) findViewById(R.id.btn_exite);
		btn_unLogin.setOnClickListener(this);
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		iv_icon.setOnClickListener(this);
		tv_nick = (TextView) findViewById(R.id.tv_nick_name);
		tv_num = (TextView) findViewById(R.id.tv_telnum);
		tv_gendar = (TextView) findViewById(R.id.tv_gendar);
		tv_region = (TextView) findViewById(R.id.tv_region);
		tv_age = (TextView) findViewById(R.id.tv_age);
		mInflater = (LayoutInflater) LayoutInflater.from(this);
		// 选择框主控件
		pick_mian = (LinearLayout) mInflater.inflate(R.layout.gd_data_pick,
				null);
		pick_title = (TextView) pick_mian.findViewById(R.id.tv_pick_title);
		pick_mian.findViewById(R.id.iv_region_cancel).setOnClickListener(this);
		pick_mian.findViewById(R.id.iv_region_ok).setOnClickListener(this);
		// 年龄选择框控件
		age_pick = (LinearLayout) pick_mian.findViewById(R.id.ll_age_pick);
		wv_year = (WheelView) pick_mian.findViewById(R.id.year);
		wv_month = (WheelView) pick_mian.findViewById(R.id.month);
		wv_day = (WheelView) pick_mian.findViewById(R.id.day);
		// 头像选择框控件
		pick_icon_main = (LinearLayout) mInflater.inflate(
				R.layout.gd_icon_pick, null);
		pick_icon_main.findViewById(R.id.tv_selecte_picture)
				.setOnClickListener(this);
		pick_icon_main.findViewById(R.id.tv_selecte_camare).setOnClickListener(
				this);
		pick_icon_main.findViewById(R.id.tv_cancel).setOnClickListener(this);
	}
	private void processLogic() {
		Intent intent = getIntent();
		mCachedUser = (UserBean) intent
				.getSerializableExtra(Constant.CACHEDUSER);
		mImageHelper = new ImageHelper(this, context);
		user = new UserDetailVO();
		getIcon(iv_icon);
	}
	private boolean getLocalIcon(ImageView head) {
		String filepath = ManagerSP.getInstance().get("icon_path", null);
		if (StringUtil.isEmpty(filepath)) {
			return false;
		}
		File file = new File(filepath);
		if (file.exists()) {
			Bitmap bmp = ImageUtils.getBitmap(this, filepath);
			Bitmap rcb = Tools.getRCB(bmp, Tools.roundPX);
			head.setImageBitmap(rcb);
			return true;
		}
		return false;
	};
	private void getPromotIcon(final ImageView head) {
		CMProgressMonitor monitor = new CMProgressMonitor(this) {

			@Override
			protected void handleFailed(Throwable cause) {

			}

			@Override
			protected void handleDone(Object returnVal) {
				byte[] icon = (byte[]) returnVal;
				if (icon != null && icon.length != 0) {
					Bitmap bitmap = BitmapFactory.decodeByteArray(icon, 0,
							icon.length);
					Bitmap rcb = Tools.getRCB(bitmap, Tools.roundPX);
					BitmapDrawable b = new BitmapDrawable(rcb);
					head.setBackgroundDrawable(b);
				}
			}
		};
		monitor.executeOnMonitor(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				return getFramework().getService(IUserHead.class).findIcon(
						Tools.getDeviceID(GDPrivateZooEditActivity.this));
			}
		});

	}
	private void getIcon(final ImageView head) {
		if (!getLocalIcon(head)) {
			getPromotIcon(head);
		}
	}

	// 重写 onResume 方法，在进入编辑界面或者是编辑完成后刷新该界面
	@Override
	protected void onResume() {
		super.onResume();
		getLocalIcon(iv_icon);
		if (mCachedUser != null) {
			String nickName = mCachedUser.getNickName();
			String telNum = mCachedUser.getTelNum();
			String gendar = mCachedUser.getGendar();
			int birthday = mCachedUser.getBirthday();
			String provice = mCachedUser.getProvice();
			if (nickName != null) {
				tv_nick.setText(nickName);
			}
			if (telNum != null) {
				tv_num.setText(telNum);
			}
			if (gendar != null) {
				tv_gendar.setText(gendar);
			}
			if (provice != null) {
				tv_region.setText(provice);
			}
			if (birthday != 0) {
				try {
					int age = Tools.getCurrentAgeByBirthdate(String
							.valueOf(birthday));
					tv_age.setText(age + "");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQ_CODE_LOCALE_BG) {
				mImageHelper.readLocalImage(data, REQ_CODE_PHOTO_CROP);
			} else if (requestCode == REQ_CODE_PICTURE_CAMARE) {
				mImageHelper.readCamareImage(data, REQ_CODE_PHOTO_CROP);
			} else if (requestCode == REQ_CODE_PHOTO_CROP) {
				mImageHelper.readCropImage(data);
			}
			hideIconPop();
		}
		if (resultCode == Constant.DE_ACTIVE) {
			deActivated();
		}
		if (resultCode == Constant.GENDER) {
			boolean isMale = data.getBooleanExtra("gendar", true);
			mCachedUser.setGendar(Tools.getGendar(isMale));
		}
		if (resultCode == Constant.NICKNAME) {
			String nickname = data.getStringExtra("nickname");
			if (nickname != null) {
				mCachedUser.setNickName(nickname);
			}
		}
	}
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.iv_icon :
				showIconMenu();
				btn_unLogin.setVisibility(View.INVISIBLE);
				break;
			case R.id.ll_age :
				showAgePick();
				IsAgePickedShowing = true;
				age_pick.setVisibility(View.VISIBLE);
				initPickAge();
				break;
			case R.id.gd_iv_titlebar_left :
				this.finish();
				break;
			case R.id.ll_telnum :
				Tools.showToast(this, "退出登录后，可用新号码登录");
				break;
			case R.id.ll_nick_name :
				Intent intent = new Intent(this,
						GDPrivateZooInformCommonActivity.class);
				intent.putExtra(Constant.PRIVATE_INFORM_EDIT,
						Constant.NICK_NAME_EIDT);
				intent.putExtra(Constant.CACHEDUSER, mCachedUser);
				startActivityForResult(intent, Constant.NICK_NAME_EIDT);
				break;
			case R.id.ll_gendar :
				Intent intent2 = new Intent(this,
						GDPrivateZooInformCommonActivity.class);
				intent2.putExtra(Constant.PRIVATE_INFORM_EDIT,
						Constant.GENDAR_EIDT);
				intent2.putExtra(Constant.CACHEDUSER, mCachedUser);
				startActivityForResult(intent2, Constant.GENDAR_EIDT);
				break;
			case R.id.iv_region_ok :
				String year = mPickedDate.getYear();
				String month = mPickedDate.getMonth();
				String day = mPickedDate.getDay();
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
				Date date = new Date();
				int y = Integer.valueOf(year.replace("年", ""));
				int m = Integer.valueOf(month.replace("月", ""));
				int d = Integer.valueOf(day.replace("日", ""));
				date.setYear(y - 1900);
				date.setMonth(m);
				date.setDate(d);
				String birthday = format.format(date).trim();
				mCachedUser.setBirthday(Integer.valueOf(birthday));
				user.setMale(Tools.isMale(mCachedUser.getGendar()));
				user.setBirthday(Integer.valueOf(birthday));
				updateUser(user);
				hideAgePick();
			case R.id.iv_region_cancel :
				hideAgePick();
				break;
			case R.id.tv_cancel :
				hideIconPop();
				btn_unLogin.setVisibility(View.VISIBLE);
				break;
			case R.id.tv_selecte_picture :
				mImageHelper.getLocalImage(REQ_CODE_LOCALE_BG);
				break;
			case R.id.tv_selecte_camare :
				mImageHelper.getCarmreImage(REQ_CODE_PICTURE_CAMARE);
				break;
			case R.id.btn_exite :
				Intent intent3 = new Intent(GDPrivateZooEditActivity.this,
						ConfirmDialogActivity.class);
				intent3.putExtra(Constant.DIALOG_KEY, Constant.DE_ACTIVE);
				startActivityForResult(intent3, Constant.DE_ACTIVE);
				break;
		}
	}
	private void initPickAge() {
		mPickedDate = new PickedDate();
		CachedYears = new ArrayList<String>();
		if (CachedYears.size() == 0) {
			CachedYears = DateForAge.getYears();
		}
		months = DateForAge.getMonths();
		days = DateForAge.getDay();
		wv_year.setAdapter(new ArrayWheelAdapter1(CachedYears));
		wv_year.addChangingListener(new YearListener());
		wv_month.setAdapter(new ArrayWheelAdapter1(months));
		wv_day.setAdapter(new ArrayWheelAdapter1(days));
		int birthday = mCachedUser.getBirthday();
		if (birthday != 0) {
			String setYear = String.valueOf(birthday).substring(0, 4) + "年";
			String setMonth = String.valueOf(birthday).substring(5, 6) + "月";
			String setDay = String.valueOf(birthday).substring(7, 8) + "日";
			wv_year.setCurrentItem(CachedYears.indexOf(setYear));
			wv_month.setCurrentItem(months.indexOf(setMonth));
			wv_day.setCurrentItem(days.indexOf(setDay));
			mPickedDate.setYear(setYear);
			mPickedDate.setMonth(setMonth);
			mPickedDate.setDay(setDay);
		} else {
			wv_year.setCurrentItem(80);
			wv_month.setCurrentItem(5);
			wv_day.setCurrentItem(14);
			mPickedDate.setYear("1980年");
			mPickedDate.setMonth("6月");
			mPickedDate.setDay("15日");
		}
	}
	private class YearListener implements OnWheelChangedListener {
		@Override
		public void onChanged(WheelView wheel, int oldValue, int newValue) {
			String year = CachedYears.get(newValue);
			mPickedDate.setYear(year);
			initMonth(newValue);
		}

		private void initMonth(int newValue) {
			wv_month.setAdapter(new ArrayWheelAdapter1(months));
			wv_month.addChangingListener(new MonthListner());
		}
		private class MonthListner implements OnWheelChangedListener {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				String month = months.get(newValue);
				mPickedDate.setMonth(month);
				initDay(newValue);
			}

			private void initDay(int newValue) {
				wv_day.addChangingListener(new DayListener());
			}
			private class DayListener implements OnWheelChangedListener {

				@Override
				public void onChanged(WheelView wheel, int oldValue,
						int newValue) {
					String day = days.get(newValue);
					mPickedDate.setDay(day);
				}
			}
		}
	}
	private void hideAgePick() {
		if (agePop != null && agePop.isShowing()) {
			agePop.dismiss();
			IsAgePickedShowing = false;
		}
	}
	private void hideIconPop() {
		if (iconPop != null && iconPop.isShowing()) {
			iconPop.dismiss();
			isIconPickedShowing = false;
		}
	}

	private void showAgePick() {
		pick_title.setText("选择出生日期");
		agePop = new PopupWindow(pick_mian, ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		agePop.setAnimationStyle(R.style.pop_anim_style);
		agePop.showAtLocation(age_pick, Gravity.BOTTOM, 0, 0);
	}
	private void showIconMenu() {
		iconPop = new PopupWindow(pick_icon_main,
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		iconPop.setAnimationStyle(R.style.pop_anim_style);
		iconPop.showAtLocation(ll_main, Gravity.BOTTOM, 0, 0);
		isIconPickedShowing = true;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (IsAgePickedShowing) {
				hideAgePick();
				return false;
			}
			if (isIconPickedShowing) {
				hideIconPop();
				btn_unLogin.setVisibility(View.VISIBLE);
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	public void deActivated() {

		getService(IUserActivationService.class).logout(
				new CMProgressMonitor(GDPrivateZooEditActivity.this) {

					@Override
					protected void handleDone(Object returnVal) {
						AppUtils.getFramework()
								.getService(IUserUsageDataRecorder.class)
								.doRecord(ActivityID.LOGOUT.ordinal());
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Tools.showToast(getApplicationContext(), "退出成功");
								finish();
							}
						});
					}

					@Override
					protected void handleFailed(Throwable cause) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								showMessageBox("退出账号失败！");
							}
						});
					}

				});
	}
	private void updateUser(final UserDetailVO user) {
		getService(IUserActivationService.class).syncUserDetail(user,
				new CMProgressMonitor(GDPrivateZooEditActivity.this) {

					@Override
					protected void handleFailed(Throwable cause) {
						Toast.makeText(getApplicationContext(), "更新失败", 1)
								.show();
					}

					@Override
					protected void handleDone(Object returnVal) {
						Toast.makeText(getApplicationContext(), "更新成功", 1)
								.show();
						int birthday = user.getBirthday();
						try {
							int age = Tools.getCurrentAgeByBirthdate(String
									.valueOf(birthday));
							tv_age.setText(age + "");
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				});
	}
}
