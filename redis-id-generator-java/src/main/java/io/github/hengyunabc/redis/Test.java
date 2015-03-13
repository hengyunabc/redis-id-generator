package io.github.hengyunabc.redis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Test {

	public static void main(String[] args) {
//		1) (integer) 1426238286
//		2) (integer) 130532
//		3) (integer) 277
//		4) (integer) 4
		Date date = new Date("Thu Mar 12 20:00:00 CST 2015");
		System.err.println(date);
		System.err.println(date.getTime());
		
		long buildId = buildId(1426212000, 0, 53, 4);
		System.err.println(buildId);
		System.err.println(parseId(buildId));
	}
	
	public static long buildId(long second, long microSecond, long shardId,
			long seq) {
		long miliSecond = (second * 1000 + microSecond / 1000);
		return (miliSecond << (12 + 10)) + (shardId << 10) + seq;
	}

	public static List<Long> parseId(long id) {
		long miliSecond = id >>> 22;
		// 2 ^ 12 = 0xFFF
		long shardId = (id & (0xFFF << 10)) >> 10;
		long seq = id & 0x3FF;

		List<Long> re = new ArrayList<Long>(4);
		re.add(miliSecond);
		re.add(shardId);
		re.add(seq);
		return re;
	}
	
}
