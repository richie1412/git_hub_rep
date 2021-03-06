package com.wxxr.mobile.core.rpc.rest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;

import com.wxxr.mobile.core.rpc.api.DataEntity;
import com.wxxr.mobile.core.rpc.http.api.HttpRequest;
import com.wxxr.mobile.core.rpc.http.api.HttpResponse;
import com.wxxr.mobile.core.rpc.http.api.HttpRpcService;
import com.wxxr.mobile.core.rpc.impl.ParamConstants;
import com.wxxr.mobile.core.util.CaseInsensitiveMap;
import com.wxxr.mobile.core.util.SelfExpandingBufferredInputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1.1 $
 */
public class HttpRpcClientEngine implements ClientHttpEngine
{
   protected final HttpRpcService httpClient;


   public HttpRpcClientEngine(HttpRpcService service)
   {
      this.httpClient = service;
   }


   public ClientResponse invoke(ClientInvocation request)
   {
      String uri = request.getUri().toString();
      Map<String, Object> params = new HashMap<String, Object>();
      params.put(ParamConstants.PARAMETER_KEY_HTTP_METHOD, request.getMethod());
      final HttpRequest httpMethod = (HttpRequest)this.httpClient.createRequest(uri, params);
      final HttpResponse res;
      try
      {
         loadHttpMethod(request, httpMethod);

         res = (HttpResponse)httpMethod.invoke(0L, TimeUnit.SECONDS);
      }
      catch (Exception e)
      {
         throw new ProcessingException("Unable to invoke request", e);
      }

      ClientResponse response = new ClientResponse(request.getClientConfiguration())
      {
         InputStream stream;
         InputStream hc4Stream;

         @Override
         protected void setInputStream(InputStream is)
         {
            stream = is;
         }

         public InputStream getInputStream()
         {
            if (stream == null)
            {
               DataEntity entity = res.getResponseEntity();
               if (entity == null) return null;
               try
               {
                  hc4Stream = entity.getContent();
                  stream = new SelfExpandingBufferredInputStream(hc4Stream);
               }
               catch (IOException e)
               {
                  throw new RuntimeException(e);
               }
            }
            return stream;
         }

         public void releaseConnection()
         {
            isClosed = true;
            // Apache Client 4 is stupid,  You have to get the InputStream and close it if there is an entity
            // otherwise the connection is never released.  There is, of course, no close() method on response
            // to make this easier.
            try
            {
               if (stream != null)
               {
                  stream.close();
               }
               else
               {
                  InputStream is = getInputStream();
                  if (is != null)
                  {
                     is.close();
                  }
               }
               if (hc4Stream != null)
               {
                  // just in case the input stream was entirely replaced and not wrapped, we need
                  // to close the apache client input stream.
                  hc4Stream.close();
               }
            }
            catch (Exception ignore)
            {
            }
         }
      };
      response.setProperties(request.getMutableProperties());
      response.setStatus(res.getStatusCode());
      response.setHeaders(extractHeaders(res));
      response.setClientConfiguration(request.getClientConfiguration());
      return response;
   }

   protected HttpRequestBase createHttpMethod(String url, String restVerb)
   {
      if ("GET".equals(restVerb))
      {
         return new HttpGet(url);
      }
      else if ("POST".equals(restVerb))
      {
         return new HttpPost(url);
      }
      else
      {
         final String verb = restVerb;
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

   protected void loadHttpMethod(final ClientInvocation request, HttpRequest httpMethod) throws Exception
   {

      if (request.getEntity() != null)
      {

         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         request.getDelegatingOutputStream().setDelegate(baos);
         try
         {
            request.writeRequestBody(request.getEntityStream());
            final ByteArrayEntity entity = new ByteArrayEntity(baos.toByteArray())
            {
               @Override
               public org.apache.http.Header getContentType()
               {
                  return new BasicHeader("Content-Type", request.getHeaders().getMediaType().toString());
               }
            };
            commitHeaders(request, httpMethod);
            httpMethod.setRequestEntity(new DataEntity() {
				
				@Override
				public boolean isRepeatable() {
					return entity.isRepeatable();
				}
				
				@Override
				public String getContentType() {
					return entity.getContentType().getValue();
				}
				
				@Override
				public long getContentLength() {
					return entity.getContentLength();
				}
				
				@Override
				public InputStream getContent() throws IOException {
					return entity.getContent();
				}
			});
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
      else // no body
      {
         commitHeaders(request, httpMethod);
      }
   }
   
   protected  CaseInsensitiveMap<String> extractHeaders(
           HttpResponse response)
   {
      final CaseInsensitiveMap<String> headers = new CaseInsensitiveMap<String>();
      Map<String,String> map = response.getHeaders();
      for (Entry<String,String> header : map.entrySet())
      {
         headers.putSingle(header.getKey(), header.getValue());
      }
      return headers;
   }


   protected void commitHeaders(ClientInvocation request, HttpRequest httpMethod)
   {
      MultivaluedMap<String, String> headers = request.getHeaders().asMap();
      for (Map.Entry<String, List<String>> header : headers.entrySet())
      {
         List<String> values = header.getValue();
         String val = null;
         if((values != null)&&(values.size() > 0)){
        	 StringBuffer buf = new StringBuffer();
        	 int cnt = 0;
        	 for (String s : values) {
        		 if(cnt > 0){
        			 buf.append(',');
        		 }
				buf.append(s);
				cnt++;
			}
        	val = buf.toString();
         }
         httpMethod.setHeader(header.getKey(), val);
      }
   }

   public void close()
   {
   }

   public boolean isClosed()
   {
      return false;
   }
}