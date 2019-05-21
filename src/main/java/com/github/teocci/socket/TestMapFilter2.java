package com.github.teocci.socket;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-May-21
 */
public class TestMapFilter2
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

        // Map -> Stream -> Filter -> Map
        Map<Integer, String> collect = HOSTING.entrySet().stream()
                .filter(map -> map.getKey() == 2)
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
        // output : {2=heroku.com}
        System.out.println(collect);

        Map<Integer, String> collect2 = HOSTING.entrySet().stream()
                .filter(map -> map.getKey() <= 3)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // output : {1=linode.com, 2=heroku.com, 3=digitalocean.com}
        System.out.println(collect2);
    }
}
