package com.ruoran.redis;

import redis.clients.jedis.Transaction;

public interface JedisTransactionCallback
{
	/**
	 * 
	 * @param tx
	 * @param key
	 * @return
	 */
	Object beforeTransaction(Transaction tx, byte[] key);
	
	/**
	 * 
	 * @param tx
	 * @param key
	 * @param ret
	 * @return
	 */
	boolean doInTransaction(Transaction tx, byte[] key, Object ret);
	
	/**
	 * 
	 * @param tx
	 * @param key
	 * @param ret
	 * @return
	 */
	Object commitTransaction(Transaction tx, byte[] key, boolean ret);
	
}
