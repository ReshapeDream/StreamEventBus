package com.nj.test;

import com.nj.eventbus.StreamEventbus;

public class TestEventbus {

    public static void main(String[] args) {
        // testSequential();
        // ignoreException();
        // stopWhenErr();
        testResult();
    }

    private static void testResult() {
        StreamEventbus.of("testResult")
                // .ignoreException()
                .parallel(10)
                // 每个post的event，返回的结果可能是多个，根据next
                .post(Action0.class, "string")
                .next(Action0.class, 1)
                .next(Action0.class)
                .exception((c, info) -> {
                    c.printStackTrace();
                })
                .exec();
    }

    public static void testParallel() {
        StreamEventbus.of("parallel")
                // .sequential()
                .parallel(10)
                .ignoreException()
                .post(Action0.class, "action0 with err")
                .post(Action1.class, new String[] { "str0", "str1" })
                .post(Action1.class, "action11")
                .post(Action2.class, "action2 no err")
                .post(Action3.class, 100_000)
                .post(Action4.class, 4)
                .exception((cause, info) -> {
                    System.out.println(info);
                    cause.printStackTrace();
                })
                .exec();
    }

    public static void testSequential() {
        StreamEventbus.of("parallel")
                .sequential()
                .post(Action0.class, "action0 with err")
                .post(Action1.class, new String[] { "str0", "str1" })
                .post(Action1.class, "action11")
                .post(Action2.class, "action2 no err")
                .post(Action3.class, 100_000)
                .post(Action4.class, 4)
                .exception((cause, info) -> {
                    System.out.println(info);
                    cause.printStackTrace();
                })
                .exec();
    }

    public static void ignoreException() {
        StreamEventbus.of("parallel")
                .sequential()
                .ignoreException()
                .post(Action0.class, "action0 with err")
                .post(Action1.class, new String[] { "str0", "str1" })
                .post(Action1.class, "action11")
                .post(Action2.class, "action2 no err")
                .post(Action3.class, 100_000)
                .post(Action4.class, 4)
                .exception((cause, info) -> {
                    System.out.println(info);
                    cause.printStackTrace();
                })
                .exec();
    }

    public static void stopWhenErr() {
        StreamEventbus.of("parallel")
                .sequential()
                .post(Action0.class, "action0 with err")
                .post(Action1.class, new String[] { "str0", "str1" })
                .post(Action1.class, "action11")
                .post(Action2.class, "action2 no err")
                .post(Action3.class, 100_000)
                .post(Action4.class, 4)
                .exception((cause, info) -> {
                    System.out.println(info);
                    cause.printStackTrace();
                })
                .exec();
    }
}
