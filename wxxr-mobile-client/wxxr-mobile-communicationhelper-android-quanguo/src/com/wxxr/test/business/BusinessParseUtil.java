package com.wxxr.test.business;

import java.io.InputStream;	
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.wxxr.mobile.core.log.api.Trace;

public class BusinessParseUtil {

	private static final Trace log = Trace.register(BusinessParseUtil.class);
	
	private XmlPullParser parser;

    public BusinessParseUtil() {
        parser = Xml.newPullParser();
    }
    
    /**
     * 解析数据
     * @param in
     * @return
     */
    public List<BusinessManagerBean> doParse(InputStream in){

        String tagName = null;
        List<BusinessManagerBean> lists = null;
        BusinessManagerBean business = null;
        try {
            parser.setInput(in, "utf-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    lists = new ArrayList<BusinessManagerBean>();
                    break;
                case XmlPullParser.END_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    tagName = parser.getName();
                    if ("business".equals(tagName)) {
                        business = new BusinessManagerBean();
                    }
                    else if ("name".equals(tagName)) {
                    	business.setName(parser.nextText().trim());
                    }
                    else if ("businessname".equals(tagName)) {
                    	business.setBusinessName(parser.nextText().trim());
                    }
                    else if("icon".equals(tagName)){
                    	business.setBusinessIcon(parser.nextText().trim());
                    }
                    else if("code".equals(tagName)){
                    	business.setBusinessCode(parser.nextText().trim());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("business".equals(parser.getName())) {
                    	lists.add(business);
                    }
                    break;
                }
                eventType = parser.next();
            }
            return lists;
        } catch (Exception e) {
            log.error(e.getMessage());
            
        }
        return null;
    
    }
}
