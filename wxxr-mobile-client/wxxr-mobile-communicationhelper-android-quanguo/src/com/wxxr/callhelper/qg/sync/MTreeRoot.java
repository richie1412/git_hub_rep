/**
 * 
 */
package com.wxxr.callhelper.qg.sync;


/**
 * @author neillin
 *
 */
public class MTreeRoot {

	private MTreeNode root;
	

	/* (non-Javadoc)
	 * @see com.wxxr.stock.utils.sync.MTreeNode#clear()
	 */
	public void clear() {
		if(this.root != null){
			this.root.clear();
			this.root = null;
		}
	}

	/**
	 * @return the root
	 */
	public MTreeNode getRoot() {
		return root;
	}

	/**
	 * @param root the root to set
	 */
	public void setRoot(MTreeNode root) {
		this.root = root;
	}


}
