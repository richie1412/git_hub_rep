package com.wxxr.callhelper.qg.bean;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * 
 * @author cuizaixi
 */
public class OptionalDialogBean implements Parcelable {
	private String titleName;
	private String Content;
	public String getTitleName() {
		return titleName;
	}

	public OptionalDialogBean(String titleName, String content) {
		this.titleName = titleName;
		Content = content;
	}
	public OptionalDialogBean() {

	}
	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public static final Parcelable.Creator<OptionalDialogBean> CREATOR = new Creator<OptionalDialogBean>() {

		@Override
		public OptionalDialogBean createFromParcel(Parcel source) {
			OptionalDialogBean bean = new OptionalDialogBean();
			bean.setTitleName(source.readString());
			bean.setContent(source.readString());
			return bean;
		}

		@Override
		public OptionalDialogBean[] newArray(int size) {
			return new OptionalDialogBean[size];
		}
	};
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(titleName);
		dest.writeString(Content);
	}

}
