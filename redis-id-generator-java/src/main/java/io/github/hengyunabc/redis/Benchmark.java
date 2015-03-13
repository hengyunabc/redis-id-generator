package io.github.hengyunabc.redis;

import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.time.StopWatch;

public class Benchmark {
	public static void main(String[] args) throws InterruptedException {
		Benchmark benchmark = new Benchmark();
		benchmark.test();
	}

	public void test() throws InterruptedException {
		int threadCount = 10;
		final int genCount = 10000;
		StopWatch watch = new StopWatch();
		
		final CountDownLatch latch = new CountDownLatch(threadCount);

		final IdGenerator idGenerator = IdGenerator.builder()
				.addHost("127.0.0.1", 6379, "28cb70057fc78c9beca0473259c4a579a9ccd26f")
//				.addHost("127.0.0.1", 7379, "28cb70057fc78c9beca0473259c4a579a9ccd26f")
//				.addHost("127.0.0.1", 8379, "28cb70057fc78c9beca0473259c4a579a9ccd26f")
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
