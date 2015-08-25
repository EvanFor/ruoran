package com.ruoran.mybatis.provider;

import java.util.Map;

import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import com.ruoran.entity.helper.EntityHelper;

public class CommonProvider extends BaseProvider
{
	
	/**
	 * 查询，入参可以是Entity.class或new Entity()
	 *
	 * @param params
	 * @return
	 */
	public String selectOne(final Map<String, Object> params)
	{
		return new SQL()
		{
			{
				Object entity = getEntity(params);
				Class<?> entityClass = getEntityClass(params);
				EntityHelper.EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
				SELECT(EntityHelper.getAllColumns(entityClass));
				FROM(entityTable.getName());
				if (entity != null)
				{
					final MetaObject metaObject = SystemMetaObject.forObject(entity);
					for (EntityHelper.EntityColumn column : entityTable.getEntityClassColumns())
					{
						Object value = metaObject.getValue(column.getProperty());
						if (value == null)
						{
							continue;
						}
						else
						{
							Class<?> javaType = column.getJavaType();
							if (EntityHelper.isCustomType(javaType))
							{
								WHERE(column.getColumn() + "=#{record." + column.getUseAs() + "}");
							}
							else
							{
								WHERE(column.getColumn() + "=#{record." + column.getProperty() + "}");
							}
						}
					}
				}
			}
		}.toString();
	}
	
	/**
	 * 查询，入参可以是Entity.class或new Entity()
	 *
	 * @param params
	 * @return
	 */
	public String select(final Map<String, Object> params)
	{
		return new SQL()
		{
			{
				Object entity = getEntity(params);
				Class<?> entityClass = getEntityClass(params);
				EntityHelper.EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
				SELECT(EntityHelper.getAllColumns(entityClass));
				FROM(entityTable.getName());
				if (entity != null)
				{
					final MetaObject metaObject = SystemMetaObject.forObject(entity);
					for (EntityHelper.EntityColumn column : entityTable.getEntityClassColumns())
					{
						Object value = metaObject.getValue(column.getProperty());
						if (value == null)
						{
							continue;
						}
						else
						{
							Class<?> javaType = column.getJavaType();
							if (EntityHelper.isCustomType(javaType))
							{
								WHERE(column.getColumn() + "=#{record." + column.getUseAs() + "}");
							}
							else
							{
								WHERE(column.getColumn() + "=#{record." + column.getProperty() + "}");
							}
						}
						
					}
				}
				String orderByClause = EntityHelper.getOrderByClause(entityClass);
				if (orderByClause.length() > 0)
				{
					ORDER_BY(orderByClause);
				}
			}
		}.toString();
	}
	
	/**
	 * 查询，入参可以是Entity.class或new Entity()
	 *
	 * @param params
	 * @return
	 */
	public String count(final Map<String, Object> params)
	{
		return new SQL()
		{
			{
				Object entity = getEntity(params);
				Class<?> entityClass;
				if (entity instanceof Class<?>)
				{
					entityClass = (Class<?>) entity;
					entity = null;
				}
				else
				{
					entityClass = getEntityClass(params);
				}
				EntityHelper.EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
				SELECT("count(*)");
				FROM(entityTable.getName());
				if (entity != null)
				{
					MetaObject metaObject = SystemMetaObject.forObject(entity);
					for (EntityHelper.EntityColumn column : entityTable.getEntityClassColumns())
					{
						Object value = metaObject.getValue(column.getProperty());
						if (value == null)
						{
							continue;
						}
						else
						{
							Class<?> javaType = column.getJavaType();
							if (EntityHelper.isCustomType(javaType))
							{
								WHERE(column.getColumn() + "=#{record." + column.getUseAs() + "}");
							}
							else
							{
								WHERE(column.getColumn() + "=#{record." + column.getProperty() + "}");
							}
						}
					}
				}
			}
		}.toString();
	}
	
	/**
	* 通过主键查询，主键字段都不能为空
	*
	* @param params
	* @return
	*/
	public String selectByPrimaryKey(final Map<String, Object> params)
	{
		return new SQL()
		{
			{
				Object entity = getEntity(params);
				Class<?> entityClass = getEntityClass(params);
				EntityHelper.EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
				SELECT(EntityHelper.getAllColumns(entityClass));
				FROM(entityTable.getName());
				if (entityTable.getEntityClassPKColumns().size() == 1)
				{
					EntityHelper.EntityColumn column = entityTable.getEntityClassPKColumns().iterator().next();
					notNullKeyProperty(column.getProperty(), entity);
					WHERE(column.getColumn() + "=#{key}");
				}
				else
				{
					applyWherePk(this, SystemMetaObject.forObject(entity), entityTable.getEntityClassPKColumns(), "key");
				}
			}
		}.toString();
	}
	
