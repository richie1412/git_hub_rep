package com.wxxr.callhelper.qg.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.bean.HtmlMessage;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
import com.wxxr.mobile.core.microkernel.api.IKernelServiceListener;
import com.wxxr.mobile.core.rpc.http.api.HttpRpcService;
import com.wxxr.mobile.core.security.api.ISiteSecurityService;
import com.wxxr.mobile.core.util.StringUtils;
import com.wxxr.mobile.web.grabber.api.IGrabberServiceContext;
import com.wxxr.mobile.web.grabber.api.IWebContentStorage;
import com.wxxr.mobile.web.grabber.api.IWebGrabberService;
import com.wxxr.mobile.web.grabber.api.IWebLinkExtractor;
import com.wxxr.mobile.web.grabber.api.IWebPageGrabbingTask;
import com.wxxr.mobile.web.grabber.common.AbstractContentStorage;
import com.wxxr.mobile.web.grabber.common.AbstractGrabberModule;
import com.wxxr.mobile.web.grabber.common.AbstractPageGrabbingTask;
import com.wxxr.mobile.web.grabber.model.WebURL;
import com.wxxr.mobile.web.grabber.module.HtmlParserModule;
import com.wxxr.mobile.web.grabber.module.WebCrawlerModule;
import com.wxxr.mobile.web.grabber.module.WebGrabberServiceImpl;
import com.wxxr.mobile.web.grabber.module.WebLinkExtractorRegistryModule;
import com.wxxr.mobile.web.grabber.module.WebPageFetcherModule;
import com.wxxr.mobile.web.link.extractor.ActionLinkExtractor;
import com.wxxr.mobile.web.link.extractor.BackgroundLinkExtractor;
import com.wxxr.mobile.web.link.extractor.CiteLinkExtractor;
import com.wxxr.mobile.web.link.extractor.CodebaseLinkExtractor;
import com.wxxr.mobile.web.link.extractor.DataLinkExtractor;
import com.wxxr.mobile.web.link.extractor.DataUrlLinkExtractor;
import com.wxxr.mobile.web.link.extractor.FormActionLinkExtractor;
import com.wxxr.mobile.web.link.extractor.HrefLinkExtractor;
import com.wxxr.mobile.web.link.extractor.IconLinkExtractor;
import com.wxxr.mobile.web.link.extractor.LongDescLinkExtractor;
import com.wxxr.mobile.web.link.extractor.SrcLinkExtractor;
import com.wxxr.mobile.web.link.extractor.UseMapLinkExtractor;

