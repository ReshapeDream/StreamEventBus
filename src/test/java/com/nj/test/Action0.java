package com.nj.test;

import java.time.LocalDateTime;

import com.nj.eventbus.Subscribe;

public class Action0 {
    @Subscribe
    public int action0(String src) {
        System.out.printf("[%s]-[%s] action0:[%s]\n", LocalDateTime.now(), Thread.currentThread().getName(), src);
        TestUtil.sleep();
        return 0;
    }

    @Subscribe
    public int action02(String src) {
        System.out.printf("[%s]-[%s] action02:[%s]\n", LocalDateTime.now(), Thread.currentThread().getName(), src);
        TestUtil.sleep();
        return 1;
    }

    @Subscribe
    public float actionInt(int src) {
        System.out.printf("[%s]-[%s] actionInt:[%s]\n", LocalDateTime.now(), Thread.currentThread().getName(), src);
        TestUtil.sleep();
        // return "action00";
        return 1.1f;
    }

    @Subscribe
    public void actionFloat(float src) {
        System.out.printf("[%s]-[%s] actionFloat:[%s]\n", LocalDateTime.now(), Thread.currentThread().getName(), src);
        TestUtil.sleep();
    }
}
