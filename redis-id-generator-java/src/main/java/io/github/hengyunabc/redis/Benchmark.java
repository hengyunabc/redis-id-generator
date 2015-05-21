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
				.addHost("127.0.0.1", 6379, "fce3758b2e0af6cbf8fea4d42b379cd0dc374418")
//				.addHost("127.0.0.1", 7379, "1abc55928f37176cb934fc7a65069bf32282d817")
//				.addHost("127.0.0.1", 8379, "b056d20feb3f89483b10c81027440cbf6920f74f")
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
