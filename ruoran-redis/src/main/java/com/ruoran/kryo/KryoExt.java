package com.ruoran.kryo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.FastInput;
import com.esotericsoftware.kryo.io.FastOutput;
import com.esotericsoftware.kryo.pool.KryoCallback;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.ruoran.kryo.util.ClassUtil;

public class KryoExt
{
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public interface ClassRegister
	{
		void registerClass(Kryo kryo, String... pkgNames);
	}
	
	protected byte[] buffer;
	protected String[] pkgName;
	protected KryoPool pool = null;
	private ClassRegister register;
	
	public KryoExt(String[] pkgName)
	{
		this.buffer = new byte[1024 * 50];
		this.pkgName = pkgName;
		this.init();
	}
	
	public KryoExt(int buffer, String[] pkgName)
	{
		this.buffer = new byte[buffer];
		this.pkgName = pkgName;
		this.init();
	}
	
	private void init()
	{
		this.register = new DefaultClassRegister();
		this.pool = new KryoPool.Builder(new KryoFactory()
		{
			@Override
			public Kryo create()
			{
				Kryo kryo = new Kryo();
				// 默认开启,解决树形结构(循环引用)
				// kryo.setReferences(true);
				register.registerClass(kryo, pkgName);
				return kryo;
			}
		}).softReferences().build();
	}
	
	public Kryo getKryo()
	{
		return pool.borrow();
	}
	
	public <T> T execute(KryoCallback<T> kryoCallback)
	{
		return pool.run(kryoCallback);
	}
	
	public void release(Kryo kryo)
	{
		pool.release(kryo);
	}
	
	public byte[] writeClassAndObject(Object object)
	{
		Kryo kryo = this.getKryo();
		FastOutput output = new FastOutput(buffer);
		kryo.writeClassAndObject(output, object);
		this.release(kryo);
		return output.toBytes();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T readClassAndObject(byte[] data)
	{
		Kryo kryo = this.getKryo();
		FastInput input = new FastInput(data);
		T obj = (T) kryo.readClassAndObject(input);
		this.release(kryo);
		return obj;
	}
	
	public byte[] write(Object object)
	{
		Kryo kryo = this.getKryo();
		FastOutput output = new FastOutput(buffer);
		kryo.writeObject(output, object);
		this.release(kryo);
		return output.toBytes();
	}
	
	public <T> T read(byte[] data, Class<T> clazz)
	{
		Kryo kryo = this.getKryo();
		FastInput input = new FastInput(data);
		T obj = kryo.readObject(input, clazz);
		this.release(kryo);
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public <T> ArrayList<T> readList(byte[] data, Class<T> clazz)
	{
		Kryo kryo = this.getKryo();
		FastInput input = new FastInput(data);
		ArrayList<T> obj = kryo.readObject(input, ArrayList.class);
		this.release(kryo);
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public <T> HashMap<String, T> readMap(byte[] data, Class<T> clazz)
	{
		Kryo kryo = this.getKryo();
		FastInput input = new FastInput(data);
		HashMap<String, T> obj = kryo.readObject(input, HashMap.class);
		this.release(kryo);
		return obj;
	}
	
	public class DefaultClassRegister implements ClassRegister
	{
		@Override
		public void registerClass(Kryo kryo, String... pkgNames)
		{
			if (pkgName.length == 0) { throw new RuntimeException("entity packageName should be not null !"); }
			for (String pkgName : pkgNames)
			{
				Set<Class<?>> classes = ClassUtil.getClasses(pkgName);
				for (Class<?> type : classes)
				{
					kryo.register(type);
					logger.debug("kryo register:{}", type);
				}
			}
			
			kryo.register(HashMap.class);
			kryo.register(TreeMap.class);
			kryo.register(LinkedHashMap.class);
			
			kryo.register(ArrayList.class);
			kryo.register(LinkedList.class);
			
			kryo.register(HashSet.class);
			kryo.register(LinkedHashSet.class);
			
		}
	}
}
