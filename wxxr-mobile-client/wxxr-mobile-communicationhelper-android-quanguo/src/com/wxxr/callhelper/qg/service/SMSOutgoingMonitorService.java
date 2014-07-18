package com.wxxr.callhelper.qg.service;

import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;

import com.wxxr.callhelper.qg.IPrivateSMService;
import com.wxxr.callhelper.qg.ITaskHandler;
import com.wxxr.callhelper.qg.bean.TextMessageBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.ui.ApplicationManager;
import com.wxxr.callhelper.qg.ui.TwoSecondDialogActivity;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.util.LRUMap;

public class SMSOutgoingMonitorService extends Service {
	private static final Trace log = Trace
			.register(SMSOutgoingMonitorService.class);
	private static int pending_status_waiting = 0;
	private static int pending_status_once = 1;
	private ContentObserver observer;
	private LRUMap<Integer, PendingMessage> pendingMap = new LRUMap<Integer, PendingMessage>(
			10, 60);
	private LinkedList<String> processedIds = new LinkedList<String>();

	private static class PendingMessage {
		Integer id, threadId;
		String content, phoneNumber;
		int status;
		boolean sent;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * return true if processed
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	private boolean markProcessed(int id, int type) {
		String key = new StringBuffer().append(id).append(type).toString();
		boolean val = processedIds.remove(key);
		if (val) {
			processedIds.addLast(key);
			return true;
		}
		processedIds.addLast(key);
		if (processedIds.size() >= 100) {
			processedIds.removeFirst();
		}
		return false;
	}

