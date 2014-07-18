package com.wxxr.callhelper.qg.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IPrivateSMService;
import com.wxxr.callhelper.qg.bean.PrivateSMSummary;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.ui.ConfirmDialogActivity;
import com.wxxr.callhelper.qg.ui.NewConfirmDialogActivity;
import com.wxxr.callhelper.qg.ui.SiMiContactsAddActivity;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.util.StringUtils;

/**
 * 
 * @author yinzhen
 * 
 */
public class SiMiContactsBaseActivity extends BaseActivity {

	private static final Trace log = Trace
			.register(SiMiContactsAddActivity.class);

	protected void operationSiMiContacts(final String number) {

//		boolean tmp = false;
//		List<PrivateSMSummary> x = getService(IPrivateSMService.class)
//				.getSummarys();
//		if (x == null || x.isEmpty()) {
//			tmp = true;
//		}
//		final boolean isAlertSpecailMessage = tmp;

//		if (StringUtils.isBlank(number) || (number.length() != 11&&number.length()!=6)
//				|| !StringUtils.isNumeric(number)) {
		if(StringUtils.isBlank(number) || !Tools.isMobileNum(number)){
//			showMessageBox("电话号码无效，请重新输入");
			Toast.makeText(this, "电话号码无效，请重新输入", Toast.LENGTH_SHORT).show();
		} else {
			CMProgressMonitor monitor = new CMProgressMonitor(this) {
				@Override
				protected void handleFailed(Throwable cause) {
					log.warn("Caught exception when add private number :"
							+ number, cause);
//					showMessageBox("现在无法把号码：[" + number + "]加入私密号码簿，请稍后再试...");
					Toast.makeText(SiMiContactsBaseActivity.this, "现在无法把号码：[" + number + "]加入私密号码簿，请稍后再试...", 0).show();
				}

				@Override
				protected void handleDone(Object returnVal) {
					//特别提示
//					if (isAlertSpecailMessage) {
//						Intent intent = new Intent(
//								SiMiContactsBaseActivity.this,
//								ConfirmDialogActivity.class);
//						intent.putExtra(Constant.DIALOG_KEY,
//								Constant.SPECIAL_PROMPT);
//						startActivity(intent);
//					} else {
						boolean b = Boolean.valueOf(returnVal.toString());
						if (b) {
//							showMessageBox("号码：[" + number + "]加入私密号码簿成功 。");
						} else {
//							showMessageBox("号码：[" + number + "]已是私密联系人，无需重复添加 。");
							Toast.makeText(SiMiContactsBaseActivity.this, "号码：[" + number + "]已是私密联系人，无需重复添加 。", 0).show();
						}

//					}
					finish();

				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see com.wxxr.callhelper.CMProgressMonitor#getDialogParams()
				 */
				@Override
				protected Map<String, Object> getDialogParams() {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(DIALOG_PARAM_KEY_TITLE, "系统提示");
					map.put(DIALOG_PARAM_KEY_MESSAGE, "正在操作私密号码簿,请稍侯...");
					return map;
				}
			};
			monitor.executeOnMonitor(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					return getService(IPrivateSMService.class)
							.addPrivateNumber(number);
				}
			});
		}
	}

	protected void operationSiMiContactsMore(final List<String> numbers,
			final ArrayList<String> NotMobNum) {

		if (numbers == null || numbers.isEmpty()) {
			return;
		}

//		boolean tmp = false;
//		List<PrivateSMSummary> x = getService(IPrivateSMService.class)
//				.getSummarys();
//		if (x == null || x.isEmpty()) {
//			tmp = true;
//		}
//		final boolean isAlertSpecailMessage = tmp;

		CMProgressMonitor monitor = new CMProgressMonitor(this) {

			@Override
			protected void handleDone(Object returnVal) {
//				if (isAlertSpecailMessage) {
//					Intent intent = new Intent(SiMiContactsBaseActivity.this,
//							ConfirmDialogActivity.class);
//					intent.putExtra(Constant.DIALOG_KEY,
//							Constant.SPECIAL_PROMPT);
//					startActivity(intent);
//				} else {
					if (!NotMobNum.isEmpty()) {
						for (String illegalNum : NotMobNum) {
//							showMessageBox("您已成功添加" + numbers.size()
//									+ "位联系人，其中[" + illegalNum + "]不是手机号码联系人,不能添加");
							String name = Tools.getContactsName(getApplicationContext(), illegalNum);
							String content = "您已成功添加" + numbers.size() + "位联系人，其中[" + (TextUtils.isEmpty(name) ? illegalNum : name) + "]不是手机号码联系人，不能添加。";
							Intent intent = new Intent(SiMiContactsBaseActivity.this, ConfirmDialogActivity.class);
							intent.putExtra(Constant.DIALOG_KEY, Constant.SPECIAL_PROMPT);
							intent.putExtra(Constant.DIALOG_CONTENT, content);
							startActivity(intent);
						}
					} 
				else {
//						showMessageBox("您已成功添加" + numbers.size() + "个联系人");
					}

//				}
				finish();
			}

			@Override
			protected void handleFailed(Throwable cause) {

			}

			@Override
			protected Map<String, Object> getDialogParams() {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(DIALOG_PARAM_KEY_TITLE, "系统提示");
				map.put(DIALOG_PARAM_KEY_MESSAGE, "正在操作私密号码簿,请稍侯...");
				return map;
			}
		};

		monitor.executeOnMonitor(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				getService(IPrivateSMService.class).addPrivateNumberMore(
						numbers);
				return null;
			}
		});
	}
}
