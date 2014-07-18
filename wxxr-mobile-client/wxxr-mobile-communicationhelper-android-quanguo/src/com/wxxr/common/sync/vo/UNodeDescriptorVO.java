/**
 * 
 */
package com.wxxr.common.sync.vo;

import java.util.Arrays;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.wxxr.javax.xml.bind.annotation.XmlRootElement;




/**
 * @author wangyan
 *
 */
@XmlRootElement(name="UNodeDescriptor")
public class UNodeDescriptorVO extends MNodeDescriptorVO {
	@XStreamImplicit(itemFieldName="children")
	private MNodeDescriptorVO[] children;		// the sequence of children is significant !!!

	/**
	 * @return the children
	 */
	
	public MNodeDescriptorVO[] getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(MNodeDescriptorVO[] children) {
		this.children = children;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UNodeDescriptorVO:")
				.append("getNodeId()=").append(getNodeId())
				.append(", getDigest()=").append(getDigest())
				.append(", isLeaf()=").append(isLeaf())
				.append("[children=")
				.append(Arrays.toString(children))
				.append("]");
		return builder.toString();
	}
	
}
