package com.nj.eventbus;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Event {

    private Class<?> topic;
    private Object event;
    private Event next;
    private Event pre;
    private int preResultIndex;

    private CopyOnWriteArrayList<Method> eventMethods;

    private CopyOnWriteArrayList<Object> results;

    public Event(Class<?> topic, Object event) {
        this.topic = topic;
        this.event = event;
        results = new CopyOnWriteArrayList<>();
        eventMethods = new CopyOnWriteArrayList<>();
    }

    public Class<?> getTopic() {
        return topic;
    }

    public Object getEvent() {
        return event;
    }

    public void setEvent(Object event) {
        this.event = event;
    }

    public Event getNext() {
        return next;
    }

    public void setNext(Event next) {
        this.next = next;
    }

    public Event getPre() {
        return pre;
    }

    public void setPre(Event pre) {
        this.pre = pre;
    }

    public List<Object> getResult() {
        return Collections.unmodifiableList(results);
    }

    public void addResult(Object result) {
        results.add(result);
    }

    public int getPreResultIndex() {
        return preResultIndex;
    }

    public void setPreResultIndex(int preResultIndex) {
        this.preResultIndex = preResultIndex;
    }

    public void addMethod(Method method) {
        this.eventMethods.add(method);
    }

    public void addMethods(List<Method> methods) {
        this.eventMethods.addAll(methods);
    }

    public List<Method> getEventMethods() {
        return Collections.unmodifiableList(eventMethods);
    }
}
