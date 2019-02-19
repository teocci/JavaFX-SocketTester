package com.github.teocci.socket.utils;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-Jan-30
 */
public class RunnableSingleton implements Runnable
{
    private static volatile RunnableSingleton instance;
    private static final Object mutex = new Object();

    private RunnableSingleton() {}

    public static RunnableSingleton getInstance()
    {
        RunnableSingleton result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null) {
                    System.out.println("Creating instance");
                    instance = result = new RunnableSingleton();
                }
            }
        }

        return result;
    }

    @Override
    public void run()
    {
        System.out.format("Thread %s is starting\n", Thread.currentThread().getName());
        getInstance();
    }
}
