package com.wxxr.callhelper.qg.widget.pick;

/**
 * JavaBean
 */
public class DialogCityItem {

	private String name;//ʡ���� ���
	private String pcode;//��Ӧ
	private String desc;//����

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public String getPcode() {
		return pcode;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPcode(String pcode) {
		this.pcode = pcode;
	}

	@Override
	public String toString() {
		return "MyListItem [name=" + name + ", pcode=" + pcode + "]";
	}
}