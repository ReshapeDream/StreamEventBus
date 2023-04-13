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

    private List<? extends Runnable> createRunnables(Event event, EventExceptionHandler exceptionHandler) {
        Class<?> topic = event.getTopic();
        Object param = event.getEvent();
        instances.computeIfAbsent(topic, key -> createInstance(key, exceptionHandler));
        return getMethods(topic, param)
                .stream()
                .filter(m -> m != null)
                .map(m -> new EventRunnable(instances.get(topic), m, param, exceptionHandler))
                .toList();
    }

    private List<Method> getMethods(Class<?> topic, Object param) {
        return Arrays.asList(topic.getDeclaredMethods())
                .stream()
                .filter(m -> m.isAnnotationPresent(Subscribe.class)
                        && m.getParameterCount() == 1
                        && Util.getWrapperClass(m.getParameterTypes()[0]).isAssignableFrom(param.getClass()))
                .toList();
    }

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

        private final Object obj;
        private final Method method;
        private final Object param;
        private final EventExceptionHandler exceptionHandler;

        public EventRunnable(Object obj, Method method, Object param, EventExceptionHandler exceptionHandler) {
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
                method.invoke(obj, param);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                if (exceptionHandler != null)
                    exceptionHandler.handle(e, new InvokeInfo(obj, method, param));
                occerr.set(true);
            }
        }
    }
}
