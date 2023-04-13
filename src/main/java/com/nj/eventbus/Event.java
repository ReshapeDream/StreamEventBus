package com.nj.eventbus;

public class Event {
    
    private Class<?> topic;
    private Object event;

    public Event(Class<?> topic, Object event) {
        this.topic = topic;
        this.event = event;
    }

    public Class<?> getTopic() {
        return topic;
    }

    public Object getEvent() {
        return event;
    }
}
