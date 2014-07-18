package com.wxxr.callhelper.qg.widget;


import com.wxxr.callhelper.qg.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author xijiadeng
 * */
public class CustomDialog extends Dialog {

	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}
	protected CustomDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}
	public CustomDialog(Context context){
		super(context);
	}
	
	public static class Builder {
        private Context mContext; //上下文
        private Object mTitle; //dialog 标题
        private Object mMessage; //信息
        private Object mPositiveButtonText; //确定按钮Text
        private Object mNegativeButtonText; //取消按钮Text
        private Object mNeutralButtonText;  //普通按钮Text
        private View mContentView;	
        private OnClickListener mPositiveButtonOnClickListener,
        	mNegativeButtonOnClickListener,
        	mNeutralButtonOnClickListener;
        
        public Builder(Context context){
        	this.mContext = context;
        }

        /**
         * Set the title using the given resource id.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(int titleId) {
            this.mTitle = titleId;
            return this;
        }    
        
        /**
         * Set the title displayed in the {@link Dialog}.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setTitle(CharSequence title) {
            this.mTitle = title;
            return this;
        } 
        
        /**
         * Set the message to display using the given resource id.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMessage(int messageId) {
            this.mMessage = mContext.getText(messageId);
            return this;
        }
        
        /**
         * Set the message to display.
          *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setMessage(CharSequence message) {
            this.mMessage = message;
            return this;
        }
        
        /**
         * Set a custom content view for the Dialog.
         */        
        public Builder setContentView(View view){
        	this.mContentView = view;
        	return this;
        }
        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         * @param textId The resource id of the text to display in the positive button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButton(int textId, final OnClickListener listener) {
            this.mPositiveButtonText = mContext.getText(textId);
            this.mPositiveButtonOnClickListener = listener;
            return this;
        }
        
        /**
         * Set a listener to be invoked when the positive button of the dialog is pressed.
         * @param text The text to display in the positive button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setPositiveButton(CharSequence text, final OnClickListener listener) {
            this.mPositiveButtonText = text;
            this.mPositiveButtonOnClickListener = listener;
            return this;
        }
        
        
        /**
         * Set a listener to be invoked when the negative button of the dialog is pressed.
         * @param textId The resource id of the text to display in the negative button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButton(int textId, final OnClickListener listener) {
            this.mNegativeButtonText = mContext.getText(textId);
            this.mNegativeButtonOnClickListener = listener;
            return this;
        }
        
        /**
         * Set a listener to be invoked when the negative button of the dialog is pressed.
         * @param text The text to display in the negative button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNegativeButton(CharSequence text, final OnClickListener listener) {
            this.mNegativeButtonText = text;
            this.mNegativeButtonOnClickListener = listener;
            return this;
        } 
        
        
        /**
         * Set a listener to be invoked when the neutral button of the dialog is pressed.
         * @param textId The resource id of the text to display in the neutral button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNeutralButton(int textId, final OnClickListener listener) {
            this.mNeutralButtonText = mContext.getText(textId);
            this.mNeutralButtonOnClickListener = listener;
            return this;
        }
        
        /**
         * Set a listener to be invoked when the neutral button of the dialog is pressed.
         * @param text The text to display in the neutral button
         * @param listener The {@link DialogInterface.OnClickListener} to use.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builder setNeutralButton(CharSequence text, final OnClickListener listener) {
            this.mNeutralButtonText = text;
            this.mNeutralButtonOnClickListener = listener;
            return this;
        }
        
        public CustomDialog createDialog(){
        	LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final CustomDialog dialog = new CustomDialog(mContext, R.style.popAlert);
            View view = inflater.inflate(R.layout.custom_dialog, null);
            dialog.addContentView(view, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
            TextView titleView = (TextView)view.findViewById(R.id.box_title);
            TextView messageView = (TextView)view.findViewById(R.id.box_message);
            Button neutralButton = (Button)view.findViewById(R.id.neutralButton);
            Button negativeButton = (Button)view.findViewById(R.id.negativeButton);
            Button positiveButton = (Button)view.findViewById(R.id.positiveButton);
            if(mTitle instanceof CharSequence){
            	titleView.setText((CharSequence) mTitle);
            }else if(mTitle instanceof Integer){
            	titleView.setText(((Integer) mTitle).intValue());
            }
            if(mMessage!=null){
	            if(mMessage instanceof CharSequence){
	            	messageView.setText((CharSequence)mMessage);
	            }else if(mMessage instanceof Integer){
	            	messageView.setText(((Integer) mMessage).intValue());
	            }
            }else if(mContentView!=null){
            	//view.findViewById(R.id.content);
            	
            }
            if(mPositiveButtonText != null){
	            if(mPositiveButtonText instanceof CharSequence){
	            	positiveButton.setText((CharSequence) mPositiveButtonText);
	            }else if(mPositiveButtonText instanceof Integer){
	            	positiveButton.setText(((Integer) mPositiveButtonText).intValue());
	            }
	            positiveButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mPositiveButtonOnClickListener.onClick(dialog,  DialogInterface.BUTTON_POSITIVE);
					}
				});
            }
            
            if(mNegativeButtonText!=null){
            	if(mNegativeButtonText instanceof CharSequence){
            		negativeButton.setText((CharSequence) mNegativeButtonText);
            	}else if(mNegativeButtonText instanceof Integer){
            		negativeButton.setText(((Integer) mNegativeButtonText).intValue());
            	}
            	negativeButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mNegativeButtonOnClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
					}
				});
            }
            
            if(mNeutralButtonText!=null){
            	if(mNeutralButtonText instanceof CharSequence){
            		neutralButton.setText((CharSequence) mNeutralButtonText);
            	}else if(mNeutralButtonText instanceof Integer){
            		neutralButton.setText((CharSequence) mNeutralButtonText);
            	}
            	neutralButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mNeutralButtonOnClickListener.onClick(dialog, BUTTON_NEUTRAL);
					}
				});
            }
            dialog.setContentView(view);
        	return dialog;
        }
	}
}
