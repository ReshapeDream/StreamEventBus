package com.nj.eventbus.test.actions;

import java.time.LocalDateTime;

import com.nj.eventbus.Subscribe;
import com.nj.eventbus.test.TestUtil;



public class Action4 {

    @Subscribe
    public void action4(int src) {
        System.out.printf("[%s]-[%s] action4:[%s] to hdfs\n",
                LocalDateTime.now(),
                Thread.currentThread().getName(),
                src);
        TestUtil.sleep();
    }
}
