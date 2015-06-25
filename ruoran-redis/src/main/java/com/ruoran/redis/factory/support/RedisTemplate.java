package com.ruoran.redis.factory.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.SortingParams;
import redis.clients.util.SafeEncoder;

import com.ruoran.kryo.KryoExt;
import com.ruoran.redis.ItrCallback;
import com.ruoran.redis.JedisReadCallback;
import com.ruoran.redis.JedisWriteCallback;

public class RedisTemplate
{
	protected JedisOptions jedisOptions;
	protected KryoExt kryo;
	
	public void setJedisOptions(JedisOptions jedisOptions)
	{
		this.jedisOptions = jedisOptions;
	}
	
	public void setKryo(KryoExt kryo)
	{
		this.kryo = kryo;
	}
	
	/**
	 * Redis String set
	 * @param key
	 * @param entity
	 * @return
	 */
	public Object setString(String key, final Object entity)
	{
		return this.jedisOptions.executeWrite(new JedisWriteCallback()
		{
			@Override
			public Object doInCallback(Jedis jedis, byte[] key)
			{
				byte[] data = kryo.write(entity);
				return jedis.set(key, data);
			}
		}, key);
	}
	
	/**
	 * Redis String set
	 * @param key
	 * @param entity
	 * @param expInSeconds
	 * @return
	 */
	public Object setString(String key, final Object entity, final int expInSeconds)
	{
		return this.jedisOptions.executeWrite(new JedisWriteCallback()
		{
			@Override
			public Object doInCallback(Jedis jedis, byte[] key)
			{
				byte[] data = kryo.write(entity);
				return jedis.setex(key, expInSeconds, data);
			}
		}, key);
	}
	
	/**
	 * Redis String set
	 * @param key
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getString(String key, final Class<T> clazz)
	{
		return (T) this.jedisOptions.executeRead(new JedisReadCallback()
		{
			@Override
			public T doInCallback(Jedis jedis, byte[] key)
			{
				byte[] data = jedis.get(key);
				if (data != null) return kryo.read(data, clazz);
				return null;
			}
		}, key);
	}
	
	/**
	 * Redis String get
	 * @param key
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getStringList(String key, final Class<T> clazz)
	{
		return (List<T>) this.jedisOptions.executeRead(new JedisReadCallback()
		{
			@Override
			public List<T> doInCallback(Jedis jedis, byte[] key)
			{
				byte[] data = jedis.get(key);
				if (data != null) return kryo.readList(data, clazz);
				return null;
			}
		}, key);
	}
	
	/**
	 * Redis String get
	 * @param key
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> Map<String, T> getStringMap(String key, final Class<T> clazz)
	{
		return (Map<String, T>) this.jedisOptions.executeRead(new JedisReadCallback()
		{
			@Override
			public Map<String, T> doInCallback(Jedis jedis, byte[] key)
			{
				byte[] data = jedis.get(key);
				if (data != null) return kryo.readMap(data, clazz);
				return null;
			}
		}, key);
	}
	
	/**
	 * Redis Key del
	 * @param key
	 * @return
	 */
	public Object delKey(String key)
	{
		return this.jedisOptions.executeWrite(new JedisWriteCallback()
		{
			@Override
			public Object doInCallback(Jedis jedis, byte[] key)
			{
				return jedis.del(key);
			}
		}, key);
	}
	
	/**
	 * Redis Key del
	 * @param pattern
	 * @return
	 */
	public Object delKeyByPattern(final String pattern)
	{
		final Set<String> delKeys = new HashSet<String>();
		this.jedisOptions.doInAllMaster(new ItrCallback()
		{
			@Override
			public void doInCallback(Jedis jedis)
			{
				Set<String> keys = jedis.keys(pattern);
				jedis.del(keys.toArray(new String[] {}));
				delKeys.addAll(keys);
			}
		});
		return delKeys;
	}
	
	/**
	 * Redis key 是否存在 
	 * @param key
	 * @return
	 */
	public Object exists(String key)
	{
		return this.jedisOptions.executeRead(new JedisReadCallback()
		{
			@Override
			public Object doInCallback(Jedis jedis, byte[] key)
			{
				return jedis.exists(key);
			}
		}, key);
	}
	
	/**
	 * 
	 * @param key
	 * @param seconds
	 * @return
	 */
	public Object expire(String key, final int seconds)
	{
		return this.jedisOptions.executeWrite(new JedisWriteCallback()
		{
			@Override
			public Object doInCallback(Jedis jedis, byte[] key)
			{
				return jedis.expire(key, seconds);
			}
		}, key);
	}
	
