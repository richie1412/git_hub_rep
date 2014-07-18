/*
 * @(#)AbstractMicroKernel.java	 Oct 22, 2010
 *
 * Copyright 2004-2010 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */

package com.wxxr.mobile.core.microkernel.api;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.wxxr.mobile.core.log.api.Trace;


/**
 * @class desc A AbstractMicroKernel.
 * 
 * @author Neil
 * @version v1.0 
 * @created time Oct 22, 2010  10:06:58 AM
 */
public abstract class AbstractMicroKernel<C extends IKernelContext, M extends IKernelModule<C>> implements IMicroKernel<C,M>{
	private static Trace log = Trace.register(AbstractMicroKernel.class);
	private Element moduleConfigure;

	private LinkedList<M> modules = new LinkedList<M>();

	private LinkedList<M> createdModules = new LinkedList<M>();

	private boolean started = false;

	private Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

	private HashMap<Class<?>, ServiceFuture<?>> serviceHandlers = new HashMap<Class<?>, ServiceFuture<?>>();
	private HashMap<Class<?>, List<WeakReference<IServiceAvailableCallback<?>>>> callbacks = new HashMap<Class<?>, List<WeakReference<IServiceAvailableCallback<?>>>>();

	private LinkedList<IKernelServiceListener> listeners = new LinkedList<IKernelServiceListener>();


	@SuppressWarnings("unchecked")
	public void start() throws Exception{
		initModules();
		for (Object mod : getAllModules()) {
			((M)mod).start(getContext());
		}
		this.started = true;
	}


	@SuppressWarnings("unchecked")
	public void stop(){
		destroyModules();
		for (Object mod : getAllModules()) {
			((M)mod).stop();
		}
		this.started = false;
	}

	protected Object getAttribute(String key) {
		return attributes.get(key);
	}

	protected <T> T getService(Class<T> interfaceClazz) {
		ServiceFuture<T> handlers = getServiceFuture(interfaceClazz);
		return handlers.getService();
	}


	@SuppressWarnings("unchecked")
	protected <T> ServiceFuture<T> getServiceFuture(Class<T> interfaceClazz) {
		ServiceFuture<T> handlers = null;
		synchronized(this.serviceHandlers){
			handlers = (ServiceFuture<T>)this.serviceHandlers.get(interfaceClazz);
			if(handlers == null){
				handlers = new ServiceFuture<T>();
				this.serviceHandlers.put(interfaceClazz, handlers);
			}
		}
		return handlers;
	}


	protected <T> ServiceFuture<T> getServiceAsync(Class<T> interfaceClazz) {
		return getServiceFuture(interfaceClazz);
	}

	protected <T> void registerService(Class<T> interfaceClazz, T handler) {
		if(getServiceFuture(interfaceClazz).addService(handler)){
			fireServiceRegistered(interfaceClazz, handler);
		}
	}

	protected void addKernelServiceListener(IKernelServiceListener listener) {
		synchronized(this.listeners){
			if(!this.listeners.contains(listener)){
				this.listeners.add(listener);
			}
		}
	}


	protected boolean removeKernelServiceListener(IKernelServiceListener listener){
		synchronized(this.listeners){
			return this.listeners.remove(listener);
		}
	}

	protected <T>  void fireServiceRegistered(Class<T> clazz, T handler){
		IKernelServiceListener[] list = null;
		synchronized(this.listeners){
			list = this.listeners.toArray(new IKernelServiceListener[this.listeners.size()]);
		}
		if(list != null){
			for (IKernelServiceListener l : list) {
				if(l.accepts(clazz)){
					l.serviceRegistered(clazz, handler);
				}
			}
		}
		List<WeakReference<IServiceAvailableCallback<?>>> cbs = null;
		synchronized(callbacks){
			cbs = callbacks.remove(clazz);
		}
		if(cbs != null){
			for (WeakReference<IServiceAvailableCallback<?>> ref : cbs) {
				@SuppressWarnings("unchecked")
				IServiceAvailableCallback<T> cb = (IServiceAvailableCallback<T>)ref.get();
				if(cb != null){
					cb.serviceAvailable(handler);
				}
			}
		}
	}

