package com.nj.eventbus.test;

import java.time.LocalDateTime;

import com.nj.eventbus.Subscribe;



public class Action4 {

    @Subscribe
    public void action4(int src) {
        System.out.printf("[%s]-[%s] action4:[%s]\n",
                LocalDateTime.now(),
                Thread.currentThread().getName(),
                src);
        TestUtil.sleep();
    }
}