	/**
	* 新增
	*
	* @param params
	* @return
	*/
	public String insert(final Map<String, Object> params)
	{
		return new SQL()
		{
			{
				Class<?> entityClass = getEntityClass(params);
				EntityHelper.EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
				INSERT_INTO(entityTable.getName());
				for (EntityHelper.EntityColumn column : entityTable.getEntityClassColumns())
				{
					if (column.getUseAs() != null)
					{
						VALUES(column.getColumn(), "#{record." + column.getUseAs() + "}");
					}
					else
					{
						VALUES(column.getColumn(), "#{record." + column.getProperty() + "}");
					}
				}
			}
		}.toString();
	}
	
	/**
	* 新增非空字段，空字段可以使用表的默认值
	*
	* @param params
	* @return
	*/
	public String insertSelective(final Map<String, Object> params)
	{
		return new SQL()
		{
			{
				Object entity = getEntity(params);
				Class<?> entityClass = getEntityClass(params);
				EntityHelper.EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
				MetaObject metaObject = SystemMetaObject.forObject(entity);
				INSERT_INTO(entityTable.getName());
				for (EntityHelper.EntityColumn column : entityTable.getEntityClassColumns())
				{
					Object value = metaObject.getValue(column.getProperty());
					if (column.isId() || value != null)
					{
						if (column.getUseAs() != null)
						{
							VALUES(column.getColumn(), "#{record." + column.getUseAs() + "}");
						}
						else
						{
							VALUES(column.getColumn(), "#{record." + column.getProperty() + "}");
						}
					}
				}
			}
		}.toString();
	}
	
	/**
	* 通过查询条件删除
	*
	* @param params
	* @return
	*/
	public String delete(final Map<String, Object> params)
	{
		return new SQL()
		{
			{
				Object entity = getEntity(params);
				Class<?> entityClass = getEntityClass(params);
				EntityHelper.EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
				MetaObject metaObject = SystemMetaObject.forObject(entity);
				DELETE_FROM(entityTable.getName());
				boolean hasValue = false;
				for (EntityHelper.EntityColumn column : entityTable.getEntityClassColumns())
				{
					Object value = metaObject.getValue(column.getProperty());
					if (value == null)
					{
						continue;
					}
					else
					{
						hasValue = true;
						Class<?> javaType = column.getJavaType();
						if (EntityHelper.isCustomType(javaType))
						{
							WHERE(column.getColumn() + "=#{record." + column.getUseAs() + "}");
						}
						else
						{
							WHERE(column.getColumn() + "=#{record." + column.getProperty() + "}");
						}
					}
				}
				
				if (!hasValue) { throw new UnsupportedOperationException("delete方法不支持删除全表的操作!"); }
			}
		}.toString();
	}
	
	/**
	 * 通过主键删除
	 *
	 * @param params
	 * @return
	 */
	public String deleteByPrimaryKey(final Map<String, Object> params)
	{
		return new SQL()
		{
			{
				Object entity = getEntity(params);
				Class<?> entityClass = getEntityClass(params);
				EntityHelper.EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
				DELETE_FROM(entityTable.getName());
				if (entityTable.getEntityClassPKColumns().size() == 1)
				{
					EntityHelper.EntityColumn column = entityTable.getEntityClassPKColumns().iterator().next();
					notNullKeyProperty(column.getProperty(), entity);
					WHERE(column.getColumn() + "=#{key}");
				}
				else
				{
					applyWherePk(this, SystemMetaObject.forObject(entity), entityTable.getEntityClassPKColumns(), "key");
				}
			}
		}.toString();
	}
	
	/**
	 * 通过主键更新
	 *
	 * @param params
	 * @return
	 */
	public String updateByPrimaryKey(final Map<String, Object> params)
	{
		return new SQL()
		{
			{
				Object entity = getEntity(params);
				Class<?> entityClass = getEntityClass(params);
				EntityHelper.EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
				MetaObject metaObject = SystemMetaObject.forObject(entity);
				UPDATE(entityTable.getName());
				for (EntityHelper.EntityColumn column : entityTable.getEntityClassColumns())
				{
					//更新不是ID的字段，因为根据主键查询的...更新后还是一样。
					if (!column.isId())
					{
						Class<?> javaType = column.getJavaType();
						if (EntityHelper.isCustomType(javaType))
						{
							SET(column.getColumn() + "=#{record." + column.getUseAs() + "}");
						}
						else
						{
							SET(column.getColumn() + "=#{record." + column.getProperty() + "}");
						}
					}
				}
				applyWherePk(this, metaObject, entityTable.getEntityClassPKColumns(), "record");
			}
		}.toString();
	}
	
