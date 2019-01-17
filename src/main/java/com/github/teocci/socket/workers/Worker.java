package com.github.teocci.socket.workers;

import com.github.teocci.socket.net.TcpClient;

import java.util.TimerTask;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-Jan-17
 */
public class Worker extends TimerTask
{
    private final TcpClient thread;

    private volatile boolean running = false;

    public Worker(TcpClient thread)
    {
        this.thread = thread;
    }

    public void run()
    {
        running = true;

        if (thread != null) {
            System.out.println("Worker");
            thread.sendUpdate();
        }
    }

    public void stop()
    {
        running = false;
    }


    public boolean isRunning()
    {
        return running;
    }
}
