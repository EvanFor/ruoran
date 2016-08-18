package com.ruoran.http;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

import com.ruoran.http.EasyRequest.Pair;

/**
 * @author ruoran
 * @email jinsefeng@163.com
 * @date 2016-05-29
 * @version 1.0
 */
public class EasyHTTP
{
	protected static Logger logger = Logger.getLogger(EasyHTTP.class);
	
	public static final int DEFAULT_TIME_OUT = 60 * 1000;
	public static final String DEFAULT_CHARSET = "UTF-8";
	public static final String APPLICATION_JSON = "application/json;charset=" + DEFAULT_CHARSET;
	public static final String APPLICATION_JAVASCRIPT = "application/javascript;charset=" + DEFAULT_CHARSET;
	public static final String TEXT_PLAIN = "text/plain;charset=" + DEFAULT_CHARSET;
	public static final String TEXT_XML = "text/xml;charset=" + DEFAULT_CHARSET;
	public static final String TEXT_HTML = "text/html;charset=" + DEFAULT_CHARSET;
	
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String MULTIPART_FORM_DATA = "multipart/form-data";
	private static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";
	
	protected static SSLSocketFactory sslSocketFactory;
	protected static final String LOCATION = "Location";
	private static final int HTTP_TEMP_REDIR = 307;
	
	enum RequestMethod
	{
		GET(false), POST(true), PUT(true), DELETE(false), PATCH(true), HEAD(false), OPTIONS(false), TRACE(false);
		
		private final boolean hasBody;
		
		RequestMethod(boolean hasBody)
		{
			this.hasBody = hasBody;
		}
		
		public final boolean hasBody()
		{
			return hasBody;
		}
	}
	
