package com.wxxr.callhelper.qg.intercept.rule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wxxr.callhelper.qg.bean.BodyBeanCongzhi;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.db.dao.CongZhiBaoGaoDao;
import com.wxxr.callhelper.qg.ui.gd.GDShareDialog;
import com.wxxr.callhelper.qg.utils.ManagerSP;

import android.content.Context;
import android.content.Intent;
import android.util.Patterns;
/**
 * 充值短信拦截handler
 * 
 * @author cuizaixi
 * 
 */
public class CongzhiSmsHandler implements ISmsHandler {
	SimpleDateFormat format_month = new SimpleDateFormat("MM");
	SimpleDateFormat format_time = new SimpleDateFormat("HH:mm");
	public static String SMS_INFO = "sms_info";
	Calendar calendar = Calendar.getInstance();
	@Override
	public boolean handle(Context context, String smsContent,
			String targetnumber, boolean dontstore) {
		if (!targetnumber.equals("10086") || smsContent == null) {
			return false;
		}
		if (!isMatch(smsContent)) {
			return false;
		}
		Integer type = getSmsType(smsContent);
		BodyBeanCongzhi bean = createBean(smsContent, targetnumber, type);
		CongZhiBaoGaoDao dao = CongZhiBaoGaoDao.getInstance(context);
		dao.insert(bean);
		int setting = ManagerSP.getInstance(context).get(
				Constant.CONGZHI_SETTING, 0);
		if (setting == 0) {
			showDialog(bean, context);
		}
		return true;
	}
	private void showDialog(BodyBeanCongzhi bean, Context context) {
		Intent intent = new Intent(context, GDShareDialog.class);
		intent.putExtra(Constant.DIALOG_KEY, Constant.DIALOG_CHONGZHI);
		intent.putExtra(SMS_INFO, bean);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);

	}
	private BodyBeanCongzhi createBean(String smsContent, String targetnumber,
			int type) {
		BodyBeanCongzhi body = new BodyBeanCongzhi();
		body.type = type;
		body.content = smsContent;
		body.address = targetnumber;
		body.cdate = new Date().getTime();
		body.month = format_month.format(new Date().getTime());
		return body;
	}
	private boolean isMatch(String content) {
		return isYueJie(content) || isYuE(content) || isTingJi(content);
	}
	private boolean isYueJie(String content) {
		String[] regexs = {".*扣取套餐费用，但由于你现在余额不足以结算，请及时充值以免造成停机.*",
				".*尊敬的客户：【温馨提示】您.*月话费.*月结账单明细.*"};
		return patternMatch(regexs, content);
	}
	private boolean isYuE(String content) {
		String[] regexs = {".*尊敬的客户.*因您的基本账户余额低于10元，为不影响您的正常通信，请即时充值.*",
				".*温馨提示：尊敬的全球通客户，截至.*本号码需缴话费.*限制呼叫。中国移动.*", ".*账户余额小于.*元.*",
				".*账户余额*.低于.*元.*"};
		return patternMatch(regexs, content);
	}
	private boolean isTingJi(String content) {
		String[] regexs = {
				".*尊敬的客户，您已进入半停状态.*您将于48小时或透支额度超过5.00元后停机，所透支的费用将在下月充值时自动补扣，请及时缴费充值。中国移动广东公司.*",
				".*您好！因账号余额不足停机，请充值后开机恢复使用.*", ".*延迟停机服务将在2天结束.*",
				".*温馨提示：尊敬的客户，本号码已限制呼出.*",
				"温馨提示：尊敬的客户，本号码已限制呼出.*当有效余额为0元将暂停服务.*"};
		return patternMatch(regexs, content);

	}
	private int getSmsType(String smsContent) {
		Integer type = 0;
		if (isYueJie(smsContent)) {
			type = BodyBeanCongzhi.YUEJIE;
		} else if (isYuE(smsContent)) {
			type = BodyBeanCongzhi.YUE;
		} else if (isTingJi(smsContent)) {
			type = BodyBeanCongzhi.TINGJIE;
		}
		return type;

	}
	private boolean patternMatch(String[] regexs, String content) {
		for (int i = 0; i < regexs.length; i++) {
			Pattern pattern = Pattern.compile(regexs[i]);
			Matcher matcher = pattern.matcher(content);
			if (matcher.matches()) {
				return matcher.matches();
			}
		}
		return false;

	}
}
