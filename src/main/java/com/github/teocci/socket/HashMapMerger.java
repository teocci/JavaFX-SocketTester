package com.github.teocci.socket;

import com.github.teocci.socket.model.Hosting;
import com.github.teocci.socket.model.Item;
import org.apache.commons.lang3.time.StopWatch;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-May-17
 */
public class HashMapMerger
{

    public static void main(String[] args)
    {
        Map<Long, Item> itemMap = Stream.of(
                entry(0L, new Item(2558584)),
                entry(1L, new Item(254243232)),
                entry(2L, new Item(986786)),
                entry(3L, new Item(672542)),
                entry(4L, new Item(4846)),
                entry(5L, new Item(76867467)),
                entry(6L, new Item(986786)),
                entry(7L, new Item(7969768)),
                entry(8L, new Item(68868486)),
                entry(9L, new Item(923)),
                entry(10L, new Item(986786)),
                entry(11L, new Item(549768)),
                entry(12L, new Item(796168)),
                entry(13L, new Item(868421)),
                entry(14L, new Item(923)),
                entry(15L, new Item(986786)),
                entry(16L, new Item(549768)),
                entry(17L, new Item(4846)),
                entry(18L, new Item(4846)),
                entry(19L, new Item(76867467)),
                entry(20L, new Item(986786)),
                entry(21L, new Item(7969768)),
                entry(22L, new Item(923)),
                entry(23L, new Item(4846)),
                entry(24L, new Item(986786)),
                entry(25L, new Item(549768))
        ).collect(entriesToMap());

//        Map<Long, Item> itemMap = Stream
//                .of(
//                        new AbstractMap.SimpleImmutableEntry<>(0L, new Item(2558584)),
//                        new AbstractMap.SimpleImmutableEntry<>(1L, new Item(254243232)),
//                        new AbstractMap.SimpleImmutableEntry<>(2L, new Item(986786)),
//                        new AbstractMap.SimpleImmutableEntry<>(3L, new Item(672542)),
//                        new AbstractMap.SimpleImmutableEntry<>(4L, new Item(4846)),
//                        new AbstractMap.SimpleImmutableEntry<>(5L, new Item(76867467)),
//                        new AbstractMap.SimpleImmutableEntry<>(6L, new Item(986786)),
//                        new AbstractMap.SimpleImmutableEntry<>(7L, new Item(7969768)),
//                        new AbstractMap.SimpleImmutableEntry<>(8L, new Item(68868486)),
//                        new AbstractMap.SimpleImmutableEntry<>(9L, new Item(923)),
//                        new AbstractMap.SimpleImmutableEntry<>(10L, new Item(986786)),
//                        new AbstractMap.SimpleImmutableEntry<>(11L, new Item(549768)),
//                        new AbstractMap.SimpleImmutableEntry<>(12L, new Item(796168)),
//                        new AbstractMap.SimpleImmutableEntry<>(13L, new Item(868421)),
//                        new AbstractMap.SimpleImmutableEntry<>(14L, new Item(923)),
//                        new AbstractMap.SimpleImmutableEntry<>(15L, new Item(986786)),
//                        new AbstractMap.SimpleImmutableEntry<>(16L, new Item(549768)),
//                        new AbstractMap.SimpleImmutableEntry<>(17L, new Item(4846)),
//                        new AbstractMap.SimpleImmutableEntry<>(18L, new Item(4846)),
//                        new AbstractMap.SimpleImmutableEntry<>(19L, new Item(76867467)),
//                        new AbstractMap.SimpleImmutableEntry<>(20L, new Item(986786)),
//                        new AbstractMap.SimpleImmutableEntry<>(21L, new Item(7969768)),
//                        new AbstractMap.SimpleImmutableEntry<>(22L, new Item(923)),
//                        new AbstractMap.SimpleImmutableEntry<>(23L, new Item(4846)),
//                        new AbstractMap.SimpleImmutableEntry<>(24L, new Item(986786)),
//                        new AbstractMap.SimpleImmutableEntry<>(25L, new Item(549768))
//                ).collect(entriesToMap());

//        Map<Long, Item> itemMap = new HashMap()
//        {
//            {
//                put(0L, new Item(2558584));
//                put(1L, new Item(254243232));
//                put(2L, new Item(986786));
//                put(3L, new Item(672542));
//                put(4L, new Item(4846));
//                put(5L, new Item(76867467));
//                put(6L, new Item(986786));
//                put(7L, new Item(7969768));
//                put(8L, new Item(68868486));
//                put(9L, new Item(923));
//                put(10L, new Item(986786));
//                put(11L, new Item(549768));
//                put(12L, new Item(796168));
//                put(13L, new Item(868421));
//                put(14L, new Item(923));
//                put(15L, new Item(986786));
//                put(16L, new Item(549768));
//                put(17L, new Item(4846));
//                put(18L, new Item(4846));
//                put(19L, new Item(76867467));
//                put(20L, new Item(986786));
//                put(21L, new Item(7969768));
//                put(22L, new Item(923));
//                put(23L, new Item(4846));
//                put(24L, new Item(986786));
//                put(25L, new Item(549768));
//            }
//        };


        StopWatch watch = new StopWatch();
        watch.start();
        System.out.println("solutionForeach()");

        solutionForeach(itemMap);
        watch.stop();
        System.out.println("Code took " + (watch.getNanoTime()) + " nano seconds");
        System.out.println();

        watch.reset();
        watch.start();
        System.out.println("solutionForeachV2()");

        solutionForeachV2(itemMap);
        watch.stop();
        System.out.println("Code took " + (watch.getNanoTime()) + " nano seconds");
        System.out.println();

        watch.reset();
        watch.start();
        System.out.println("solutionFor()");

        solutionFor(itemMap);
        watch.stop();
        System.out.println("Code took " + (watch.getNanoTime()) + " nano seconds");
        System.out.println();

        watch.reset();
        watch.start();
        System.out.println("solutionStream()");

        solutionStream(itemMap);
        watch.stop();
        System.out.println("Code took " + (watch.getNanoTime()) + " nano seconds");
        System.out.println();

        watch.reset();
        watch.start();
        System.out.println("solutionIterator()");

        solutionIterator(itemMap);
        watch.stop();
        System.out.println("Code took " + (watch.getNanoTime()) + " nano seconds");
        System.out.println();


        List<Date> items = new ArrayList<>(Arrays.asList(
                new Date(),
                new Date(),
                new Date()
        ));

        List<Date> dates = filter(Date.class, items);
        System.out.println(dates);
    }