	private EasyHTTP()
	{
		
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static EasyResponse httpGet(EasyRequest request) throws Exception
	{
		return execute(request.method(RequestMethod.GET), EasyResponse.create(request));
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static EasyResponse httpPost(EasyRequest request) throws Exception
	{
		return execute(request.method(RequestMethod.POST), EasyResponse.create(request));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private static EasyResponse execute(EasyRequest request, EasyResponse response) throws Exception
	{
		String protocol = request.url().getProtocol();
		if (!protocol.equals("http") && !protocol.equals("https"))
		{
			throw new MalformedURLException("仅支持http和https协议");
		}
		
		final boolean methodHasBody = request.method().hasBody();
		final boolean hasRequestBody = request.requestBody() != null;
		if (!methodHasBody)
		{
			if (hasRequestBody) throw new IllegalArgumentException("不能为http方法:" + request.method() + "设置body");
		}
		CookieManager manager = new CookieManager();
		manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieHandler.setDefault(manager);
		
		String mimeBoundary = null;
		if (request.data().size() > 0 && !methodHasBody)
		{
			rebuildRequestUrl(request);
		}
		else if (methodHasBody)
		{
			mimeBoundary = setOutputContentType(request);
		}
		
		HttpURLConnection conn = createConnection(request);
		try
		{
			conn.connect();
			if (conn.getDoOutput())
			{
				writeData(request, conn.getOutputStream(), mimeBoundary);
			}
			
			int status = conn.getResponseCode();
			int contentLength = conn.getContentLength();
			String contentType = conn.getContentType();
			String charset = HttpUtil.getCharsetFromContentType(contentType);
			String responseMessage = conn.getResponseMessage();
			
			response.statusCode(status);
			response.contentType(contentType);
			response.charset(charset);
			response.contentLength(contentLength);
			response.statusMessage(responseMessage);
			response.headers(conn.getHeaderFields());
			response.cookies(manager.getCookieStore().getCookies());
			
			String location = response.header(LOCATION);
			if (location != null && request.followRedirects())
			{
				if (status != HTTP_TEMP_REDIR)
				{
					request.method(RequestMethod.GET);
					request.data().clear();
				}
				
				if (location != null && location.startsWith("http:/") && location.charAt(6) != '/')
				{
					location = location.substring(6);
				}
				
				request.url(HttpUtil.resolve(request.url(), HttpUtil.encodeUrl(location)));
				for (Map.Entry<String, String> cookie : response.cookiesMap().entrySet())
				{
					request.cookie(cookie.getKey(), cookie.getValue());
				}
				return execute(request, response);
			}
			
			if ((status < 200 || status >= 400) && !request.ignoreHttpErrors())
			{
				throw new RuntimeException("遇到异常,详情如下: status=" + status + "; message=" + responseMessage + "; url=" + request.url());
			}
			
			if ( request.method() != RequestMethod.HEAD)
			{
				InputStream bodyStream = conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream();
				if (response.contentType() == null)
				{
					response.contentType(URLConnection.guessContentTypeFromStream(bodyStream));
				}
				response.data(HttpUtil.readDataFromResponse(bodyStream));
			}
			 
		}
		catch (Exception e)
		{
			logger.error("请求出错", e);
		}
		finally
		{
			conn.disconnect();
		}
		
		return response.executed(true);
	}
	
	private static void writeData(final EasyRequest request, final OutputStream outputStream, final String bound) throws IOException
	{
		logger.debug("request的参数发送优先级:inputStream > requestBody > data ; 请注意优先级进行参数调整!");
		
		final Collection<Pair> data = request.data();
		final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, request.postDataCharset()));
		if (bound != null)
		{
			if (request.requestBody() != null)
			{
				logger.warn("请求已经包含inputStream参数,requestBody已经被忽略发送!");
			}
			
			// boundary will be set if we're in multipart mode
			for (Pair pair : data)
			{
				writer.write("--");
				writer.write(bound);
				writer.write("\r\n");
				writer.write("Content-Disposition: form-data; name=\"");
				writer.write(HttpUtil.encodeMimeName(pair.key()));
				writer.write("\"");
				if (pair.hasInputStream())
				{
					writer.write("; filename=\"");
					writer.write(HttpUtil.encodeMimeName(pair.value()));
					writer.write("\"\r\nContent-Type: application/octet-stream\r\n\r\n");
					writer.flush();
					HttpUtil.crossStreams(pair.inputStream(), outputStream);
					outputStream.flush();
				}
				else
				{
					writer.write("\r\n\r\n");
					writer.write(pair.value());
				}
				writer.write("\r\n");
			}
			writer.write("--");
			writer.write(bound);
			writer.write("--");
		}
		else if (request.requestBody() != null)
		{
			// data will be in query string, we're sending a plaintext body
			writer.write(request.requestBody());
		}
		else
		{
			boolean first = true;
			for (Pair keyVal : data)
			{
				if (!first) writer.append('&');
				else first = false;
				writer.write(URLEncoder.encode(keyVal.key(), request.postDataCharset()));
				writer.write('=');
				writer.write(URLEncoder.encode(keyVal.value(), request.postDataCharset()));
			}
		}
		writer.close();
	}
	
	private static HttpURLConnection createConnection(EasyRequest request) throws IOException
	{
		final HttpURLConnection conn = (HttpURLConnection) (request.proxy() == null ? request.url().openConnection() : request.url().openConnection(request.proxy()));
		conn.setRequestMethod(request.method().name());
		conn.setInstanceFollowRedirects(false);
		conn.setConnectTimeout(request.timeout());
		conn.setReadTimeout(request.timeout());
		conn.setUseCaches(request.useCaches());
		
		if (!request.validateTLSCertificates())
		{
			initUnSecureTSL();
			((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
			((HttpsURLConnection) conn).setHostnameVerifier(getInsecureVerifier());
		}
		
		if (request.method().hasBody()) conn.setDoOutput(true);
		if (request.cookies().size() > 0) conn.addRequestProperty("Cookie", request.cookiesString());
		for (Map.Entry<String, String> header : request.headers().entrySet())
		{
			conn.addRequestProperty(header.getKey(), header.getValue());
		}
		return conn;
	}
	
	private static HostnameVerifier getInsecureVerifier()
	{
		return new HostnameVerifier()
		{
			public boolean verify(String urlHostName, SSLSession session)
			{
				return true;
			}
		};
	}
	
	private static synchronized void initUnSecureTSL() throws IOException
	{
		if (sslSocketFactory == null)
		{
			final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()
			{
				public void checkClientTrusted(final X509Certificate[] chain, final String authType)
				{
				}
				
				public void checkServerTrusted(final X509Certificate[] chain, final String authType)
				{
				}
				
				public X509Certificate[] getAcceptedIssuers()
				{
					return null;
				}
			} };
			
			final SSLContext sslContext;
			try
			{
				sslContext = SSLContext.getInstance("SSL");
				sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
				sslSocketFactory = sslContext.getSocketFactory();
			}
			catch (NoSuchAlgorithmException e)
			{
				throw new IOException("Can't create unsecure trust manager");
			}
			catch (KeyManagementException e)
			{
				throw new IOException("Can't create unsecure trust manager");
			}
		}
	}
	
	private static void rebuildRequestUrl(EasyRequest request) throws IOException
	{
		URL in = request.url();
		StringBuilder url = new StringBuilder();
		boolean first = true;
		url.append(in.getProtocol()).append("://").append(in.getAuthority()).append(in.getPath()).append("?");
		if (in.getQuery() != null)
		{
			url.append(in.getQuery());
			first = false;
		}
		
		for (Pair pair : request.data())
		{
			if (pair.hasInputStream())
			{
				logger.warn("get请求将忽略流式参数!");
			}
			
			if (!first) url.append('&');
			else first = false;
			url.append(URLEncoder.encode(pair.key(), DEFAULT_CHARSET)).append('=').append(URLEncoder.encode(pair.value(), DEFAULT_CHARSET));
		}
		request.url(url.toString());
		request.data().clear();
	}
	
	private static String setOutputContentType(final EasyRequest request)
	{
		String bound = null;
		if (needsMultipart(request))
		{
			bound = HttpUtil.mimeBoundary();
			request.header(CONTENT_TYPE, MULTIPART_FORM_DATA + "; boundary=" + bound);
		}
		else
		{
			if (request.header(CONTENT_TYPE) == null)
			{
				request.header(CONTENT_TYPE, FORM_URL_ENCODED + "; charset=" + request.postDataCharset());
			}
		}
		return bound;
	}
	
	private static boolean needsMultipart(EasyRequest request)
	{
		boolean needsMulti = false;
		for (Pair pair : request.data())
		{
			if (pair.hasInputStream())
			{
				needsMulti = true;
				break;
			}
		}
		return needsMulti;
	}
}
