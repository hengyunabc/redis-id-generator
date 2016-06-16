package io.github.hengyunabc.redis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.time.StopWatch;

public class Benchmark {
	public static void main(String[] args) throws InterruptedException {
		Benchmark benchmark = new Benchmark();
		benchmark.test();
	}

	public void test() throws InterruptedException {
		int threadCount = 20;
		final int genCount = 10000;
		StopWatch watch = new StopWatch();

		final CountDownLatch latch = new CountDownLatch(threadCount);

		final IdGenerator idGenerator = IdGenerator.builder()
				.addHost("127.0.0.1", 6379, "c5809078fa6d652e0b0232d552a9d06d37fe819c")
//				.addHost("127.0.0.1", 7379, "accb7a987d4fb0fd85c57dc5a609529f80ec3722")
//				.addHost("127.0.0.1", 8379, "f55f781ca4a00a133728488e15a554c070b17255")
				.build();

		// unique id check
		final Map<Long, Object> map = new ConcurrentHashMap<Long, Object>();
		watch.start();

		final Object o = new Object();
		for (int i = 0; i < threadCount; ++i) {
			Thread thread = new Thread() {
				public void run() {
					for (int j = 0; j < genCount; ++j) {
						long next = idGenerator.next("test", j);
						map.put(next, o);
					}
					latch.countDown();
				}
			};
			thread.start();
		}

		latch.await();
		watch.stop();

		System.out.println("threadCount:" + threadCount + ", genCount:" + genCount);
		System.out.println("map size:" + map.size());

		System.out.println("time:" + watch);
		System.out.println("speed:" + genCount * threadCount / (watch.getTime() / 1000.0));

		if (map.size() != threadCount * genCount) {
			System.err.println("It seems generated the same id!!!");
			System.exit(-1);
		}
	}
}
