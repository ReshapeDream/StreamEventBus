package com.nj.eventbus;

public interface Bus<T extends Bus<?>> {
    /**
     * 标记顺序执行
     * 
     * @return
     */
    T sequential();

    /**
     * 并行
     * 
     * @return
     */
    T parallel();

    /**
     * 并行
     * 
     * @return
     */
    T parallel(int parallelism);

    /**
     * 返回是否顺序执行
     * 
     * @return
     */
    boolean isParallel();

    /**
     * post a event to a topic
     * 
     * @param event
     * @param topic
     */
    T post(Event event);

    /**
     * close the bus
     */
    void close();

    /**
     * return the bus name
     * 
     * @return
     */
    String getBusName();
}
