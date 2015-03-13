package io.github.hengyunabc.redis;

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
				.addHost("127.0.0.1", 6379, "16ea0a3f345892d8a51243aa3ab5695ce25a178d")
//				.addHost("127.0.0.1", 7379, "921966051517703de806a15209922df8dbf0365e")
//				.addHost("127.0.0.1", 8379, "20b3e56ec6ebbb7fb242c3d62372d0f7b1a233b7")
				.build();
		
		watch.start();
		for (int i = 0; i < threadCount; ++i) {
			Thread thread = new Thread() {
				public void run() {
					for (int j = 0; j < genCount; ++j) {
						idGenerator.next("test", j);
					}
					latch.countDown();
				}
			};
			thread.start();
		}

		latch.await();
		watch.stop();

		System.err.println("time:" + watch);
		System.err.println("speed:" + genCount * threadCount / (watch.getTime() / 1000.0));
	}
}