	/**
	 * 
	 * @param key
	 * @param unixTime
	 * @return
	 */
	public Object expireAt(String key, final long unixTime)
	{
		return this.jedisOptions.executeWrite(new JedisWriteCallback()
		{
			@Override
			public Object doInCallback(Jedis jedis, byte[] key)
			{
				return jedis.expireAt(key, unixTime);
			}
		}, key);
	}
	
	public Object keyType(String key)
	{
		return this.jedisOptions.executeRead(new JedisReadCallback()
		{
			@Override
			public Object doInCallback(Jedis jedis, byte[] key)
			{
				return jedis.type(key);
			}
		}, key);
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public Object timeToLive(String key)
	{
		return this.jedisOptions.executeRead(new JedisReadCallback()
		{
			@Override
			public Object doInCallback(Jedis jedis, byte[] key)
			{
				return jedis.ttl(key);
			}
		}, key);
	}
	
	/**
	 * Redis 计数器 自增
	 * @param key
	 * @return
	 */
	public Object counter(String key)
	{
		return this.jedisOptions.executeWrite(new JedisWriteCallback()
		{
			@Override
			public Object doInCallback(Jedis jedis, byte[] key)
			{
				return jedis.incr(key);
			}
		}, key);
	}
	
	/**
	 * Redis 计数器 按间隔自增
	 * @param key
	 * @param gap
	 * @return
	 */
	public Object counter(String key, final Long gap)
	{
		return this.jedisOptions.executeWrite(new JedisWriteCallback()
		{
			@Override
			public Object doInCallback(Jedis jedis, byte[] key)
			{
				return jedis.incrBy(key, gap);
			}
		}, key);
	}
	
	/**
	 * Redis 计数器读取
	 * @param key
	 * @return
	 */
	public Long getCounter(String key)
	{
		byte[] ret = (byte[]) this.jedisOptions.executeRead(new JedisReadCallback()
		{
			@Override
			public Object doInCallback(Jedis jedis, byte[] key)
			{
				return jedis.get(key);
			}
		}, key);
		return Long.valueOf(new String(ret));
	}
	
	/**
	 * Redis List set
	 * @param key
	 * @param entity
	 * @return
	 */
	public Object setList(String key, final List<?> list)
	{
		return this.jedisOptions.executeWrite(new JedisWriteCallback()
		{
			@Override
			public Object doInCallback(Jedis jedis, byte[] key)
			{
				byte[][] data = new byte[list.size()][];
				for (int i = 0; i < list.size(); i++)
				{
					data[i] = kryo.write(list.get(i));
				}
				return jedis.rpush(key, data);
			}
		}, key);
	}
	
	public Object appendListAtTail(String key, final List<?> list)
	{
		return setList(key, list);
	}
	
	public Object appendListAtTail(String key, final Object value)
	{
		return setList(key, Arrays.asList(value));
	}
	
	/**
	 * 
	 * @param key
	 * @param list
	 * @return
	 */
	public Object appendListAtHead(String key, final List<?> list)
	{
		return this.jedisOptions.executeWrite(new JedisWriteCallback()
		{
			@Override
			public Object doInCallback(Jedis jedis, byte[] key)
			{
				byte[][] data = new byte[list.size()][];
				for (int i = 0; i < list.size(); i++)
				{
					data[i] = kryo.write(list.get(i));
				}
				return jedis.lpush(key, data);
			}
		}, key);
	}
	
	public Object appendListAtHead(String key, final Object value)
	{
		return appendListAtHead(key, Arrays.asList(value));
	}
	
	/**
	 * Redis List get
	 * @param key
	 * @param start
	 * @param end
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getList(String key, final long start, final long end, final Class<T> clazz)
	{
		return (List<T>) this.jedisOptions.executeRead(new JedisReadCallback()
		{
			@Override
			public Object doInCallback(Jedis jedis, byte[] key)
			{
				List<T> ret = new ArrayList<T>();
				List<byte[]> data = jedis.lrange(key, start, end);
				for (byte[] entity : data)
				{
					ret.add(kryo.read(entity, clazz));
				}
				return ret;
			}
		}, key);
	}
	
	/**
	 * Redis List get All
	 * @param key
	 * @param clazz
	 * @return
	 */
	public <T> List<T> getList(String key, Class<T> clazz)
	{
		return this.getList(key, 0, -1, clazz);
	}
	
	/**
	 * Redis List len
	 * @param key
	 * @return
	 */
	public Long listSize(String key)
	{
		Jedis jedis = this.jedisOptions.getReadableResource(key);
		return jedis.llen(SafeEncoder.encode(key));
	}
	
	/**
	 * Redis List get Page
	 * @param key
	 * @param pageSize
	 * @param pageNum
	 * @param clazz
	 * @return
	 */
	public <T> List<T> getListByPage(String key, long pageSize, long pageNum, Class<T> clazz)
	{
		long total = listSize(key);
		long[] params = this.calculatePageParams(total, pageSize, pageNum);
		return getList(key, params[0], params[1], clazz);
	}
	
	/**
	 * 
	 * @param total
	 * @param pageSize
	 * @param pageNum
	 * @return
	 */
	public long[] calculatePageParams(long total, long pageSize, long pageNum)
	{
		long maxPage = (total + pageSize - 1) / pageSize;
		long nps = pageSize < 1 ? 1 : pageSize;
		long npn = pageNum < 1 ? 1 : (pageNum > maxPage ? maxPage : pageNum);
		long start = nps * (npn - 1);
		long end = (npn * nps - 1) > total ? total : (npn * nps - 1);
		return new long[] { start, end };
	}
	
	/**
	 * 
	 * @param key
	 * @param oldVal
	 * @param newVal
	 * @return
	 */
	public Object updateListElem(String key, final Object oldVal, final Object newVal)
	{
		return jedisOptions.executeWrite(new JedisWriteCallback()
		{
			@Override
			public Object doInCallback(Jedis jedis, byte[] key)
			{
				byte[] old = kryo.write(oldVal);
				byte[] dest = kryo.write(newVal);
				long size = jedis.llen(key);
				for (long i = 0; i < size; i++)
				{
					byte[] data = jedis.lindex(key, i);
					if (Arrays.equals(data, old)) { return jedis.lset(key, i, dest); }
				}
				return false;
			}
		}, key);
	}
	
	/**
	 * 
	 * @param key
	 * @param target
	 * @return
	 */
	public Object removeListElem(String key, final Object target)
	{
		return jedisOptions.executeWrite(new JedisWriteCallback()
		{
			@Override
			public Object doInCallback(Jedis jedis, byte[] key)
			{
				byte[] dest = kryo.write(target);
				// 这里默认认为List元素不重复
				return jedis.lrem(key, 1, dest);
			}
		}, key);
	}
	
	/**
	 * 
	 * @param key
	 * @param target
	 * @return
	 */
	public Object removeListElems(String key, final List<?> target)
	{
		return jedisOptions.executeWrite(new JedisWriteCallback()
		{
			ArrayList<Long> retList = new ArrayList<Long>();
			
			@Override
			public Object doInCallback(Jedis jedis, byte[] key)
			{
				for (int i = 0; i < target.size(); i++)
				{
					byte[] dest = kryo.write(target.get(i));
					Long ret = jedis.lrem(key, 1, dest);
					retList.add(ret);
				}
				return retList;
			}
		}, key);
	}
	
	/**
	 * 对于数字和字符串类型的值的排序
	 * @param key
	 * @param clazz
	 * @param desc
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> sort(String key, final Class<T> clazz, final boolean desc)
	{
		return (List<T>) this.jedisOptions.executeWrite(new JedisWriteCallback()
		{
			@Override
			public Object doInCallback(Jedis jedis, byte[] key)
			{
				final List<T> list = new ArrayList<T>();
				if (jedis.type(key).equals("list"))
				{
					SortingParams sp = new SortingParams();
					if (desc)
					{
						sp.desc();
					}
					
					if (!clazz.isPrimitive())
					{
						sp.alpha();
					}
					
					List<byte[]> dest = jedis.sort(key, sp);
					for (byte[] data : dest)
					{
						list.add(kryo.read(data, clazz));
					}
				}
				return list;
			}
		}, key);
	}
	
	/**
	 * 发布一条消息
	 * @param channel
	 * @param message
	 * @return
	 */
	public Object publish(String channel, String message)
	{
		Jedis shard = this.jedisOptions.getWriteableResource(channel);
		return shard.publish(channel, message);
	}
	
	/**
	 * 
	 * @param pubSub
	 * @param channels
	 */
	public void subscribe(final JedisPubSub pubSub, final String... channels)
	{
		
	}
	
	/**
	 * 
	 * @param pubSub
	 * @param patterns
	 */
	public void psubscribe(JedisPubSub pubSub, String... patterns)
	{
		
	}
	
	public void close()
	{
		if (this.jedisOptions != null)
		{
			this.jedisOptions.close();
		}
	}
}
