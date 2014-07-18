/**
 * 
 */
package com.wxxr.callhelper.qg.bean;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * @author wangyan
 *
 */
public class DownloadTaskInfo implements Externalizable {
	private String id;
	private String url;
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}


	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public void readExternal(ObjectInput input) throws IOException,
			ClassNotFoundException {
		if(input.read()==1){
			this.id=input.readUTF();
		}
		if(input.read()==1){
			this.url=input.readUTF();
		}

	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DownloadTaskInfo other = (DownloadTaskInfo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
	@Override
	public void writeExternal(ObjectOutput output) throws IOException {
		if(id==null){
			output.write(0);
		}else{
			output.write(1);
			output.writeUTF(id);
		}
		if(url==null){
			output.write(0);
		}else{
			output.write(1);
			output.writeUTF(url);
		}

	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DownloadTaskInfo [id=" + id + ", url=" + url + "]";
	}
	
	
	
	
}
