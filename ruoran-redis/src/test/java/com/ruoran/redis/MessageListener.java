package com.ruoran.redis;

import redis.clients.jedis.JedisPubSub;

public class MessageListener extends JedisPubSub
{
	@Override
	public void onSubscribe(String channel, int subscribedChannels)
	{
		System.out.println("channel(" + channel + "):" + subscribedChannels);
	}
	
	@Override
	public void onMessage(String channel, String message)
	{
		System.out.println("channel(" + channel + "):" + message);
	}
	
	@Override
	public void onPSubscribe(String pattern, int subscribedChannels)
	{
		System.out.println("pattern(" + pattern + "):" + subscribedChannels);
	}
	
	@Override
	public void onPMessage(String pattern, String channel, String message)
	{
		System.out.println("pattern(" + pattern + "):channel(" + channel + "):" + message);
	}
}
