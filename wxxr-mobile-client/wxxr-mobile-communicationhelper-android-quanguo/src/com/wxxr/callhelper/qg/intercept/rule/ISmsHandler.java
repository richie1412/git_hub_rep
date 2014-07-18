package com.wxxr.callhelper.qg.intercept.rule;

import android.content.Context;

public interface ISmsHandler {
    /**
     * 
     * @param context
     * @param smsContent 短信内容
     * @param targetnumber 接收短信的对方号码
     * @param dontstore  是否闪信
     * @return boolean 是否满足拦截条件
     */
    public boolean handle(final Context context, String smsContent, String targetnumber, boolean dontstore);
}
