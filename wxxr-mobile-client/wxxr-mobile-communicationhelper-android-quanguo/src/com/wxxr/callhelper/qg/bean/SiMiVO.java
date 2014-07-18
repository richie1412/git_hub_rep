package com.wxxr.callhelper.qg.bean;

//私密联系人实体
public class SiMiVO
{
	public Integer mstyle;// 短信类型 收 发
	public String address;// 短信号码
	public String content;// 短信内容
	public long cdate;// 短信时间
	public Integer islock;// 是否解锁  islock 为1 解锁 dao中不取出数据

	
	
	@Override
	public boolean equals(Object o)
	{

		if (!(o instanceof SiMiVO))
			return false;

		SiMiVO p = (SiMiVO) o;

		if (p.address.equals(this.address))
		{
			return true;
		}
		else
		{
			return false;
		}

	}
}
