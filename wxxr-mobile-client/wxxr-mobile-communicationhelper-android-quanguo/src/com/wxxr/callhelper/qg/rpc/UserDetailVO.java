/**
 * 
 */
package com.wxxr.callhelper.qg.rpc;

import java.io.Serializable;
import java.util.Dictionary;

import com.wxxr.callhelper.qg.utils.StringUtil;
import com.wxxr.javax.xml.bind.annotation.XmlRootElement;


/**
 * @author wangyan
 *
 */
@XmlRootElement(name = "UserDetailVO")
public class UserDetailVO  implements Serializable{
	/**用户名*/
	private String userName;
	/**昵称*/
	private String nickName;
	/**手机*/
	private String moblie;
	/**头像*/
	private String icon;
	
	//日期格式 20080101
	private int birthday;
	private boolean male;
	/**
	 * @return the userName
	 */
	private Boolean updated;

    private String provinceCode;

    public String getProvinceCode() {
        return provinceCode;
    }
    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }
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

	
	public int getBirthday() {
		return birthday;
	}

	public void setBirthday(int birthday) {
		this.birthday = birthday;
	}

	
	public boolean isMale() {
		return male;
	}
	/**
	 * @param male the male to set
	 */
	public void setMale(boolean male) {
		this.male = male;
	}
	
	public void updateTo(Dictionary<String, String> map) {
	    if(this.provinceCode != null){
            map.put("proviceCode", this.provinceCode.toString());
        }else{
            map.remove("proviceCode");
        }
        if(this.userName != null){
            map.put("userName", this.userName.toString());
        }else{
            map.remove("userName");
        }
        if(this.nickName != null){
            map.put("nickName", this.nickName.toString());
        }else{
            map.remove("nickName");
        }
        if(this.moblie != null){
            map.put("moblie", this.moblie.toString());
        }else{
            map.remove("moblie");
        }
        if(this.icon != null){
            map.put("icon", this.icon);
        }else{
            map.remove("icon");
        }
        if(StringUtil.isNotEmpty(String.valueOf(this.birthday))){
            map.put("birthday", String.valueOf(this.birthday));
        }else{
            map.remove("birthday");
        }
        if(this.male){
            map.put("male", String.valueOf(male));
        }else{
            map.remove("male");
        }
        
        if(this.updated != null){
            map.put("updated", this.updated.toString());
        }else{
            map.remove("updated");
        }

        
    }
    
    public void updateFrom(Dictionary<String, String> map ) {
        String s = map.get("provinceCode");
        if(s != null){
            this.provinceCode = s;
        }
        s = map.get("userName");
        if(s != null){
            this.userName = s;
        }
        s = map.get("nickName");
        if(s != null){
            this.nickName = s;
        }
        s = map.get("moblie");
        if(s != null){
            this.moblie = s;
        }
        
        s = map.get("icon");
        if(s != null){
            this.icon = s;
        }
        s = map.get("birthday");
        if(s != null){
            this.birthday = Integer.valueOf(s);
        }
        this.moblie = map.get("moblie");

        s = map.get("male");
        if(s != null){
            this.male = Boolean.valueOf(s);
        }
        s = map.get("updated");
        if(s != null){
            this.updated = Boolean.valueOf(s);
        }
    }

    /**
     * @return the updated
     */
    public Boolean getUpdated() {
        return updated;
    }

    /**
     * @param updated the updated to set
     */
    public void setUpdated(Boolean updated) {
        this.updated = updated;
    }

	
	@Override
    public String toString() {
        return "UserDetailVO [userName=" + userName + ", nickName=" + nickName + ", moblie=" + moblie + ", icon=" + icon + ", birthday=" + birthday + ", male=" + male + ", updated=" + updated + ", proviceCode=" + provinceCode + "]";
    }
    public static UserDetailVO clone(UserDetailVO vo){
	    UserDetailVO target=new UserDetailVO();
	    target.setBirthday(vo.getBirthday());
	    target.setIcon(vo.getIcon());
	    target.setMale(vo.isMale());
	    target.setNickName(vo.getNickName());
	    target.setUpdated(vo.getUpdated());
	    target.setUserName(vo.getUserName());
	    target.setProvinceCode(vo.getProvinceCode());
        return target;
	    
	}

}
