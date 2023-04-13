package com.nj.eventbus;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class StreamEventbus implements Bus<StreamEventbus> {

    /**
     * 定义名称
     */
    private String busName;

    /**
     * 错误处理器
     */
    private EventExceptionHandler exceptionHandler;
    /**
     * 执行器
     */
    private Executor executor;

    /**
     * 是否并行
     */
    private boolean isParallel = false;

    /**
     * 并行度
     */
    private int parallelism = 0;
    /**
     * 是否忽略错误
     */
    private boolean ignoreException = false;

    private LinkedList<Event> events = new LinkedList<>();

    public static StreamEventbus of(String name) {
        return new StreamEventbus(name);
    }

    private StreamEventbus(String name) {
        this.busName = name;
    }

    @Override
    public StreamEventbus sequential() {
        this.isParallel = true;
        return this;
    }

    @Override
    public StreamEventbus parallel() {
        this.isParallel = true;
        return this;
    }

    @Override
    public StreamEventbus parallel(int parallelism) {
        this.isParallel = true;
        this.parallelism = parallelism;
        return this;
    }

    @Override
    public boolean isParallel() {
        return isParallel;
    }

    public StreamEventbus ignoreException() {
        this.ignoreException = true;
        return this;
    }

    public StreamEventbus post(Class<?> topic, Object event) {
        return post(new Event(topic, event));
    }

    public StreamEventbus post(Event event) {
        events.addLast(event);
        return this;
    }

    public void exec() {
        if (executor == null) {
            executor = !isParallel ? Executors.newSingleThreadExecutor(new ExecutorThreadFactory(exceptionHandler))
                    : Executors.newFixedThreadPool(Math.max(parallelism, Runtime.getRuntime().availableProcessors()),
                            new ExecutorThreadFactory(exceptionHandler));
        }
        new EventExecutor().exec(executor, events, exceptionHandler, isParallel, ignoreException);
        close();
    }

    public StreamEventbus exception(EventExceptionHandler handler) {
        this.exceptionHandler = handler;
        return this;
    }

    public StreamEventbus executor(Executor executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public void close() {
        if (executor != null && executor instanceof ExecutorService) {
            ((ExecutorService) executor).shutdown();
        }
    }

    @Override
    public String getBusName() {
        return this.busName;
    }

    private class ExecutorThreadFactory implements ThreadFactory {

        private final EventExceptionHandler exceptionHandler;
        private final AtomicInteger seq = new AtomicInteger(0);

        public ExecutorThreadFactory(EventExceptionHandler exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "event-pool-" + seq.getAndIncrement());
            t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    if (exceptionHandler != null) {
                        exceptionHandler.handle(e, null);
                    }
                }
            });
            return t;
        }
    }
}
