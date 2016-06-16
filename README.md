# redis-id-generator
distributed id generator based on redis.

基于redis的分布式ID生成器。
## 准备

首先，要知道redis的EVAL，EVALSHA命令：

http://redis.io/commands/eval

http://redis.io/commands/evalsha

##原理
利用redis的lua脚本执行功能，在每个节点上通过lua脚本生成唯一ID。
生成的ID是64位的：

* 使用41 bit来存放时间，精确到毫秒，可以使用41年。  
* 使用12 bit来存放逻辑分片ID，最大分片ID是4095
* 使用10 bit来存放自增长ID，意味着每个节点，每毫秒最多可以生成1024个ID  

比如GTM时间 ```Fri Mar 13 10:00:00 CST 2015``` ，它的距1970年的毫秒数是 ```1426212000000```，假定分片ID是53，自增长序列是4，则生成的ID是：
```
5981966696448054276 = 1426212000000 << 22 + 53 << 10 + 4
```

redis提供了[TIME](http://redis.io/commands/time)命令，可以取得redis服务器上的秒数和微秒数。因些lua脚本返回的是一个四元组。
```
second, microSecond, partition, seq
```

客户端要自己处理，生成最终ID。
```
((second * 1000 + microSecond / 1000) << (12 + 10)) + (shardId << 10) + seq;
```

###集群实现原理
假定集群里有3个节点，则节点1返回的seq是：
```
0, 3, 6, 9, 12 ...
```
节点2返回的seq是
```
1, 4, 7, 10, 13 ...
```
节点3返回的seq是
```
2, 5, 8, 11, 14 ...
```
这样每个节点返回的数据都是唯一的。

###注意事项

* 要求redis server版本是3.2以上，因为使用到了`redis.replicate_commands()`

参考：http://redis.io/commands/eval

* 因为是利用了redis的time命令来获取到redis服务器上的时间，所以reids服务器的时间要保证是只增长的，要关闭服务器上的ntp等时间同步机制。

##单个节点部署

下载redis-script-node1.lua，并把它load到redis上。
```bash
cd redis-directory/
wget https://raw.githubusercontent.com/hengyunabc/redis-id-generator/master/redis-script-node1.lua
./redis-cli script load "$(cat redis-script-node1.lua)"
```
获取lua脚本的sha1值，可能是：
```
c5809078fa6d652e0b0232d552a9d06d37fe819c
```
在代码里，通过EVALSHA命令，传递这个sha1值，就可以得到生成的ID。

比如，通过命令行执行：
```bash
./redis-cli EVALSHA c5809078fa6d652e0b0232d552a9d06d37fe819c 2 test 123456789
```
结果可能是：
```
1) (integer) 1426238286
2) (integer) 130532
3) (integer) 277
4) (integer) 4
```

## 集群部署
假定集群是3个节点，则分别对三个节点执行：
```bash
./redis-cli -host node1 -p 6379 script load "$(cat redis-script-node1.lua)"
./redis-cli -host node2 -p 7379 script load "$(cat redis-script-node2.lua)"
./redis-cli -host node3 -p 8379 script load "$(cat redis-script-node3.lua)"
```

##性能
redis默认配置。

```
单节点，单线程：
time:0:00:00.959
speed:10427.52867570386
单节点，20线程：
time:0:00:06.710
speed:29806.259314456034
```
结论：
- 单节点，qps约3w
- 可以线性扩展，3个结点足以满足绝大部分的应用

##java客户端封装
在redis-id-generator-java目录下，有example和benchmark代码。

在调用时，要传入两个参数
- tag，即为哪一类服务生成ID
- shardId，即分片由哪个ID生成，比如一个用户的订单，则分片ID应该由userId来生成

```java
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
```

##多语言客户端
只要支持redis evalsha命令就可以了。