	/**
	 * 通过主键更新非空字段
	 *
	 * @param params
	 * @return
	 */
	public String updateByPrimaryKeySelective(final Map<String, Object> params)
	{
		return new SQL()
		{
			{
				Object entity = getEntity(params);
				Class<?> entityClass = getEntityClass(params);
				EntityHelper.EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
				MetaObject metaObject = SystemMetaObject.forObject(entity);
				UPDATE(entityTable.getName());
				for (EntityHelper.EntityColumn column : entityTable.getEntityClassColumns())
				{
					Object value = metaObject.getValue(column.getProperty());
					//更新不是ID的字段，因为根据主键查询的...更新后还是一样。
					if (value != null && !column.isId())
					{
						Class<?> javaType = column.getJavaType();
						if (EntityHelper.isCustomType(javaType))
						{
							SET(column.getColumn() + "=#{record." + column.getUseAs() + "}");
						}
						else
						{
							SET(column.getColumn() + "=#{record." + column.getProperty() + "}");
						}
					}
				}
				applyWherePk(this, metaObject, entityTable.getEntityClassPKColumns(), "record");
			}
		}.toString();
	}
	
	public String countByExample(final Map<String, Object> params)
	{
		return new SQL()
		{
			{
				MetaObject example = getExample(params);
				Class<?> entityClass = getEntityClass(params);
				EntityHelper.EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
				SELECT("count(*)");
				FROM(entityTable.getName());
				applyWhere(this, example);
			}
		}.toString();
	}
	
	public String deleteByExample(final Map<String, Object> params)
	{
		return new SQL()
		{
			{
				MetaObject example = getExample(params);
				Class<?> entityClass = getEntityClass(params);
				EntityHelper.EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
				DELETE_FROM(entityTable.getName());
				applyWhere(this, example);
			}
		}.toString();
	}
	
	public String selectByExample(final Map<String, Object> params)
	{
		return new SQL()
		{
			{
				MetaObject example = getExample(params);
				Class<?> entityClass = getEntityClass(params);
				EntityHelper.EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
				SELECT(EntityHelper.getAllColumns(entityClass));
				FROM(entityTable.getName());
				applyWhere(this, example);
				applyOrderBy(this, example, EntityHelper.getOrderByClause(entityClass));
			}
		}.toString();
	}
	
	public String updateByExampleSelective(final Map<String, Object> params)
	{
		return new SQL()
		{
			{
				Object entity = getEntity(params);
				MetaObject example = getExample(params);
				Class<?> entityClass = getEntityClass(params);
				EntityHelper.EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
				MetaObject metaObject = SystemMetaObject.forObject(entity);
				UPDATE(entityTable.getName());
				for (EntityHelper.EntityColumn column : entityTable.getEntityClassColumns())
				{
					Object value = metaObject.getValue(column.getProperty());
					//更新不是ID的字段，因为根据主键查询的...更新后还是一样。
					if (value != null)
					{
						Class<?> javaType = column.getJavaType();
						if (EntityHelper.isCustomType(javaType))
						{
							SET(column.getColumn() + "=#{record." + column.getUseAs() + "}");
						}
						else
						{
							SET(column.getColumn() + "=#{record." + column.getProperty() + "}");
						}
					}
				}
				applyWhere(this, example);
			}
		}.toString();
	}
	
	public String updateByExample(final Map<String, Object> params)
	{
		return new SQL()
		{
			{
				MetaObject example = getExample(params);
				Class<?> entityClass = getEntityClass(params);
				EntityHelper.EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
				UPDATE(entityTable.getName());
				for (EntityHelper.EntityColumn column : entityTable.getEntityClassColumns())
				{
					//更新不是ID的字段，因为根据主键查询的...更新后还是一样。
					if (!column.isId())
					{
						Class<?> javaType = column.getJavaType();
						if (EntityHelper.isCustomType(javaType))
						{
							SET(column.getColumn() + "=#{record." + column.getUseAs() + "}");
						}
						else
						{
							SET(column.getColumn() + "=#{record." + column.getProperty() + "}");
						}
					}
				}
				applyWhere(this, example);
			}
		}.toString();
	}
}
