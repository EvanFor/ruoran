package com.ruoran;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class Util
{
	public static void print(Object tar)
	{
		if (tar instanceof int[])
		{
			int[] ret = (int[]) tar;
			for (int i : ret)
			{
				System.out.print(i + ",");
			}
			System.out.println();
		}
		else if (tar instanceof long[])
		{
			long[] ret = (long[]) tar;
			for (long i : ret)
			{
				System.out.print(i + ",");
			}
			System.out.println();
		}
		else if (tar instanceof float[])
		{
			float[] ret = (float[]) tar;
			for (float i : ret)
			{
				System.out.print(i + ",");
			}
			System.out.println();
		}
		else if (tar instanceof double[])
		{
			double[] ret = (double[]) tar;
			for (double i : ret)
			{
				System.out.print(i + ",");
			}
			System.out.println();
		}
		else if (tar instanceof Collection)
		{
			Iterable<?> ret = (Iterable<?>) tar;
			for (Object o : ret)
			{
				System.out.println(o);
			}
		}
		else if (tar instanceof Map)
		{
			Map<?, ?> ret = (Map<?, ?>) tar;
			for (Entry<?, ?> obj : ret.entrySet())
			{
				System.out.println(obj.getKey() + ":" + obj.getValue());
			}
		}
		else
		{
			System.out.println(tar);
		}
	}
	
	private static Random random = new Random();
	
	public static Date date()
	{
		return new Date(System.currentTimeMillis() - random.nextInt(1000000000));
	}
}
