package io.github.hengyunabc.redis;

import java.util.List;

public class Example {

	public static void main(String[] args) {
		String tab = "order";
		long userId = 123456789;

		IdGenerator idGenerator = IdGenerator.builder()
				.addHost("127.0.0.1", 6379, "422f225e8dfaa7c1f81a04a48cf901a8f12346b5")
//				.addHost("127.0.0.1", 7379, "7486ab152e84a4a7d79a213ef5db5ccad63aaeb1")
//				.addHost("127.0.0.1", 8379, "cce3a2434c91a9c9ac26e95ab9623b43f2a64546")
				.build();

		long id = idGenerator.next(tab, userId);

		System.out.println("id:" + id);
		List<Long> result = IdGenerator.parseId(id);

		System.out.println("miliSeconds:" + result.get(0) + ", partition:"
				+ result.get(1) + ", seq:" + result.get(2));
	}
}
