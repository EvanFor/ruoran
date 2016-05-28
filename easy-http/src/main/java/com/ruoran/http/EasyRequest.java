package com.ruoran.http;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.ruoran.http.EasyHTTP.RequestMethod;

/**
 * @author ruoran
 * @email jinsefeng@163.com
 * @date 2016-05-29
 * @version 1.0
 */
public class EasyRequest
{
	private String id;
	private URL url;
	private Proxy proxy;
	private String body;
	private boolean useCaches = false;
	private boolean followRedirects = true;
	private boolean ignoreHttpErrors = false;
	private boolean validateTSLCertificates = true;
	private RequestMethod method = RequestMethod.GET;
	private int timeoutMilliseconds = EasyHTTP.DEFAULT_TIME_OUT;
	private Collection<Pair> data = new ArrayList<Pair>();
	private String postDataCharset = EasyHTTP.DEFAULT_CHARSET;
	private Map<String, String> headers = new LinkedHashMap<>();
	private Map<String, String> cookies = new LinkedHashMap<>();
	
	static public class Pair
	{
		private String key;
		private String value;
		private InputStream stream;
		
		public static Pair create(String key, String value)
		{
			return new Pair().key(key).value(value);
		}
		
		public static Pair create(String key, String filename, InputStream stream)
		{
			return new Pair().key(key).value(filename).inputStream(stream);
		}
		
		private Pair()
		{
		}
		
		public Pair key(String key)
		{
			this.key = key;
			return this;
		}
		
		public String key()
		{
			return key;
		}
		
		public Pair value(String value)
		{
			this.value = value;
			return this;
		}
		
		public String value()
		{
			return value;
		}
		
		public Pair inputStream(InputStream inputStream)
		{
			this.stream = inputStream;
			return this;
		}
		
		public InputStream inputStream()
		{
			return stream;
		}
		
		public boolean hasInputStream()
		{
			return stream != null;
		}
		
		@Override
		public String toString()
		{
			return key + "=" + value;
		}
	}
	
	private EasyRequest(String id)
	{
		this.id = id;
		this.headers.put("Accept-Encoding", "gzip, deflate");
	}
	
	public static EasyRequest create(String url)
	{
		return new EasyRequest(UUID.randomUUID().toString()).url(url);
	}
	
	public String id()
	{
		return this.id;
	}
	
	public EasyRequest url(String url)
	{
		try
		{
			this.url = new URL(url);
			return this;
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public EasyRequest url(URL url)
	{
		this.url = url;
		return this;
	}
	
	public URL url()
	{
		return this.url;
	}
	
	public EasyRequest timeout(int timeoutMilliseconds)
	{
		this.timeoutMilliseconds = timeoutMilliseconds;
		return this;
	}
	
	public int timeout()
	{
		return this.timeoutMilliseconds;
	}
	
	public EasyRequest requestBody(String body)
	{
		this.body = body;
		return this;
	}
	
	public String requestBody()
	{
		return this.body;
	}
	
	public EasyRequest method(RequestMethod method)
	{
		this.method = method;
		return this;
	}
	
	public RequestMethod method()
	{
		return this.method;
	}
	
	public EasyRequest header(String name, String value)
	{
		this.headers.put(name, value);
		return this;
	}
	
	public Map<String, String> headers()
	{
		return this.headers;
	}
	
	public EasyRequest proxy(String host, int port)
	{
		this.proxy = new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(host, port));
		return this;
	}
	
	public Proxy proxy()
	{
		return this.proxy;
	}
	
	public EasyRequest followRedirects(boolean followRedirects)
	{
		this.followRedirects = followRedirects;
		return this;
	}
	
	public boolean followRedirects()
	{
		return followRedirects;
	}
	
	public EasyRequest ignoreHttpErrors(boolean ignoreHttpErrors)
	{
		this.ignoreHttpErrors = ignoreHttpErrors;
		return this;
	}
	
	public boolean ignoreHttpErrors()
	{
		return ignoreHttpErrors;
	}
	
	public EasyRequest postDataCharset(String charset)
	{
		if (!Charset.isSupported(charset)) throw new IllegalCharsetNameException(charset);
		this.postDataCharset = charset;
		return this;
	}
	
	public String postDataCharset()
	{
		return postDataCharset;
	}
	
	public boolean validateTLSCertificates()
	{
		return this.validateTSLCertificates;
	}
	
	public EasyRequest validateTLSCertificates(boolean value)
	{
		this.validateTSLCertificates = value;
		return this;
	}
	
	public EasyRequest cookie(String name, String value)
	{
		this.cookies.put(name, value);
		return this;
	}
	
	public EasyRequest cookies(Map<String, String> cookies)
	{
		this.cookies.putAll(cookies);
		return this;
	}
	
	public Map<String, String> cookies()
	{
		return this.cookies;
	}
	
	public String cookiesString()
	{
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, String> cookie : this.cookies().entrySet())
		{
			if (!first) sb.append("; ");
			else first = false;
			sb.append(cookie.getKey()).append('=').append(cookie.getValue());
		}
		return sb.toString();
	}
	
