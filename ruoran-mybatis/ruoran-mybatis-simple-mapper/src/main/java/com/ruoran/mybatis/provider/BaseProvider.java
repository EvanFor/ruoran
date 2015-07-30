package com.ruoran.mybatis.provider;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import com.ruoran.entity.helper.EntityHelper;

/**
 * 基础类
 *
 * @author liuzh
 */
public class BaseProvider
{
	
	/**
	 * 主键字段不能为空
	 *
	 * @param property
	 * @param value
	 */
	protected void notNullKeyProperty(String property, Object value)
	{
		if (value == null || (value instanceof String && isEmpty((String) value)))
		{
			throwNullKeyException(property);
		}
	}
	
	protected void throwNullKeyException(String property)
	{
		throw new NullPointerException("主键属性" + property + "不能为空!");
	}
	
	/**
	 * 获取实体类型
	 *
	 * @param params
	 * @return
	 */
	protected Class<?> getEntityClass(Map<String, Object> params)
	{
		Class<?> entityClass = null;
		if (params.containsKey("record"))
		{
			entityClass = getEntity(params).getClass();
		}
		else if (params.containsKey("entityClass"))
		{
			entityClass = (Class<?>) params.get("entityClass");
		}
		if (entityClass == null) { throw new RuntimeException("无法获取实体类型!"); }
		return entityClass;
	}
	
	/**
	 * 获取实体类
	 *
	 * @param params
	 * @return
	 */
	protected Object getEntity(Map<String, Object> params)
	{
		Object result;
		if (params.containsKey("record"))
		{
			result = params.get("record");
		}
		else if (params.containsKey("key"))
		{
			result = params.get("key");
		}
		else
		{
			throw new RuntimeException("当前方法没有实体或主键参数!");
		}
		if (result == null) { throw new NullPointerException("实体或者主键参数不能为空!"); }
		return result;
	}
	
	/**
	 * 获取Example类 - 不在此校验是否为合法的Example类
	 *
	 * @param params
	 * @return
	 */
	protected MetaObject getExample(Map<String, Object> params)
	{
		Object result = null;
		if (params.containsKey("example"))
		{
			result = params.get("example");
		}
		if (result == null) { return null; }
		//根据Example的结构，通过判断是否包含某些属性来判断条件是否为合法的example类型
		MetaObject example = SystemMetaObject.forObject(result);
		if (example.hasGetter("orderByClause") && example.hasGetter("oredCriteria") && example.hasGetter("distinct")) { return example; }
		throw new IllegalArgumentException("Example参数不是合法的Mybatis Example对象!");
	}
	
	/**
	 * 根据主键查询
	 *
	 * @param sql
	 * @param metaObject
	 * @param columns
	 * @param suffix
	 */
	protected void applyWherePk(SQL sql, MetaObject metaObject, Set<EntityHelper.EntityColumn> columns, String suffix)
	{
		for (EntityHelper.EntityColumn column : columns)
		{
			notNullKeyProperty(column.getProperty(), metaObject.getValue(column.getProperty()));
			sql.WHERE(column.getColumn() + "=#{" + (suffix != null ? suffix + "." : "") + column.getProperty() + "}");
		}
	}
	
	/**
	 * Example条件
	 */
	protected void applyOrderBy(SQL sql, MetaObject example, String defaultOrderByClause)
	{
		if (example == null) { return; }
		Object orderBy = example.getValue("orderByClause");
		if (orderBy != null)
		{
			sql.ORDER_BY((String) orderBy);
		}
		else if (defaultOrderByClause != null && defaultOrderByClause.length() > 0)
		{
			sql.ORDER_BY(defaultOrderByClause);
		}
	}
	
	/**
	 * Example条件
	 */
	protected void applyWhere(SQL sql, MetaObject example)
	{
		if (example == null) { return; }
		String parmPhrase1 = "%s #{example.oredCriteria[%d].allCriteria[%d].value}";
		String parmPhrase1_th = "%s #{example.oredCriteria[%d].allCriteria[%d].value,typeHandler=%s}";
		String parmPhrase2 = "%s #{example.oredCriteria[%d].allCriteria[%d].value} and #{example.oredCriteria[%d].criteria[%d].secondValue}";
		String parmPhrase2_th = "%s #{example.oredCriteria[%d].allCriteria[%d].value,typeHandler=%s} and #{example.oredCriteria[%d].criteria[%d].secondValue,typeHandler=%s}";
		String parmPhrase3 = "#{example.oredCriteria[%d].allCriteria[%d].value[%d]}";
		String parmPhrase3_th = "#{example.oredCriteria[%d].allCriteria[%d].value[%d],typeHandler=%s}";
		
		StringBuilder sb = new StringBuilder();
		
		List<?> oredCriteria = (List<?>) example.getValue("oredCriteria");
		boolean firstCriteria = true;
		for (int i = 0; i < oredCriteria.size(); i++)
		{
			MetaObject criteria = SystemMetaObject.forObject(oredCriteria.get(i));
			List<?> criterions = (List<?>) criteria.getValue("criteria");
			if (criterions.size() > 0)
			{
				if (firstCriteria)
				{
					firstCriteria = false;
				}
				else
				{
					sb.append(" or ");
				}
				
				sb.append('(');
				boolean firstCriterion = true;
				for (int j = 0; j < criterions.size(); j++)
				{
					MetaObject criterion = SystemMetaObject.forObject(criterions.get(j));
					if (firstCriterion)
					{
						firstCriterion = false;
					}
					else
					{
						sb.append(" and ");
					}
					
					if ((Boolean) criterion.getValue("noValue"))
					{
						sb.append(criterion.getValue("condition"));
					}
					else if ((Boolean) criterion.getValue("singleValue"))
					{
						if (criterion.getValue("typeHandler") == null)
						{
							sb.append(String.format(parmPhrase1, criterion.getValue("condition"), i, j));
						}
						else
						{
							sb.append(String.format(parmPhrase1_th, criterion.getValue("condition"), i, j, criterion.getValue("typeHandler")));
						}
					}
					else if ((Boolean) criterion.getValue("betweenValue"))
					{
						if (criterion.getValue("typeHandler") == null)
						{
							sb.append(String.format(parmPhrase2, criterion.getValue("condition"), i, j, i, j));
						}
						else
						{
							sb.append(String.format(parmPhrase2_th, criterion.getValue("condition"), i, j, criterion.getValue("typeHandler"), i, j, criterion.getValue("typeHandler")));
						}
					}
					else if ((Boolean) criterion.getValue("listValue"))
					{
						sb.append(criterion.getValue("condition"));
						sb.append(" (");
						List<?> listItems = (List<?>) criterion.getValue("value");
						boolean comma = false;
						for (int k = 0; k < listItems.size(); k++)
						{
							if (comma)
							{
								sb.append(", ");
							}
							else
							{
								comma = true;
							}
							if (criterion.getValue("typeHandler") == null)
							{
								sb.append(String.format(parmPhrase3, i, j, k));
							}
							else
							{
								sb.append(String.format(parmPhrase3_th, i, j, k, criterion.getValue("typeHandler")));
							}
						}
						sb.append(')');
					}
				}
				sb.append(')');
			}
		}
		
		if (sb.length() > 0)
		{
			sql.WHERE(sb.toString());
		}
	}
	
	protected boolean isEmpty(String value)
	{
		return value == null || value.length() == 0;
	}
	
	protected boolean isNotEmpty(String value)
	{
		return !isEmpty(value);
	}
}
