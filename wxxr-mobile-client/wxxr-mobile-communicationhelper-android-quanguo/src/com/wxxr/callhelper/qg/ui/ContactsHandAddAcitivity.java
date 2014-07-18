package com.wxxr.callhelper.qg.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.ui.gd.BaseActityExtend;
import com.wxxr.callhelper.qg.utils.Tools;
/**
 * 手动添加联系人
 * 
 * @author cuizaixi
 * @see ContactsListActivity
 */
public class ContactsHandAddAcitivity extends BaseActityExtend {
	private EditText et_add_telnum;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_hand_add);
		findView();
		processLogic();
	}
	@Override
	protected void findView() {
		et_add_telnum = (EditText) findViewById(R.id.et_add_telnum);
		findViewById(R.id.btn_dialog_ok).setOnClickListener(this);
		findViewById(R.id.btn_dialog_cancel).setOnClickListener(this);
	}

	@Override
	protected void processLogic() {

	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_dialog_ok :
				if (Tools.isValidNumOrPwd(this, et_add_telnum.getText()
						.toString())) {
					Intent intent = new Intent(ContactsHandAddAcitivity.this,
							CSManagerActvity.class);
					intent.putExtra(CSManagerActvity.CONTACTS_NUMBERE,
							et_add_telnum.getText().toString());
					setResult(RESULT_OK, intent);
					finish();
				}
				break;
			case R.id.btn_dialog_cancel :
				finish();
				break;

		}

	}
}
