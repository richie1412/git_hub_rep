/**
 * 
 */
package com.wxxr.callhelper.qg.service;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import android.content.Context;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.bean.HtmlMessage;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.utils.GDWebCachePath;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.api.IProgressMonitor;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;

/**
 * @author wangyan
 *
 */
public class OfficeLineHtmlProvideService  extends AbstractModule<ComHelperAppContext> implements IOfficeLineHtmlProvideService{

	private static final String HOME_ID="home";
	private static final String MICRNEW_ID="micrnew";
	private static final String CHEAP_HOM="/cheaphome/";
	private static final String GUESS_LIKE="/guesslike/";
	private static final String SQUARE_HOME="/squarehome/";
	
	private static final String GD_21CITYCHEAP_ID="gdcheaps";
	
	private static final String GD_DEFAULT_ZHUSHOUBOBAO="zsbbdefault";
//	protected Map<String,String> processing=new ConcurrentHashMap<String,String>();
	private Set<String> processing=Collections.synchronizedSet(new HashSet<String>());
	private static Trace log =Trace.register(OfficeLineHtmlProvideService.class);
	@Override
	public HtmlMessage getHome(String url,IProgressMonitor monitor) {
 		return getHtml(url, HOME_ID,monitor);
	}
	
	@Override
	public HtmlMessage getMicrNews(String url,IProgressMonitor monitor) {
		return getHtml(url, MICRNEW_ID,monitor);
	}
	
	protected HtmlMessage getHtml(String url,String id,IProgressMonitor monitor) {
		if(monitor != null){
			if(!processing.contains(id)){
				Invoker invoker=new Invoker( monitor, id, url);
				invoker.start();
				processing.add(id);
			}
		}
		IHtmlMessageManager htmlMessageManager=context.getService(IHtmlMessageManager.class);
		HtmlMessage htmlMessage=htmlMessageManager.getHtmlMessage(id);
		return htmlMessage;
	}
	
	@Override
	protected void initServiceDependency() {
		addRequiredService(IContentManager.class);
		addRequiredService(IHtmlMessageManager.class);
	}

	@Override
	protected void startService() {
		context.registerService(IOfficeLineHtmlProvideService.class, this);
	}

	@Override
	protected void stopService() {
		context.unregisterService(IOfficeLineHtmlProvideService.class, this);
	}
	class Invoker implements Runnable{
		private Task task;
		private IProgressMonitor monitor;
		public Invoker(
				IProgressMonitor monitor, String id,
				String url) {
			super();
			this.task = new Task(id, url);
			this.monitor = monitor;
		}

		@Override
		public void run() {
			monitor.beginTask(-1);
			Future<HtmlMessage> future=context.getExecutor().submit(task);
			try {
				HtmlMessage htmlMessage=future.get(20,TimeUnit.SECONDS);
				
				monitor.done(htmlMessage);
				
			} catch (Throwable e) {
				monitor.taskFailed(e);
				log.warn("",e);
			}
		}
		public void start(){
			new Thread(this).start();
		}
	}
	class Task implements Callable<HtmlMessage>{
		private String id;
		private String url;
		public Task(String id,String url){
			this.id=id;
			this.url=url;
		}
		@Override
		public HtmlMessage call() throws Exception {
			boolean down=context.getService(IHtmlMessageManager.class).syncDownload(url, id);
			HtmlMessage htmlMessage=null;
			if(down){
				htmlMessage= context.getService(IHtmlMessageManager.class).getHtmlMessage(id);
			}
			processing.remove(id);
			return htmlMessage;
		}

	}
	@Override
	public HtmlMessage get21CheapNews(String url, IProgressMonitor monitor,String citycode) {
		
		GDWebCachePath  aa=	GDWebCachePath.getInstance(context.getApplication().getAndroidApplication().getApplicationContext());
		String path=aa.getPathOfKey(citycode+url);
		String time=System.currentTimeMillis()+"";
		if(path.length()==0){
			path="/webCache/gdcheaps/"+time+"/";
			aa.setKeyOfListFilePath(citycode+url, "/gdcheaps/"+time+"/");
		}else{
			path="/webCache"+path;
			String[]times=path.split("/");
			time=times[times.length-1];
		}
		
		File f=new File(this.context.getApplication().getDataDir(Constant.APP_DATA_BASE, Context.MODE_PRIVATE).getPath()+path);
		if(!f.exists()){
			f.mkdirs();
		}		
		return getHtml(url, GD_21CITYCHEAP_ID+"/"+time,monitor);
	//	return  syncGetHtml(url, monitor, GD_21CITYCHEAP_ID+"/"+time);
	}
	
	
	private  HtmlMessage  syncGetHtml(String url, IProgressMonitor monitor,String id){
		
		HtmlMessage html=null;
		
		boolean ok=context.getService(IHtmlMessageManager.class).syncDownload(url, id);
		if(ok){
			html=	context.getService(IHtmlMessageManager.class).getHtmlMessage(id);
			if(monitor!=null){
				monitor.done(html);
			}
		}else{
			ok=context.getService(IHtmlMessageManager.class).syncDownload(url, id);
			if(ok){	
				html=	context.getService(IHtmlMessageManager.class).getHtmlMessage(id);
				if(monitor!=null){
					monitor.done(html);
				}
				
			}else{
				if(monitor!=null){
					monitor.taskFailed(new Exception("加载失败"));
				}
			}
			
		}
		
		return  html;
		
	}
	
	
	
	
	
	
	
	

