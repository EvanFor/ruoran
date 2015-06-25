package com.ruoran.redis;

import redis.clients.jedis.Transaction;

public class JedisTxCallbackAdapter implements JedisTransactionCallback
{
	
	@Override
	public Object beforeTransaction(Transaction tx, byte[] key)
	{
		return null;
	}
	
	@Override
	public boolean doInTransaction(Transaction tx, byte[] key, Object ret)
	{
		return true;
	}
	
	@Override
	public Object commitTransaction(Transaction tx, byte[] key, boolean ret)
	{
		if (ret) return tx.exec();
		return tx.discard();
	}
	
}