    private static Map<String, Hosting> list2Map(List<Hosting> list)
    {
        final Map<String, Hosting> result = new HashMap<>();
        list.forEach(item -> result.put(item.getName(), item));

        final Map<String, Hosting> result1 = list
                .stream()
                .collect(Collectors.toMap(Hosting::getName, Function.identity()));

        System.out.println("List2Map: ");
        System.out.println(result1);

        Map<String, List<Hosting>> result2 = list
                .stream()
                .collect(Collectors.groupingBy(Hosting::getName));

        System.out.println("List2Map: ");
        System.out.println(result2);

        result2 = list
                .stream()
                .collect(Collectors.groupingBy(Hosting::getName));

        System.out.println("List2Map: ");
        System.out.println(result2);

        Map<String, Hosting> result3 = list
                .stream()
                .collect(HashMap::new, (m, c) -> m.put(c.getName(), c), (m, u) -> {});

        System.out.println("List2Map: ");
        System.out.println(result3);


        return result3;
    }

    private static void solutionForeach(Map<Long, Item> itemMap)
    {
        Map<Long, Item> fooMap = itemMap
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new Item(e.getValue().getId())));

        List<Long> toRemove = new ArrayList<>();
        fooMap.forEach((key, item) -> {
            boolean shouldFind = true;
            System.out.format("key: %s, value: %s\n", key, item);
            if (!toRemove.isEmpty()) {
                shouldFind = !toRemove.contains(key);
            }
            if (shouldFind) {
                List<Long> duplicates = findDuplicates(fooMap, key, item);
                if (!duplicates.isEmpty()) {
                    toRemove.addAll(duplicates);
                }
            }
        });

        System.out.println("Before deletion: ");
        System.out.println(fooMap);
        toRemove.forEach(fooMap::remove);

        System.out.println("After deletion: ");
        System.out.println(fooMap);
//        fooMap.forEach((key, item) -> {
//            System.out.format("key: %s, value: %s\n", key, item);
//        });
    }

    private static void solutionForeachV2(Map<Long, Item> itemMap)
    {
        Map<Long, Item> fooMap = itemMap
                .entrySet()
                .stream()
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, e -> new Item(e.getValue().getId())));
        Map<Long, Long> valueMapper = new HashMap<>();

        System.out.println("Before deletion: ");
        System.out.println(fooMap);

        fooMap.forEach((key, item) -> {
            Long index = valueMapper.get(item.getId());
            if (index != null) {
                Item accumulator = fooMap.get(index);
                accumulator.merge(item);
                fooMap.remove(key);
            } else {
                valueMapper.put(item.getId(), key);
            }
        });

        System.out.println("After deletion: ");
        System.out.println(fooMap);
    }

    private static void solutionFor(Map<Long, Item> itemMap)
    {
        Map<Long, Item> fooMap = itemMap
                .entrySet()
                .stream()
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, e -> new Item(e.getValue().getId())));
        Map<Long, Long> valueMapper = new HashMap<>();

        System.out.println("Before deletion: ");
        System.out.println(fooMap);


        Set<Map.Entry<Long, Item>> entries = fooMap.entrySet();
        for (Map.Entry<Long, Item> entry : entries) {
            Long index = valueMapper.get(entry.getValue().getId());
            if (index != null) {
                Item accumulator = fooMap.get(index);
                accumulator.merge(entry.getValue());
                fooMap.remove(entry.getKey());
            } else {
                valueMapper.put(entry.getValue().getId(), entry.getKey());
            }
        }

