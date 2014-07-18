/**
 * 
 */
package com.wxxr.callhelper.qg.rpc;

import com.wxxr.mobile.core.api.IProgressMonitor;
import com.wxxr.mobile.core.log.api.Trace;

/**
 * @author neillin
 *
 */
public abstract class AbstractMonitorRunnable implements Runnable {

	private final IProgressMonitor monitor;
	private final Trace log;
	
	public AbstractMonitorRunnable(IProgressMonitor m, Trace log){
		this.monitor = m;
		this.log = log;
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		if(monitor != null){
			monitor.beginTask(-1);
		}
		try {
			Object ret = executeTask();
		if(monitor != null){
			monitor.done(ret);
		}
		}catch(Throwable t){
			if(log != null){
				log.warn("Failed to require user activation password", t);
			}
			if(monitor != null){
				monitor.taskFailed(t);
			}
		}		
	}

	
	protected abstract Object executeTask() throws Throwable;
}
