package com.wxxr.callhelper.qg.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.framework.SiMiContactsBaseActivity;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.util.StringUtils;

public class SiMiContactsAddActivity extends SiMiContactsBaseActivity implements OnClickListener {
	private EditText gd_et_simi_add_telnum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.simi_contacts_add);

		gd_et_simi_add_telnum = (EditText) findViewById(R.id.gd_et_simi_add_telnum);
		
		findViewById(R.id.btn_dialog_ok).setOnClickListener(this);
		findViewById(R.id.btn_dialog_cancel).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_dialog_ok:
			final String number = StringUtils.trim(gd_et_simi_add_telnum.getText()
					.toString());
			operationSiMiContacts(number);
			break;
		case R.id.btn_dialog_cancel:
			finish();
			break;

		}

	}

}
