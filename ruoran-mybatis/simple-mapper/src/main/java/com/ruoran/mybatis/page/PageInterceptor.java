package com.ruoran.mybatis.page;

import java.util.Properties;

import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.ruoran.entity.constant.IdentityDialect;

@Intercepts({ @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }) })
public class PageInterceptor implements Interceptor
{
	protected IdentityDialect dialect;
	
	@SuppressWarnings("unchecked")
	@Override
	public Object intercept(Invocation invocation) throws Throwable
	{
		Object[] args = invocation.getArgs();
		MappedStatement ms = (MappedStatement) args[0];
		ParamMap<Object> paramMap = null;
		if (args[1] instanceof ParamMap)
		{
			paramMap = (ParamMap<Object>) args[1];
		}
		
		PageParam page = null;
		if (paramMap.get("page") != null)
		{
			page = (PageParam) paramMap.get("page");
			BoundSql boundSql = ms.getBoundSql(paramMap);
			Configuration cfg = ms.getConfiguration();
			MetaObject meta = MetaObject.forObject(ms, cfg.getObjectFactory(), cfg.getObjectWrapperFactory(), cfg.getReflectorFactory());
			meta.setValue("sqlSource", newSqlSource(ms, boundSql, page));
		}
		
		return invocation.proceed();
	}
	
	private StaticSqlSource newSqlSource(MappedStatement ms, BoundSql boundSql, PageParam page)
	{
		int offset = (page.pageNum - 1) * page.pageSize;
		String sql = boundSql.getSql();
		switch (dialect)
		{
			case MYSQL:
				String limit = " limit " + offset + "," + page.pageSize;
				sql += limit;
				break;
			default:
				break;
		}
		
		return new StaticSqlSource(ms.getConfiguration(), sql, boundSql.getParameterMappings());
	}
	
	@Override
	public Object plugin(Object target)
	{
		if (target instanceof Executor) { return Plugin.wrap(target, this); }
		return target;
	}
	
	@Override
	public void setProperties(Properties properties)
	{
		String dialect = properties.getProperty("dialect");
		if (dialect == null) { throw new IllegalArgumentException("必须指定dialect属性"); }
		this.dialect = IdentityDialect.getDatabaseDialect(dialect);
	}
}
