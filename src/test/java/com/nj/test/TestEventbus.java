package com.nj.test;

import org.junit.jupiter.api.Test;

import com.nj.eventbus.StreamEventbus;
import com.nj.eventbus.test.Action0;
import com.nj.eventbus.test.Action1;
import com.nj.eventbus.test.Action2;
import com.nj.eventbus.test.Action3;
import com.nj.eventbus.test.Action4;

public class TestEventbus {

    @Test
    void tes(){
        System.out.println("=================");
    }

    @Test
    void testParallel() {
        StreamEventbus.of("parallel")
                // .sequential()
                .parallel(10)
                // .ignoreException()
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
