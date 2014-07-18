package com.wxxr.callhelper.qg.utils;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.provider.ContactsContract.CommonDataKinds.Note;

import com.wxxr.callhelper.qg.bean.MoblieBusinessBean;
import com.wxxr.callhelper.qg.bean.UrlBean;

public class XMLParstTool {
	
/** 
 * 解析 服务器返回的urls 集合
 * @param content
 * @return
 */
	public static UrlBean parseUrlsXml(String content) {
		UrlBean bean = new UrlBean();
		 ByteArrayInputStream inStream=new 
		 ByteArrayInputStream(content.getBytes());

	
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(inStream);

			Element root = dom.getDocumentElement();

			NodeList childNodes = root.getChildNodes();
			int leng = childNodes.getLength();
			for (int i = 0; i < leng; i++) {
				if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
					if ("Version".equals(childNodes.item(i).getNodeName())) {
						bean.setVersion(childNodes.item(i).getFirstChild()
								.getNodeValue());
					} else if ("MsgType".equals(childNodes.item(i)
							.getNodeName())) {
						bean.setMsgtype(childNodes.item(i).getFirstChild()
								.getNodeValue());
					} else if ("Items".equals(childNodes.item(i).getNodeName())) {
						ArrayList<String>  urls=new  ArrayList<String>();
						ArrayList<Node> list0 = getStingValueArray(childNodes
								.item(i).getChildNodes(), "item");
						for (int w = 0; w < list0.size(); w++) {
							Node node = getStingValue(list0.get(w)
									.getChildNodes(), "Url");
							urls.add(node.getFirstChild().getNodeValue());
						}
						bean.setUrl(urls);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return bean;
	}
	
	
	
/** 
 * 解析 服务器返回的url 单个
 * @param content
 * @return
 */
	public static UrlBean parseUrlXml(String content) {
		if(content==null){
			return null;
		}
		UrlBean bean = new UrlBean();
		 ByteArrayInputStream inStream=new 
		 ByteArrayInputStream(content.getBytes());
	
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(inStream);

			Element root = dom.getDocumentElement();

			NodeList childNodes = root.getChildNodes();
			int leng = childNodes.getLength();
			for (int i = 0; i < leng; i++) {
				if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
					if ("Version".equals(childNodes.item(i).getNodeName())) {
						bean.setVersion(childNodes.item(i).getFirstChild()
								.getNodeValue());
					} else if ("MsgType".equals(childNodes.item(i)
							.getNodeName())) {
						bean.setMsgtype(childNodes.item(i).getFirstChild()
								.getNodeValue());
					} else if ("Url".equals(childNodes.item(i).getNodeName())) {
						ArrayList<String>  urls=new  ArrayList<String>();
						urls.add(childNodes.item(i).getFirstChild()
									.getNodeValue());
						bean.setUrl(urls);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return bean;
	}
	
	/**
	 * 解析业务查询和业务办理的
	 * @param content
	 * @return
	 */
	
	public static ArrayList<MoblieBusinessBean> parseBusinessBeanXml(String content) {
		if(content==null||content.length()<10){
			return null;
		}
		ArrayList<MoblieBusinessBean> list=new ArrayList<MoblieBusinessBean>();
		UrlBean bean = new UrlBean();
		 ByteArrayInputStream inStream=null;
		try {
			inStream = new
			 ByteArrayInputStream(content.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(inStream);

			Element root = dom.getDocumentElement();

			NodeList childNodes = root.getChildNodes();
			int leng = childNodes.getLength();
			for (int i = 0; i < leng; i++) {
				if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
					if ("Version".equals(childNodes.item(i).getNodeName())) {
						bean.setVersion(childNodes.item(i).getFirstChild()
								.getNodeValue());
					} else if ("MsgType".equals(childNodes.item(i)
							.getNodeName())) {
						bean.setMsgtype(childNodes.item(i).getFirstChild()
								.getNodeValue());
					} else if ("Items".equals(childNodes.item(i).getNodeName())) {
						ArrayList<String>  urls=new  ArrayList<String>();
						ArrayList<Node> list0 = getStingValueArray(childNodes
								.item(i).getChildNodes(), "item");
						for (int w = 0; w < list0.size(); w++) {
							MoblieBusinessBean bbean=new MoblieBusinessBean();
							NodeList itemchild=	list0.get(w).getChildNodes();
							int childleng = itemchild.getLength();
							for (int t = 0; t < childleng; t++) {
								if (itemchild.item(t).getNodeType() == Node.ELEMENT_NODE)
									if(itemchild.item(t).getNodeName().equals("Title")){
										bbean.setTile(itemchild.item(t).getFirstChild().getNodeValue());
									}else if(itemchild.item(t).getNodeName().equals("ServiceCode")){
										bbean.setSmscode(itemchild.item(t).getFirstChild().getNodeValue());
									}else if(itemchild.item(t).getNodeName().equals("PicUrl")){
										bbean.setIcon(itemchild.item(t).getFirstChild().getNodeValue());
									}else if(itemchild.item(t).getNodeName().equals("Description")){
										bbean.setDescription(itemchild.item(t).getFirstChild().getNodeValue());
									}else if(itemchild.item(t).getNodeName().equals("SpNumber")){
										bbean.setSpNumber(itemchild.item(t).getFirstChild().getNodeValue());
									}
								
								}
							
							list.add(bbean);
							}
											
				}
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}
	
	
	/**
	 * 解析来电提醒和短信回执马上办理
	 * @param content
	 * @return
	 */
	
	public static MoblieBusinessBean parseOneBusinessBeanXml(String content) {
		if(content==null||content.length()<10){
			return null;
		}
		
		MoblieBusinessBean bbean=new MoblieBusinessBean();
//		ArrayList<MoblieBusinessBean> list=new ArrayList<MoblieBusinessBean>();
		UrlBean bean = new UrlBean();
		 ByteArrayInputStream inStream=null;
		try {
			inStream = new
			 ByteArrayInputStream(content.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(inStream);

			Element root = dom.getDocumentElement();

			NodeList childNodes = root.getChildNodes();
			int leng = childNodes.getLength();
			for (int i = 0; i < leng; i++) {
				if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
					if ("Version".equals(childNodes.item(i).getNodeName())) {
						bean.setVersion(childNodes.item(i).getFirstChild()
								.getNodeValue());
					} else if ("MsgType".equals(childNodes.item(i)
							.getNodeName())) {
						bean.setMsgtype(childNodes.item(i).getFirstChild()
								.getNodeValue());
					} 
//					else if("Items".equals(childNodes.item(i).getNodeName())){
//						NodeList nodes = childNodes.item(i).getChildNodes();
//						for(int j = 0; j < nodes.getLength(); j++){
//							if (nodes.item(j).getNodeType() == Node.ELEMENT_NODE)
//								if(nodes.item(j).getNodeName().equals("Title")){
//									bbean.setTile(nodes.item(j).getFirstChild().getNodeValue());
//								}else if(nodes.item(j).getNodeName().equals("ServiceCode")){
//									bbean.setSmscode(nodes.item(j).getFirstChild().getNodeValue());
//								}else if(nodes.item(j).getNodeName().equals("SpNumber")){
//									bbean.setSpNumber(nodes.item(j).getFirstChild().getNodeValue());
//								}
//							
//							}
//						}
			
					else if ("item".equals(childNodes.item(i).getNodeName())) {
						NodeList nodes = childNodes.item(i).getChildNodes();
						for(int j = 0; j < nodes.getLength(); j++){
							if (nodes.item(j).getNodeType() == Node.ELEMENT_NODE)
								if(nodes.item(j).getNodeName().equals("ServiceCode")){
									bbean.setSmscode(nodes.item(j).getFirstChild().getNodeValue());
								}else if(nodes.item(j).getNodeName().equals("SpNumber")){
									bbean.setSpNumber(nodes.item(j).getFirstChild().getNodeValue());
								}
							}
						
//						Node d = getStingValue(childNodes
//								.item(i).getChildNodes(), "ServiceCode");
//						bbean.setSmscode(d.getFirstChild().getNodeValue());
//						
//						Node p = getStingValue(childNodes
//								.item(i).getChildNodes(), "SpNumber");
//						bbean.setSpNumber(p.getFirstChild().getNodeValue());
				
				}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bbean;
	}

	private static Node getStingValue(NodeList childNodes, String name) {
		int leng = childNodes.getLength();
		for (int t = 0; t < leng; t++) {
			if ((childNodes.item(t).getNodeType() == Node.ELEMENT_NODE)
					&& name.equals(childNodes.item(t).getNodeName())) {
				return childNodes.item(t);
			}
		}
		return null;
	}

	private static ArrayList<Node> getStingValueArray(NodeList childNodes,
			String name) {
		ArrayList<Node> list = new ArrayList<Node>();
		int leng = childNodes.getLength();
		for (int t = 0; t < leng; t++) {
			if ((childNodes.item(t).getNodeType() == Node.ELEMENT_NODE)
					&& name.equals(childNodes.item(t).getNodeName())) {
				list.add(childNodes.item(t));
			}
		}
		return list;
	}
}
