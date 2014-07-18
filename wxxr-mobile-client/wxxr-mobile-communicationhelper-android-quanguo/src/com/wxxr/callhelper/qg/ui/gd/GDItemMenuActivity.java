package com.wxxr.callhelper.qg.ui.gd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.utils.Tools;
/**
 * 广东版——长按菜单框。
 * 
 * @author cuizaixi
 * 
 */
public class GDItemMenuActivity extends BaseActityExtend {
	private Intent mIntent;
	private String message;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gd_item_menu);
		findView();
		processLogic();
	}

	@Override
	protected void findView() {
		findViewById(R.id.gd_ll_item_first).setOnClickListener(this);
		findViewById(R.id.gd_ll_item_second).setOnClickListener(this);
	}

	@Override
	protected void processLogic() {
		mIntent = getIntent();
		if (mIntent != null) {
			message = mIntent
					.getStringExtra(GDGuessLikeDetailActivity.GUESS_LIKE_MESSAGE);
		}
	}
	@Override
	public void onClick(View v) {
		super.onClick(v);
		int id = v.getId();
		switch (id) {
			case R.id.gd_ll_item_first :
				Tools.copyToClipboard(this, message);
				showToast("已复制到粘贴板");
				this.finish();
				break;
			case R.id.gd_ll_item_second :
				Tools.ShareBySMS(this, message);
				this.finish();
				break;
			default :
				break;
		}
	}
}
// 代码重构使用
/*
 * abstract class AbastractMenuItem { protected ItemImplementator
 * mImplementator; public AbastractMenuItem(ItemImplementator implementator) {
 * this.mImplementator = implementator; } abstract void onClick(); abstract void
 * setMenuTitle(); } class FirstMenuItem extends AbastractMenuItem {
 * 
 * public FirstMenuItem(ItemImplementator implementator) { super(implementator);
 * }
 * 
 * @Override void onClick() { super.mImplementator.method(); }
 * 
 * @Override void setMenuTitle() {
 * 
 * } } interface ItemImplementator { void method(); } class PastAction
 * implements ItemImplementator {
 * 
 * @Override public void method() {
 * 
 * }
 * 
 * }
 */