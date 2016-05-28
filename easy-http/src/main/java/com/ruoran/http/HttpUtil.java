package com.ruoran.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

/**
 * @author ruoran
 * @email jinsefeng@163.com
 * @date 2016-05-29
 * @version 1.0
 */
public class HttpUtil
{
	protected static Logger logger = Logger.getLogger(EasyHTTP.class);
	
	private static final Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*(?:\"|')?([^\\s,;\"']*)");
	private static final char[] mimeBoundaryChars = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	private static final int boundaryLength = 32;
	private static final int bufferSize = 0x20000; // ~130K.
	
	static String mimeBoundary()
	{
		final StringBuilder mime = new StringBuilder(boundaryLength);
		final Random rand = new Random();
		for (int i = 0; i < boundaryLength; i++)
		{
			mime.append(mimeBoundaryChars[rand.nextInt(mimeBoundaryChars.length)]);
		}
		return mime.toString();
	}
	
	static String encodeUrl(String url)
	{
		if (url == null) return null;
		return url.replaceAll(" ", "%20");
	}
	
	static String encodeMimeName(String val)
	{
		if (val == null) return null;
		return val.replaceAll("\"", "%22");
	}
	
	static void crossStreams(final InputStream in, final OutputStream out) throws IOException
	{
		final byte[] buffer = new byte[bufferSize];
		int len;
		while ((len = in.read(buffer)) != -1)
		{
			out.write(buffer, 0, len);
		}
	}
	
	static String getCharsetFromContentType(String contentType)
	{
		if (contentType == null) return null;
		Matcher m = charsetPattern.matcher(contentType);
		if (m.find())
		{
			String charset = m.group(1).trim();
			charset = charset.replace("charset=", "");
			return validateCharset(charset);
		}
		return null;
	}
	
	private static String validateCharset(String cs)
	{
		if (cs == null || cs.length() == 0) return null;
		cs = cs.trim().replaceAll("[\"']", "");
		try
		{
			if (Charset.isSupported(cs)) return cs;
			cs = cs.toUpperCase(Locale.ENGLISH);
			if (Charset.isSupported(cs)) return cs;
		}
		catch (IllegalCharsetNameException e)
		{
			logger.error("", e);
		}
		return null;
	}
	
	static byte[] readDataFromResponse(InputStream inputStream)
	{
		ByteArrayOutputStream baos = null;
		byte[] data = null;
		try
		{
			byte[] buffer = new byte[1024];
			baos = new ByteArrayOutputStream();
			int len = 0;
			while ((len = inputStream.read(buffer)) != -1)
			{
				baos.write(buffer, 0, len);
			}
			data = baos.toByteArray();
			
		}
		catch (IOException e)
		{
			logger.error("", e);
		}
		finally
		{
			try
			{
				if (null != inputStream) inputStream.close();
				if (null != baos) baos.close();
			}
			catch (IOException e)
			{
				logger.error("", e);
			}
		}
		return data;
	}
	
	public static byte[] unGzip(byte[] buf)
	{
		try
		{
			GZIPInputStream gzi = null;
			ByteArrayOutputStream bos = null;
			try
			{
				gzi = new GZIPInputStream(new ByteArrayInputStream(buf));
				bos = new ByteArrayOutputStream(buf.length);
				int count = 0;
				byte[] tmp = new byte[2048];
				while ((count = gzi.read(tmp)) != -1)
				{
					bos.write(tmp, 0, count);
				}
				buf = bos.toByteArray();
			}
			finally
			{
				if (bos != null)
				{
					bos.flush();
					bos.close();
				}
				if (gzi != null) gzi.close();
			}
			return buf;
		}
		catch (IOException e)
		{
			logger.error("", e);
		}
		return null;
	}
	
	public static byte[] gzip(byte[] val) throws IOException
	{
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream(val.length);
			GZIPOutputStream gos = null;
			try
			{
				gos = new GZIPOutputStream(bos);
				gos.write(val, 0, val.length);
				gos.finish();
				gos.flush();
				bos.flush();
				val = bos.toByteArray();
			}
			finally
			{
				if (gos != null) gos.close();
				if (bos != null) bos.close();
			}
			return val;
		}
		catch (Exception e)
		{
			logger.error("", e);
		}
		return null;
	}
	
	public static String removeHtmlTag(String content)
	{
		content = content.replaceAll("</?[^<]+>", "");
		content = content.replaceAll("\\s+|\t|\r|\n", " ");
		content = content.replaceAll("&nbsp;", "");
		return content;
	}
	
	public static URL resolve(URL base, String relUrl) throws MalformedURLException
	{
		if (relUrl.startsWith("?")) relUrl = base.getPath() + relUrl;
		if (relUrl.indexOf('.') == 0 && base.getFile().indexOf('/') != 0)
		{
			base = new URL(base.getProtocol(), base.getHost(), base.getPort(), "/" + base.getFile());
		}
		return new URL(base, relUrl);
	}
}
