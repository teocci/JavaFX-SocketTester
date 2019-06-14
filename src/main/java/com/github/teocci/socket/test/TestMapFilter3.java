package com.github.teocci.socket.test;

import com.github.teocci.socket.model.Hosting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-May-21
 */
public class TestMapFilter3
{
    /**
     * Generic Map filterByValue, with predicate
     *
     * @param map the original map
     * @param predicate predicate to be applied
     * @param <K> key type
     * @param <V>  value type
     * @return a sub-map after filter the map with a predicate
     */
    public static <K, V> Map<K, V> filterByValue(Map<K, V> map, Predicate<V> predicate)
    {
        return map.entrySet()
                .stream()
                .filter(x -> predicate.test(x.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

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

        //  {1=linode.com}
        Map<Integer, String> filteredMap = filterByValue(HOSTING, x -> x.contains("linode"));
        System.out.println(filteredMap);

        // {1=linode.com, 4=aws.amazon.com, 5=aws2.amazon.com}
        Map<Integer, String> filteredMap2 = filterByValue(HOSTING, x -> (x.contains("aws") || x.contains("linode")));
        System.out.println(filteredMap2);

        // {4=aws.amazon.com}
        Map<Integer, String> filteredMap3 = filterByValue(HOSTING, x -> (x.contains("aws") && !x.contains("aws2")));
        System.out.println(filteredMap3);

        // {1=linode.com, 2=heroku.com, 6=google.com, 8=naver.com, 9=yandex.com}
        Map<Integer, String> filteredMap4 = filterByValue(HOSTING, x -> (x.length() <= 10));
        System.out.println(filteredMap4);


        List<Hosting> list = new ArrayList<>();
        list.add(new Hosting(1, "liquidweb.com", 80000));
        list.add(new Hosting(2, "linode.com", 90000));
        list.add(new Hosting(3, "digitalocean.com", 120000));
        list.add(new Hosting(4, "aws.amazon.com", 200000));
        list.add(new Hosting(5, "mkyong.com", 1));

        list.add(new Hosting(6, "linode.com", 100000)); // new line

        // key = name, value - websites , but the key 'linode' is duplicated!?
        Map<String, Long> result1 = list.stream().collect(
                Collectors.toMap(Hosting::getName, Hosting::getWebsites));

        System.out.println("Result 1 : " + result1);

        Map<String, Long> result2 = list.stream().collect(
                Collectors.toMap(Hosting::getName, Hosting::getWebsites,
                        (oldValue, newValue) -> oldValue
                )
        );

        System.out.println("Result 2 : " + result2);
    }
}