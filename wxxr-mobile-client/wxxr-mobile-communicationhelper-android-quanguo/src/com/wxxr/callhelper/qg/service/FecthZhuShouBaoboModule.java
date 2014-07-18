package com.wxxr.callhelper.qg.service;

import java.io.File;

import android.content.Context;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.bean.UrlBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.utils.GDWebCachePath;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.core.api.AbstractProgressMonitor;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
/**
 * 异步获取 助手播报的 默认文章
 * @author yangrunfei
 *
 */
public class FecthZhuShouBaoboModule  extends AbstractModule<ComHelperAppContext> implements IFetchDefaultZhushouBobao {

	private static final Trace log = Trace.register(FecthZhuShouBaoboModule.class);


	
	@Override
	protected void initServiceDependency() {
		// TODO Auto-generated method stub
		addRequiredService(IFetchURLofContent.class);		
	    addRequiredService(IOfficeLineHtmlProvideService.class);
	}

	
	@Override
	public void  getDefaultZhushouBaobao(){
		
		context.getExecutor().execute(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				try{
				GDWebCachePath  aa=	GDWebCachePath.getInstance(context.getApplication().getAndroidApplication().getApplicationContext());
				String path[]=aa.getAllPathOfKeys("zsbb");
				//没有记录，删除文件下的文件
				if(path.length==0){
				String temppath="/webCache/zsbbdefault/";							
	            File f=new File(context.getApplication().getDataDir(Constant.APP_DATA_BASE, Context.MODE_PRIVATE).getPath()+temppath); 
					if(f.exists()){
						Tools.deleteDirectory(f.getAbsolutePath());
					}
				}
				}catch(Exception ee){
					if(log.isDebugEnabled()){
						log.debug("start  del file   err", ee.toString());
					}
				}
				
				
				try{
					UrlBean res = getService(IFetchURLofContent.class)
						.getContentOfZSBB();

				AbstractProgressMonitor Monitor = new AbstractProgressMonitor() {

				};
				
				

				if (res != null && res.getUrl() != null) {
					int size = res.getUrl().size();
					for (int i = 0; i < size; i++) {
						getService(IOfficeLineHtmlProvideService.class)
								.getDefaultZhushouBaobao(
										res.getUrl().get(i),
										Monitor);
					}
				}
				}catch(Exception ee){
					if(log.isDebugEnabled()){
						log.debug("get defaul zsbb    err", ee.toString());
					}
				}
			}
		});
		
	}


	@Override
	protected void startService() {
		// TODO Auto-generated method stub
		context.registerService(IFetchDefaultZhushouBobao.class, this);
	}


	@Override
	protected void stopService() {
		// TODO Auto-generated method stub
		context.unregisterService(IFetchDefaultZhushouBobao.class, this);
	}
	
	
	
}
