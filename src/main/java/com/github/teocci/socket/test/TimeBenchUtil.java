package com.github.teocci.socket.test;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.time.StopWatch;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-May-22
 */
public class TimeBenchUtil
{
    public static void main(String[] args) throws InterruptedException
    {
        stopWatch();
        stopWatchGuava();
        stopWatchApacheCommons();
        stopWatchCurrentTimeMillis();
        stopWatchInstantNow();
        stopWatchDate();
        stopWatchCalendar();
    }

    public static void stopWatch() throws InterruptedException
    {
        long endTime, timeElapsed, startTime = System.nanoTime();

        /* ... the code being measured starts ... */

        // sleep for 5 seconds
        TimeUnit.SECONDS.sleep(5);

        /* ... the code being measured ends ... */

        endTime = System.nanoTime();

        // get difference of two nanoTime values
        timeElapsed = endTime - startTime;

        System.out.println("Execution time in nanoseconds   : " + timeElapsed);
//        System.out.println("Execution time in milliseconds  : " + timeElapsed / 1000000);
    }

    public static void stopWatchGuava() throws InterruptedException
    {
        // Creates and starts a new stopwatch
        Stopwatch stopwatch = Stopwatch.createStarted();

        /* ... the code being measured starts ... */

        // sleep for 5 seconds
        TimeUnit.SECONDS.sleep(5);
        /* ... the code being measured ends ... */

        stopwatch.stop();    // optional

        // get elapsed time, expressed in milliseconds
        long timeElapsed = stopwatch.elapsed(TimeUnit.NANOSECONDS);

        System.out.println("Execution time in nanoseconds   : " + timeElapsed);
    }

    public static void stopWatchApacheCommons() throws InterruptedException
    {
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();

        /* ... the code being measured starts ... */

        // sleep for 5 seconds
        TimeUnit.SECONDS.sleep(5);

        /* ... the code being measured ends ... */

        stopwatch.stop();    // Optional

        long timeElapsed = stopwatch.getNanoTime();

        System.out.println("Execution time in nanoseconds   : " + timeElapsed);
    }

    public static void stopWatchCurrentTimeMillis() throws InterruptedException
    {
        long startTime = System.currentTimeMillis();

        /* ... the code being measured starts ... */

        // sleep for 5 seconds
        TimeUnit.SECONDS.sleep(5);

        /* ... the code being measured ends ... */

        long endTime = System.currentTimeMillis();

        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in milliseconds  : " + timeElapsed);
    }

    public static void stopWatchInstantNow() throws InterruptedException
    {
        long startTime = Instant.now().toEpochMilli();

        /* ... the code being measured starts ... */

        // sleep for 5 seconds
        TimeUnit.SECONDS.sleep(5);

        /* ... the code being measured ends ... */

        long endTime = Instant.now().toEpochMilli();

        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in milliseconds  : " + timeElapsed);
    }


    public static void stopWatchDate() throws InterruptedException
    {
        long startTime = new Date().getTime();

        /* ... the code being measured starts ... */

        // sleep for 5 seconds
        TimeUnit.SECONDS.sleep(5);

        /* ... the code being measured ends ... */

        long endTime = new Date().getTime();

        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in milliseconds  : " + timeElapsed);

    }

    public static void stopWatchCalendar() throws InterruptedException
    {
        long startTime = Calendar.getInstance().getTime().getTime();

        /* ... the code being measured starts ... */

        // sleep for 5 seconds
        TimeUnit.SECONDS.sleep(5);

        /* ... the code being measured ends ... */

        long endTime = Calendar.getInstance().getTime().getTime();

        long timeElapsed = endTime - startTime;

        System.out.println("Execution time in milliseconds  : " + timeElapsed);
    }
}
