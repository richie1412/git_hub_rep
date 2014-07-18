package com.wxxr.callhelper.qg.ui.gd;

import java.io.File;
import java.util.concurrent.Callable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.ActivityID;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.ComHelperFramework;
import com.wxxr.callhelper.qg.IUserActivationService;
import com.wxxr.callhelper.qg.IUserUsageDataRecorder;
import com.wxxr.callhelper.qg.bean.Region;
import com.wxxr.callhelper.qg.bean.UserBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.rpc.ClientInfo;
import com.wxxr.callhelper.qg.rpc.UserDetailVO;
import com.wxxr.callhelper.qg.service.DownAPK;
import com.wxxr.callhelper.qg.service.GuiShuDiService;
import com.wxxr.callhelper.qg.service.IAdvaceRegionQueryService;
import com.wxxr.callhelper.qg.service.IClientCustomService;
import com.wxxr.callhelper.qg.service.IGuiShuDiService;
import com.wxxr.callhelper.qg.service.IUserHead;
import com.wxxr.callhelper.qg.ui.ApplicationManager;
import com.wxxr.callhelper.qg.ui.ConfirmDialogActivity;
import com.wxxr.callhelper.qg.utils.ImageUtils;
import com.wxxr.callhelper.qg.utils.ManagerSP;
import com.wxxr.callhelper.qg.utils.StringUtil;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.callhelper.qg.widget.IImageView;
import com.wxxr.callhelper.qg.widget.ImageViewAware;
import com.wxxr.mobile.android.app.AppUtils;
/**
 * 广东版——个人中心界面
 * 
 * @author cuizaixi
 * 
 */
