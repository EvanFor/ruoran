package com.ruoran.entity.helper;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.ruoran.entity.domain.User;
import com.ruoran.entity.helper.EntityHelper.EntityColumn;
import com.ruoran.entity.helper.EntityHelper.EntityTable;

public class EntityHelperTest
{
	@Test
	public void testGetEntityTable()
	{
		EntityTable table = EntityHelper.getEntityTable(User.class);
		System.out.println(table);
	}
	
	@Test
	public void testGetOrderByClause()
	{
		String orderByClause = EntityHelper.getOrderByClause(User.class);
		System.out.println(orderByClause);
	}
	
	@Test
	public void testGetColumns()
	{
		Set<EntityColumn> columns = EntityHelper.getColumns(User.class);
		System.out.println(columns);
	}
	
	@Test
	public void testGetPKColumns()
	{
		Set<EntityColumn> pkcolumns = EntityHelper.getPKColumns(User.class);
		System.out.println(pkcolumns);
	}
	
	@Test
	public void testGetColumnAlias()
	{
		Map<String, String> columnAlias = EntityHelper.getColumnAlias(User.class);
		System.out.println(columnAlias);
	}
	
	@Test
	public void testGetSelectColumns()
	{
		String selectColumns = EntityHelper.getSelectColumns(User.class);
		System.out.println(selectColumns);
	}
	
	@Test
	public void testGetAllColumns()
	{
		String selectColumns = EntityHelper.getAllColumns(User.class);
		System.out.println(selectColumns);
	}
	
	@Test
	public void testGetPrimaryKeyWhere()
	{
		String primaryKeyWhere = EntityHelper.getPrimaryKeyWhere(User.class);
		System.out.println(primaryKeyWhere);
	}
	
	@Test
	public void testInitEntityNameMap()
	{
	
	}
	
	@Test
	public void testCamelhumpToUnderline()
	{
		String name = EntityHelper.camelhumpToUnderline("lastAccessUrl");
		System.out.println(name);
	}
	
	@Test
	public void testUnderlineToCamelhump()
	{
		String name = EntityHelper.underlineToCamelhump("last_access_url");
		System.out.println(name);
	}
	
	@Test
	public void testIsUppercaseAlpha()
	{
	
	}
	
	@Test
	public void testToUpperAscii()
	{
	
	}
	
	@Test
	public void testMap2AliasMap()
	{
	 
	}
	
	@Test
	public void testMap2Bean()
	{
	
	}
	
	@Test
	public void testMaplist2BeanList()
	{
	
	}
	
}
