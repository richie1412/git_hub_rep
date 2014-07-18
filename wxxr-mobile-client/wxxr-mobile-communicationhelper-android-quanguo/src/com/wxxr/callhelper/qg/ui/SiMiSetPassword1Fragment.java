package com.wxxr.callhelper.qg.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.utils.ManagerSP;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.callhelper.qg.widget.LocusPassWordView;
import com.wxxr.callhelper.qg.widget.LocusPassWordView.OnCompleteListener;
import com.wxxr.mobile.core.log.api.Trace;

public class SiMiSetPassword1Fragment extends AbstractFragment implements OnClickListener
{
	private static final Trace log = Trace.register(SiMiSetPassword1Fragment.class);
	
	private LocusPassWordView lpwv;
	private String password;
	private ManagerSP sp;
	private ImageView iv_sm_lock_first;
	private int first;
	private final ISimiPasswdSetupContext setupCtx;

	private boolean resetpass;
	
	public SiMiSetPassword1Fragment(ISimiPasswdSetupContext ctx,boolean isretpass){
		this.setupCtx = ctx;
		resetpass=isretpass;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.setpassword_phase1, container, false);
		view.findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(this);
		view.findViewById(R.id.gd_iv_titlebar_right).setVisibility(View.INVISIBLE);
		((TextView)view.findViewById(R.id.tv_titlebar_name)).setText("私密信息锁");
		if(resetpass){
			((TextView)view.findViewById(R.id.tv_titlebar_name)).setText("重置密码");
		}
		
		RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.simi_main_back);
		Bitmap readBitmap = Tools.readBitmap(getActivity(), R.drawable.simi_background);
		BitmapDrawable bitmapDrawable = new BitmapDrawable(readBitmap);
		rl.setBackgroundDrawable(bitmapDrawable);
		lpwv = (LocusPassWordView) view.findViewById(R.id.mLocusPassWordView);
		lpwv.setOnCompleteListener(new OnCompleteListener()
		{
			@Override
			public void onComplete(String mPassword)
			{

				password = mPassword;
				
				if (password != null)
				{
					setupCtx.doPhase2(password);
				}

			}
		});

		RelativeLayout buttonSave = (RelativeLayout) view.findViewById(R.id.tvSave);
		RelativeLayout tvReset = (RelativeLayout) view.findViewById(R.id.tvReset);
		buttonSave.setOnClickListener(this);
		tvReset.setOnClickListener(this);

		iv_sm_lock_first = (ImageView) view.findViewById(R.id.iv_sm_lock_first);
		sp = ManagerSP.getInstance(getActivity());
//		processLogic();
		return view;
	}
	
//	private void processLogic() {
//		first = sp.get(Constant.SM_LOCK_FIRST, 0);
//		if(first == 0){
//			iv_sm_lock_first.setVisibility(View.VISIBLE);
//			iv_sm_lock_first.setBackgroundResource(R.drawable.sm_lock_first);
//		}
//		iv_sm_lock_first.setOnClickListener(this);
//	}


//	private void showDialog(String title)
//	{
//		Toast.makeText(SiMiSuoHomePasswordActivity.this, title, 0).show();
//	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		
		case R.id.gd_iv_titlebar_left:
			lpwv.clearPassword();
			this.password = null;	
			getActivity().finish();
			break;
		
		case R.id.tvSave:

			if (password != null)
			{
				setupCtx.doPhase2(password);
			}
			else
			{
				setupCtx.showMessageBox("密码不能为空,请设置密码");
			}

			break;
		case R.id.tvReset:
			lpwv.clearPassword();
			this.password = null;
			break;
			
		case R.id.iv_sm_lock_first:
			sp.update(Constant.SM_LOCK_FIRST, 1);
			iv_sm_lock_first.setVisibility(View.GONE);
			break;
			
		}

	}

	@Override
	protected Trace getLogger() {
		return log;
	}


	/* (non-Javadoc)
	 * @see com.wxxr.callhelper.ui.AbstractFragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	/* (non-Javadoc)
	 * @see com.wxxr.callhelper.ui.AbstractFragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
	}
}
