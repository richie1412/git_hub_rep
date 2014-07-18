package com.wxxr.callhelper.qg.ui;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.adapter.AbstractCommonAdapter;
/**
 * 对话框形式的菜单
 * 
 * @author cuizaixi
 * 
 */
public class DialogMenuActivity extends Activity {
	public static final String ITEM_LABLE_LIST = "item_lable_list";
	public static final String SELETED_ITEM = "seleted_item";
	public List<String> mLables;
	private ListView lv_main;
	private Intent mIntent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_menu);
		findView();
		processLogic();
	}

	private void findView() {
		lv_main = (ListView) findViewById(R.id.lv_dialog_menu_main);
	}
	private void processLogic() {
		mIntent = getIntent();
		if (mIntent == null) {
			return;
		}
		mLables = Arrays.asList(mIntent.getStringArrayExtra(ITEM_LABLE_LIST));
		if (mLables == null) {
			throw new InvalidParameterException(
					"the item lables  should be deliveried to use this dialog  ativity");
		}
		lv_main.setAdapter(new ItemAdapter(mLables, this));
		lv_main.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.putExtra(SELETED_ITEM, position + 1);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
	class ItemAdapter extends AbstractCommonAdapter<String> {

		ItemAdapter(List<String> data, Context context) {
			super(data, context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			view = getInflater().inflate(R.layout.dialog_menu_item, null);
			TextView item = (TextView) view
					.findViewById(R.id.tv_dialog_menu_item);
			item.setText(mData.get(position));
			return view;
		}

	}
}
