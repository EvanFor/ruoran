package com.ruoran.redis.factory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

import com.ruoran.redis.factory.support.JedisOptions;
import com.ruoran.redis.util.JedisUtil;
import com.ruoran.redis.util.JedisUtil.NodeMode;
import com.ruoran.redis.util.JedisUtil.NodeType;

/**
 * 支持单节点的Master方法,或者带Slave的读写分离.
 * @author Evan
 *
 */
public class CommonJedisPool extends AbstractJedisPool implements JedisOptions
{
	protected Logger logger = LoggerFactory.getLogger(getClass());
	private volatile List<JedisPool> cachedSlaves = new ArrayList<JedisPool>();
	
	protected JedisPool pool;
	protected String host;
	protected int port;
	
	public CommonJedisPool(String host, int port)
	{
		this(new GenericObjectPoolConfig(), host, port);
	}
	
	public CommonJedisPool(GenericObjectPoolConfig poolConfig, String host, int port)
	{
		this(poolConfig, host, port, Protocol.DEFAULT_TIMEOUT);
	}
	
	public CommonJedisPool(GenericObjectPoolConfig poolConfig, String host, int port, int timeout)
	{
		this(poolConfig, host, port, Protocol.DEFAULT_TIMEOUT, null);
	}
	
	public CommonJedisPool(GenericObjectPoolConfig poolConfig, String host, int port, int timeout, String password)
	{
		this(poolConfig, host, port, Protocol.DEFAULT_TIMEOUT, null, Protocol.DEFAULT_DATABASE);
	}
	
	public CommonJedisPool(GenericObjectPoolConfig poolConfig, String host, int port, int timeout, String password, int database)
	{
		this.pool = new JedisPool(poolConfig, host, port, timeout, password, database);
		this.host = host;
		this.port = port;
		this.init();
	}
	
	private void init()
	{
		Jedis jedis = new Jedis(host, port);
		if (JedisUtil.detectNodeMode(jedis).equals(NodeMode.common))
		{
			if (JedisUtil.detectNodeType(jedis).equals(NodeType.Master))
			{
				Set<HostAndPort> slaves = JedisUtil.getSlaves(jedis);
				if (slaves.size() > 0)
				{
					for (HostAndPort addr : slaves)
					{
						cachedSlaves.add(new JedisPool(addr.getHost(), addr.getPort()));
					}
				}
			}
			else
			{
				logger.error("this Jedis Node is with SLave Type !");
				throw new RuntimeException("this Jedis Node is with SLave Type,Please use a Master instead !");
			}
		}
		else if (JedisUtil.detectNodeMode(jedis).equals(NodeMode.sentinel))
		{
			logger.error("this Jedis Node is with Sentinel Mode ,Please use ShardedJedisSentinelPool instead !");
		}
		else
		{
			logger.error("this Jedis Node is with Cluster Mode ,Please use JedisCluster instead !");
		}
	}
	
	public Jedis getNativeJedis()
	{
		return pool.getResource();
	}
	
	private static SecureRandom random = new SecureRandom();
	
	@Override
	public Jedis getReadableResource(String key)
	{
		if (cachedSlaves.size() == 0) { return this.pool.getResource(); }
		return cachedSlaves.get(random.nextInt(cachedSlaves.size())).getResource();
	}
	
	@Override
	public Jedis getWriteableResource(String key)
	{
		return this.pool.getResource();
	}
	
	public void close()
	{
		pool.close();
	}
	
	@Override
	public List<JedisPool> getMasters()
	{
		return Arrays.asList(new JedisPool(host, port));
	}
}