public class ContentManager extends AbstractModule<ComHelperAppContext>
		implements IContentManager {

	
	private static final String CONTENT_FILE = "content.bin";

	private static Trace log = Trace.register(ContentManager.class);
	private SimpleDateFormat httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz",Locale.ENGLISH);
	
	private static class WebGrabData {
		String tmpDir;
		String root;
	}

	private MyWebGrabberServiceImpl  webGrabberServiceImpl;
	private File storeDir;
	class MyWebGrabberServiceImpl extends WebGrabberServiceImpl implements IExWebGrabberService {
		
		private class CrawlerServiceContext extends AbstractContext implements IGrabberServiceContext{

			/* (non-Javadoc)
			 * @see com.wxxr.mobile.core.microkernel.api.AbstractMicroKernel.AbstractContext#getService(java.lang.Class)
			 */
			@Override
			public <T> T getService(Class<T> interfaceClazz) {
				T t=super.getService(interfaceClazz);
				if(t==null){
					t=ContentManager.this.context.getService(interfaceClazz);
				}
				return t;
			}
			
		}
		private CrawlerServiceContext context=new CrawlerServiceContext();
		/* (non-Javadoc)
		 * @see com.wxxr.mobile.core.microkernel.api.AbstractMicroKernel#getService(java.lang.Class)
		 */
		@Override
		protected IGrabberServiceContext getContext() {
			return context;
		}

		@Override
		protected void initModules() {
			registerKernelModule(new GrabbingTaskFactory());
			registerKernelModule(createWebLinkExtratorRegistryModule());
			getContext().registerService(IWebContentStorage.class, storage);
			registerKernelModule(new HtmlParserModule());
			registerKernelModule(new WebPageFetcherModule());
			registerKernelModule(new WebCrawlerModule());
		}
		public <T> void register(Class<T> clazz,T handler){
			getContext().registerService(clazz, handler);
		}
		public <T> void unregister(Class<T> clazz,T handler){
			getContext().unregisterService(clazz, handler);
			
		}

		@Override
		public boolean grabWebPage(String htmlUrl, String tmpDir, String dir) {
			WebGrabData data = new WebGrabData();
			data.root = dir;
			data.tmpDir = tmpDir;
			return super.grabWebPage(htmlUrl, data);
		}

	}
	
	private IKernelServiceListener serviceListener = new IKernelServiceListener() {
		
		@Override
		public <S> void serviceUnregistered(Class<S> clazz, S handler) {
			webGrabberServiceImpl.unregister(clazz, handler);
			if(log.isDebugEnabled()){
				log.debug("Service "+clazz+" unregistered");
			}
		}
		
		@Override
		public <S> void serviceRegistered(Class<S> clazz, S handler) {
			webGrabberServiceImpl.register(clazz, handler);
			if(log.isDebugEnabled()){
				log.debug("Service "+clazz+" registered");
			}
		}
		
		@Override
		public boolean accepts(Class<?> clazz) {
			return clazz==HttpRpcService.class||clazz==ISiteSecurityService.class;
		}
	};
	private AbstractContentStorage storage = new AbstractContentStorage() {
		

		@Override
		public String getContentLastModified(IWebPageGrabbingTask task, WebURL url) {
			File resFile;
			if(task.getCustomData() instanceof WebGrabData){
				resFile= ContentManager.this.getResourceFile(((WebGrabData)task.getCustomData()).root, url.getURL(),
						url.getDepth());

			}else{
				resFile = getResourceFile(task,url);
			}
			try {
				if(resFile==null||!resFile.exists()){
					return null;
				}
				String path = resFile.getCanonicalPath();
				String date = null;
				Date d=null;
				File origFile = new File(path+".orig");
				if(!origFile.exists()){
					d=new Date(resFile.lastModified());
					synchronized(httpDateFormat){
						date = httpDateFormat.format(d);
					}
					return date;
				}
				File propFile = new File(path + ".x");
				FileInputStream fis = null;

				if( !propFile.exists()){
					d=new Date(origFile.lastModified());
					synchronized(httpDateFormat){
						date = httpDateFormat.format(d);
					}
					return date;
				}
				try {
					fis = new FileInputStream(propFile);
					Properties properties = new Properties();
					properties.load(fis);
					String purl = properties.getProperty("URL");
					if(purl!=null && url.getURL().equals(purl)){
						if(origFile.exists()){
							d = new Date(origFile.lastModified());
							synchronized(httpDateFormat){
								date = httpDateFormat.format(d);
							}
							return date;
						}
					}
				}finally{
					if(fis!=null){
						fis.close();
					}
				}

				return date;
			} catch (IOException e) {
				return null;
			}
		}
		@Override
		protected File getResourceFile(IWebPageGrabbingTask task, WebURL url) {
			String root=null;
			if(task.getCustomData() instanceof WebGrabData){
				WebGrabData data=(WebGrabData)task.getCustomData(); 
				root=data.tmpDir;
			}else if(task.getCustomData() instanceof String){
				root= (String)task.getCustomData();

			}else{
				throw new IllegalArgumentException("task in no defined type:"+task.getClass());
			}
			return ContentManager.this.getResourceFile(root.toString(), url.getURL(),
					url.getDepth());

		}
		
		@Override
		protected String getRelativePath(IWebPageGrabbingTask task, WebURL url) {
			if(log.isDebugEnabled()){
				log.debug("getRelativePath  task id:"+task.getCustomData()+" url:"+url);
			}
			return ContentManager.this.getRelativePath(url.getURL(),
					url.getDepth());
		}

		@Override
		protected File getContentRoot(IWebPageGrabbingTask task) {
			if(log.isDebugEnabled()){
				log.debug("getContentRoot  task id:"+task.getCustomData());
			}
			
			String root=null;
			if(task.getCustomData() instanceof WebGrabData){
				WebGrabData data=(WebGrabData)task.getCustomData(); 
				root=data.tmpDir;
			}else if(task.getCustomData() instanceof String){
				root= (String)task.getCustomData();

			}else{
				throw new IllegalArgumentException("task in no defined type:"+task.getClass());
			}
			return ContentManager.this.getContentRoot(root);
		}

		protected void processContent(IWebPageGrabbingTask task, WebURL webURL)
				throws IOException {
			if(log.isDebugEnabled()){
				log.debug("processContent  task id:"+task.getCustomData()+" url:"+webURL);
			}
			Document document = task.getHtmlData().getDocument();
			Elements elements = document.select("meta");
			if (elements == null || elements.isEmpty()) {
				return;
			}
			Properties properties = new Properties();
			for (Element element : elements) {
				String name = element.attr("name");
				if ("titleStr".equals(name) || "comeFrom".equals(name)
						|| "iconsUrl".equals(name)
						|| "introduction".equals(name)
						|| "lastmodified".equals(name)
						|| "defaultPage".equals(name)
						|| "linkUrl".equals(name)
						|| "pageIndex".equals(name)
						|| "createtime".equals(name)
						|| "listimageUrl".equals(name)) {
					String content = element.attr("content");
					if(log.isDebugEnabled()){
						log.debug("task id:"+task.getCustomData()+" url:"+webURL+" set property "+"name:"+name+" content:"+content);
					}
					properties.setProperty(name, content);
				}
			}
			
			Elements elements2 = document.select("dsname");
			if (elements2!=null&&elements2.size()>0) {
				String cityname=elements2.get(0).text();
				properties.setProperty("21cityname", cityname);

			}
		
		
			
			
			
			if (!properties.isEmpty()) {
				File resFile = getResourceFile(task, webURL);
				String path = resFile.getCanonicalPath();
				File propFile = new File(path + ".x");
				if(log.isDebugEnabled()){
					log.debug("store  properties file:"+propFile);
				}
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(propFile, true);
					properties.store(fos, null);
				} finally {
					if (fos != null) {
						fos.close();
					}
				}
			}

		}
	};

	protected File getResourceFile(String root, String url, int depth) {
		return new File(getContentRoot(root), getRelativePath(url, depth));
	}

	protected String getRelativePath(String url, int depth) {
		if (depth == 0) {
			return getFileName(url, depth);
		} else {
			return "resources/" + getFileName(url, depth);
		}
	}

	protected File getContentRoot(String root) {
		return new File(getWebCacheRoot(), root);
	}

	protected File getWebCacheRoot() {
		return new File(storeDir, "webCache");
	}

	protected File getStoreRoot(String name) {
		return new File(storeDir,name);
	}
