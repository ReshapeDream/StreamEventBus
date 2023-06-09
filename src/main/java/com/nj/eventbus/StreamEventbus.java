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
     * 是否可并行 Parallelizable
     */
    private boolean isParallelizable = true;

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
        this.isParallel = false;
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

    /**
     * @param topic
     * @param preEventResultIndex 上次event结果的坐标
     * @return
     */
    public StreamEventbus next(Class<?> topic, int preEventResultIndex) {
        if (events.size() < 1)
            throw new RuntimeException("No pre event to use.");
        Event event = new Event(topic, null);
        event.setPreResultIndex(preEventResultIndex);
        Event preEvent = events.getLast();
        event.setPre(preEvent);
        preEvent.setNext(event);
        events.addLast(event);
        // 因为需要取到前一个event的结果。不可以并行
        isParallelizable = false;
        return this;
    }

    public StreamEventbus next(Class<?> topic) {
        return next(topic, 0);
    }

    public void exec() {
        if (executor == null) {
            executor = isParallel && isParallelizable
                    ? Executors.newFixedThreadPool(Math.max(parallelism, Runtime.getRuntime().availableProcessors()),
                            new ExecutorThreadFactory(exceptionHandler))
                    : Executors.newSingleThreadExecutor(new ExecutorThreadFactory(exceptionHandler));
        }
        new EventExecutor().exec(executor, events, exceptionHandler, isParallel && isParallelizable, ignoreException);
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

        public ExecutorThreadFactory(EventExceptionHandler exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("event-pool-" + t.hashCode());
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
