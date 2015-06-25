package com.ruoran.redis.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.util.JedisClusterCRC16;

public class JedisUtil
{
	public static Pattern SLAVE_PATTERN = Pattern.compile("slave\\d+:ip=(.*?),port=(.*?),.*");
	public static Pattern MASTER_PATTERN = Pattern.compile("master_host:(\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3})\\s+master_port:(\\d{1,5})");
	
	public enum NodeMode
	{
		common, sentinel, cluster;
	}
	
	public enum InfoSection
	{
		Server, Clients, Memory, Persistence, Stats, Replication, CPU, Cluster, Keyspace, Sentinel;
	}
	
	public enum NodeType
	{
		Master, Slave, Sentinel, Unkown;
	}
	
	private static boolean check(Jedis jedis)
	{
		return jedis != null && jedis.ping().equals("PONG");
	}
	
	public static NodeMode detectNodeMode(Jedis jedis)
	{
		if (check(jedis))
		{
			try
			{
				jedis.clusterNodes();
			}
			catch (Exception e)
			{
				try
				{
					jedis.sentinelMasters();
				}
				catch (Exception ex)
				{
					return NodeMode.common;
				}
				return NodeMode.sentinel;
			}
			return NodeMode.cluster;
		}
		return null;
	}
	
	public static boolean detectCanWrite(Jedis jedis)
	{
		if (check(jedis))
		{
			try
			{
				jedis.set("testKey", "0");
				jedis.del("testKey");
				return true;
			}
			catch (Exception e)
			{
				return false;
			}
		}
		return false;
	}
	
	public static NodeType detectNodeType(Jedis jedis)
	{
		if (check(jedis))
		{
			String replication = jedis.info(InfoSection.Replication.name());
			if (replication.contains("role:master")) return NodeType.Master;
			else if (replication.contains("role:slave")) return NodeType.Slave;
			else
			{
				String sentinel = jedis.info(InfoSection.Sentinel.name());
				if (sentinel != null) return NodeType.Sentinel;
			}
		}
		return NodeType.Unkown;
	}
	
	public static Set<HostAndPort> getSlaves(Jedis master)
	{
		Set<HostAndPort> slaves = new HashSet<HostAndPort>();
		NodeType type = detectNodeType(master);
		if (!type.equals(NodeType.Master))
		{
			throw new IllegalArgumentException("the node you given here is not a MASTER node !");
		}
		else
		{
			String replication = master.info(InfoSection.Replication.name());
			Matcher m = SLAVE_PATTERN.matcher(replication);
			while (m.find())
			{
				slaves.add(new HostAndPort(m.group(1), Integer.valueOf(m.group(2))));
			}
		}
		return slaves;
	}
	
	public static HostAndPort getMaster(Jedis slave)
	{
		NodeType type = detectNodeType(slave);
		if (!type.equals(NodeType.Slave))
		{
			throw new IllegalArgumentException("the node you given here is not a SLAVE node !");
		}
		else
		{
			String replication = slave.info(InfoSection.Replication.name());
			Matcher m = MASTER_PATTERN.matcher(replication);
			if (m.find()) { return new HostAndPort(m.group(1), Integer.valueOf(m.group(2))); }
		}
		return null;
	}
	
	public static HostAndPort buildHostAndPort(String host_port)
	{
		if (host_port != null && host_port.trim().length() > 0 && host_port.split(":").length == 2)
		{
			String[] hap = host_port.split(":");
			return new HostAndPort(hap[0], Integer.valueOf(hap[1]));
		}
		throw new IllegalArgumentException("the param should be like host:port !");
	}
	
	public static int getSlot(String key)
	{
		return JedisClusterCRC16.getSlot(key);
	}
	
	public static String getNodeId(Jedis cluster)
	{
		if (check(cluster))
		{
			if (detectNodeMode(cluster).equals(NodeMode.cluster))
			{
				for (String infoLine : cluster.clusterNodes().split("\n"))
				{
					if (infoLine.contains("myself")) { return infoLine.split("\\s")[0]; }
				}
			}
		}
		return "";
	}
}
