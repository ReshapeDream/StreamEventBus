# 一个简单的 Stream EventBus
##　使用示例
```java
public class TestEventbus {

    @Test
    public void testParallel() {
        StreamEventbus.of("parallel")
                // .sequential()
                // .parallel(10)
                .ignoreException()
                .post(Action0.class, "action0 with err")
                .post(Action1.class, new String[] { "str0", "str1" })
                .post(Action1.class, "action11")
                .post(Action2.class, "action2 no err")
                .post(Action3.class, 100_000)
                .post(Action4.class, 1)
                .exception((cause,info)->{
                    System.out.println(info);
                    cause.printStackTrace();
                })
                .exec();
    }
}
```