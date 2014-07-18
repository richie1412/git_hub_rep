package com.wxxr.callhelper.qg.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.wxxr.callhelper.qg.bean.LouhuaSmsInterceptRuleDescriptor;
import com.wxxr.mobile.core.log.api.Trace;

/**
 * pull������
 * @author cuizaixi
 *
 */
public class PullParseUtil {
    private static final Trace log = Trace.register(PullParseUtil.class);

    private XmlPullParser parser;

    public PullParseUtil() {
        //����pull������
        parser = Xml.newPullParser();
    }
    public List<LouhuaSmsInterceptRuleDescriptor> doParse(InputStream is) {
        //��ǩ���
        String tagName = null;
        List<LouhuaSmsInterceptRuleDescriptor> reminderTexts = null;
        LouhuaSmsInterceptRuleDescriptor reminderText = null;
        try {
            //����������
            parser.setInput(is, "utf-8");
            //�õ��¼�����
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                //�ĵ���ʼ�¼�
                case XmlPullParser.START_DOCUMENT:
                    reminderTexts = new ArrayList<LouhuaSmsInterceptRuleDescriptor>();
                    break;
                //�ĵ������¼�
                case XmlPullParser.END_DOCUMENT:
                    break;
                //��ǩ��ʼ�¼�
                case XmlPullParser.START_TAG:
                    tagName = parser.getName();
                    //provinceԪ��
                    if ("province".equals(tagName)) {
                        reminderText = new LouhuaSmsInterceptRuleDescriptor();
                    }
                    //name��ǩ
                    else if ("name".equals(tagName)) {
                        //ȡ���ı��ڵ�
                        reminderText.setName( parser.nextText().trim());
                    }
                    //remindertext��ǩ
                    else if ("remindertext".equals(tagName)) {
                        //ȡ���ı��ڵ�
                        reminderText.setPattern( parser.nextText().trim());
                    }
                    break;
                //��ǩ����
                case XmlPullParser.END_TAG:
                    //province��ǩ����
                    if ("province".equals(parser.getName())) {
                        reminderTexts.add(reminderText);
                    }
                    break;
                }
                //������һ���¼���ִ��
                eventType = parser.next();
            }
            return reminderTexts;
        } catch (Exception e) {
            log.error(e.getMessage());
            
        }
        return null;
    }
}
