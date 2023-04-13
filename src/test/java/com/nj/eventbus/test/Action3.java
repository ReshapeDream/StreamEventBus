package com.nj.eventbus.test;

import java.time.LocalDateTime;

import com.nj.eventbus.Subscribe;

public class Action3 {
    @Subscribe
    public void action3(Integer log) {
        System.out.printf("[%s]-[%s] action3: %s\n",
                LocalDateTime.now(),
                Thread.currentThread().getName(),
                log);
        TestUtil.sleep();
    }
}
