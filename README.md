## Fantastic-RPC

`Fantastic-RPC` is a RPC framework based on Java.
 
### Architecture

![Architecture](/architecture.png)

**Registry & Discovery**

Use `persistent node` in `Zookeeper` to register the service name of provider.

Use `ephemeral node` in `Zookeeper` to register the address of provider and consumer.

Add the `watcher` for all the service that consumer subscribed;


**Invoke**

Consumer gets the address of provider from `Zookeeper`.

Then, Consumer use `TCP` to communicate with provider.

Use `multiplexing IO` in `Netty` to build a `TCP` connection.


**Frame Format**

Design a Protocol for request and response.

```
MagicNum | SerializeType | PacketType | DataLength | Data
0x860860 | 0             | 0          | 32         | xxxxxxxxxxxxx
```



For simplicity, serialization protocol is `JSON`.




### Example

Init a Provider and register services.

```java
package com.github.fantasticlab.rpc.core.test;

import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import com.github.fantasticlab.rpc.core.initializer.ProviderInitializer;

public class ProviderExample {

    public static void main(String[] args) throws FrpcZookeeperException {

        String zk = "localhost:2181";
        Integer port = 8080;
        String group = "test";
        
        ProviderInitializer initializer = new ProviderInitializer(zk, port, group);
        initializer.register(HelloServiceImpl.class);
    }
}
```

Init a Consumer and invoke services.
```java
package com.github.fantasticlab.rpc.core.test;

import com.github.fantasticlab.rpc.core.exception.FrpcZookeeperException;
import com.github.fantasticlab.rpc.core.initializer.ConsumerInitializer;

public class ConsumerExample {

    public static void main(String[] args) throws FrpcZookeeperException, InterruptedException {

        String zk = "localhost:2181";
        String group = "test";

        ConsumerInitializer initializer = new ConsumerInitializer(zk, group);
        HelloService helloService = initializer.getService(HelloService.class);
        helloService.sayHi("Hello!!!");

    }
}
```


### License

```
MIT License

Copyright (c) 2019 Fantastic Lab

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
