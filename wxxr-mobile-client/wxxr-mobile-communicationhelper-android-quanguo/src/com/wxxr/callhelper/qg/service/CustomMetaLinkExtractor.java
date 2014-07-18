/**
 * 
 */
package com.wxxr.callhelper.qg.service;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.wxxr.mobile.web.grabber.api.IGrabberServiceContext;
import com.wxxr.mobile.web.grabber.model.ExtractedUrlAnchorPair;
import com.wxxr.mobile.web.link.extractor.MetaLinkExtractor;

/**
 * @author wangyan
 *
 */
public class CustomMetaLinkExtractor extends MetaLinkExtractor {

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.web.link.extractor.MetaLinkExtractor#extractLink(com.wxxr.mobile.web.grabber.api.IGrabberServiceContext, org.jsoup.nodes.Element)
	 */
	@Override
	public ExtractedUrlAnchorPair extractLink(IGrabberServiceContext ctx,
			Element elem) {
		Attributes attributes = elem.attributes();

		String name = attributes.get("name");
		String content = attributes.get("content");
		if("iconsUrl".equals(name)){
			String metaLocation = content;
			ExtractedUrlAnchorPair curUrl = new ExtractedUrlAnchorPair();
			curUrl.setHref(metaLocation);
			curUrl.setElement(elem);
			curUrl.setAttrName("iconsUrl");
			curUrl.setPrefetchable(true);
			return curUrl;
		}else if("listimageUrl".equals(name)){
			String metaLocation = content;
			ExtractedUrlAnchorPair curUrl = new ExtractedUrlAnchorPair();
			curUrl.setHref(metaLocation);
			curUrl.setElement(elem);
			curUrl.setAttrName("listimageUrl");
			curUrl.setPrefetchable(true);
			return curUrl;
		}

		return super.extractLink(ctx, elem);
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.web.link.extractor.MetaLinkExtractor#updateLink(com.wxxr.mobile.web.grabber.api.IGrabberServiceContext, com.wxxr.mobile.web.grabber.model.ExtractedUrlAnchorPair, java.lang.String)
	 */
	@Override
	public boolean updateLink(IGrabberServiceContext ctx, ExtractedUrlAnchorPair link,
			String newLink) {

		if("iconsUrl".equals(link.getAttrName())){
			Attributes attributes = link.getElement().attributes();
			link.getElement().attr("content",newLink);
			return true;
		}
		
		
		if("listimageUrl".equals(link.getAttrName())){
			Attributes attributes = link.getElement().attributes();
			link.getElement().attr("content",newLink);
			return true;
		}
		return super.updateLink(ctx, link, newLink);
	}

}