	protected <T>  void fireServiceUnregistered(Class<T> clazz, T handler){
		IKernelServiceListener[] list = null;
		synchronized(this.listeners){
			list = this.listeners.toArray(new IKernelServiceListener[this.listeners.size()]);
		}
		if(list != null){
			for (IKernelServiceListener l : list) {
				if(l.accepts(clazz)){
					l.serviceUnregistered(clazz, handler);
				}
			}
		}

	}

	protected <T> void addServiceAvailableCallback(Class<T> clazz, IServiceAvailableCallback<T> cb){
		if((clazz == null)||(cb == null)){
			throw new IllegalArgumentException("Service class and callback cannot be NULL !");
		}
		T service = getService(clazz);
		if(service != null){
			cb.serviceAvailable(service);
		}else{
			synchronized(this.callbacks){
				List<WeakReference<IServiceAvailableCallback<?>>> list = callbacks.get(clazz);
				if(list == null){
					list = new ArrayList<WeakReference<IServiceAvailableCallback<?>>>();
				}
				list.add(new WeakReference<IServiceAvailableCallback<?>>(cb));
			}
		}
	}


	protected Object removeAttribute(String key) {
		return attributes.remove(key);
	}

	protected void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	protected <T> void unregisterService(Class<T> interfaceClazz, T handler) {
		if(getServiceFuture(interfaceClazz).removeService(handler)){
			fireServiceUnregistered(interfaceClazz, handler);
		}
	}


	public void setModuleConfigure(Element serviceConfigure) {
		if(null != serviceConfigure){
			this.moduleConfigure = serviceConfigure;
		}
	}

	public void registerKernelModule(M module) {
		boolean added = false;
		synchronized(modules){
			if(!this.modules.contains(module)){
				this.modules.add(module);
				added = true;
			}
		}
		if(added){
			if(this.started){
				if(log.isInfoEnabled()){
					log.info("Starting module :["+module+"] ...");
				}
				module.start(getContext());
				if(log.isInfoEnabled()){
					log.info("Module :["+module+"] started !");
				}
			}
		}
	}

	public void unregisterKernelModule(M module) {
		boolean removed = false;
		synchronized(modules){
			removed = this.modules.remove(module);
		}
		if(removed){
			if(this.started){
				if(log.isInfoEnabled()){
					log.info("Stopping module :["+module+"] ...");
				}
				module.stop();
				if(log.isInfoEnabled()){
					log.info("Module :["+module+"] stopped !");
				}
			}      
		}
	}


	abstract protected C getContext();


	protected void destroyModules() {
		for (M mod : this.createdModules) {
			try {
				unregisterKernelModule(mod);
			}catch(Exception e){
				log.warn("Failed to unregister module :"+mod, e);
			}
		}
		this.createdModules.clear();
	}

	protected void initModules() throws Exception {
		if(moduleConfigure == null){
			return;
		}
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		NodeList nodes = moduleConfigure.getElementsByTagName("module");
		int len = nodes.getLength();
		for (int i = 0; i < len; i++) {
			Node node = nodes.item(i);
			if(node instanceof Element){
				Element elem = (Element)node;
				String clsName = elem.getAttribute("class");
				clsName = clsName != null ? clsName.trim() : null;
				clsName = clsName != null && clsName.length() > 0 ? clsName : null;
				if(clsName != null){
					@SuppressWarnings("unchecked")
					Class<M> clazz = (Class<M>)cl.loadClass(clsName);
					M mod = clazz.newInstance();
					Method initMethod = null;
					try {
						initMethod = clazz.getMethod("init", new Class[]{Element.class});
					}catch(Exception e){
						log.warn("Cannot find init(Element) method to init module :"+mod, e);
					}
					if(initMethod != null){
						initMethod.invoke(mod, new Object[]{elem});
					}
					registerKernelModule(mod);
					this.createdModules.add(mod);
				}
			}
		}
	}

	private Object[] getAllModules() {
		synchronized(modules){
			if(modules.isEmpty()){
				return new IKernelModule[0];
			}else{
				return modules.toArray(new IKernelModule[modules.size()]);
			}
		}
	}
}
