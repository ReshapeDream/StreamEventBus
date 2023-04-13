package com.nj.eventbus.test.actions;

import java.time.LocalDateTime;

import com.nj.eventbus.Subscribe;
import com.nj.eventbus.test.TestUtil;

public class Action0 {
    @Subscribe
    public void action0(String src) {
        System.out.printf("[%s]-[%s] action0:[%s]\n", LocalDateTime.now(), Thread.currentThread().getName(), src);
        TestUtil.sleep();
        int k = 1 / 0;
        System.out.println(k);
    }
}