	@Override
	public HtmlMessage getSquareHome(String url, IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		String code = AppUtils.getService(IClientCustomService.class)
				.getProviceCode();
		if (code != null) {
			code = code.toLowerCase();
		}
		 	return syncGetHtml(url, monitor,SQUARE_HOME+code+"/");
	}

	@Override
	public HtmlMessage get21CheapHome(String url, IProgressMonitor monitor) {
		//return syncGetHtml(url, monitor,CHEAP_HOM);
		return  getHtml(url, CHEAP_HOM,monitor);
	}
	@Override
	public HtmlMessage getDefaultZhushouBaobao(String url,IProgressMonitor monitor){
		GDWebCachePath  aa=	GDWebCachePath.getInstance(context.getApplication().getAndroidApplication().getApplicationContext());
		String path=aa.getPathOfKey("zsbb"+url);
		String time=System.currentTimeMillis()+"";
		if(path.length()==0){
			path="/webCache/zsbbdefault/"+time+"/";
			aa.setKeyOfListFilePath("zsbb"+url, "/zsbbdefault/"+time+"/");
		}else{
			path="/webCache"+path;
			String[]times=path.split("/");
			time=times[times.length-1];
		}
		
		File f=new File(this.context.getApplication().getDataDir(Constant.APP_DATA_BASE, Context.MODE_PRIVATE).getPath()+path);
		if(!f.exists()){
			f.mkdirs();
		}		
		return getHtml(url, GD_DEFAULT_ZHUSHOUBOBAO+"/"+time,monitor);
	}
	
	
	@Override
	public void downCheapsOfOneCity(String[] url,String key) {
	
		for(int i=0;i<url.length;i++){
			HtmlMessage hmlt=	getService(IOfficeLineHtmlProvideService.class).get21CheapNews(url[i],
					new IProgressMonitor(){
						@Override
						public void beginTask(int arg0) {
							// TODO Auto-generated method stub
							
						}
						@Override
						public void done(Object arg0) {
							// TODO Auto-generated method stub
												    
						}

						@Override
						public void setTaskName(String arg0) {
							// TODO Auto-generated method stub							
						}

						@Override
						public void taskCanceled(boolean arg0) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void taskFailed(Throwable arg0) {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void updateProgress(int arg0) {
							// TODO Auto-generated method stub
							
						}
				
			},key);
			
			
		}
		
	}

	@Override
	public HtmlMessage getGuessYouLikeHome(String url, IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return getHtml(url, GUESS_LIKE,monitor);
		 //	return syncGetHtml(url, monitor,GUESS_LIKE);
	}

}
