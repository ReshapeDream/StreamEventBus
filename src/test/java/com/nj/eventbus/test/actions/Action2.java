package com.nj.eventbus.test.actions;

import java.time.LocalDateTime;

import com.nj.eventbus.Subscribe;
import com.nj.eventbus.test.TestUtil;

public class Action2 {

    @Subscribe
    public void action2(String src) {
        System.out.printf("[%s]-[%s] action2 [%s]\n",
                LocalDateTime.now(),
                Thread.currentThread().getName(),
                src);
        TestUtil.sleep();
    }
}
