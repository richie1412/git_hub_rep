/**
 * 
 */
package com.wxxr.callhelper.qg.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.util.Base64;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.IPiggybackService;
import com.wxxr.callhelper.qg.PiggybackPayload;
import com.wxxr.callhelper.qg.rpc.IClientConfigManagerService;
import com.wxxr.callhelper.qg.sync.IMTreeDataSyncServerConnector;
import com.wxxr.callhelper.qg.sync.MNodeDescriptor;
import com.wxxr.callhelper.qg.sync.UNodeDescriptor;
import com.wxxr.common.sync.vo.MNodeDescriptorVO;
import com.wxxr.common.sync.vo.ResultBaseVO;
import com.wxxr.common.sync.vo.UNodeDescriptorVO;
import com.wxxr.mobile.core.api.IDataExchangeCoordinator;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
import com.wxxr.mobile.core.rpc.http.api.IRestProxyService;
import com.wxxr.mobile.core.util.StringUtils;
import com.wxxr.phone.helper.workstation.resource.ICollectHtmlDataResource;

/**
 * @author wangxuyang
 * 
 */
public class MTreeSyncConnector extends AbstractModule<ComHelperAppContext> implements IMTreeDataSyncServerConnector {
	private static final Trace log = Trace.register(MTreeSyncConnector.class);
	private String serverUrl;

	private boolean isWifiConnected(){
		return context.getService(IDataExchangeCoordinator.class).checkAvailableNetwork()>0;
	}	
	private UNodeDescriptor fromVO(UNodeDescriptorVO vo) {
		if (vo == null) {
			return null;
		}
		UNodeDescriptor node = new UNodeDescriptor();
		node.setNodeId(vo.getNodeId());
		node.setChildren(fromVOS(vo.getChildren()));
		node.setLeaf(vo.isLeaf());
		if (vo.isLeaf()) {
			String leafPayload = vo.getLeafPayload();
			if (leafPayload!=null) {
				byte[] data = Base64.decode(leafPayload,Base64.NO_WRAP);
				node.setLeafPayload(data);
			}
		}		
		node.setDigest(Base64.decode(vo.getDigest(),Base64.NO_WRAP));
		return node;
	}

	private MNodeDescriptor fromVO(MNodeDescriptorVO vo) {
		if (vo == null) {
			return null;
		}
		MNodeDescriptor node = new MNodeDescriptor();
		node.setNodeId(vo.getNodeId());
		node.setLeaf(vo.isLeaf());
		if (vo.isLeaf()) {
			String leafPayload = vo.getLeafPayload();
			if (leafPayload!=null) {
				byte[] data = Base64.decode(leafPayload,Base64.NO_WRAP);
				node.setLeafPayload(data);
			}
		}	
		node.setDigest(Base64.decode(vo.getDigest(),Base64.NO_WRAP));
		return node;
	}

	private MNodeDescriptor[] fromVOS(MNodeDescriptorVO[] vos) {
		if (vos == null) {
			return null;
		}
		List<MNodeDescriptor> nodeList = new ArrayList<MNodeDescriptor>();
		for (MNodeDescriptorVO vo : vos) {
			if (vo instanceof UNodeDescriptorVO) {
				nodeList.add(fromVO((UNodeDescriptorVO) vo));
			} else {
				nodeList.add(fromVO(vo));
			}
		}
		return nodeList.size() > 0 ? nodeList.toArray(new MNodeDescriptor[nodeList.size()]) : null;
	}

	public UNodeDescriptor getNodeDescriptor(String key, String nodePath) throws IOException {
		if (!isWifiConnected()) {// 检测网络环境
			throw new IOException("Network is not connected.");
		}
		ICollectHtmlDataResource resource = context.getService(IRestProxyService.class).getRestService(ICollectHtmlDataResource.class);
		try {
			UNodeDescriptorVO vo = resource.getNodeDescriptor(key, nodePath);
			return fromVO(vo);
		} catch (Exception e) {
			log.warn("Error when Get Node Descriptor", e);
		}
		return null;
	}
	public byte[] getNodeData(String key, String nodePath) throws IOException {
		if (!isWifiConnected()) {// 检测网络环境
			throw new IOException("Network is not connected.");
		}
	
		ICollectHtmlDataResource resource = getRestService(ICollectHtmlDataResource.class);
		try {
			return resource.getNodeData(key, nodePath);			
		} catch (Exception e) {
			log.warn(String.format("Error when get data for key[%s],nodePath[%s]", key, nodePath), e);
			throw new RuntimeException(String.format("Check data change error,key[%s],nodepath[%s]", key, nodePath), e);
		}
	}

	

