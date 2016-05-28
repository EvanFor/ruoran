package com.ruoran.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.log4j.Logger;

/**
 * @author ruoran
 * @email jinsefeng@163.com
 * @date 2016-05-29
 * @version 1.0
 */
public class EasyResponse
{
	protected static Logger logger = Logger.getLogger(EasyResponse.class);
	
	private String id;
	private byte[] data = null;
	private List<HttpCookie> cookies;
	private Map<String, List<String>> headers;
	private EasyRequest request;
	
	private String contentType;
	private String statusMessage;
	private int statusCode;
	private String charset;
	private boolean gizp = false;
	private boolean executed = false;
	private int contentLength;
	
	private EasyResponse(String id)
	{
		this.id = id;
	}
	
	public static EasyResponse create()
	{
		return new EasyResponse(UUID.randomUUID().toString());
	}
	
	public static EasyResponse create(EasyRequest request)
	{
		return new EasyResponse(UUID.randomUUID().toString()).from(request);
	}
	
	public String id()
	{
		return this.id;
	}
	
	public EasyRequest request()
	{
		return request;
	}
	
	public EasyResponse from(EasyRequest request)
	{
		this.request = request;
		return this;
	}
	
	public String from()
	{
		return request.url().toString();
	}
	
	public void data(byte[] data)
	{
		this.data = data;
	}
	
	public byte[] data()
	{
		return data;
	}
	
	public String stringBody()
	{
		return stringResult(EasyHTTP.DEFAULT_CHARSET);
	}
	
	public String stringResult(String charset)
	{
		try
		{
			if (this.gizp)
			{
				return new String(HttpUtil.unGzip(this.data), charset);
			}
			else
			{
				return new String(this.data, charset);
			}
		}
		catch (UnsupportedEncodingException e)
		{
			logger.error("", e);
		}
		return null;
	}
	
	public InputStream streamBody()
	{
		if (this.gizp)
		{
			return new ByteArrayInputStream(HttpUtil.unGzip(this.data));
		}
		else
		{
			return new ByteArrayInputStream(this.data);
		}
	}
	
	public Map<String, List<String>> headers()
	{
		return headers;
	}
	
	public void headers(Map<String, List<String>> headers)
	{
		this.headers = headers;
	}
	
	public void cookies(List<HttpCookie> cookies)
	{
		this.cookies = cookies;
	}
	
	public List<HttpCookie> cookies()
	{
		return cookies;
	}
	
	public Map<String, String> cookiesMap()
	{
		Map<String, String> cookieMap = new HashMap<String, String>();
		if (this.cookies != null)
		{
			for (HttpCookie cookie : cookies)
			{
				cookieMap.put(cookie.getName(), cookie.getValue());
			}
		}
		
		String headerCookie = this.header("Set-Cookie");
		if (headerCookie != null && headerCookie.trim().length() > 0)
		{
			String[] str = headerCookie.split("; ");
			for (String m : str)
			{
				if (m.contains("="))
				{
					String x[] = m.split("=");
					if (!cookieMap.containsKey(x[0]))
					{
						cookieMap.put(x[0], x[1]);
					}
				}
			}
		}
		return cookieMap;
	}
	
	public String cookiesString()
	{
		StringBuffer sb = new StringBuffer();
		if (cookiesMap().size() > 0)
		{
			for (Entry<String, String> me : cookiesMap().entrySet())
			{
				sb.append(me.getKey()).append("=").append(me.getValue()).append("; ");
			}
		}
		
		if (sb.length() > 0) sb.deleteCharAt(sb.length() - 2);
		return sb.toString();
	}
	
	private Entry<String, List<String>> scanHeaders(String name)
	{
		String lc = name.toLowerCase();
		for (Entry<String, List<String>> entry : headers.entrySet())
		{
			String key = entry.getKey();
			if (key != null && key.toLowerCase().equals(lc)) return entry;
		}
		return null;
	}
	
	public boolean hasHeaderWithValue(String name, String value)
	{
		String header = header(name);
		return (header != null) && header.equalsIgnoreCase(value);
	}
	
	public boolean hasHeader(String name)
	{
		return header(name) != null;
	}
	
	public String header(String name)
	{
		return getHeaderCaseInsensitive(name);
	}
	
	private String getHeaderCaseInsensitive(String name)
	{
		List<String> value = headers.get(name);
		if (value == null) value = headers.get(name.toLowerCase());
		if (value == null)
		{
			Entry<String, List<String>> entry = scanHeaders(name);
			if (entry != null) value = entry.getValue();
		}
		
		if (value == null) return null;
		if (value.size() == 1) return value.get(0);
		else
		{
			StringBuffer buffer = new StringBuffer();
			for (String str : value)
			{
				buffer.append(str).append("; ");
			}
			if (buffer.length() > 0) buffer.deleteCharAt(buffer.length() - 1);
			return buffer.toString();
		}
	}
	
	public EasyResponse statusCode(int statusCode)
	{
		this.statusCode = statusCode;
		return this;
	}
	
	public int statusCode()
	{
		return this.statusCode;
	}
	
	public String statusMessage()
	{
		return statusMessage;
	}
	
	public EasyResponse statusMessage(String statusMessage)
	{
		this.statusMessage = statusMessage;
		return this;
	}
	
	public String contentType()
	{
		return this.contentType;
	}
	
	public EasyResponse contentType(String contentType)
	{
		this.contentType = contentType;
		return this;
	}
	
	public String charset()
	{
		return charset;
	}
	
	public EasyResponse charset(String charset)
	{
		this.charset = charset;
		return this;
	}
	
	public boolean gzip()
	{
		return this.gizp;
	}
	
	public EasyResponse gzip(boolean gzip)
	{
		this.gizp = gzip;
		return this;
	}
	
	public boolean executed()
	{
		return this.executed;
	}
	
	public EasyResponse executed(boolean executed)
	{
		this.executed = executed;
		return this;
	}
	
	public int contentLength()
	{
		return contentLength;
	}
	
	public EasyResponse contentLength(int contentLength)
	{
		this.contentLength = contentLength;
		return this;
	}
	
}