//	private File getExStoreDir() {
//		File storeDir;
//		if (Environment.getExternalStorageState().equals(
//	
//				Environment.MEDIA_MOUNTED)) {
//			storeDir = new File(Environment.getExternalStorageDirectory(),
//					Constant.PACKAGE_NAME);
//			if(log.isDebugEnabled()){
//				log.debug("store dir is sdcard");
//			}
//		} else {
//			storeDir = this.context.getApplication().getAndroidApplication()
//					.getCacheDir();
//			if(log.isDebugEnabled()){
//				log.debug("store dir is phone");
//			}
//		}
//		
//		return storeDir;
//	}

	@Override
	protected void initServiceDependency() {

	}

	@Override
	protected void startService() {

		storeDir = this.context.getApplication().getDataDir(Constant.APP_DATA_BASE, Context.MODE_PRIVATE);

		log.info("store dir"+storeDir);
		this.context.registerService(IContentManager.class, this);
		try {
			webGrabberServiceImpl = new MyWebGrabberServiceImpl() ;
			webGrabberServiceImpl.start();
			this.context.registerService(IExWebGrabberService.class,
					webGrabberServiceImpl);
			this.context.registerService(IWebGrabberService.class, webGrabberServiceImpl);
		} catch (Exception e) {
			log.error("start webGrabberServiceImpl failed",e);
		}
		this.context.addKernelServiceListener(serviceListener);
		if(log.isDebugEnabled()){
			log.debug("content manager  is started");
		}
	}

	@Override
	protected void stopService() {
		this.context.unregisterService(IContentManager.class, this);
		this.context.unregisterService(IWebGrabberService.class,
				webGrabberServiceImpl);
		this.context.unregisterService(IExWebGrabberService.class, webGrabberServiceImpl);
		webGrabberServiceImpl.stop();
		webGrabberServiceImpl = null;
		this.context.removeKernelServiceListener(serviceListener);
		if(log.isDebugEnabled()){
			log.debug("content manager  is stop");
		}
	}

	protected String getFileName(String path, int depth) {

		int idx = path.lastIndexOf('/');
		if (idx > 0) {
			path = path.substring(idx + 1);
		}
		idx = path.lastIndexOf('.');
		if ((depth == 0) && (idx < 0)) {
			return path + ".html";
		}
		try {
			return URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("System shoud support UTF-8 charset!!!");
		}
	}
	
	/**
	 * 
	 */
	protected AbstractGrabberModule createWebLinkExtratorRegistryModule() {
		WebLinkExtractorRegistryModule m = new WebLinkExtractorRegistryModule();
		IWebLinkExtractor extractor = new HrefLinkExtractor();
		m.registerExtractor("a", extractor);
		m.registerExtractor("area", extractor);
		m.registerExtractor("link", extractor);
		extractor = new SrcLinkExtractor();
		m.registerExtractor("frame", extractor);
		m.registerExtractor("iframe", extractor);
		m.registerExtractor("img", extractor);
		m.registerExtractor("input", extractor);
		m.registerExtractor("script", extractor);
		m.registerExtractor("audio", extractor);
		m.registerExtractor("embed", extractor);
		m.registerExtractor("source", extractor);
		m.registerExtractor("video", extractor);
		m.registerExtractor("meta", new CustomMetaLinkExtractor());	
		m.registerExtractor("li", new DataUrlLinkExtractor());
		extractor = new DataLinkExtractor();
		m.registerExtractor("object", extractor);
		extractor = new ActionLinkExtractor();
		m.registerExtractor("form", extractor);
		extractor = new FormActionLinkExtractor();
		m.registerExtractor("button", extractor);
		m.registerExtractor("input", extractor);
		extractor = new UseMapLinkExtractor();
		m.registerExtractor("img", extractor);
		m.registerExtractor("input", extractor);
		m.registerExtractor("object", extractor);
		extractor = new BackgroundLinkExtractor();
		m.registerExtractor("body", extractor);
		extractor = new CodebaseLinkExtractor();
		m.registerExtractor("applet", extractor);
		m.registerExtractor("object", extractor);
		extractor = new CiteLinkExtractor();
		m.registerExtractor("del", extractor);
		m.registerExtractor("blockquote", extractor);
		m.registerExtractor("ins", extractor);
		m.registerExtractor("q", extractor);
		extractor = new LongDescLinkExtractor();
		m.registerExtractor("frame", extractor);
		m.registerExtractor("iframe", extractor);
		m.registerExtractor("img", extractor);
		extractor = new IconLinkExtractor();
		m.registerExtractor("command", extractor);
		return m;
	}

	@Override
	public void saveContent(String type, String id, byte[] content)
			throws IOException {
		validateArgumentsNotEmpty(type,id,content);

		if(log.isDebugEnabled()){
			log.debug("save content type:"+type+",id:"+id);
		}
		File contentFile = getContentFile(type, id, true);
		File savingFile = new File(contentFile.getParent(), contentFile.getName()+".saving");
		if((!savingFile.exists())&&(!savingFile.createNewFile())){
				log.error("create file exception. file:"+savingFile);
				throw new IOException("create file exception. file:"+savingFile);
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(savingFile);
			fos.write(content);
			fos.flush();
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
		if(!savingFile.renameTo(contentFile)){
			throw new IOException("rename file failed,saving file:"+savingFile+" to file"+contentFile);
		}
		savingFile.setLastModified(System.currentTimeMillis());
	}

	@Override
	public byte[] getContent(String type, String id) throws IOException {
		validateArgumentsNotEmpty(type,id);
		File storeFile;
		try {
			storeFile = getContentFile(type, id,false);
			if(storeFile.exists() == false){
				return null;
			}
			FileInputStream fis = null;
			ByteArrayOutputStream os=null;
			try {
				fis = new FileInputStream(storeFile);
				os=new ByteArrayOutputStream();
	            byte[] tmp = new byte[4096];
	            int l;
	            while((l = fis.read(tmp)) != -1) {
	                os.write(tmp,0,l);
	            }
	           return os.toByteArray();
			} finally {
				if (os != null) {
					os.close();
				}
				if(fis!=null){
					fis.close();
				}
			}
			
		} catch (IOException e) {
			return null;
		}
		

	}

	@Override
	public void delete(String type, String id) throws IOException {
		validateArgumentsNotEmpty(type,id);
		try {
			File dir = getContentFile(type, id,false);
			deleteFile(dir);
		} catch (Exception e) {
			
		}
		
	}

	private File getContentFile(String type, String id,boolean createDirIfNotExisting) throws IOException {
		File dir = new File(getStoreRoot(type),id);
		File storeFile=new File(dir, CONTENT_FILE);
		if((storeFile.exists() == false)&&createDirIfNotExisting){
			if((dir.exists() == false)&&(dir.mkdirs() == false)){
				throw new IOException("Failed to create directory :"+dir.getCanonicalPath());
			}
		}
		return storeFile;
	}

	protected File getStatusFile(String type,String id,String statusName,String status,boolean createDirIfNotExisting) throws IOException{
		File dir = new File(getStoreRoot(type),id);
		File statusFile=new File(dir, statusName+"."+status+".s");
		if((statusFile.exists() == false)&&createDirIfNotExisting){
			File parent = statusFile.getParentFile();
			if((dir.exists() == false)&&(parent.mkdirs() == false)){
				throw new IOException("Failed to create directory :"+parent.getCanonicalPath());
			}
		}
		return statusFile;
	}
	
	
	protected File findStatusFile(String type,String id,final String statusName) {
		File dir = new File(getStoreRoot(type),id);
		File[] files = dir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename) {
				return filename.startsWith(statusName)&& filename.endsWith(".s");
			}
		});
		if((files != null)&&(files.length > 0)){
			if(files.length > 1){
				for(int i=1; i < files.length ; i++){		// delete all redundant files
					if(!files[i].delete()){
						log.warn("Failed to delete redundant status file :"+files[i]);
					}
				}
			}
			return files[0];
		}
		return null;
	}
	
	@Override
	public void updateStatus(String type, String id, String statusName,
			String status) throws IOException {
		validateArgumentsNotEmpty(type,id,statusName,status);
		File oldStatusFile = findStatusFile(type, id, statusName);
		File statusFile=getStatusFile(type, id, statusName, status,true);
		if(oldStatusFile != null) {
			if(!oldStatusFile.renameTo(statusFile)){
				log.error("create not rename status file from :"+oldStatusFile+" to file:"+statusFile);
				throw new IOException("create not rename status file from :"+oldStatusFile+" to file:"+statusFile);
			}
			oldStatusFile.setLastModified(System.currentTimeMillis());
		}else{
			if(!statusFile.createNewFile()){
				log.error("create file exception. file:"+statusFile);
				throw new IOException("create file exception. file:"+statusFile);
			}
			statusFile.setLastModified(System.currentTimeMillis());
		}
//		String[] fileNames=getFileListWithPrefix(storeDir, statusName);
//		if(fileNames==null || fileNames.length==0){
//			if(!statusFile.createNewFile()){
//				log.error("create file exception. file:"+statusFile);
//				throw new IOException("create file exception. file:"+statusFile);
//			}
//			return ;
//		}
//		
//		if(fileNames.length>1){
//			for(int i=1;i<fileNames.length;i++){
//				deleteFile(new File(storeDir,fileNames[i]));
//			}
//		}
//		File oldStatusFile=new File(storeDir,fileNames[0]);
//		if(!oldStatusFile.renameTo(statusFile)){
//			log.error("create file exception. file:"+statusFile);
//			throw new IOException("rename file exception. file:"+oldStatusFile +" to file:"+statusFile);
//		}
		
	}

	private void validateArgumentsNotEmpty(Object ... args) {
		if(args==null){
			throw new IllegalArgumentException(
					"Validate arguments error");
		}
		for(Object arg:args){
			if(arg instanceof String){
				if (StringUtils.isBlank((String) arg)) {
					throw new IllegalArgumentException(
							"Argument "+arg+" can't be empty");
				}
			}else{
				if(arg==null){
					throw new IllegalArgumentException(
							"Argument "+arg+" can't be null");
				}
			}
		}

	}

	@Override
	public String getStatus(String type, String id, String statusName)
			throws IOException {
		validateArgumentsNotEmpty(type,id,statusName);
		File statusFile = findStatusFile(type, id, statusName);
		if(statusFile == null){
			return null;
		}
		return StringUtils.split(statusFile.getName(),'.')[1];
//		File storeDir = getStoreDir(type, id);
//		String[] fileNames=getFileListWithPrefix(storeDir, statusName);
//		if(fileNames==null || fileNames.length==0){
//			log.error("not found "+statusName +" file");
//			return "";
//		}
//		if(fileNames.length>1){
//			log.error(" found many "+statusName +" file");
//			throw new IOException(" found many "+statusName +" file");
//			
//		}
//		String suffix= getFileSuffix(fileNames[0]);
//		return suffix;
	}

	@Override
	public void deleteStatus(String type, String id,String statusName) throws IOException {
		validateArgumentsNotEmpty(type,id,statusName);
		File statusFile = findStatusFile(type, id, statusName);
		if(statusFile != null){
			if(!statusFile.delete()){
				throw new IOException(" Failed to delete status file :"+statusFile.getCanonicalPath());	
			}
		}
//		File storeDir = getStoreDir(type, id);
//		String[] fileNames=getFileListWithPrefix(storeDir, statusName);
//		if(fileNames==null || fileNames.length==0){
//			log.error("not found "+statusName +" file");
//			return ;
//		}
//		if(fileNames.length>1){
//			log.error(" found many "+statusName +" file");
//			throw new IOException(" found many "+statusName +" file");	
//		}
//		deleteFile(new File(storeDir,fileNames[0]));
	}

	@Override
	public String[] queryContentIds(String type, String statusName,
			String statusValue) throws IOException {
		validateArgumentsNotEmpty(type);
		File dir = getStoreRoot(type);
		if(!dir.exists()){
			if(log.isDebugEnabled()){
				log.debug("dir:"+dir.getCanonicalPath()+" not exists");
			}
			return null ;
		}


		ArrayList<String> matchFiles = new ArrayList<String>();
		String fileName="";
		if(statusName==null){
			fileName=CONTENT_FILE;
		}else{
			validateArgumentsNotEmpty(statusValue);
			fileName=statusName+"."+statusValue+".s";
		}
		searchMatchStatusContentIds(dir.getCanonicalPath(),dir, fileName, matchFiles);
		if(matchFiles.isEmpty()){
			return null;
		}
		return matchFiles.toArray(new String[matchFiles.size()]);
//		File[] list = dir.listFiles();
//		if (list == null) {
//			if(log.isDebugEnabled()){
//				log.debug(" no found "+type+ "files");
//			}
//			return null;
//		}
//		List<String> ids = new ArrayList<String>();
//		for (File d : list) {
//			if (d.isDirectory()) {
//				File file=new File(d,statusName+"."+statusValue);
//				if(file.exists()){
//					ids.add(d.getName());
//				}
//			}
//		}
//		return ids.toArray(new String[0]);
	}
	
	protected String getContentIdFromFile(String rootPath,File file) throws IOException {
		String path = file.getParentFile().getCanonicalPath();
		if(path.startsWith(rootPath)){
			path = path.substring(rootPath.length());
			if(path.startsWith("/")){
				path = path.substring(1);
			}
			return path;
		}
		return null;
	}
	
	protected void searchMatchStatusContentIds(String rootPath, File dir, String matchname, List<String> result) throws IOException {
		if(!dir.isDirectory()){
			return;
		}
		File[] files = dir.listFiles();
		if(files != null){
			for (File file : files) {
				if(file.isDirectory()){
					searchMatchStatusContentIds(rootPath,file, matchname, result);
				}else{
					String fname = file.getName();
					if(fname.equals(matchname)){
						String id = getContentIdFromFile(rootPath,file);
						if(id != null){
							result.add(id);
						}
					}
				}
			}
		}
	}

	protected void deleteFile(File file) {
//		LinkedList<File> queue=new LinkedList<File>();
//		queue.add(file);
//		LinkedList<File> list=new LinkedList<File>();
//		while(!queue.isEmpty()){
//			File f=queue.removeFirst();
//			if(f.isDirectory()){
//				File[] files=f.listFiles();
//				queue.addAll((Arrays.asList(files)));
//				list.addLast(f);
//			}else{
//				f.delete();
//			}
//		}
//
//		while(!list.isEmpty()){
//			if(log.isDebugEnabled()){
//				log.debug("dirs are "+list);
//			}
//			File f=list.removeLast();
//			f.delete();
//		}
		
		if(!file.exists()){
			return;
		}
		if(file.isFile()){
			file.delete();
		}else if(file.isDirectory()){
			File[] files = file.listFiles();
			if(files != null){
				for (File f : files) {
					deleteFile(f);
				}
			}
			file.delete();
		}
	}
