package com.github.teocci.socket.test;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by teocci. Based on https://www.mkyong.com/java8/java-8-filter-a-map-examples/
 *
 * @author teocci@yandex.com on 2019-May-21
 */
public class TestMapFilter
{
    public static void main(String[] args)
    {
        Map<Integer, String> HOSTING = new HashMap<>();
        HOSTING.put(1, "linode.com");
        HOSTING.put(2, "heroku.com");
        HOSTING.put(3, "digitalocean.com");
        HOSTING.put(4, "aws.amazon.com");
        HOSTING.put(5, "aws2.amazon.com");
        HOSTING.put(6, "google.com");
        HOSTING.put(7, "console.google.com");
        HOSTING.put(8, "naver.com");
        HOSTING.put(9, "yandex.com");

        // Before Java 8
        String result = "";
        for (Map.Entry<Integer, String> entry : HOSTING.entrySet()) {
            if ("aws.amazon.com".equals(entry.getValue())) {
                result = entry.getValue();
            }
        }
        System.out.println("Before Java 8 : " + result);

        //Map -> Stream -> Filter -> String
        result = HOSTING.entrySet()
                .stream()
                .filter(map -> "aws.amazon.com".equals(map.getValue()))
                .map(map -> map.getValue())
                .collect(Collectors.joining());

        System.out.println("With Java 8 : " + result);

        // filter more values
        result = HOSTING
                .entrySet()
                .stream()
                .filter(x -> (!x.getValue().contains("amazon") && !x.getValue().contains("digital")))
                .map(map -> map.getValue())
                .collect(Collectors.joining(","));

        System.out.println("With Java 8 : " + result);
    }
}