public class FragmentPersonCenter extends Fragment implements OnClickListener {
	String TAG = this.getClass().getSimpleName();
	private LinearLayout main;
	private LinearLayout ll_logined;
	private TextView tv_unlogin;
	private TextView tv_nick;
	private TextView tv_region;
	private TextView tv_tel;
	private UserBean userBean;
	private ImageView iv_icon;
	private ManagerSP sp;
	private LinearLayout no_network;
	private ImageView iv_new;
	public static String MOBLIE_MARKET = "moblie_market";
	private Boolean atFectchDetail = false;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 主界面控件
		main = (LinearLayout) inflater.inflate(R.layout.gd_pereson_center_home,
				container, false);
		main.findViewById(R.id.tv_privacy_items).setOnClickListener(this);
		main.findViewById(R.id.ll_go_edit).setOnClickListener(this);
		main.findViewById(R.id.ll_share).setOnClickListener(this);
		main.findViewById(R.id.ll_mobile_market).setOnClickListener(this);
		main.findViewById(R.id.ll_check_version).setOnClickListener(this);
		main.findViewById(R.id.ll_idea_back).setOnClickListener(this);
		main.findViewById(R.id.ll_apps_recommend).setOnClickListener(this);
		no_network = (LinearLayout) main.findViewById(R.id.no_network);
		no_network.setVisibility(View.GONE);
		iv_new = (ImageView) main.findViewById(R.id.iv_new);
		// 标题界面控件
		TextView tv_mid = (TextView) main.findViewById(R.id.tv_titlebar_name);
		main.findViewById(R.id.gd_iv_titlebar_right).setVisibility(
				View.INVISIBLE);
		main.findViewById(R.id.gd_iv_titlebar_left).setVisibility(
				View.INVISIBLE);
		tv_mid.setText("个人中心");
		// 头像区控件
		iv_icon = (ImageView) main.findViewById(R.id.iv_icon);
		ll_logined = (LinearLayout) main.findViewById(R.id.ll_logined);
		tv_unlogin = (TextView) main.findViewById(R.id.tv_unlogin);
		tv_nick = (TextView) main.findViewById(R.id.tv_nick_name);
		tv_region = (TextView) main.findViewById(R.id.tv_region);
		tv_tel = (TextView) main.findViewById(R.id.tv_tel);
		sp = ManagerSP.getInstance(getActivity());
		return main;
	}
	@SuppressWarnings("deprecation")
	private void processLogic() {
		// nothing;
	}
	@Override
	public void onResume() {
		super.onResume();
		if (hasUpdateVersion()) {
			iv_new.setVisibility(View.VISIBLE);
		}
		if (isUserAcitived()) {
			tv_unlogin.setVisibility(View.GONE);
			ll_logined.setVisibility(View.VISIBLE);
			userBean = new UserBean();
			getIcon(new ImageViewAware(iv_icon));
			if (!atFectchDetail) {
				atFectchDetail = true;
				getUserDetail();
			}

		} else {
			ll_logined.setVisibility(View.GONE);
			tv_unlogin.setVisibility(View.VISIBLE);
		}

		boolean ishot = Tools.isHotCity(this.getResources());
		if (ishot) {
			main.findViewById(R.id.ll_apps_recommend).setVisibility(
					View.VISIBLE);
			main.findViewById(R.id.ll_apps_recommend_div).setVisibility(
					View.VISIBLE);
		} else {
			main.findViewById(R.id.ll_apps_recommend).setVisibility(View.GONE);
			main.findViewById(R.id.ll_apps_recommend_div).setVisibility(
					View.GONE);
		}
	}
	private void getUserDetail() {
		getFramework().getService(IUserActivationService.class).getUserDetail(
				new CMProgressMonitor(getActivity()) {

					@Override
					protected void handleFailed(Throwable cause) {
						atFectchDetail = false;
						Toast.makeText(getActivity(), "获取用户信息失败", 1).show();
					}

					@Override
					protected void handleDone(Object returnVal) {
						atFectchDetail = false;
						UserDetailVO v = (UserDetailVO) returnVal;
						String tel = v.getUserName();
						String nickName = v.getNickName();
						if (v.getUserName() != null) {
							userBean.setTelNum(tel);
							tv_tel.setText(tel);
							Region region = getFramework().getService(
									IGuiShuDiService.class).getRegionByMsisdn(
									v.getUserName());
							String provice = region.getRegionName();
							String pProvice = null;
							if (isZhiXia(provice)) {
								pProvice = provice;
							} else {
								pProvice = region.getpRegionName();
							}

							if (pProvice != null) {
								userBean.setProvice(pProvice);
								tv_region.setText(pProvice);
								int hasUpdate = sp.get("hasUpdate", 0);
								if (hasUpdate == 0) {
									getFramework()
											.getService(
													IClientCustomService.class)
											.setProviceCode(
													Constant.getProviceCode(pProvice));
									sp.update("hasUpdate", 1);
								}

							}
						}
						if (nickName != null) {
							tv_nick.setText(nickName);
							userBean.setNickName(nickName);
						}
						int birthday = v.getBirthday();
						userBean.setBirthday(birthday);
						userBean.setGendar(Tools.getGendar(v.isMale()));
					}
				});
	}
	/**
	 * 如果是直辖市的话，市名就是省名。
	 * 
	 * @param regionName
	 * @return
	 */
	private boolean isZhiXia(String regionName) {
		return regionName.equals("北京") || regionName.equals("上海")
				|| regionName.equals("重庆") || regionName.equals("天津");
	}
	private boolean getLocalIcon(IImageView head) {
		String filepath = sp.get("icon_path", null);
		if (StringUtil.isEmpty(filepath)) {
			return false;
		}
		File file = new File(filepath);
		if (file.exists()) {
			Bitmap bmp = ImageUtils.getBitmap(getActivity(), filepath);
			Bitmap rcb = Tools.getRCB(bmp, Tools.roundPX);
			head.getWrappedImage().setImageBitmap(rcb);
			return true;
		}
		return false;
	};
	private void getPromotIcon(final IImageView head) {
		CMProgressMonitor monitor = new CMProgressMonitor(getActivity()) {

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
					head.getWrappedImage().setImageDrawable(b);
				}
			}
		};
		monitor.executeOnMonitor(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				return getFramework().getService(IUserHead.class).findIcon(
						Tools.getDeviceID(getActivity()));
			}
		});

	}
	private void getIcon(IImageView head) {
		if (!getLocalIcon(head)) {
			getPromotIcon(head);
		}
	}
	private boolean isUserAcitived() {
		return getFramework().getService(IUserActivationService.class)
				.isUserActivated();

	}
	private ComHelperFramework getFramework() {
		return ((ApplicationManager) getActivity().getApplication())
				.getFramework();
	}
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.ll_go_edit :
				if (isUserAcitived()) {
					if (userBean != null) {
						Intent intent = new Intent(getActivity(),
								GDPrivateZooEditActivity.class);
						intent.putExtra(Constant.CACHEDUSER, userBean);
						startActivity(intent);
					} else {
						Toast.makeText(getActivity(), "获取用户信息失败，无法编辑个人信息", 1)
								.show();
					}

				} else {
					Tools.Login(getActivity(), null);
				}
				break;
			case R.id.ll_share :
				Intent intent2 = new Intent(getActivity(), GDShareDialog.class);
				intent2.putExtra(Constant.DIALOG_KEY, Constant.DIALOG_SHARE);
				intent2.putExtra(Constant.SHARE_ID,
						ActivityID.SAHRE_FROM_PERSONCENTER);
				startActivity(intent2);
				break;
			case R.id.ll_check_version :
				iv_new.setVisibility(View.GONE);
				String description = sp.get("description", "");
				ClientInfo clientInfo = new ClientInfo();
				if (description.contains("wr")) {
					description = description.replace("wr", "\n");
				}
				clientInfo.setDescription(description);
				if (hasUpdateVersion()) {
					Intent intent = new Intent(getActivity(),
							ConfirmDialogActivity.class);
					intent.putExtra(Constant.DIALOG_KEY, Constant.VERSION);
					intent.putExtra(Constant.CLIENT_INFO, clientInfo);

					if (AppUtils.getFramework().getService(
							IUserUsageDataRecorder.class) != null) {
						AppUtils.getFramework()
								.getService(IUserUsageDataRecorder.class)
								.doRecord(ActivityID.UPDATEDIALOG.ordinal());
					}

					startActivityForResult(intent, 100);
				} else {
					Toast.makeText(getActivity(), "已经是最新版本", 1).show();
				}
				break;
			case R.id.ll_idea_back :
				Intent intent = new Intent(getActivity(),
						GDPrivateZooInformCommonActivity.class);
				intent.putExtra(Constant.PRIVATE_INFORM_EDIT,
						Constant.IDEA_BACK);
				startActivity(intent);
				break;
			case R.id.ll_apps_recommend :
				// Intent intent3 = new Intent(getActivity(),
				// GDPrivateZooInformCommonActivity.class);
				// intent3.putExtra(Constant.PRIVATE_INFORM_EDIT,
				// Constant.APP_RECOMMENED);
				// startActivity(intent3);
				Intent intent3 = new Intent(getActivity(),
						GDPushMsgWebLinkActivity.class);
				intent3.putExtra(Constant.DIALOG_KEY,
						Constant.MOBLE_APP_RECOMMEND);
				startActivity(intent3);

				break;
			case R.id.ll_mobile_market :
				// Uri uri = Uri
				// .parse("http://mm.10086.cn/android/info/221039.html?stag=cT0lRTklODAlOUElRTQlQkYlQTElRTUlOEElQTklRTYlODklOEImcD0xJnQ9JUU1JTg1JUE4JUU5JTgzJUE4JnNuPTEmYWN0aXZlPTE%3D");
				// Intent it = new Intent(Intent.ACTION_VIEW, uri);
				// startActivity(it);'
				Intent intent5 = new Intent(getActivity(),
						GDPushMsgWebLinkActivity.class);
				intent5.putExtra(Constant.DIALOG_KEY, MOBLIE_MARKET);
				startActivity(intent5);
				break;
			case R.id.tv_privacy_items :
				Intent intent4 = new Intent(getActivity(),
						GDPrivateZooInformCommonActivity.class);
				intent4.putExtra(Constant.PRIVATE_INFORM_EDIT,
						Constant.PRIVACY_ITEM);
				startActivity(intent4);
				break;
			default :
				break;
		}
	}
	private boolean hasUpdateVersion() {
		return sp.hasUpdatedApp();
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == -1) {
			upDataApp();
		}
	}
	private void upDataApp() {
		final String downloadUrl = sp.get("url", "");
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			if (!TextUtils.isEmpty(downloadUrl)) {
				if (AppUtils.getFramework().getService(
						IUserUsageDataRecorder.class) != null) {
					AppUtils.getFramework()
							.getService(IUserUsageDataRecorder.class)
							.doRecord(ActivityID.UPDATENOW.ordinal());
				}
				Intent t = new Intent(getActivity(), DownAPK.class);
				t.putExtra("downurl", downloadUrl);
				getActivity().startService(t);
			} else {
				Tools.showToast(getActivity(), "未获取到下载地址！");
				Log.v("downloadUrl", "下载地址为空");
			}

		} else {
			Tools.showToast(getActivity(), "请检查SD卡是否可用后重试!");
		}

	}
}
