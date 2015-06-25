package com.ruoran.redis.factory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.Hashing;
import redis.clients.util.SafeEncoder;

import com.ruoran.redis.factory.support.JedisOptions;
import com.ruoran.redis.util.JedisUtil;
import com.ruoran.redis.util.JedisUtil.NodeMode;
import com.ruoran.redis.util.JedisUtil.NodeType;

/**
 * @date 2015-06-16
 * @author Evan
 * 这是客户端的Redis分区
 * 对于主从(多个)模式支持读写分离 和容灾,使用Sentinel(至少三个节点,否则没意义)监控redis集群的主备状态,master写入数据,slave读取数据.
 */

public class ShardedJedisSentinelPool extends AbstractJedisPool implements JedisOptions
{
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private ShardedJedisPool shardedJedisPool;
	
	protected GenericObjectPoolConfig poolConfig;
	protected int timeout = Protocol.DEFAULT_TIMEOUT;
	protected String password;
	protected int database = Protocol.DEFAULT_DATABASE;
	protected Pattern keyTagPattern;
	
	protected Set<MasterListener> masterListeners = new HashSet<MasterListener>();
	private volatile List<HostAndPort> currentHostMasters;
	private volatile ConcurrentMap<String, List<JedisPool>> cachedSlaves = new ConcurrentHashMap<String, List<JedisPool>>();
	
	private List<String> masterNames = new ArrayList<String>();
	
	public ShardedJedisSentinelPool(Set<String> sentinels)
	{
		this(sentinels, new GenericObjectPoolConfig(), Protocol.DEFAULT_TIMEOUT, null, Protocol.DEFAULT_DATABASE);
	}
	
	public ShardedJedisSentinelPool(Set<String> sentinels, String password)
	{
		this(sentinels, new GenericObjectPoolConfig(), Protocol.DEFAULT_TIMEOUT, password);
	}
	
	public ShardedJedisSentinelPool(final GenericObjectPoolConfig poolConfig, Set<String> sentinels)
	{
		this(sentinels, poolConfig, Protocol.DEFAULT_TIMEOUT, null, Protocol.DEFAULT_DATABASE);
	}
	
	public ShardedJedisSentinelPool(Set<String> sentinels, final GenericObjectPoolConfig poolConfig, int timeout, final String password)
	{
		this(sentinels, poolConfig, timeout, password, Protocol.DEFAULT_DATABASE);
	}
	
	public ShardedJedisSentinelPool(Set<String> sentinels, final GenericObjectPoolConfig poolConfig, final int timeout)
	{
		this(sentinels, poolConfig, timeout, null, Protocol.DEFAULT_DATABASE);
	}
	
	public ShardedJedisSentinelPool(Set<String> sentinels, final GenericObjectPoolConfig poolConfig, final String password)
	{
		this(sentinels, poolConfig, Protocol.DEFAULT_TIMEOUT, password);
	}
	
	public ShardedJedisSentinelPool(Set<String> sentinels, final GenericObjectPoolConfig poolConfig, int timeout, final String password, final int database)
	{
		this.poolConfig = poolConfig;
		this.timeout = timeout;
		this.password = password;
		this.database = database;
		this.initPool(initSentinels(sentinels));
	}
	
	public void destroy()
	{
		for (MasterListener m : masterListeners)
		{
			m.shutdown();
		}
		shardedJedisPool.destroy();
	}
	
	public List<HostAndPort> getCurrentHostMaster()
	{
		return currentHostMasters;
	}
	
	public void setKeyTagPattern(Pattern keyTagPattern)
	{
		this.keyTagPattern = keyTagPattern;
	}
	
	private void initPool(List<HostAndPort> masters)
	{
		if (!equals(currentHostMasters, masters))
		{
			StringBuffer sb = new StringBuffer();
			for (HostAndPort master : masters)
			{
				sb.append(master.toString());
				sb.append(" ");
			}
			logger.info("Created ShardedJedisPool to master at [" + sb.toString() + "]");
			List<JedisShardInfo> shardMasters = makeShardInfoList(masters);
			this.shardedJedisPool = new ShardedJedisPool(poolConfig, shardMasters, Hashing.MURMUR_HASH, keyTagPattern);
			currentHostMasters = masters;
		}
	}
	
