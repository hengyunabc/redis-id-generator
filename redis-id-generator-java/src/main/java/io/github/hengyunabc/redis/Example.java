package io.github.hengyunabc.redis;

import java.util.List;

public class Example {

	public static void main(String[] args) {
		String tab = "order";
		long userId = 123456789;

		IdGenerator idGenerator = IdGenerator.builder()
				.addHost("127.0.0.1", 6379, "c5809078fa6d652e0b0232d552a9d06d37fe819c")
//				.addHost("127.0.0.1", 7379, "accb7a987d4fb0fd85c57dc5a609529f80ec3722")
//				.addHost("127.0.0.1", 8379, "f55f781ca4a00a133728488e15a554c070b17255")
				.build();

		long id = idGenerator.next(tab, userId);

		System.out.println("id:" + id);
		List<Long> result = IdGenerator.parseId(id);

		System.out.println("miliSeconds:" + result.get(0) + ", partition:"
				+ result.get(1) + ", seq:" + result.get(2));
	}
}
