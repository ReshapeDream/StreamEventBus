package com.nj.test;

import java.time.LocalDateTime;

import com.nj.eventbus.Subscribe;

public class Action1 {

    @Subscribe
    public void action10(String[] src) {
        System.out.printf("[%s]-[%s] action10:[%s] to [%s]\n",
                LocalDateTime.now(), Thread.currentThread().getName(),
                src[0], src[1]);
        TestUtil.sleep();
    }

    @Subscribe
    public void action11(String ok) {
        System.out.printf("[%s]-[%s] action11:[%s]\n",
                LocalDateTime.now(), Thread.currentThread().getName(),
                ok);
        TestUtil.sleep();
    }

}
