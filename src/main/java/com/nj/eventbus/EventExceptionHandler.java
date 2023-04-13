package com.nj.eventbus;

@FunctionalInterface
public interface EventExceptionHandler {
    void handle(Throwable cause, InvokeInfo context);

    default void Handle(Throwable cause, Event event) {
        System.out.println(event);
        cause.printStackTrace();
    }
}