	public EasyRequest userAgent(String userAgent)
	{
		this.headers.put("User-Agent", userAgent);
		return this;
	}
	
	public EasyRequest contentType(String contentType)
	{
		this.headers.put("Content-Type", contentType);
		return this;
	}
	
	public EasyRequest contentLength(long contentLength)
	{
		this.headers.put("Content-Length", String.valueOf(contentLength));
		return this;
	}
	
	public EasyRequest referer(String referer)
	{
		this.headers.put("Referer", referer);
		return this;
	}
	
	public EasyRequest host(String host)
	{
		this.headers.put("Host", host);
		return this;
	}
	
	public EasyRequest accept(String accept)
	{
		this.headers.put("Accept", accept);
		return this;
	}
	
	public EasyRequest accept()
	{
		this.headers.put("Accept", "*/*");
		return this;
	}
	
	public EasyRequest acceptLanguage(String language)
	{
		this.headers.put("Accept-Language", language);
		return this;
	}
	
	public EasyRequest acceptLanguage()
	{
		this.headers.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		return this;
	}
	
	public EasyRequest noCache()
	{
		this.headers.put("Cache-Control", "max-age=0");
		return this;
	}
	
	public EasyRequest keepAlive()
	{
		this.headers.put("Connection", "keep-alive");
		return this;
	}
	
	public EasyRequest xhr()
	{
		this.headers.put("X-Requested-With", "XMLHttpRequest");
		return this;
	}
	
	/**
	 * 可以多个,逗号隔开
	 * @param x_forwarded_for
	 * @return
	 */
	public EasyRequest XFF(String x_forwarded_for)
	{
		this.headers.put("X-Forwarded-For", x_forwarded_for);
		this.headers.put("True-Client-IP", x_forwarded_for);
		return this;
	}
	
	public Collection<Pair> data()
	{
		return this.data;
	}
	
	public EasyRequest data(Pair kv)
	{
		this.data.add(kv);
		return this;
	}
	
	public EasyRequest data(String key, String val)
	{
		this.data.add(Pair.create(key, val));
		return this;
	}
	
	public EasyRequest data(Collection<Pair> data)
	{
		for (Pair entry : data)
		{
			this.data(entry);
		}
		return this;
	}
	
	public EasyRequest data(String... pairs)
	{
		if (pairs.length % 2 == 0)
		{
			for (int i = 0; i < pairs.length; i += 2)
			{
				String key = pairs[i];
				String value = pairs[i + 1];
				this.data(Pair.create(key, value));
			}
		}
		else
		{
			throw new IllegalArgumentException("param num must mod 2 = 0 ");
		}
		return this;
	}
	
	public Pair data(String key)
	{
		for (Pair pair : this.data())
		{
			if (pair.key.equals(key)) return pair;
		}
		return null;
	}
	
	public boolean useCaches()
	{
		return useCaches;
	}
	
	public void useCaches(boolean useCaches)
	{
		this.useCaches = useCaches;
	}
	
	public String header(String headerName)
	{
		return this.headers.get(headerName);
	}
}