//        int size = fooMap.size();
//
//        for (int i = 0; i < size; i++) {
//            Long key = (long) i;
//            Item item = fooMap.get(key);
//            Long index = valueMapper.get(item.getId());
//            if (index != null) {
//                Item accumulator = fooMap.get(index);
//                accumulator.merge(item);
//                fooMap.remove(key);
//            } else {
//                valueMapper.put(item.getId(), key);
//            }
//        }

        System.out.println("After deletion: ");
        System.out.println(fooMap);
    }

    private static void solutionStream(Map<Long, Item> itemMap)
    {
        Map<Long, Item> fooMap = itemMap
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new Item(e.getValue().getId())));
//        Map<Long, Item> fooMap = SerializationUtils.clone(itemMap);
        Map<Long, Long> valueMapper = new HashMap<>();

        System.out.println("Before deletion: ");
        System.out.println(fooMap);

//        Iterator<Map.Entry<Long, Item>> iterator = fooMap.entrySet().iterator();
        Map<Long, Item> result = fooMap.entrySet()
                .stream()
                .filter(entry -> {
                    Long index = valueMapper.get(entry.getValue().getId());
                    if (index != null) {
                        Item item = fooMap.get(index);
                        item.merge(entry.getValue());
                        return false;
                    } else {
                        valueMapper.put(entry.getValue().getId(), entry.getKey());
                    }
                    return true;
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        System.out.println("After deletion: ");
        System.out.println(result);
    }

    private static void solutionIterator(Map<Long, Item> itemMap)
    {
        Map<Long, Item> fooMap = itemMap
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new Item(e.getValue().getId())));

        Map<Long, Long> reversed = new HashMap<>();

        System.out.println("Before deletion: ");
        System.out.println(fooMap);

        Iterator<Map.Entry<Long, Item>> iterator = fooMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Item> pair = iterator.next();
            Long itemKey = reversed.get(pair.getValue().getId());
            if (itemKey != null) {
                Item item = fooMap.get(itemKey);
                item.merge(pair.getValue());
                iterator.remove();
            } else {
                reversed.put(pair.getValue().getId(), pair.getKey());
            }
        }

        System.out.println("After deletion: ");
        System.out.println(fooMap);
    }

    private static List<Long> findDuplicates(Map<Long, Item> itemMap, Long key, Item item)
    {
        List<Long> results = new ArrayList<>();

        itemMap.forEach((k, v) -> {
            if (!k.equals(key)) {
                if (v.isSame(item)) {
                    System.out.format("found for %s key: %s, value: %s\n", key, k, v);
                    results.add(k);
                }
            }
        });

        return results;
    }

    public static <T> HashMap<Integer, List<T>> deepCopy(HashMap<Integer, List<T>> original)
    {
        HashMap<Integer, List<T>> copy = new HashMap<>();
        for (Map.Entry<Integer, List<T>> entry : original.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }

    public static <T> Map<Integer, List<T>> deepCopyStream(Map<Integer, List<T>> original)
    {
        return original
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, valueMapper -> new ArrayList<>(valueMapper.getValue())));
    }

    public static <E> E createContents(Class<E> clazz)
    {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, Hosting> nameMap(List<Hosting> choices)
    {
        final Map<String, Hosting> hashMap = new HashMap<>();
        for (final Hosting choice : choices) {
            hashMap.put(choice.getName(), choice);
        }

        return hashMap;
    }

    public Map<String, Hosting> convertListToMap(List<Hosting> items)
    {
        return items.stream()
                .collect(Collectors.toMap(Hosting::getName, choice -> choice, (oldValue, newValue) -> newValue));
    }


    public static <K, V> Map.Entry<K, V> entry(K key, V value)
    {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    public static <K, U> Collector<Map.Entry<K, U>, ?, Map<K, U>> entriesToMap()
    {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    public static <K, U> Collector<Map.Entry<K, U>, ?, ConcurrentMap<K, U>> entriesToConcurrentMap()
    {
        return Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue);
    }


    public static void sortAndCollect()
    {
        List<Hosting> list = new ArrayList<>();
        list.add(new Hosting(1, "liquidweb.com", 80000));
        list.add(new Hosting(2, "linode.com", 90000));
        list.add(new Hosting(3, "digitalocean.com", 120000));
        list.add(new Hosting(4, "aws.amazon.com", 200000));
        list.add(new Hosting(5, "mkyong.com", 1));
        list.add(new Hosting(6, "linode.com", 100000));

        //example 1
        Map result1 = list.stream()
                .sorted(Comparator.comparingLong(Hosting::getWebsites).reversed())
                .collect(
                        Collectors.toMap(
                                Hosting::getName, Hosting::getWebsites, // key = name, value = websites
                                (oldValue, newValue) -> oldValue,       // if same key, take the old key
                                LinkedHashMap::new                      // returns a LinkedHashMap, keep order
                        ));

        System.out.println("Result 1 : " + result1);
    }

    /**
     * Based on https://blog.frankel.ch/dynamic-casting-java/
     *
     * @param clazz
     * @param items
     * @param <T>
     * @return
     */
    public static <T> List<T> filter(Class<T> clazz, List<?> items)
    {
        return items.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toList());
    }
}
