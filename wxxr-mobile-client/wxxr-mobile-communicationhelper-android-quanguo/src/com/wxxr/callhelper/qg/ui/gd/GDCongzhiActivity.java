package com.wxxr.callhelper.qg.ui.gd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.adapter.AbstractListAdapter;
import com.wxxr.callhelper.qg.adapter.CongzhiAdapter;
import com.wxxr.callhelper.qg.adapter.AbstractListAdapter.onSelecteChangeListener;
import com.wxxr.callhelper.qg.bean.BodyBeanCongzhi;
import com.wxxr.callhelper.qg.bean.SwitchSettingBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.db.dao.CongZhiBaoGaoDao;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.ui.ConfirmDialogActivity;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.callhelper.qg.widget.BottomTabBar;
/**
 * 广东版——充值界面
 * 
 * @author cuizaixi
 * 
 */
public class GDCongzhiActivity extends BaseActivity {
	private long mDeleteItemDate;
	private ListView lv_congzhi;
	private AbstractListAdapter<BodyBeanCongzhi> mAdapter;
	private CongZhiBaoGaoDao dao;
	private List<BodyBeanCongzhi> congzhi;
	private RelativeLayout ll_main;
	private View selete_menu;
	private LinearLayout bottom_menu;
	private TextView tv_content;
	private TextView tv_type;
	private LinearLayout empty;
	private TextView titlebar_name;
	private BottomTabBar btb;
	private ViewMediator mViewMediator;
	private SelectMenu mSelectMenu;
	private OperationMenu mOperationMenu;
	private ButtomMenu mButtomMenu;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gd_congzhi);
		findView();
		btb = (BottomTabBar) findViewById(R.id.home_bottom_tabbar);
		btb.setActivtiy(this, 0);
		processLogic();
	}

	private void findView() {
		lv_congzhi = (ListView) findViewById(R.id.lv_congzhi);
		ll_main = (RelativeLayout) findViewById(R.id.ll_main);
		tv_content = (TextView) findViewById(R.id.tv_description);
		tv_type = (TextView) findViewById(R.id.tv_prompt_type);
		findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(this);
		findViewById(R.id.gd_iv_titlebar_right).setOnClickListener(this);
		findViewById(R.id.btn_recharge).setOnClickListener(this);
		findViewById(R.id.btn_query_reminning).setOnClickListener(this);
		findViewById(R.id.iv_menu_close).setOnClickListener(this);
		bottom_menu = (LinearLayout) findViewById(R.id.ll_congzhi_bottom_menu);
		titlebar_name = (TextView) findViewById(R.id.tv_titlebar_name);
		selete_menu = this.getLayoutInflater().inflate(R.layout.menu, null);
		// 无短信界面
		empty = (LinearLayout) this.getLayoutInflater().inflate(
				R.layout.congzhi_sms_empty, null);
		empty.findViewById(R.id.btn_empty_recharg).setOnClickListener(this);
	}
	@Override
	protected void onResume() {
		super.onResume();
		refreshList();
	}
	private void processLogic() {
		titlebar_name.setText("充值");
		dao = CongZhiBaoGaoDao.getInstance(this);
		mAdapter = new CongzhiAdapter(this);
		((ViewGroup) lv_congzhi.getParent()).addView(empty);
		lv_congzhi.setEmptyView(empty);
		lv_congzhi.setDividerHeight(1);
		congzhi = dao.findAll();
		mAdapter.setData(congzhi);
		lv_congzhi.setAdapter(mAdapter);
		mViewMediator = new ViewMediator();
		mSelectMenu = new SelectMenu(mViewMediator);
		mOperationMenu = new OperationMenu(mViewMediator);
		mButtomMenu = new ButtomMenu(mViewMediator);
		mViewMediator
				.setColleguesMenu(mSelectMenu, mOperationMenu, mButtomMenu);
		mAdapter.setOnSelecteChangeListener(new onSelecteChangeListener() {

			@Override
			public void onSelecteChange(HashMap<Integer, Boolean> selecte) {
				mSelectMenu.invalidateSelBtn();
			}
		});
		// 单击弹出充值对话界面
		lv_congzhi.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				BodyBeanCongzhi bean = congzhi.get(position);
				mOperationMenu.showOptMenu(bean);
			}

		});
		// 长按弹出删除弹框
		lv_congzhi.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				mDeleteItemDate = congzhi.get(position).getCdate();
				Intent intent = new Intent(getApplicationContext(),
						ConfirmDialogActivity.class);
				intent.putExtra(Constant.DIALOG_KEY,
						Constant.DELETE_SINGLE_ITEM);
				startActivityForResult(intent, Constant.DELETE_SINGLE_ITEM);
				return false;
			}
		});
	}
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.iv_menu_close :
				mOperationMenu.hideOptMenu();
				break;
			case R.id.gd_iv_titlebar_left :
				this.finish();
				break;
			case R.id.gd_iv_titlebar_right :
				Intent intent = new Intent(this,
						GDSwitchSettingCommonActivty.class);
				intent.putExtra(GDSwitchSettingCommonActivty.SWITCH_SETTING,
						new SwitchSettingBean("设置", "开启充值提醒",
								R.string.congzhi_setting_description,
								Constant.CONGZHI_SETTING, 0));
				startActivity(intent);
				break;
			case R.id.btn_recharge :
				Tools.call(this, "10086");
				Toast.makeText(this, "语音充值，进入主菜单后按2再按1", 1).show();
				break;
			case R.id.btn_empty_recharg :
				Intent intent2 = new Intent(this, GDShareDialog.class);
				intent2.putExtra(Constant.DIALOG_KEY,
						Constant.CONFIRMED_CONGZHI);
				startActivity(intent2);
				break;
			case R.id.btn_query_reminning :
				Tools.sendMsg(this, "101", "10086", 1);
				Toast.makeText(this, "请到手机收件箱查收10086余额短信", 1).show();
				break;
			case R.id.ll_all_choose :
				mAdapter.selecteAll();
				mSelectMenu.invalidateSelBtn();
				break;
			case R.id.ll_delete :
				Intent intent1 = new Intent(getApplicationContext(),
						ConfirmDialogActivity.class);
				intent1.putExtra(Constant.DIALOG_KEY,
						Constant.CONGZHI_MENU_DELETE);
				startActivityForResult(intent1, Constant.CONGZHI_MENU_DELETE);
				break;
			case R.id.ll_cancle :
				mAdapter.disSelectAll();
				mSelectMenu.invalidateSelBtn();
				break;
			default :
				break;
		}
	}
	private void refreshList() {
		congzhi = dao.findAll();
		mAdapter.setData(congzhi);
		mAdapter.notifyDataSetChanged();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
			case Constant.DELETE_SINGLE_ITEM :
				dao.deleteSingleSms(mDeleteItemDate);
				refreshList();
				break;
			case Constant.CONGZHI_MENU_DELETE :
				List<BodyBeanCongzhi> checkedItem = mAdapter.getCheckedItem();
				if (!checkedItem.isEmpty()) {
					for (int i = 0; i < checkedItem.size(); i++) {
						dao.deleteSingleSms(checkedItem.get(i).getCdate());
					}
					refreshList();
				}
				mSelectMenu.showSelectMenu(mAdapter);
				break;
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			mSelectMenu.showSelectMenu(mAdapter);
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mViewMediator.hasBtnShowing()) {
				mViewMediator.HideAllShowingBtn();
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	// 用来整体控制底部导航栏、选择框、和单击弹出框的中介者(控制器)。
	private class ViewMediator {
		private ButtomMenu btmMenu;
		private OperationMenu optMenu;
		private SelectMenu selMenu;
		protected void execute(String viewName, Object... objects) {
			if (viewName.equals("btmMenu.show")) {
				this.showBtmMenu();
			} else if (viewName.equals("optMenu.show")) {
				this.showOptMenu((BodyBeanCongzhi) objects[0]);
			} else if (viewName.equals("selMenu.show")) {
				this.showSelMenu((CongzhiAdapter) objects[0]);
			} else if (viewName.equals("optMenu.hide")) {
				this.hideOptMenu();
			}
		}
		private void setColleguesMenu(SelectMenu pselectMenu,
				OperationMenu poperationMenu, ButtomMenu pbuttomMenu) {
			this.optMenu = poperationMenu;
			this.selMenu = pselectMenu;
			this.btmMenu = pbuttomMenu;
		}
		private void showBtmMenu() {
			if (this.btmMenu.getViewState() == ViewState.isShowing) {
				return;
			} else {
				this.btmMenu.inflateBtmMenu();
				this.btmMenu.setViewState(ViewState.isShowing);
			}

		}
		private void showOptMenu(BodyBeanCongzhi bean) {
			if (this.selMenu.getViewState() == ViewState.isShowing) {
				return;
			} else {
				this.optMenu.inflateOptMenu(bean);
				this.btmMenu.hideBtmMenu(true);
				this.btmMenu.setViewState(ViewState.isHiding);
				this.optMenu.setViewState(ViewState.isShowing);
			}

		}
		private void showSelMenu(AbstractListAdapter<BodyBeanCongzhi> adapter) {
			if (this.optMenu.getViewState() == ViewState.isShowing) {
				return;
			}
			if (this.selMenu.getViewState() == ViewState.isShowing) {
				adapter.hideCheckBox();
				this.selMenu.hideSelectMenu();
				this.showBtmMenu();
				this.btmMenu.setViewState(ViewState.isShowing);
				this.selMenu.setViewState(ViewState.isHiding);
			} else if (this.selMenu.inflateSelectMenu(adapter)) {
				adapter.showCheckBox();
				this.btmMenu.hideBtmMenu(false);
				this.selMenu.setViewState(ViewState.isShowing);
				this.btmMenu.setViewState(ViewState.isHiding);
			}
		}
		private void hideOptMenu() {
			this.optMenu.hideSelf();
			this.optMenu.setViewState(ViewState.isHiding);
			this.btmMenu.showButtomMenu();
		}
		private boolean hasBtnShowing() {
			return this.optMenu.getViewState() == ViewState.isShowing
					|| this.selMenu.getViewState() == ViewState.isShowing;
		}
		private void HideAllShowingBtn() {
			if (this.optMenu.getViewState() == ViewState.isShowing) {
				this.optMenu.hideOptMenu();
			}
			if (this.selMenu.getViewState() == ViewState.isShowing) {
				this.selMenu.showSelectMenu(mAdapter);
			}
		}
	}
	// 底部导航栏
	public class ButtomMenu extends ViewCollegue {

		protected ButtomMenu(ViewMediator viewMediator) {
			super(viewMediator);
		}
		public void showButtomMenu() {
			super._viewMediator.execute("btmMenu.show");
		}
		public void inflateBtmMenu() {
			btb.setVisibility(View.VISIBLE);
		}
		public void hideBtmMenu(boolean gone) {
			if (btb.getVisibility() == View.VISIBLE) {
				if (gone) {
					btb.setVisibility(View.GONE);
				} else {
					btb.setVisibility(View.INVISIBLE);
				}

			}
		}
	}

	// 单击操作弹出框
	public class OperationMenu extends ViewCollegue {

		protected OperationMenu(ViewMediator viewMediator) {
			super(viewMediator);
			this.setViewState(ViewState.isHiding);
		}
		private void showOptMenu(Object obj) {
			super._viewMediator.execute("optMenu.show", obj);
		}
		private void hideOptMenu() {
			super._viewMediator.execute("optMenu.hide");
		}
		private void hideSelf() {
			bottom_menu.setVisibility(View.GONE);
		}
		public void inflateOptMenu(BodyBeanCongzhi bean) {
			Integer type = bean.getType();
			switch (type) {
				case BodyBeanCongzhi.YUEJIE :
					tv_type.setText("月结提醒:");
					break;
				case BodyBeanCongzhi.YUE :
					tv_type.setText("余额提醒:");
					break;
				case BodyBeanCongzhi.TINGJIE :
					tv_type.setText("停机提醒:");
					break;
			}
			String content = bean.getContent();
			tv_content.setText(content);
			bottom_menu.setVisibility(View.VISIBLE);
		}
	}
	// 选择导航栏
	public class SelectMenu extends ViewCollegue {
		private PopupWindow mPop;
		private TextView tv_delete;
		private TextView tv_cancel;
		private LinearLayout ll_all_choose;
		private LinearLayout ll_delete;
		private LinearLayout ll_cancle;
		private SelectMenu(ViewMediator viewMediator) {
			super(viewMediator);
			this.setViewState(ViewState.isHiding);
		}
		private void showSelectMenu(AbstractListAdapter<BodyBeanCongzhi> adapter) {
			super._viewMediator.execute("selMenu.show", adapter);
		}
		public boolean inflateSelectMenu(
				AbstractListAdapter<BodyBeanCongzhi> adapter) {
			if (adapter.getCount() == 0) {
				return false;
			}
			if (this.mPop == null) {
				this.mPop = getMenu(GDCongzhiActivity.this, selete_menu);
				this.mPop.showAtLocation(ll_main, Gravity.BOTTOM, 0, 0);
				ll_all_choose = (LinearLayout) selete_menu
						.findViewById(R.id.ll_all_choose);
				ll_delete = (LinearLayout) selete_menu
						.findViewById(R.id.ll_delete);
				ll_cancle = (LinearLayout) selete_menu
						.findViewById(R.id.ll_cancle);
				ll_all_choose.setOnClickListener(GDCongzhiActivity.this);
				ll_delete.setOnClickListener(GDCongzhiActivity.this);
				ll_cancle.setOnClickListener(GDCongzhiActivity.this);
				ll_cancle.setClickable(false);
				ll_delete.setClickable(false);
				tv_delete = (TextView) selete_menu
						.findViewById(R.id.gd_tv_menu_del);
				tv_cancel = (TextView) selete_menu
						.findViewById(R.id.gd_tv_menu_cancel);
				tv_delete.setText("删  除");
				tv_cancel.setTextColor(getResources().getColor(
						R.color.gd_item_eighty));
				tv_delete.setTextColor(getResources().getColor(
						R.color.gd_item_eighty));
				lv_congzhi
						.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
				adapter.notifyDataSetChanged();
				return true;
			}
			return true;
		}
		private void hideSelectMenu() {
			if (this.mPop != null) {
				this.mPop.dismiss();
				this.mPop = null;
				mAdapter.notifyDataSetChanged();
			}
		}
		private void invalidateSelBtn() {
			if (mAdapter.atLeastOnChecked()) {
				ll_cancle.setClickable(true);
				ll_delete.setClickable(true);
				this.tv_cancel.setTextColor(getResources().getColor(
						R.color.gd_titlebar_text));
				this.tv_delete.setTextColor(getResources().getColor(
						R.color.gd_titlebar_text));
			} else {
				ll_cancle.setClickable(false);
				ll_delete.setClickable(false);
				this.tv_cancel.setTextColor(getResources().getColor(
						R.color.gd_item_eighty));
				this.tv_delete.setTextColor(getResources().getColor(
						R.color.gd_item_eighty));
			}
		};
	}
	private abstract class ViewCollegue {
		protected ViewMediator _viewMediator;
		protected ViewState mViewState;
		protected ViewCollegue(ViewMediator viewMediator) {
			this._viewMediator = viewMediator;
		}
		protected void setViewState(ViewState viewstate) {
			this.mViewState = viewstate;
		}
		protected ViewState getViewState() {
			return this.mViewState;
		}
	}
	private enum ViewState {
		isShowing, isHiding
	}
}
