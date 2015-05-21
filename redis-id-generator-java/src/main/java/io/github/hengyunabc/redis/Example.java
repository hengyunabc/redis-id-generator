package io.github.hengyunabc.redis;

import java.util.List;

public class Example {

	public static void main(String[] args) {
		String tab = "order";
		long userId = 123456789;

		IdGenerator idGenerator = IdGenerator.builder()
				.addHost("127.0.0.1", 6379, "fce3758b2e0af6cbf8fea4d42b379cd0dc374418")
//				.addHost("127.0.0.1", 7379, "1abc55928f37176cb934fc7a65069bf32282d817")
//				.addHost("127.0.0.1", 8379, "b056d20feb3f89483b10c81027440cbf6920f74f")
				.build();

		long id = idGenerator.next(tab, userId);

		System.out.println("id:" + id);
		List<Long> result = IdGenerator.parseId(id);

		System.out.println("miliSeconds:" + result.get(0) + ", partition:"
				+ result.get(1) + ", seq:" + result.get(2));
	}
}
