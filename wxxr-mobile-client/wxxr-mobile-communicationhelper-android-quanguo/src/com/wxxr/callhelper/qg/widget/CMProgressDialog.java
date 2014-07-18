package com.wxxr.callhelper.qg.widget;


import com.wxxr.callhelper.qg.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * @author xijiadeng
 * */
public class CMProgressDialog extends ProgressDialog {

	private CharSequence mTitle,mMessage;
	private Drawable mIcon;
	private int mResId;
	private Context mContext;
	private int mTheme;
	private TextView mTextViewTitle,mTextViewMessage;
	private ImageView mImageViewIcon;
	
	public CMProgressDialog(Context context){
		super(context);
		this.mContext = context;
	}
	public CMProgressDialog(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
		this.mTheme = theme;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cm_progress_dialog);
		mTextViewTitle = (TextView)findViewById(R.id.cm_p_d_title);
		mTextViewMessage = (TextView)findViewById(R.id.cm_p_d_message);
		mImageViewIcon = (ImageView)findViewById(R.id.cm_p_d_icon);
		if(mTitle !=null){
			mTextViewTitle.setText(mTitle);
		}
		if(mMessage != null){
			mTextViewMessage.setText(mMessage);
		}
		if(mResId > 0){
			mImageViewIcon.setImageResource(mResId);
		}
		if(mIcon != null){
			mImageViewIcon.setImageDrawable(mIcon);
		}
	}
	
	@Override
	public void setMessage(CharSequence message) {
		super.setMessage(message);
		this.mMessage = message;
	}
	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		this.mTitle = title;
	}
	@Override
	public void setIcon(Drawable icon) {
		super.setIcon(icon);
		this.mIcon = icon;
	}
	@Override
	public void setIcon(int resId) {
		super.setIcon(resId);
		this.mResId = resId;
	}
}