	private boolean equals(List<HostAndPort> currentShardMasters, List<HostAndPort> shardMasters)
	{
		if (currentShardMasters != null && shardMasters != null)
		{
			if (currentShardMasters.size() == shardMasters.size())
			{
				for (int i = 0; i < currentShardMasters.size(); i++)
				{
					if (!currentShardMasters.get(i).equals(shardMasters.get(i))) return false;
				}
				return true;
			}
		}
		return false;
	}
	
	private List<JedisShardInfo> makeShardInfoList(List<HostAndPort> masters)
	{
		List<JedisShardInfo> shardMasters = new ArrayList<JedisShardInfo>();
		for (HostAndPort master : masters)
		{
			JedisShardInfo jedisShardInfo = new JedisShardInfo(master.getHost(), master.getPort(), timeout);
			jedisShardInfo.setPassword(password);
			shardMasters.add(jedisShardInfo);
		}
		return shardMasters;
	}
	
	private List<HostAndPort> initSentinels(Set<String> sentinels)
	{
		cachedSlaves.clear();
		List<HostAndPort> availableMasters = new ArrayList<HostAndPort>();
		logger.info("Trying to find all master from available Sentinels...");
		
		for (String sentinel : sentinels)
		{
			final HostAndPort hap = buildHostAndPort(Arrays.asList(sentinel.split(":")));
			logger.info("Connecting to Sentinel " + hap);
			Jedis jedis = null;
			HostAndPort node = null;
			try
			{
				jedis = new Jedis(hap.getHost(), hap.getPort());
				List<Map<String, String>> masters = jedis.sentinelMasters();
				for (Map<String, String> nodeParams : masters)
				{
					String host = nodeParams.get("ip");
					String name = nodeParams.get("name");
					int port = Integer.valueOf(nodeParams.get("port"));
					
					Jedis temp = new Jedis(host, port);
					if (JedisUtil.detectNodeMode(temp).equals(NodeMode.common))
					{
						if (JedisUtil.detectNodeType(temp).equals(NodeType.Master))
						{
							node = new HostAndPort(host, port);
							List<String> masterAddr = jedis.sentinelGetMasterAddrByName(name);
							if (masterAddr != null && masterAddr.size() == 2)
							{
								if (!availableMasters.contains(node))
								{
									masterNames.add(name);
									availableMasters.add(node);
									logger.info("Found Redis master at " + node);
									
									List<JedisPool> slavesJedis = new ArrayList<JedisPool>();
									Set<HostAndPort> slaves = JedisUtil.getSlaves(temp);
									for (HostAndPort addr : slaves)
									{
										slavesJedis.add(new JedisPool(addr.getHost(), addr.getPort()));
									}
									cachedSlaves.put(host + ":" + port, slavesJedis);
								}
							}
						}
						else if (JedisUtil.detectNodeType(temp).equals(NodeType.Slave))
						{
							logger.warn("node[" + node.toString() + "] is a SLAVE node,should not configure in the sentinel.conf file !!");
						}
					}
					temp.close();
				}
			}
			catch (Exception e)
			{
				logger.error("Connecting to Sentinel error:" + e.getMessage());
			}
			finally
			{
				jedis.close();
			}
		}
		
		if (masterNames.size() > 0)
		{
			logger.info("Starting Sentinel listeners...");
			
			for (String sentinel : sentinels)
			{
				final HostAndPort hap = buildHostAndPort(Arrays.asList(sentinel.split(":")));
				MasterListener masterListener = new MasterListener(masterNames, hap.getHost(), hap.getPort());
				masterListeners.add(masterListener);
				masterListener.start();
			}
		}
		
		if (availableMasters.size() == 0)
		{
			logger.warn("no usable Redis Master Node !");
		}
		
		return availableMasters;
	}
	
	private HostAndPort buildHostAndPort(List<String> getMasterAddrByNameResult)
	{
		String host = getMasterAddrByNameResult.get(0);
		int port = Integer.parseInt(getMasterAddrByNameResult.get(1));
		return new HostAndPort(host, port);
	}
	
