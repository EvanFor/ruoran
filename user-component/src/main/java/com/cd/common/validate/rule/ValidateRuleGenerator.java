package com.cd.common.validate.rule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.apache.commons.io.IOUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.cd.common.validate.ext.Future;
import com.cd.common.validate.ext.Past;

public class ValidateRuleGenerator
{
	
	private static PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(Thread.currentThread().getContextClassLoader());
	
	private static Set<Class<?>> parseClassName(String pkgName, String pkgNamePrefix)
	{
		Set<Class<?>> clazzes = new HashSet<>();
		Resource[] resources = null;
		try
		{
			resources = resolver.getResources(pkgName);
			for (Resource resource : resources)
			{
				URL url = resource.getURL();
				String filePath = url.getFile();
				String className = filePath.replace("/", ".").replace(".class", "");
				if (className.indexOf("$") < 0)
				{
					className = className.substring(className.indexOf(pkgNamePrefix));
					if (className.contains("jar!."))
					{
						className = className.substring(className.lastIndexOf("jar!.") + 5);
					}
					Class<?> clazz = null;
					try
					{
						// clazz = Class.forName(className);
						clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
					}
					catch (ClassNotFoundException e)
					{
						e.printStackTrace();
					}
					
					if (!clazz.isInterface())
					{
						clazzes.add(clazz);
					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return clazzes;
	}
	
	/**
	 * 
	 * @param pkgName
	 * @return
	 */
	private static Map<String, String> generateAsMap(String pkgName, String pkgNamePrefix)
	{
		Map<String, String> map = new LinkedHashMap<String, String>();
		Set<Class<?>> clazzes = parseClassName(pkgName, pkgNamePrefix);
		for (Class<?> clazz : clazzes)
		{
			map.put(clazz.getCanonicalName(), generateSingle(clazz));
		}
		return map;
	}
	
	private static String generateSingle(Class<?> clazz)
	{
		StringBuffer buffer = new StringBuffer("{");
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields)
		{
			if (field.getAnnotations().length > 0)
			{
				buffer.append(generateSingleField(field)).append(",");
			}
		}
		if (!buffer.toString().equals("{"))
		{
			buffer.deleteCharAt(buffer.length() - 1).append("}");
		}
		else
		{
			buffer.append("}");
		}
		return buffer.toString();
	}
	
	private static String generateSingleField(Field field)
	{
		StringBuffer buffer = new StringBuffer("\"").append(field.getName()).append("\"").append(":\"validate[");
		Annotation[] annotations = field.getAnnotations();
		SimpleDateFormat sdf = new SimpleDateFormat();
		for (Annotation annotation : annotations)
		{
			String name = annotation.annotationType().getCanonicalName();
			switch (name)
			{
				case "javax.validation.constraints.AssertFalse":
					break;
				case "javax.validation.constraints.AssertTrue":
					break;
				case "javax.validation.constraints.DecimalMax":
					DecimalMax decimalMax = (DecimalMax) annotation;
					buffer.append("max[").append(decimalMax.value()).append("]").append(",");
					break;
				case "javax.validation.constraints.DecimalMin":
					DecimalMin decimalMin = (DecimalMin) annotation;
					buffer.append("min[").append(decimalMin.value()).append("]").append(",");
					break;
				case "javax.validation.constraints.Digits":
					break;
				case "com.cd.common.validate.ext.Future":
					Future future = (Future) annotation;
					String vf = "".equals(future.value().trim()) ? sdf.format(new Date()) : future.value();
					buffer.append("future[" + vf + "]").append(",");
					break;
				case "javax.validation.constraints.Max":
					Max max = (Max) annotation;
					buffer.append("max[").append(max.value()).append("]").append(",");
					break;
				case "javax.validation.constraints.Min":
					Min min = (Min) annotation;
					buffer.append("min[").append(min.value()).append("]").append(",");
					break;
				case "javax.validation.constraints.NotNull":
					buffer.append("required").append(",");
					break;
				case "javax.validation.constraints.Null":
					break;
				case "com.cd.common.validate.ext.Past":
					Past past = (Past) annotation;
					String vp = "".equals(past.value().trim()) ? sdf.format(new Date()) : past.value();
					buffer.append("past[" + vp + "]").append(",");
					break;
				case "javax.validation.constraints.Pattern":
					
					break;
				case "javax.validation.constraints.Size":
					Size size = (Size) annotation;
					buffer.append("min[").append(size.min()).append("]").append(",");
					buffer.append("max[").append(size.max()).append("]").append(",");
					break;
				case "org.hibernate.validator.constraints.br.CNPJ":
					break;
				case "org.hibernate.validator.constraints.br.CPF":
					break;
				case "org.hibernate.validator.constraints.br.TituloEleitoral":
					break;
				case "org.hibernate.validator.constraints.CompositionType":
					break;
				case "org.hibernate.validator.constraints.ConstraintComposition":
					break;
				case "org.hibernate.validator.constraints.CreditCardNumber":
					break;
				case "org.hibernate.validator.constraints.Email":
					break;
				case "org.hibernate.validator.constraints.Length":
					Length length = (Length) annotation;
					buffer.append("min[").append(length.min()).append("]").append(",");
					buffer.append("max[").append(length.max()).append("]").append(",");
					break;
				case "org.hibernate.validator.constraints.ModCheck":
					break;
				case "org.hibernate.validator.constraints.NotBlank":
					break;
				case "org.hibernate.validator.constraints.NotEmpty":
					break;
				case "org.hibernate.validator.constraints.Range":
					Range range = (Range) annotation;
					buffer.append("min[").append(range.min()).append("]").append(",");
					buffer.append("max[").append(range.max()).append("]").append(",");
					break;
				case "org.hibernate.validator.constraints.SafeHtml":
					break;
				case "org.hibernate.validator.constraints.ScriptAssert":
					break;
				case "org.hibernate.validator.constraints.URL":
					break;
				default:
					break;
			}
		}
		
		if (buffer.charAt(buffer.length() - 1) == '[')
		{
			buffer.append("]\"");
		}
		else
		{
			buffer.deleteCharAt(buffer.length() - 1).append("]\"");
		}
		
		return buffer.toString();
	}
	
	public static String generateAsString(String pkgName, String pkgNamePrefix)
	{
		StringBuffer buffer = new StringBuffer("{");
		Map<String, String> ret = generateAsMap(pkgName, pkgNamePrefix);
		Set<Entry<String, String>> set = ret.entrySet();
		Iterator<Entry<String, String>> it = set.iterator();
		while (it.hasNext())
		{
			Entry<String, String> me = it.next();
			buffer.append("\"" + me.getKey() + "\":").append(me.getValue()).append(",");
		}
		buffer.deleteCharAt(buffer.length() - 1).append("}");
		return buffer.toString();
	}
	
	/**
	 * 
	 * @param pkgName
	 * @param pkgNamePrefix
	 * @param outputPath
	 */
	public static void generateAsFile(String pkgName, String pkgNamePrefix, String outputPath)
	{
		File tarDir = new File(outputPath);
		if (tarDir.exists())
		{
			if (!tarDir.isDirectory())
			{
				tarDir = tarDir.getParentFile();
			}
		}
		else
		{
			tarDir.mkdirs();
		}
		Map<String, String> ret = generateAsMap(pkgName, pkgNamePrefix);
		Set<Entry<String, String>> set = ret.entrySet();
		for (Entry<String, String> me : set)
		{
			File fileName = new File(tarDir, me.getKey() + ".json");
			try
			{
				IOUtils.write(me.getValue(), new FileOutputStream(fileName), "UTF-8");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args)
	{
		generateAsFile("classpath*:com/**/domain/*.class", "com.", "d:/");
	}
	
}