//	protected String getFileSuffix(String fileName) {
//		if(fileName.lastIndexOf(".")==-1){
//			if(log.isDebugEnabled()){
//				log.debug(fileName+" not exists . suffix");
//			}
//			return "";
//		}
//		String suffix= fileName.substring(fileName.lastIndexOf(".")+1);
//		if(log.isDebugEnabled()){
//			log.debug(fileName+" suffix is "+suffix);
//		}
//		return suffix;
//	}
//
//	protected String getFilePrefix(String fileName) {
//		if(fileName.lastIndexOf(".")==-1){
//			if(log.isDebugEnabled()){
//				log.debug(fileName+"not exists . suffix");
//			}
//			return fileName;
//		}
//		String prefix=fileName.substring(0, fileName.lastIndexOf("."));
//		if(log.isDebugEnabled()){
//			log.debug(fileName+" prefix is "+prefix);
//		}
//		return prefix;
//	}
//
//	protected String[] getFileListWithPrefix(File dir, final String prefix) {
//		return dir.list(new FilenameFilter() {
//			@Override
//			public boolean accept(File dir, String filename) {
//				return prefix.toLowerCase().equals(
//						getFilePrefix(filename).toLowerCase());
//			}
//		});
//	}

	protected String[] getFileListWithSuffix(File dir, final String suffix) {
		return dir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
//				boolean accept=suffix.toLowerCase().equals(
//						getFileSuffix(filename).toLowerCase());
				return filename.endsWith(suffix);
			}
		});
	}

	@Override
	public boolean isExistContent(String type, String id) {
		validateArgumentsNotEmpty(type,id);
		try{
			File f = getContentFile(type, id, false);
			return f.exists()&&f.canRead();
		}catch(IOException e){
			// should never happen
			return false;
		}
	}
	
	
	@Override
	public HtmlMessage getHtmlMessage(String id) {
		validateArgumentsNotEmpty(id);
		return getHtmlMessage(getWebCacheRoot().getAbsolutePath(),id);
	}	
	
	@Override
	public HtmlMessage getHtmlMessage(String path,String id) {
		validateArgumentsNotEmpty(id);
		File dir = new File(path, id.toString());
		if(!dir.exists()){
			if(log.isDebugEnabled()){
				log.debug("dir "+dir+" not  exists");
			}
			return null;
		}
		String[] origFiles=getFileListWithSuffix(dir, ".orig");
		if(origFiles==null || origFiles.length==0){
			return null;
		}
		String[] fileNames = getFileListWithSuffix(dir, "x");
		if (fileNames != null && fileNames.length > 0) {
			HtmlMessage htmlMessage = new HtmlMessage();
			File proFile = new File(dir, fileNames[0]);
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(proFile);
				Properties properties = new Properties();
				properties.load(fis);
				String url = properties.getProperty("URL");
				File file = getResourceFile(id.toString(), url, 0);
				htmlMessage.setOrigUrl(url);
				htmlMessage.setUrl("file://" + file.getCanonicalPath());
				String title = properties.getProperty("titleStr");
				String source = properties.getProperty("comeFrom");
				String image = properties.getProperty("iconsUrl");
				if(image!=null){
					File imageFile = getResourceFile(id.toString(), image, 1);
					htmlMessage.setImage("file://"+imageFile.getCanonicalPath());
				}
				
				String listimage = properties.getProperty("listimageUrl");
				if(listimage!=null){
					File imageFile = getResourceFile(id.toString(), listimage, 1);
					htmlMessage.setListimageUrl("file://"+imageFile.getCanonicalPath());
				}
				
				
				
				String introduction = properties.getProperty("introduction");
				String createDate = properties.getProperty("lastmodified");
				htmlMessage.setAbstrct(introduction);
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if (createDate != null && !createDate.trim().equals("")) {
					try {
						htmlMessage.setCreateDate(sdf.parse(createDate));
					} catch (java.text.ParseException e) {
						log.warn("Failed to parse create date :"+createDate);
					}
					
				}
					
				htmlMessage.setDefaultPage(properties.getProperty("introduction"));
				htmlMessage.setLinkUrl(properties.getProperty("linkUrl"));
				htmlMessage.setPageIndex(properties.getProperty("pageIndex"));
				htmlMessage.setCheap21cityname(properties.getProperty("21cityname"));
				
				
				htmlMessage.setSource(source);
				htmlMessage.setTitle(title);
				
				String  time=properties.getProperty("createtime");
			
				htmlMessage.setCreatetime(time);
					
				if(log.isDebugEnabled()){
					log.debug("html message: "+htmlMessage);
				}
				return htmlMessage;
			} catch (IOException e) {
				log.error("", e);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {

					}
				}
			}
		}
		return null;
	}

	@Override
	public void removeHtmlMesage(String id) throws IOException {
		validateArgumentsNotEmpty(id);
		File dir = new File(getWebCacheRoot(), id.toString());
		if(dir.exists()){
			deleteFile(dir);
		}
	}

	@Override
	public void moveHtmlMessage(String oldId, String newId) throws IOException {
		removeHtmlMesage(newId);
		File newDir = new File(getWebCacheRoot(), newId);
		File oldDir=new File(getWebCacheRoot(), oldId);
		if(!oldDir.renameTo(newDir)){
			throw new IOException("rename file failed,old dir:"+oldDir+" new dir"+newDir);
		}
		oldDir.setLastModified(System.currentTimeMillis());
	}

	@Override
	public Long getStatusLastModified(String type, String id, String statusName){
		File file=findStatusFile(type, id, statusName);
		if(file==null||!file.exists()){
			return null;
		}
		return file.lastModified();
	}

	@Override
	public Long getContentLastModified(String type, String id){
		File file;
		try {
			file = getContentFile(type, id,false);
			if(file==null||!file.exists()){
				return null;
			}
			return file.lastModified();
		} catch (IOException e) {
			return null;
		}
		
	}

	
	
}
