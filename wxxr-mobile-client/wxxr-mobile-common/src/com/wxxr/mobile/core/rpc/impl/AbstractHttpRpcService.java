/**
 * 
 */
package com.wxxr.mobile.core.rpc.impl;

import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;

import com.wxxr.mobile.core.api.IApplication;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.IServiceAvailableCallback;
import com.wxxr.mobile.core.rpc.api.Request;
import com.wxxr.mobile.core.rpc.http.api.HttpMethod;
import com.wxxr.mobile.core.rpc.http.api.HttpResponse;
import com.wxxr.mobile.core.rpc.http.api.HttpRpcService;
import com.wxxr.mobile.core.security.api.ISiteSecurityService;

/**
 * @author neillin
 *
 */
public class AbstractHttpRpcService implements HttpRpcService, IHttpClientContext {
	
	private static final Trace log = Trace.register(AbstractHttpRpcService.class);

	private HttpClient httpClient;
	private IApplication appContext;

   protected boolean disableTrustManager;
   protected int connectionPoolSize;
   protected int maxPooledPerRoute = 0;
   protected long connectionTTL = -1;



	   protected void initHttpEngine(ISiteSecurityService securityService)
	   {
	      final HostnameVerifier verifier = securityService != null ? securityService.getHostnameVerifier() : null;
	      X509HostnameVerifier x509verifier = null;
	      if (verifier != null)  {
	    	  	x509verifier = new X509HostnameVerifier() {

	    	      @Override
	    	      public void verify(String host, SSLSocket ssl) throws IOException
	    	      {
	    	         if (!verifier.verify(host, ssl.getSession())) throw new SSLException("Hostname verification failure");
	    	      }

	    	      @Override
	    	      public void verify(String host, X509Certificate cert) throws SSLException
	    	      {
	    	         throw new SSLException("This verification path not implemented");
	    	      }

	    	      @Override
	    	      public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException
	    	      {
	    	         throw new SSLException("This verification path not implemented");
	    	      }

	    	      @Override
	    	      public boolean verify(String s, SSLSession sslSession)
	    	      {
	    	         return verifier.verify(s, sslSession);
	    	      }
	    	   };	      
	      }
	      else
	      {
	           x509verifier = new AllowAllHostnameVerifier();
	      }
	      try
	      {
			  KeyStore clientKeyStore = null;
			  KeyStore truststore = null;
			  clientKeyStore = securityService != null ? securityService.getSiteKeyStore() : null;
			  truststore = securityService != null ? securityService.getTrustKeyStore() : null;
	         SSLSocketFactory sslsf = null;
	         if (clientKeyStore != null || truststore != null)
	         {
	            sslsf = new SSLSocketFactory(clientKeyStore, null, truststore);
	            sslsf.setHostnameVerifier(x509verifier);
	         }
	         else
	         {
	            sslsf = SSLSocketFactory.getSocketFactory();
	            sslsf.setHostnameVerifier(x509verifier);
	         }
	         SchemeRegistry registry = new SchemeRegistry();
	         registry.register(
	                 new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	         Scheme httpsScheme = new Scheme("https", sslsf, 443);
	         registry.register(httpsScheme);
	         ClientConnectionManager cm = null;
	         if (connectionPoolSize > 0)
	         {
	        	BasicHttpParams params = new BasicHttpParams();
	        	ConnManagerParams.setMaxTotalConnections(params, connectionPoolSize);
	        	ConnManagerParams.setTimeout(params, connectionTTL);
	            if (maxPooledPerRoute == 0) maxPooledPerRoute = connectionPoolSize;
	            ThreadSafeClientConnManager tcm = new ThreadSafeClientConnManager(params,registry);
	            cm = tcm;

	         }
	         else
	         {
	            cm = new SingleClientConnManager(new BasicHttpParams(),registry);
	         }
	         this.httpClient = new DefaultHttpClient(cm, new BasicHttpParams());
	      }
	      catch (Exception e)
	      {
	         throw new RuntimeException(e);
	      }
	   }



		@Override
		public ExecutorService getExecutor() {
			return this.appContext.getExecutor();
		}

		@Override
		public Request createRequest(String endpointUrl, Map<String, Object> params) {
			return new HttpRequestImpl(this, endpointUrl, params);
		}
		
		  protected HttpRequestBase createHttpMethod(String url, Map<String, Object> params)
		   {
			   HttpMethod method = (HttpMethod)params.get(ParamConstants.PARAMETER_KEY_HTTP_METHOD);
			   switch(method){
			   case GET:
				   HttpGet m = new HttpGet(url);
				   HttpClientParams.setRedirecting(m.getParams(), true);
				   return m;
			   case POST:
				   return new HttpPost(url);
			   default:
			         final String verb = method.name();
			         return new HttpPost(url)
			         {
			            @Override
			            public String getMethod()
			            {
			               return verb;
			            }
			         };
				   
			   }
		   }


		   public HttpResponse invoke(HttpRequestBase request) throws Exception 
		   {
		         return new HttpResponseImpl(httpClient.execute(request));
		   }

		/**
		 * @return the disableTrustManager
		 */
		public boolean isDisableTrustManager() {
			return disableTrustManager;
		}


		/**
		 * @return the connectionPoolSize
		 */
		public int getConnectionPoolSize() {
			return connectionPoolSize;
		}

		/**
		 * @return the maxPooledPerRoute
		 */
		public int getMaxPooledPerRoute() {
			return maxPooledPerRoute;
		}

		/**
		 * @return the connectionTTL
		 */
		public long getConnectionTTL() {
			return connectionTTL;
		}

		/**
		 * @param disableTrustManager the disableTrustManager to set
		 */
		public void setDisableTrustManager(boolean disableTrustManager) {
			this.disableTrustManager = disableTrustManager;
		}

		/**
		 * @param connectionPoolSize the connectionPoolSize to set
		 */
		public void setConnectionPoolSize(int connectionPoolSize) {
			this.connectionPoolSize = connectionPoolSize;
		}

		/**
		 * @param connectionTTL the connectionTTL to set
		 */
		public void setConnectionTTL(long connectionTTL) {
			this.connectionTTL = connectionTTL;
		}


		protected void startup(IApplication ctx) {
			this.appContext = ctx;
			ctx.checkServiceAvailable(ISiteSecurityService.class, new IServiceAvailableCallback<ISiteSecurityService>() {

				@Override
				public void serviceAvailable(ISiteSecurityService service) {
					try {
						initHttpEngine(service);
						appContext.registerService(HttpRpcService.class, AbstractHttpRpcService.this);
					} catch (Throwable e) {
						log.error("Failed to initialize http client",e);
					}
				}
			});
		}



		public void shutdown() {
			appContext.unregisterService(HttpRpcService.class, this);
			if(this.httpClient != null){
				this.httpClient.getConnectionManager().shutdown();
				this.httpClient = null;
			}
		}

}
