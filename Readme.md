# 一个简单的 Stream EventBus
##　使用示例
```java
StreamEventbus.of("hello")
    // .parallel(10)
    .ignoreException()
    .post(ActionMove.class, new String[] { "a", "b" })
    .post(ActionMove.class, "ok")
    .post(ActionCheck.class, "check")
    .post(ActionUncompress.class, "uncompress")
    .post(ActionUpdateLog.class, "log")
    .post(ActionUpdateToHdfs.class, "log")
    .post(ActionUpdateLog.class, "log")
    .exception((cause, context) -> {
        cause.printStackTrace();
    })
    .exec();
```