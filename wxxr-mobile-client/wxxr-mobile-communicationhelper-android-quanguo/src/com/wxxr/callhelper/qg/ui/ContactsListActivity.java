package com.wxxr.callhelper.qg.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.SeekedListDataAdapter;
import com.wxxr.callhelper.qg.SeekedListDataAdapter.ListedEntity;
import com.wxxr.callhelper.qg.adapter.AbstractListAdapter;
import com.wxxr.callhelper.qg.adapter.AbstractListAdapter.onSelecteChangeListener;
import com.wxxr.callhelper.qg.adapter.SeekedAdapter;
import com.wxxr.callhelper.qg.bean.ContactsInfoBean;
import com.wxxr.callhelper.qg.ui.gd.BaseActityExtend;
import com.wxxr.callhelper.qg.utils.ContactsUtil;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.callhelper.qg.widget.BladeView;
import com.wxxr.callhelper.qg.widget.BladeView.OnTextItemClickListener;
/**
 * 联系人列表
 * 
 * @author cuizaixi
 * 
 */
public class ContactsListActivity extends BaseActityExtend {
	private ListView lv_contacts_mian;
	private SeekedListDataAdapter mDataModule;
	private BladeView bv;
	private ArrayList<String> names;
	private List<ContactsInfoBean> allContacts;
	private RelativeLayout rv_search;
	private ImageView iv_search;
	private EditText et_search;
	public static final int SUCCESS_GET_CONTACTS = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContent(R.layout.contacts_list);
		findView();
		processLogic();
	}
	@Override
	protected void findView() {
		lv_contacts_mian = (ListView) findViewById(R.id.lv_contacts_mian);
		rv_search = (RelativeLayout) findViewById(R.id.rv_search);
		et_search = (EditText) findViewById(R.id.et_search);
		iv_search = (ImageView) findViewById(R.id.iv_serach);
		bv = (BladeView) findViewById(R.id.bv);
		bv.setBladeViewTitle("#");
		iv_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		iv_right.setVisibility(View.INVISIBLE);
		tv_title_name.setText("添加联系人");

	}
	@Override
	protected void processLogic() {
		AllContactsTask task = new AllContactsTask();
		task.execute(null);
		bv.setOnItemClickListener(new OnTextItemClickListener() {

			@Override
			public void onItemClick(String s) {
				if (mDataModule.getIndex().get(s) != null) {
					lv_contacts_mian
							.setSelection(mDataModule.getIndex().get(s));
				}
			}
		});
		lv_contacts_mian.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListedEntity item = (ListedEntity) lv_contacts_mian
						.getAdapter().getItem(position);
				String name = item.getName();
				for (ContactsInfoBean bean : allContacts) {
					if (bean.getName().equals(name)) {
						Intent intent = new Intent(ContactsListActivity.this,
								CSManagerActvity.class);
						intent.putExtra(CSManagerActvity.CONTACTS_NUMBERE,
								bean.getTelNum());
						setResult(RESULT_OK, intent);
						finish();
						break;
					}
				}
			}
		});
	}
	class AllContactsTask
			extends
				AsyncTask<String, Integer, List<ContactsInfoBean>> {

		@Override
		protected List<ContactsInfoBean> doInBackground(String... params) {
			return allContacts = ContactsUtil
					.getAllContacts(ContactsListActivity.this);
		}
		@Override
		protected void onPostExecute(List<ContactsInfoBean> result) {
			if (result != null && !result.isEmpty()) {
				names = new ArrayList<String>();
				for (ContactsInfoBean contactsInfoBean : allContacts) {
					String name = contactsInfoBean.getName();
					names.add(name);
				}
				mDataModule = new SeekedListDataAdapter(
						Tools.arrayConvertToCollection(names));
				lv_contacts_mian.setAdapter(new SeekedAdapter(
						R.layout.contacts_item, R.id.title, R.id.content,
						mDataModule, ContactsListActivity.this));
				mDataModule.prepareEntitiesList();
			}
		}
	}
}