	public UNodeDescriptor isDataChanged(String key, String nodePath, byte[] digest) throws IOException {
		if (!isWifiConnected()) {// 检测网络环境
			throw new IOException("Network is not connected.");
		}
		PiggybackPayload payload = context.getService(IPiggybackService.class).getPiggybackPayload();
		int type =-1;
		byte[] data =  new byte[0];
		if (payload!=null) {
			type = payload.getPayloadId();
			data = payload.getData();
		}
		if (digest==null) {
			digest = new byte[0];
		}
		String digestStr = Base64.encodeToString(digest,Base64.NO_WRAP);
		
		ICollectHtmlDataResource resource = getRestService(ICollectHtmlDataResource.class);
		try {
			if (log.isDebugEnabled()) {
				log.debug(String.format("isDataChanged:[key:%s,nodePath:%s,digest:%s,piggyType:%d,piggyData:%s]", key, nodePath, digestStr , type, data));
			}
			UNodeDescriptorVO vo = resource.isDataChanged(key, nodePath, digestStr , type, data);
			return fromVO(vo);
		} catch (Exception e) {
			log.warn(String.format("Check data change error,key[%s],nodepath[%s]", key, nodePath), e);
			throw new RuntimeException(String.format("Check data change error,key[%s],nodepath[%s]", key, nodePath), e);
		}

	}

	public byte[] getDataDigest(String key, String nodePath) throws IOException {
		if (!isWifiConnected()) {// 检测网络环境
			throw new IOException("Network is not connected.");
		}
		ICollectHtmlDataResource resource = getRestService(ICollectHtmlDataResource.class);
		byte[] digest = null;
		try {
			ResultBaseVO vo = resource.getDataDigest(key, nodePath);
			if (vo != null) {
				if (vo.getResulttype() == 0) {
					String hexString = vo.getResultInfo();
					if (StringUtils.isNotBlank(hexString)) {
						digest = Base64.decode(hexString,Base64.NO_WRAP);
					}
				} else {
					throw new Exception(vo.getResultInfo());
				}
			}
		} catch (Exception e) {
			log.warn("Get digest error", e);
		}
		return digest;
	}

	@Override
	protected void initServiceDependency() {
		addRequiredService(IRestProxyService.class);
		addRequiredService(IDataExchangeCoordinator.class);
		addRequiredService(IPiggybackService.class);
		addRequiredService(IClientConfigManagerService.class);
	}

	@Override
	protected void startService() {
		if (log.isDebugEnabled()) {
			log.debug("serverUrl:" + getServerUrl());
		}
		if (getServerUrl() == null) {
			throw new IllegalStateException("There is not sshx sync server url setup, you should specified server target url[SSHX_SYNC_SERVER_URI] !!!");
		}
		context.registerService(IMTreeDataSyncServerConnector.class, this);

	}

	@Override
	protected void stopService() {
		context.unregisterService(IMTreeDataSyncServerConnector.class, this);
	}
	@Override
	public String getModuleName() {
		return this.getClass().getSimpleName();
	}
	protected String getServerUrl() {
		if(this.serverUrl == null){
			this.serverUrl = context.getService(IClientConfigManagerService.class).getSSHXSyncRestServiceUrl();
		}
		return this.serverUrl;
		
	}

	public <T> T getRestService(Class<T> clazz) {
		String url = getServerUrl();
		if (url == null) {
			throw new IllegalArgumentException("There is not sshx sync server url setup, you should specified server target url[SSHX_SYNC_SERVER_URI] !!!");
		}
		return context.getService(IRestProxyService.class).getRestService(clazz, url);
	}
	
	/**
	 * @param serverUrl the serverUrl to set
	 */
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
}
