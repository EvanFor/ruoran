package com.ruoran.mybatis.ext;

import java.util.List;

import com.ruoran.entity.Example;
import com.ruoran.entity.helper.EntityHelper;
import com.ruoran.mybatis.mapper.CommonMapper;

/**
 * 封装的CommonMapper,实际上只对select方法做了处理<br>
 * <p/>
 * 该类起名Mapper结尾只是为了表面上看着统一，实际上和普通的Mapper不是一类东西
 *
 * @author liuzh
 */
public class EntityMapper
{
	//需要注入该类，可以构造参数注入 -- 注意这里
	private CommonMapper commonMapper;
	
	public EntityMapper(CommonMapper commonMapper)
	{
		this.commonMapper = commonMapper;
	}
	
	/**
	 * 根据参数进行查询，查询结果最多只能有一个
	 * <br>查询条件为属性String类型不为空，其他类型!=null时
	 * <br>where property = ? and property2 = ? 条件
	 *
	 * @param record
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T selectOne(T record)
	{
		if (record == null) { throw new NullPointerException("实体或者主键参数不能为空!"); }
		return (T) EntityHelper.map2Bean(commonMapper.selectOne(record), record.getClass());
	}
	
	/**
	 * 根据参数进行查询,record可以是Class<?>类型
	 * <br>查询条件为属性String类型不为空，其他类型!=null时
	 * <br>where property = ? and property2 = ? 条件
	 *
	 * @param record
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> select(T record)
	{
		if (record == null) { throw new NullPointerException("实体或者主键参数不能为空!"); }
		return (List<T>) EntityHelper.maplist2BeanList(commonMapper.select(record), record.getClass());
	}
	
	/**
	 * 根据参数进行查询总数,record可以是Class<?>类型
	 * <br>查询条件为属性String类型不为空，其他类型!=null时
	 * <br>where property = ? and property2 = ? 条件
	 *
	 * @param record
	 * @param <T>
	 * @return
	 */
	public <T> int count(T record)
	{
		return commonMapper.count(record);
	}
	
	/**
	* 根据主键查询结果，主键不能为null或空
	*
	* @param entityClass
	* @param key
	* @param <T>
	* @return
	*/
	public <T> T selectByPrimaryKey(Class<T> entityClass, Object key)
	{
		return (T) EntityHelper.map2Bean(commonMapper.selectByPrimaryKey(entityClass, key), entityClass);
	}
	
	/**
	* 插入数据库，主键字段没有值的时候不会出现在sql中
	* <br>如果是自增主键，会自动获取值
	* <br>如果是自增主键，并且该主键属性有值，会使用主键的属性值，不会使用自增
	*
	* @param record
	* @param <T>
	* @return
	*/
	public <T> int insert(T record)
	{
		return commonMapper.insert(record);
	}
	
	/**
	 * 插入非空字段，其他和上面方法类似
	 *
	 * @param record
	 * @param <T>
	 * @return
	 */
	public <T> int insertSelective(T record)
	{
		return commonMapper.insertSelective(record);
	}
	
	/**
	 * 根据条件进行删除，条件不能为空，并且必须有至少一个条件才能删除
	 * <br>该方法不能直接删除全部数据
	 *
	 * @param record
	 * @param <T>
	 * @return
	 */
	public <T> int delete(T record)
	{
		return commonMapper.delete(record);
	}
	
	/**
	 * 根据主键进行删除，主键不能为null或空
	 *
	 * @param entityClass
	 * @param key
	 * @param <T>
	 * @return
	 */
	public <T> int deleteByPrimaryKey(Class<T> entityClass, Object key)
	{
		return commonMapper.deleteByPrimaryKey(entityClass, key);
	}
	
	/**
	 * 根据主键更新全部字段，空字段也会更新数据库
	 *
	 * @param record
	 * @param <T>
	 * @return
	 */
	public <T> int updateByPrimaryKey(T record)
	{
		return commonMapper.updateByPrimaryKey(record);
	}
	
	/**
	 * 根据主键更新非空属性字段，不能给数据库数据设置null或空
	 *
	 * @param record
	 * @param <T>
	 * @return
	 */
	public <T> int updateByPrimaryKeySelective(T record)
	{
		return commonMapper.updateByPrimaryKeySelective(record);
	}
	
	/**
	 * 通过Example类来查询count
	 *
	 * @param entityClass
	 * @param example     可以是Mybatis生成器生成的Example类或者通用的Example类
	 * @param <T>
	 * @return
	 */
	public <T> int countByExample(Class<T> entityClass, Object example)
	{
		return commonMapper.countByExample(entityClass, example);
	}
	
	/**
	* 通过Example删除
	*
	* @param entityClass
	* @param example     可以是Mybatis生成器生成的Example类或者通用的Example类
	* @param <T>
	* @return
	*/
	public <T> int deleteByExample(Class<T> entityClass, Object example)
	{
		return commonMapper.deleteByExample(entityClass, example);
	}
	
	/**
	 * 通过Example删除
	 *
	 * @param example
	 * @param <T>
	 * @return
	 */
	public <T> int deleteByExample(Example example)
	{
		if (example == null) { throw new NullPointerException("example参数不能为空!"); }
		return commonMapper.deleteByExample(example.getEntityClass(), example);
	}
	
	/**
	* 通过Example来查询
	*
	* @param entityClass
	* @param example     可以是Mybatis生成器生成的Example类或者通用的Example类
	* @param <T>
	* @return
	*/
	public <T> List<T> selectByExample(Class<T> entityClass, Object example)
	{
		return (List<T>) EntityHelper.maplist2BeanList(commonMapper.selectByExample(entityClass, example), entityClass);
	}
	
	/**
	 * 通过Example来查询
	 *
	 * @param example
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> selectByExample(Example example)
	{
		if (example == null) { throw new NullPointerException("example参数不能为空!"); }
		return (List<T>) EntityHelper.maplist2BeanList(commonMapper.selectByExample(example.getEntityClass(), example), example.getEntityClass());
	}
	
	/**
	* 通过Example进行更新非空字段
	*
	* @param record
	* @param example 可以是Mybatis生成器生成的Example类或者通用的Example类
	* @param <T>
	* @return
	*/
	public <T> int updateByExampleSelective(T record, Object example)
	{
		return commonMapper.updateByExampleSelective(record, example);
	}
	
	/**
	 * 通过Example进行更新全部字段
	 *
	 * @param record
	 * @param example 可以是Mybatis生成器生成的Example类或者通用的Example类
	 * @param <T>
	 * @return
	 */
	public <T> int updateByExample(T record, Object example)
	{
		return commonMapper.updateByExample(record, example);
	}
}
