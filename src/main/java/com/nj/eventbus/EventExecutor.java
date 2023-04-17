package com.nj.eventbus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

class EventExecutor {

    private ConcurrentHashMap<Class<?>, Object> instances = new ConcurrentHashMap<>();
    private AtomicBoolean occerr = new AtomicBoolean(false);
    private AtomicBoolean ignorException = new AtomicBoolean(false);

    public void exec(Executor executor, List<Event> events, boolean order) {
        this.exec(executor, events, null, order, false);
    }

    /**
     * 依次执行所有的event
     * 
     * @param executor
     * @param events
     * @param exceptionHandler
     * @param parallel
     * @param ignoreException
     */
    public void exec(Executor executor, List<Event> events, EventExceptionHandler exceptionHandler, boolean parallel,
            boolean ignoreException) {
        this.ignorException.set(ignoreException);
        Stream<Event> stream = events.stream();
        if (parallel) {
            stream = stream.parallel();
        }
        stream.map(event -> createRunnables(event, exceptionHandler))
                .forEach(rs -> {
                    for (Runnable runnable : rs) {
                        executor.execute(runnable);
                    }
                });
    }

    /**
     * 构造出Event的所有Runnable对象。
     * 
     * @param event
     * @param exceptionHandler
     * @return
     */
    private List<? extends Runnable> createRunnables(Event event, EventExceptionHandler exceptionHandler) {
        Class<?> topic = event.getTopic();
        Object param = event.getEvent();
        instances.computeIfAbsent(topic, key -> createInstance(key, exceptionHandler));
        return getMethods(event)
                .stream()
                .filter(m -> m != null)
                // 默认定义顺序
                // .sorted((m1, m2) -> {
                // return m1.getName().compareTo(m2.getName());
                // })
                .map(m -> new EventRunnable(event, instances.get(topic), m, param, exceptionHandler))
                .toList();
    }

    /**
     * 获取所有符合的方法
     * 
     * @param topic
     * @param param
     * @return
     */
    private List<Method> getMethods(Event event) {
        Class<?> topic = event.getTopic();
        Object param = event.getEvent();
        Class<?> paramClass = null;
        if (param != null) {
            paramClass = param.getClass();

        } else {
            if (event.getPre() == null) {
                throw new NullPointerException("param null");
            }
            paramClass = event.getPre().getEventMethods().get(event.getPreResultIndex()).getReturnType();
        }
        Class<?> fpClass = Util.getWrapperClass(paramClass);
        event.addMethods(Arrays.asList(topic.getDeclaredMethods())
                .stream()
                .filter(m -> m.isAnnotationPresent(Subscribe.class)
                        && m.getParameterCount() == 1
                        && Util.getWrapperClass(m.getParameterTypes()[0]).isAssignableFrom(fpClass))
                .toList());
        return event.getEventMethods();
    }

    /**
     * 创建topic 的实例。
     * 
     * @param key
     * @param exceptionHandler
     * @return
     */
    private Object createInstance(Class<?> key, EventExceptionHandler exceptionHandler) {
        try {
            return key.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            occerr.set(true);
            if (exceptionHandler != null) {
                exceptionHandler.handle(e, null);
            }
            return null;
        }
    }

    private class EventRunnable implements Runnable {

        private final Event event;
        private final Object obj;
        private final Method method;
        private final Object param;
        private final EventExceptionHandler exceptionHandler;

        public EventRunnable(Event event, Object obj, Method method, Object param,
                EventExceptionHandler exceptionHandler) {
            this.event = event;
            this.obj = obj;
            this.method = method;
            this.param = param;
            this.exceptionHandler = exceptionHandler;
        }

        @Override
        public void run() {
            try {
                if (!ignorException.get() && occerr.get())
                    return;
                Object tParam = param != null ? param : event.getPre().getResult().get(event.getPreResultIndex());
                Object invoke = method.invoke(obj, tParam);
                event.addResult(invoke);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                if (exceptionHandler != null)
                    exceptionHandler.handle(e, new InvokeInfo(obj, method, param));
                occerr.set(true);
            }
        }
    }
}
