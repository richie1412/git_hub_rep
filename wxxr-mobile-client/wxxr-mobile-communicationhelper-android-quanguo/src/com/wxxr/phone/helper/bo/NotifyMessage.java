package com.wxxr.phone.helper.bo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

import com.wxxr.javax.xml.bind.annotation.XmlRootElement;

/**
 * @ClassName: NotificationMessageBO
 * @Description: 系统通知BO
 * @author haoxiaoqi
 * @date Nov 26, 2013 11:12:02 AM
 */
@XmlRootElement(name = "NotificationMessageBO")
public class NotifyMessage implements Externalizable {
    /**
     * @Fields id : 通知消息主键
     */
    private Long id;

    /**
     * @Fields title : 通知消息内容
     */
    private String title;

    /**
     * @Fields content : 内容
     */
    private String content;

    /**
     * @Fields type : 通知类型：    1 公告，2 通知
     */
    private Integer type;

    /**
     * @Fields startTime : 通知开始时间
     */
    private Date startTime;

    /**
     * @Fields endTime : 通知结束时间
     */
    private Date endTime;

    /**
     * @Fields showTime : 滚动速度(秒/字)
     */
    private Byte showTime;

    /**
     * @Fields toURL : 跳转地址
     */
    private String toURL;

    /**
     * @Fields isActive : 是否生效
     */
    private Byte isActive;

    /**
     * @Fields createDate : 创建时间
     */
    private Date createDate;

    /**
     * @Fields createBy : 创建者
     */
    private String createBy;

    /**
     * @Fields updateDate : 更新时间
     */
    private Date updateDate;

    /**
     * @Fields updateBy : 更新者
     */
    private String updateBy;

    /**
     * @Fields forwardType : 转向页面类型
     */
    private int forwardType;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @return the type
     */
    public Integer getType() {
        return type;
    }

    /**
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @return the showTime
     */
    public Byte getShowTime() {
        return showTime;
    }

    /**
     * @return the toURL
     */
    public String getToURL() {
        return toURL;
    }

    /**
     * @return the isActive
     */
    public Byte getIsActive() {
        return isActive;
    }

    /**
     * @return the createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @return the createBy
     */
    public String getCreateBy() {
        return createBy;
    }

    /**
     * @return the updateDate
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * @return the updateBy
     */
    public String getUpdateBy() {
        return updateBy;
    }

    /**
     * @param id
     *        the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param title
     *        the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @param content
     *        the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @param type
     *        the type to set
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * @param startTime
     *        the startTime to set
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @param endTime
     *        the endTime to set
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @param showTime
     *        the showTime to set
     */
    public void setShowTime(Byte showTime) {
        this.showTime = showTime;
    }

    /**
     * @param toURL
     *        the toURL to set
     */
    public void setToURL(String toURL) {
        this.toURL = toURL;
    }

    /**
     * @param isActive
     *        the isActive to set
     */
    public void setIsActive(Byte isActive) {
        this.isActive = isActive;
    }

    /**
     * @param createDate
     *        the createDate to set
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @param createBy
     *        the createBy to set
     */
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    /**
     * @param updateDate
     *        the updateDate to set
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * @param updateBy
     *        the updateBy to set
     */
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * @return the forwardType
     */
    public int getForwardType() {
        return forwardType;
    }

    /**
     * @param forwardType
     *        the forwardType to set
     */
    public void setForwardType(int forwardType) {
        this.forwardType = forwardType;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.id = in.readLong();
        this.type = in.readInt();
        this.title = in.readUTF();
        this.content  = in.readUTF();
        this.showTime = in.readByte();
        this.startTime = new Date(in.readLong());
        this.endTime = new Date(in.readLong());
        this.isActive = in.readByte();
        this.forwardType = in.readByte();
      //可选参数处理
        if(in.read() != 0){
            this.toURL = in.readUTF();
        }


    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(this.id);
        out.writeInt(this.type);
        out.writeUTF(this.title);
        out.writeUTF(this.content);
        out.writeByte(this.showTime);
        out.writeLong(this.startTime.getTime());
        out.writeLong(this.endTime.getTime());
        out.writeByte(this.isActive);
        out.writeByte(this.forwardType);
        //可选参数处理
        if(this.toURL != null){
            out.write(1);
            out.writeUTF(this.toURL);
        }else{
            out.write(0);
        }


    }

    @Override
    public String toString() {
        return "NotifyMessage [id=" + id + ", title=" + title + ", content=" + content + ", type=" + type
                + ", startTime=" + startTime + ", endTime=" + endTime + ", showTime=" + showTime + ", toURL=" + toURL
                + ", isActive=" + isActive + ", createDate=" + createDate + ", createBy=" + createBy + ", updateDate="
                + updateDate + ", updateBy=" + updateBy + ", forwardType=" + forwardType + "]";
    }
    
    

}
