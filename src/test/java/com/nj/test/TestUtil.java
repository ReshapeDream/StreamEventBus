package com.nj.test;

import java.util.concurrent.TimeUnit;

public class TestUtil {
    public static void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
