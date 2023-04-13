package com.nj.eventbus.test.actions;

import java.time.LocalDateTime;

import com.nj.eventbus.Subscribe;
import com.nj.eventbus.test.TestUtil;

public class Action3 {
    @Subscribe
    public void action3(Integer log) {
        System.out.printf("[%s]-[%s] update log: %s\n",
                LocalDateTime.now(),
                Thread.currentThread().getName(),
                log);
        TestUtil.sleep();
    }
}
