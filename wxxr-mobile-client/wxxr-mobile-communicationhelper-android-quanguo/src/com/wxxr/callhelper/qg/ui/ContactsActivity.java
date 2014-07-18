package com.wxxr.callhelper.qg.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.adapter.ContactAdapter;
import com.wxxr.callhelper.qg.adapter.ContactAdapter.ViewHolder;
import com.wxxr.callhelper.qg.bean.ContactInfo;
import com.wxxr.callhelper.qg.framework.SiMiContactsBaseActivity;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.callhelper.qg.widget.DCMyViewGroupContacts;
import com.wxxr.callhelper.qg.widget.MyLetterListView;
import com.wxxr.callhelper.qg.widget.MyLetterListView.OnTouchingLetterChangedListener;
import com.wxxr.mobile.core.log.api.Trace;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ContactsActivity extends SiMiContactsBaseActivity {
	private BaseAdapter adapter;
	private ListView personList;//
	private TextView overlay;//
	private MyLetterListView letterListView;// 对象会改变
	private MyAsyncQueryHandler asyncQuery;
	private static final String NAME = "name", NUMBER = "number",
			FIRST_CHAR = "firstnum", SORT_KEY = "sort_key";
	private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置,字母-位置
	private String[] sections;// 存放存在的汉语拼音首字母
	private OverlayThread overlayThread;
	private LetterListViewListener onTouchingLetterChangedListener;// 这个是这个类里面定义的对象
	private LinearLayout ll_sure;
	private LinearLayout ll_cancel;
	private HashMap<String, Boolean> perMap;
	private HashMap<String, CheckBox> telMap;
	private ArrayList<CheckBox> checks;
	private WindowManager windowManager;
	private EditText editText;
	private ListView listView;
	private ContactAdapter contactAdapter;
	private List<ContactInfo> mContactInfos;
	private RelativeLayout rl_getFocus;
	private DisplayMetrics metric;
	private LinkedList<DCMyViewGroupContacts> items = new LinkedList<DCMyViewGroupContacts>();
	private int num = 0;
	private List<String> lists = new ArrayList<String>();
	private Cursor phoneCur;
	private TextView tv_sure, tv_cancle;
	private String values;
	private String selection;
	private static final Trace log = Trace.register(ContactsActivity.class);

	// 被选定的手机号码
	Hashtable<String, String> selphones = new Hashtable<String, String>();
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (phoneCur != null)
				initContactInfos(phoneCur);
		}
	};
	private FrameLayout fl_contacts_main;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_main);
		findView();
		processLogic();
	}

	@Override
	protected void onResume() {
		// 初始化汉语拼音首字母弹出提示框
		initOverlay();
		Uri uri = Uri.parse("content://com.android.contacts/data/phones");
		String[] projection = {"_id", "display_name", "data1", "sort_key"};
		asyncQuery.startQuery(0, null, uri, projection, null, null,
				"sort_key COLLATE LOCALIZED asc");
		if (num > 0) {
			num = 0;
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		windowManager.removeView(overlay);
		super.onPause();
	}

	private void findView() {
		TextView tv_titlebar_name = (TextView) findViewById(R.id.tv_titlebar_name);
		tv_titlebar_name.setText("添加私密联系人");
		findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(this);
		findViewById(R.id.gd_iv_titlebar_right).setVisibility(View.INVISIBLE);

		personList = (ListView) findViewById(R.id.list_view);
		listView = (ListView) findViewById(R.id.lv_list1);
		letterListView = (MyLetterListView) findViewById(R.id.MyLetterListView01);

		ll_sure = (LinearLayout) findViewById(R.id.ll_contacts_sure);
		ll_cancel = (LinearLayout) findViewById(R.id.ll_contacts_cancle);
		rl_getFocus = (RelativeLayout) findViewById(R.id.rl_get_focus);
		editText = (EditText) findViewById(R.id.EditText01);
		tv_sure = (TextView) findViewById(R.id.tv_sure);
		tv_cancle = (TextView) findViewById(R.id.tv_cancle);

		metric = new DisplayMetrics();
		perMap = new HashMap<String, Boolean>();
		checks = new ArrayList<CheckBox>();
		telMap = new HashMap<String, CheckBox>();
		onTouchingLetterChangedListener = new LetterListViewListener();// new对象
		overlayThread = new OverlayThread();

		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		alphaIndexer = new HashMap<String, Integer>();
		mContactInfos = new ArrayList<ContactInfo>();

		fl_contacts_main = (FrameLayout) findViewById(R.id.fl_contacts_main);
	}

	private void processLogic() {
		ll_sure.setOnClickListener(this);
		ll_cancel.setOnClickListener(this);
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		letterListView
				.setOnTouchingLetterChangedListener(onTouchingLetterChangedListener);// 传给暴露的方法
		editText.addTextChangedListener(new MyTextWatcher());
		rl_getFocus.setFocusable(true);
		rl_getFocus.setFocusableInTouchMode(true);
		rl_getFocus.requestFocus();
		listView.setOnItemClickListener(new MyOnItemClickListener());
	}

	// 结果列表条目点击事件
	private class MyOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			log.info("点击条目位置：" + position);
			ViewHolder viewHolder = (ViewHolder) view.getTag();
			String number = viewHolder.tvItemNumAddr.getText().toString();
			int size = adapter.getCount();
			int selpos = 0;
			for (int i = 0; i < size; i++) {
				ContentValues v = (ContentValues) adapter.getItem(i);
				if (v.getAsString(NUMBER).equals(number)) {
					selpos = i;
					break;
				}
			}

			selphones.put(number, "");
			personList.smoothScrollToPosition(selpos);
			num++;
			adapter.notifyDataSetChanged();
			editText.setText("");
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		}

	}

	/**
	 * 搜索框
	 * 
	 * @author yinzhen
	 * 
	 */
	private class MyTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

			log.info("onTextChanged:" + s);
			if (mContactInfos != null) {
				mContactInfos.clear();
			}
			values = s.toString();

			if (!TextUtils.isEmpty(values)) {
				listView.setVisibility(View.VISIBLE);
				fl_contacts_main.setVisibility(View.GONE);
				letterListView.setVisibility(View.GONE);
				new Thread(new Runnable() {

					@Override
					public void run() {
						if (isLetterBegin(values)) {
							// 如果以字母开头
							log.info("是以字母开头：" + values);
							ergodicLetter(values);

						} else {

							selection = "data1 like ?";
							phoneCur = getContentResolver()
									.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
											null, selection,
											new String[]{"%" + values + "%"},
											null);

							Message msg = handler.obtainMessage();
							handler.sendMessage(msg);
						}

						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								if (contactAdapter == null) {
									// 结果列表adapter
									contactAdapter = new ContactAdapter(
											ContactsActivity.this,
											mContactInfos);
									listView.setAdapter(contactAdapter);
								} else {
									contactAdapter.setData(mContactInfos);
									contactAdapter.notifyDataSetChanged();
								}

							}
						});
					}
				}).start();
			} else {
				listView.setVisibility(View.GONE);
				fl_contacts_main.setVisibility(View.VISIBLE);
				letterListView.setVisibility(View.VISIBLE);
			}

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

	}

	/**
	 * 遍历号码集合
	 * 
	 * @param phoneCur
	 */
	private void initContactInfos(Cursor phoneCur) {
		if (phoneCur == null)
			return;
		phoneCur.move(-1);
		while (phoneCur.moveToNext()) {
			ContactInfo contactInfo = new ContactInfo();
			contactInfo.personId = phoneCur
					.getLong(phoneCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
			contactInfo.number = phoneCur
					.getString(phoneCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			contactInfo.name = phoneCur
					.getString(phoneCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			if (contactInfo.name == null) {
				contactInfo.name = contactInfo.number;
			}

			mContactInfos.add(contactInfo);
		}

		if (phoneCur != null)
			phoneCur.close();

	}

	/**
	 * @author yinzhen 遍历字母集合
	 * @param value
	 */
	private void ergodicLetter(String value) {
		List<ContentValues> lists = asyncQuery.getContactList();
		if (lists != null) {
			for (int i = 0; i < lists.size(); i++) {
				String pinyin = lists.get(i).getAsString(SORT_KEY);// 转换成拼音
				pinyin = pinyin.toLowerCase();
				pinyin = getPinyin(pinyin);
				if (pinyin != null && pinyin.contains(value)) {
					ContactInfo conInfo = new ContactInfo();
					conInfo.name = lists.get(i).getAsString(NAME);
					conInfo.number = lists.get(i).getAsString(NUMBER);
					mContactInfos.add(conInfo);
				}
			}
		}
	}

	/**
	 * 查询联系人
	 */
	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		private String number;
		private List<ContentValues> list;

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);

		}

		public List<ContentValues> getContactList() {
			return this.list;
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				items.clear();
				List<ContentValues> list = new ArrayList<ContentValues>();

				if (cursor.moveToFirst()) {
					do {
						ContentValues cv = new ContentValues();

						String name = cursor.getString(1);
						// String number = cursor.getString(2);
						number = cursor.getString(2);
						String sortKey = cursor.getString(3);
						if (number.startsWith("+86")) {
							cv.put(NAME, name);
							cv.put(NUMBER, number.substring(3)); // 去掉+86
							cv.put(SORT_KEY, sortKey);
						} else {
							cv.put(NAME, name);
							cv.put(NUMBER, number);
							cv.put(SORT_KEY, sortKey);
						}

						list.add(cv); // 所以的数据信息
					} while (cursor.moveToNext());
				}

				if (list.size() > 0) {
					this.list = list;
					setAdapter(list); // 传递给构造函数
				}

			}
		}
	}

	private void setAdapter(List<ContentValues> list) {
		telMap.clear();
		checks.clear();
		perMap.clear();
		adapter = new ListAdapter(this, list);
		personList.setAdapter(adapter);

	}

	/**
	 * 
	 * 显示列表adapter
	 * 
	 */
	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<ContentValues> list;

		public ListAdapter(Context context, List<ContentValues> list) {
			this.inflater = LayoutInflater.from(context);
			this.list = list;// 所有联系人
			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				// 当前汉语拼音首字母
				String currentStr = getAlpha(list.get(i).getAsString(SORT_KEY));
				// 上一个汉语拼音首字母，如果不存在为“ ”
				String previewStr = (i - 1) >= 0 ? getAlpha(list.get(i - 1)
						.getAsString(SORT_KEY)) : " ";
				if (!previewStr.equals(currentStr)) {
					String name = getAlpha(list.get(i).getAsString(SORT_KEY));
					alphaIndexer.put(name, i); // listItem中的 "首字母" 对应的listItem的
					sections[i] = name; //
				}
			}
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				view = inflater.inflate(R.layout.selectcity_list_item, null);
			} else {
				view = convertView;
			}

			// view = inflater.inflate(R.layout.selectcity_list_item, null);
			// 显示 拼音首字母
			TextView alpha = (TextView) view.findViewById(R.id.alpha);
			TextView name = (TextView) view.findViewById(R.id.name);
			View alpha_line = view.findViewById(R.id.alpha_line);
			TextView tv_total_contacts = (TextView) view
					.findViewById(R.id.tv_total_contacts);
			if (position == 0) {
				tv_total_contacts.setVisibility(View.VISIBLE);
				tv_total_contacts.setText(getCount() + "位联系人");
			} else {
				tv_total_contacts.setVisibility(View.GONE);
			}

			CheckBox cb = (CheckBox) view.findViewById(R.id.dc_group_check);
			cb.setTag(position);
			checks.add(cb);
			RelativeLayout rl_ck = (RelativeLayout) view
					.findViewById(R.id.check_relative);

			ContentValues cv = list.get(position);
			name.setText(cv.getAsString(NAME));

			String tempnum = cv.getAsString(NUMBER);
			if (selphones.containsKey(tempnum)) {
				cb.setChecked(true);
			} else {
				cb.setChecked(false);
			}

			String currentStr = getAlpha(list.get(position).getAsString(
					SORT_KEY));

			String previewStr = (position - 1) >= 0 ? getAlpha(list.get(
					position - 1).getAsString(SORT_KEY)) : " ";

			LinearLayout.LayoutParams params = null;
			if (!previewStr.equals(currentStr)) {
				alpha.setVisibility(View.VISIBLE);
				alpha.setText(currentStr);
				params = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, 4);
				params.setMargins(31, 0, 0, 0);
				alpha_line.setLayoutParams(params);
				alpha_line.setBackgroundColor(getResources().getColor(
						R.color.gd_private_lock_text_press));
			} else {
				alpha.setVisibility(View.GONE);
				params = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, 1);
				params.setMargins(31, 0, 0, 0);
				alpha_line.setLayoutParams(params);
				alpha_line.setBackgroundColor(getResources().getColor(
						R.color.gd_dialog_line));
			}

			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(
						CompoundButton paramCompoundButton, boolean paramBoolean) {
					if (num < 0) {
						num = 0;
					}
					String number = list
							.get(((Integer) paramCompoundButton.getTag())
									.intValue()).getAsString(NUMBER);
					if (paramBoolean) {
						if (!selphones.containsKey(number)) {
							selphones.put(number, "");
							num++;
						}

					} else {
						if (selphones.containsKey(number)) {
							num--;
							selphones.remove(number);
						}
					}

					if (num != 0) {
						tv_sure.setText("确  定" + "(" + num + ")");
					} else {
						tv_sure.setText("确  定");
					}

					// }
				}

			});

			return view;

		}

	}

	/**
	 * 初始化汉语拼音首字母弹出提示框
	 */
	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		windowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	// 弹出右侧字母（1秒钟）
	private class LetterListViewListener
			implements
				OnTouchingLetterChangedListener {
		@Override
		public void onTouchingLetterChanged(final String s) {// 反正这个方法会回调.这个String
																// s会改变
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				personList.setSelection(position);// 控制listView的移动
				overlay.setText(sections[position]);// 显示什么字母
				overlay.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overlayThread);// 隔一秒钟就不显示
				handler.postDelayed(overlayThread, 1500);
			}
		}

	}

	// 设置overlay不可见
	private class OverlayThread implements Runnable {
		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}

	}

	// 获得汉语拼音首字母
	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}

		if (str.trim().length() == 0) {
			return "#";
		}

		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式，判断首字母是否是英文字母
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		} else {
			return "#";
		}
	}

	/**
	 * 判断是否是字母开头
	 * 
	 * @author yinzhen
	 * @param s
	 * @return
	 */
	private boolean isLetterBegin(String s) {
		if (s == null) {
			return false;
		}
		log.info("截前：" + s);
		s = s.trim().substring(0, 1);
		log.info("截后：" + s);
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(s).matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取纯拼音字段
	 * 
	 * @author yinzhen
	 * @param str
	 * @return
	 */
	private String getPinyin(String str) {
		String result = "";
		for (int i = 0; i < str.length(); i++) {
			String c = str.substring(i, i + 1);
			if (c.matches("[a-zA-Z0-9]")) {
				result += c;
			}
		}
		return result;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.gd_iv_titlebar_left :
				finish();
				break;
			case R.id.ll_contacts_sure :
				lists.clear();
				String[] numbers = {};
				numbers = selphones.keySet().toArray(numbers);
				int size = numbers.length;
				if (size == 0) {
					return;
				}
				// NotMobNum,存放所有的非手机号码
				ArrayList<String> NotMobNum = new ArrayList<String>();
				for (int i = 0; i < size; i++) {
					String numWithNoSpacAndPre = Tools.checking(numbers[i]);
					if (Tools.isMobileNum(numWithNoSpacAndPre)) {
						lists.add(numWithNoSpacAndPre);
					} else {
						NotMobNum.add(numWithNoSpacAndPre);
					}
				}
				tv_sure.setText("确  定");
				tv_cancle.setTextColor(getResources().getColor(
						R.color.contacts_sure));

				num = 0;
				selphones.clear();
				adapter.notifyDataSetChanged();

				if (!NotMobNum.isEmpty() && lists.isEmpty()) {
					// 选择全是非手机号码
					for (String noNum : NotMobNum) {
						String name = Tools.getContactsName(this, noNum);
						// showMessageBox("[" + name + "]不是手机号码联系人，添加失败。");
						if (TextUtils.isEmpty(name)) {
							Toast.makeText(this,
									"[" + noNum + "]不是手机号码联系人，添加失败。", 0).show();
						} else {
							Toast.makeText(this,
									"[" + name + "]不是手机号码联系人，添加失败。", 0).show();
						}
						return;
					}
				} else {
					// 至少有一个手机号码
					operationSiMiContactsMore(lists, NotMobNum);
				}
				break;

			case R.id.ll_contacts_cancle :
				if (num != 0) {
					num = 0;
					adapter.notifyDataSetChanged();
					selphones.clear();
					tv_sure.setText("确  定");
				}
				break;
		}

	}

}