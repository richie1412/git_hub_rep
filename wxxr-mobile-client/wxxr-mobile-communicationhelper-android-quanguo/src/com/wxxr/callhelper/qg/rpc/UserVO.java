/**
 * 
 */
package com.wxxr.callhelper.qg.rpc;

import java.io.Serializable;

import com.wxxr.javax.xml.bind.annotation.XmlRootElement;



/**
 * @author wangyan
 *
 */
@XmlRootElement(name = "user")
public class UserVO  implements Serializable{
	
    private String userName;
	private String nickName;
	private String moblie;
	private String icon;
	
	private int age;
    private boolean sex;

	public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public boolean isSex() {
        return sex;
    }
    public void setSex(boolean sex) {
        this.sex = sex;
    }

	
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the nickName
	 */
	public String getNickName() {
		return nickName;
	}
	/**
	 * @param nickName the nickName to set
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	/**
	 * @return the moblie
	 */
	public String getMoblie() {
		return moblie;
	}
	/**
	 * @param moblie the moblie to set
	 */
	public void setMoblie(String moblie) {
		this.moblie = moblie;
	}
	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}
	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Override
    public String toString() {
        return "UserVO [userName=" + userName + ", nickName=" + nickName + ", moblie=" + moblie + ", icon=" + icon + ", age=" + age + ", sex=" + sex + "]";
    }
}
