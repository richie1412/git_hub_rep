package com.wxxr.callhelper.qg.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.callhelper.qg.widget.LocusPassWordView;
import com.wxxr.callhelper.qg.widget.LocusPassWordView.OnCompleteListener;
import com.wxxr.mobile.core.log.api.Trace;

public class SiMiSetPassword2Fragment extends AbstractFragment implements OnClickListener
{
	private static final Trace log = Trace.register(SiMiSetPassword2Fragment.class);

	private LocusPassWordView lpwv;
	private String secondPassword;
	RelativeLayout rl_cancel;
	RelativeLayout rl_sure;
	private final ISimiPasswdSetupContext setupCtx;

	private boolean resetpass;
	
	public SiMiSetPassword2Fragment(ISimiPasswdSetupContext ctx,boolean isresetpass){
		this.setupCtx = ctx;
		resetpass=isresetpass;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.setpassword_phase2,container,false);
		
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
		lpwv = (LocusPassWordView) view.findViewById(R.id.mLocusPassWordView2);
		lpwv.setOnCompleteListener(new OnCompleteListener()
		{
			@Override
			public void onComplete(String mPassword)
			{
				secondPassword = mPassword;
				
				if(!setupCtx.updatePassword(secondPassword)){
					lpwv.clearPassword();
				}
			}
		});
		findView(view);
		return view;
	}

	private void findView(View v)
	{
		rl_cancel = (RelativeLayout) v.findViewById(R.id.rl_cancel);
		rl_sure = (RelativeLayout) v.findViewById(R.id.rl_sure);
		rl_sure.setOnClickListener(this);
		rl_cancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		
		case R.id.gd_iv_titlebar_left:
			setupCtx.cancelPasswdSetup();
			break;
		case R.id.rl_cancel:
			setupCtx.cancelPasswdSetup();
			break;
		case R.id.rl_sure:

			if(!setupCtx.updatePassword(secondPassword)){
				lpwv.clearPassword();
			}
			break;
		}
	}

	@Override
	protected Trace getLogger() {
		return log;
	}

}
