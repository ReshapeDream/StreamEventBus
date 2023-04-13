package com.nj.eventbus.test.actions;

import java.time.LocalDateTime;

import com.nj.eventbus.Subscribe;
import com.nj.eventbus.test.TestUtil;

public class Action1 {

    @Subscribe
    public void action10(String[] src) {
        System.out.printf("[%s]-[%s] action10:[%s] to [%s]\n",
                LocalDateTime.now(), Thread.currentThread().getName(),
                src[0], src[1]);
        TestUtil.sleep();
        // int k = 1 / 0;
    }

    @Subscribe
    public void action11(String ok) {
        System.out.printf("[%s]-[%s] action11:[%s]\n",
                LocalDateTime.now(), Thread.currentThread().getName(),
                ok);
        TestUtil.sleep();
        // int k = 1 / 0;
    }

}