	@Override
	public void onCreate() {
		log.info("creating SMSOutgoingMonitorService ...");

		final ContentResolver contentResolver = getApplication()
				.getContentResolver();
		final Uri uriSMSURI = Uri.parse("content://sms");
		this.observer = new ContentObserver(null) {
			// private int messageId;
			@Override
			public void onChange(boolean selfChange) {
				if (log.isInfoEnabled()) {
					log.info("Received SMS outgoing event, selfChange :"
							+ selfChange);
				}
				Cursor cur = contentResolver.query(uriSMSURI, null, null, null,
						null);
				// this will make it point to the first record, which is the
				// last SMS sent
				try {
					cur.moveToNext();
					if (log.isInfoEnabled()) {
						StringBuffer info = new StringBuffer();
						for (int i = 0; i < cur.getColumnCount(); i++) {
							info.append(cur.getColumnName(i)).append("=[")
									.append(cur.getString(i)).append("], ");
						}
						log.info("Outgoing SM :{" + info.toString() + "}");
					}

					int type = cur.getInt(cur.getColumnIndex("type"));
					final String mt = cur.getString(cur
							.getColumnIndex("address"));
					final IPrivateSMService service = ((ApplicationManager) getApplication())
							.getFramework().getService(IPrivateSMService.class);
					if (!service.isAPrivateNumber(mt)) {
						if (log.isDebugEnabled()) {
							log.debug("Target number :" + mt
									+ " is not a private number !");
						}
						return;
					}
					final Integer id = cur.getInt(cur.getColumnIndex("_id"));
					final String content = cur.getString(cur.getColumnIndex("body"));
					final Integer threadId = cur.getInt(cur.getColumnIndex("thread_id"));
					final long sendtime = cur.getLong(cur.getColumnIndex("date"));
					if (markProcessed(id, type)) {
						return;
					}
					if (type == SMSConstants.TYPE_SENT) {

						// 获取 是否 导出给 私密联系人的信息，到我们的程序里，删除发件箱里的短信
						// 还没有设置为导出，弹出 询问框，设置为导出后，直接
						service.setMoveOutgoing2PrivateOutbox(true);
						Boolean bool = service.getMoveOutgoing2PrivateOutbox();
						// 是否是，刚刚从程序里导出来的 之前发出去的信息
						boolean addjust = Tools.remove_to_sysbox
								.containsKey(sendtime);

						if (!addjust) {
							if (bool == null || (!bool.booleanValue())) {
//								//没有设置为 导出
//								ITaskHandler handler1 = new ITaskHandler() {
//									@Override
//									public Object doExecute(Context context,
//											Object... args) {
//										if (log.isDebugEnabled()) {
//											log.debug("User select yes to move outgoing sm to private SM !");
//										}
//										service.setMoveOutgoing2PrivateOutbox(true);

//										((ApplicationManager) getApplication())
//												.getFramework().getExecutor()
//												.execute(new Runnable() {
//													@Override
//													public void run() {
//														Boolean bool = service
//																.getMoveOutgoing2PrivateOutbox();
//														// 刚刚从私信里移动到系统里的，就不用处理了
//														boolean addjust = Tools.remove_to_sysbox
//																.containsKey(sendtime);
//														// PendingMessage pmsg =
//														// pendingMap.peek(id);
//														if ((!addjust)&& ((bool != null) && bool.booleanValue())) {
//															processSentMessage(mt, id,content,threadId);
//															// pendingMap.remove(id);
//														}
//													}
//												});
//										return null;
//									}
//								};

//								Intent intent = new Intent(getApplication(),
//										TwoSecondDialogActivity.class);
//
//								intent.putExtra(Constant.DIALOG_MESSAGE,
//										"此联系人为私密联系人，是否将所有私密联系人信息移至私密信息锁中？");
//								intent.putExtra("OK_TEXT", "好  的");
//								intent.putExtra("CANCEL_TEXT", "不  用");
//								if (!Tools.isLiving(getApplication())) {
//									intent.putExtra(Constant.DIALOG_TITLE_KEY,
//											"中国移动通信助手");
//									intent.putExtra(Constant.DIALOG_ICON_KEY,
//											"LOGO");
//								}
//								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//								Long h1 = ((ApplicationManager) getApplication())
//										.getFramework().registerTaskHandler(
//												handler1);
//								intent.putExtra("OK_HANDLER",
//										String.valueOf(h1));
//								intent.putExtra(
//										Constant.DIALOG_INTERACTIVE_MODE,
//										Constant.DIALOG_INTERACTIVE_MODE_OK_CANCEL);
//								getApplication().startActivity(intent);
							} else {
								// // PendingMessage pmsg = pendingMap.peek(id);
								// if((!addjust)&&((bool !=
								// null)&&bool.booleanValue())){
							 		processSentMessage(mt, id, content, threadId);
								// pendingMap.remove(id);
								// }

						}

						}
					}
				} finally {
					if (cur != null) {
						try {
							cur.close();
						} catch (Throwable t) {
						}
					}
				}
			}
		};

		contentResolver.registerContentObserver(uriSMSURI, true, this.observer);

		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {

		log.info("start SMSOutgoingMonitorService ...");

		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		log.info("stopping SMSOutgoingMonitorService ...");
		if (this.observer != null) {
			ContentResolver contentResolver = getApplication()
					.getContentResolver();
			contentResolver.unregisterContentObserver(this.observer);
			this.observer = null;
		}
		Intent it = new Intent(SMSOutgoingMonitorService.this,
				SMSOutgoingMonitorService.class);
		this.startService(it);
	}

	/**
	 * 把系统短信导入到私密信息中
	 * @param mt
	 * @param service
	 * @param id
	 * @param content
	 * @param threadId
	 */
	protected void processSentMessage(String mt, final Integer id,
			final String content, final Integer threadId) {
		if (log.isInfoEnabled()) {
			log.info("Goging to move outging SM to private SM repository, mt :"
					+ mt);
		}
		final IPrivateSMService service = ((ApplicationManager) getApplication())
				.getFramework().getService(IPrivateSMService.class);
		TextMessageBean bean = new TextMessageBean();
		bean.setContent(content);
		bean.setMt(mt);
		bean.setNumber(mt);
		bean.setTimestamp(System.currentTimeMillis());
		service.addOutgoingMessage(bean);
		if (log.isInfoEnabled()) {
			log.info("SM was added to private SM repository, mt :" + mt);
		}

		final ContentResolver contentResolver = getApplication()
				.getContentResolver();
		// this.getContentResolver().delete(Uri.parse("content://sms/conversations/3"),
		// "_id=?", new String{"5"});
		final Uri uriSMSURI = Uri.parse("content://sms/conversations/"
				+ threadId);
		int cnt = contentResolver.delete(uriSMSURI, "_id = ?",
				new String[] { String.valueOf(id) });
		// Intent intent = new
		// Intent(getApplication(),TwoSecondDialogActivity.class);
		//
		// intent.putExtra(Constant.DIALOG_MESSAGE,
		// "您的短信已通过通信助手发送，如果您手机的系统发件箱还能看到这条短信，请刷新发件箱。");
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// getApplication().startActivity(intent);
		if (log.isInfoEnabled()) {
			if (cnt > 0) {
				log.info("SM was removed from system outbox, mt :" + mt);
			} else {
				log.info("SM was not removed from system outbox, mt :" + mt);
			}
		}
	}

}
