/**
 * 
 */
package com.wxxr.mobile.core.microkernel.api;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.wxxr.mobile.core.log.api.Trace;

/**
 * @author neillin
 *
 */
public abstract class AbstractModule<T extends IKernelContext> implements IKernelModule<T> {
	private static final Trace log = Trace.register(AbstractModule.class);
	
	protected T context;
	private Set<Class<?>> requiredServices;
	private Set<Class<?>> pendingServices;
	private AtomicBoolean started = new AtomicBoolean(false);
	private AtomicBoolean initted = new AtomicBoolean(false);
	private IKernelServiceListener serviceListener = new IKernelServiceListener() {
		
		@Override
		public <S> void serviceUnregistered(Class<S> clazz, S handler) {
			if(log.isTraceEnabled()){
				log.trace("Service %s was unregistered !",clazz);
			}
			synchronized(AbstractModule.this){
				if(pendingServices == null) {
					pendingServices = new HashSet<Class<?>>();
				}
				pendingServices.add(clazz);
			}
			doStopService();
		}
		
		@Override
		public <S> void serviceRegistered(Class<S> clazz, S handler) {
			if(log.isTraceEnabled()){
				log.trace("Service %s was registered !",clazz);
			}
			boolean start = false;
			synchronized(AbstractModule.this){
				if(pendingServices != null) {
					pendingServices.remove(clazz);
					if(pendingServices.size() == 0){
						if(log.isDebugEnabled()){
							log.debug("No more pending services,going to start service ...");
						}
						start = true;
					}
				}
			}
			if(start){
				doStartService();
			}
		}
		
		@Override
		public boolean accepts(Class<?> clazz) {
			synchronized(AbstractModule.this){
				return requiredServices != null && requiredServices.contains(clazz);
			}
		}
	};
	
	protected void doStopService() {
		if(started.compareAndSet(true,false)){
			if(log.isInfoEnabled()){
				log.info("Going to stop service in module %s ...",this);
			}
			try {
				stopService();
			}catch(Throwable t){
				log.warn("Caught throwable when try to stop service in module %s", this, t);
			}
		}
	}
	
	protected void doStartService() {
		if(started.compareAndSet(false,true)){
			if(log.isInfoEnabled()){
				log.info("Going to start service in module %s ...",this);
			}
			try {
				startService();
			}catch(Throwable t){
				log.error("Caught throwable when try to start service in module %s", this, t);
			}
		}
	}
	
	protected synchronized void initPendingServices() {
		this.context.addKernelServiceListener(serviceListener);
		if(this.requiredServices != null){
			for (Class<?> clazz : this.requiredServices) {
				Object h = this.context.getService(clazz);
				if(h == null){
					if(this.pendingServices == null){
						this.pendingServices = new HashSet<Class<?>>();
					}
					this.pendingServices.add(clazz);
				}
			}
		}
	}
	
	protected synchronized void clearPendingServices() {
		this.context.removeKernelServiceListener(serviceListener);
		if(this.pendingServices != null){
			this.pendingServices.clear();
			this.pendingServices = null;
		}
		if(this.requiredServices != null){
			this.requiredServices.clear();
			this.requiredServices = null;
		}
	}

	protected <S> S getService(Class<S> clazz) {
		return this.context.getService(clazz);
	}
	
	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.IKernelModule#start(com.wxxr.mobile.core.microkernel.api.IKernelContext)
	 */
	@Override
	public void start(T ctx) {
		this.context = ctx;
		if(!initted.get()){
			initServiceDependency();
			initPendingServices();
			initted.set(true);
		}
		boolean start = false;
		synchronized(this){
			start = this.pendingServices == null || this.pendingServices.isEmpty();
		}
		if(start){
			doStartService();
		}
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.IKernelModule#stop()
	 */
	@Override
	public void stop() {
		doStopService();
		clearPendingServices();
		initted.set(false);
	}
	
	protected abstract void initServiceDependency();
	
	protected abstract void startService();
	
	protected abstract void stopService();

	
	protected synchronized void addRequiredService(Class<?> serviceInterface){
		if(serviceInterface == null){
			throw new IllegalArgumentException("Invalid service interface : NULL");
		}
		if(initted.get()){
			throw new IllegalStateException("module has been started, it's illegal to add new service dependency !");
		}
		if(this.requiredServices == null){
			this.requiredServices = new HashSet<Class<?>>();
		}
		this.requiredServices.add(serviceInterface);
	}
	
	protected synchronized boolean removeRequiredService(Class<?> serviceInterface){
		if(initted.get()){
			throw new IllegalStateException("module has been started, it's illegal to remove service dependency !");
		}
		if(this.requiredServices != null){
			return this.requiredServices.remove(serviceInterface);
		}
		return false;
	}


}
