package com.wxxr.callhelper.qg.zjhz.bean;

import java.io.Serializable;

public abstract class UserState implements Cloneable,Serializable{
    private final String name;
    private final Long definitionId;
    private long setTime;

    public UserState(String id,Long defId){
        name = id;
        this.definitionId = defId;
    }
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    
    public abstract int getPriority();
    /**
     * @return Returns the setTime.
     */
    public long getSetTime() {
        return setTime;
    }
    /**
     * @param setTime The setTime to set.
     */
    public void setSetTime(long setTime) {
        this.setTime = setTime;
    }
    /**
     * @return Returns the definitionId.
     */
    public Long getDefinitionId() {
        return definitionId;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
 
