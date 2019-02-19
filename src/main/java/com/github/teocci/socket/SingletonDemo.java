package com.github.teocci.socket;

import com.github.teocci.socket.utils.RunnableSingleton;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-Jan-30
 */
public class SingletonDemo
{
    public static void main(String[] args) {
        System.out.println("test");
        RunnableSingleton t = RunnableSingleton.getInstance();
        Thread t1 = new Thread(t);
        Thread t2 = new Thread(t);
        Thread t3 = new Thread(t);
        t1.start();
        t2.start();
        t3.start();
    }
}
