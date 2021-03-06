/**
 * 
 */
package com.wxxr.mobile.core.rpc.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.ProcessingException;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHeader;

import com.wxxr.mobile.core.rpc.api.DataEntity;
import com.wxxr.mobile.core.rpc.api.RequestCallback;
import com.wxxr.mobile.core.rpc.api.Response;
import com.wxxr.mobile.core.rpc.http.api.HttpMethod;
import com.wxxr.mobile.core.rpc.http.api.HttpRequest;
import com.wxxr.mobile.core.rpc.http.api.HttpResponse;
/**
 * @author neillin
 *
 */
public class HttpRequestImpl implements HttpRequest {

	private IHttpClientContext clientContext;
	private Map<String,String> headers = new HashMap<String,String>();
	private DataEntity dataEntity;
	private String targetUrl;
	private Map<String, Object> httpParams;
	
	
	public HttpRequestImpl(IHttpClientContext ctx, String url, Map<String, Object> params){
		this.clientContext = ctx;
		this.targetUrl = url;
		this.httpParams = params;
	}
	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.rpc.api.Request#submit(long, java.util.concurrent.TimeUnit, com.wxxr.mobile.core.rpc.api.RequestCallback)
	 */
	@Override
	public void invokeAsync(final long timeout, final TimeUnit unit, final RequestCallback callback) {
		clientContext.getExecutor().execute(new Runnable() {
			
			@Override
			public void run() {
				Future<HttpResponse> future = clientContext.getExecutor().submit(new Callable<HttpResponse>() {

					@Override
					public HttpResponse call() throws Exception {
						return clientContext.invoke(loadHttpMethod(createHttpMethod(targetUrl, httpParams)));
					}
				});
				try {
					HttpResponse resp = null;
					if(timeout > 0L) {
						resp = future.get(timeout, unit);
					}else{
						resp = future.get();
					}
					if(callback != null){
						callback.onResponseReceived(HttpRequestImpl.this, resp);
					}
				} catch (Throwable e) {
					if(e instanceof TimeoutException){
						future.cancel(true);
					}
					if(callback != null){
						callback.onError(HttpRequestImpl.this, e);
					}
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.rpc.api.Request#setRequestEntity(com.wxxr.mobile.core.rpc.api.DataEntity)
	 */
	@Override
	public void setRequestEntity(DataEntity entity) {
		this.dataEntity = entity;
	}

	
	@Override
	public DataEntity getRequestEntity() {
		return this.dataEntity;
	}
	
	@Override
	public Map<String, String> getHeaders() {
		return Collections.unmodifiableMap(this.headers);
	}
	
	
	@Override
	public String getHeader(String key) {
		return this.headers.get(key);
	}
	
	
	@Override
	public void setHeader(String key, String value) {
		this.headers.put(key, value);
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


	   protected HttpRequestBase loadHttpMethod(final HttpRequestBase httpMethod) throws Exception
	   {
	      if (httpMethod instanceof HttpGet) // todo  && request.followRedirects())
	      {
	         HttpClientParams.setRedirecting(httpMethod.getParams(), true);
	      }
	      else
	      {
	         HttpClientParams.setRedirecting(httpMethod.getParams(), false);
	      }

	      if (this.dataEntity != null)
	      {
	         if (httpMethod instanceof HttpGet) {
	        	 throw new ProcessingException("A GET request cannot have a body.");
	         }
        	 final DataEntity entity = this.dataEntity;
             HttpPost post = (HttpPost) httpMethod;
             commitHeaders(httpMethod);
             post.setEntity(new InputStreamEntity(entity.getContent(), entity.getContentLength()){
            	
            	@Override
                public boolean isRepeatable() {
                    return false;
                }
            	
                @Override
                public Header getContentType()
                {
                   return new BasicHeader("Content-Type", entity.getContentType());
                }


            });
	      } else { // no body
	         commitHeaders(httpMethod);
	      }
	      return httpMethod;
	   }

	   protected void commitHeaders(HttpRequestBase httpMethod)
	   {
	      for (Map.Entry<String, String> header : headers.entrySet())
	      {
	         String value = header.getValue();
	         httpMethod.addHeader(header.getKey(), value);
	      }
	   }
	@Override
	public Response invoke(long timeout, TimeUnit unit) throws Exception {
		if(timeout <= 0L){
			return clientContext.invoke(loadHttpMethod(createHttpMethod(targetUrl, httpParams)));
		}else{
			Future<HttpResponse> future = clientContext.getExecutor().submit(new Callable<HttpResponse>() {

				@Override
				public HttpResponse call() throws Exception {
					return clientContext.invoke(loadHttpMethod(createHttpMethod(targetUrl, httpParams)));
				}
			});
			return future.get(timeout, unit);
		}
	}
}
