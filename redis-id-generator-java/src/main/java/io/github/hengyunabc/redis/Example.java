package io.github.hengyunabc.redis;

import java.util.List;

public class Example {

	public static void main(String[] args) {
		String tab = "order";
		long userId = 123456789;

		IdGenerator idGenerator = IdGenerator.builder()
				.addHost("127.0.0.1", 6379, "16ea0a3f345892d8a51243aa3ab5695ce25a178d")
//				.addHost("127.0.0.1", 7379, "921966051517703de806a15209922df8dbf0365e")
//				.addHost("127.0.0.1", 8379, "20b3e56ec6ebbb7fb242c3d62372d0f7b1a233b7")
				.build();

		long id = idGenerator.next(tab, userId);

		System.out.println("id:" + id);
		List<Long> result = IdGenerator.parseId(id);

		System.out.println("miliSeconds:" + result.get(0) + ", partition:"
				+ result.get(1) + ", seq:" + result.get(2));
	}
}