	protected class MasterListener extends Thread
	{
		protected List<String> masters;
		protected String host;
		protected int port;
		protected long subscribeRetryWaitTimeMillis = 5000;
		protected Jedis jedis;
		protected AtomicBoolean running = new AtomicBoolean(false);
		
		protected MasterListener()
		{
		}
		
		public MasterListener(List<String> masters, String host, int port)
		{
			this.masters = masters;
			this.host = host;
			this.port = port;
		}
		
		public MasterListener(List<String> masters, String host, int port, long subscribeRetryWaitTimeMillis)
		{
			this(masters, host, port);
			this.subscribeRetryWaitTimeMillis = subscribeRetryWaitTimeMillis;
		}
		
		public void run()
		{
			running.set(true);
			while (running.get())
			{
				jedis = new Jedis(host, port);
				try
				{
					jedis.subscribe(new JedisPubSub()
					{
						@Override
						public void onMessage(String channel, String message)
						{
							logger.info("Sentinel " + host + ":" + port + " published: " + message + ".");
							
							String[] switchMasterMsg = message.split(" ");
							
							if (switchMasterMsg.length > 3)
							{
								
								int index = masters.indexOf(switchMasterMsg[0]);
								if (index >= 0)
								{
									HostAndPort newHostMaster = buildHostAndPort(Arrays.asList(switchMasterMsg[3], switchMasterMsg[4]));
									List<HostAndPort> newHostMasters = new ArrayList<HostAndPort>();
									for (int i = 0; i < masters.size(); i++)
									{
										newHostMasters.add(null);
									}
									Collections.copy(newHostMasters, currentHostMasters);
									newHostMasters.set(index, newHostMaster);
									
									initPool(newHostMasters);
								}
								else
								{
									StringBuffer sb = new StringBuffer();
									for (String masterName : masters)
									{
										sb.append(masterName);
										sb.append(",");
									}
									logger.info("Ignoring message on +switch-master for master name " + switchMasterMsg[0] + ", our monitor master name are [" + sb + "]");
								}
								
							}
							else
							{
								logger.warn("Invalid message received on Sentinel " + host + ":" + port + " on channel +switch-master: " + message);
							}
						}
					}, "+switch-master");
					
				}
				catch (JedisConnectionException e)
				{
					
					if (running.get())
					{
						logger.warn("Lost connection to Sentinel at " + host + ":" + port + ". Sleeping 5000ms and retrying.");
						try
						{
							Thread.sleep(subscribeRetryWaitTimeMillis);
						}
						catch (InterruptedException e1)
						{
							e1.printStackTrace();
						}
					}
					else
					{
						logger.info("Unsubscribing from Sentinel at " + host + ":" + port);
					}
				}
			}
		}
		
		public void shutdown()
		{
			try
			{
				logger.info("Shutting down listener on " + host + ":" + port);
				running.set(false);
				jedis.close();
			}
			catch (Exception e)
			{
				logger.error("Caught exception while shutting down: " + e.getMessage());
			}
		}
	}
	
	@Override
	public Jedis getWriteableResource(String key)
	{
		ShardedJedis shardJedis = this.shardedJedisPool.getResource();
		return shardJedis.getShard(SafeEncoder.encode(key));
	}
	
	private static SecureRandom random = new SecureRandom();
	
	@SuppressWarnings("resource")
	public Jedis getReadableResource(String key)
	{
		ShardedJedis temp = this.shardedJedisPool.getResource();
		JedisShardInfo si = temp.getShardInfo(SafeEncoder.encode(key));
		String master = si.getHost() + ":" + si.getPort();
		List<JedisPool> soga = cachedSlaves.get(master);
		if (soga == null || soga.size() == 0) { return new JedisPool(si.getHost(), si.getPort()).getResource(); }
		return soga.get(random.nextInt(soga.size())).getResource();
	}
	
	public void close()
	{
		this.shardedJedisPool.close();
		this.destroy();
	}
	
	@Override
	public List<JedisPool> getMasters()
	{
		List<JedisPool> mp = new ArrayList<JedisPool>();
		List<HostAndPort> masters = this.getCurrentHostMaster();
		for (HostAndPort hap : masters)
		{
			JedisPool jp = new JedisPool(hap.getHost(), hap.getPort());
			mp.add(jp);
		}
		return mp;
	}
}
