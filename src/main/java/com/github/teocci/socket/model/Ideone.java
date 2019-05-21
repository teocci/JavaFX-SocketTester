package com.github.teocci.socket.model;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-May-21
 */
public class Ideone
{
    public String maximumm(List<Integer> numbers, String sss)
    {
        int toIndex = 3, fromIndex = 0;
        int size = numbers.size();
//        System.out.println("numbers.size(): " + size);
        List<Integer> result = new ArrayList<>();
        while (toIndex < size) {
            System.out.println("fromIndex: " + fromIndex + " | toIndex: " + toIndex);
            Map<Integer, Integer> map = IntStream
                    .range(fromIndex, toIndex)
                    .filter(i -> numbers.get(i) > 0)
                    .mapToObj(i -> new AbstractMap.SimpleEntry<>(i, numbers.get(i)))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));

            System.out.println(Collections.singletonList(map));
            // find max of sublist
            int maxOfSub = numbers.subList(fromIndex, toIndex).stream().max(Integer::compareTo).get();

            System.out.println("maxOfSub: " + maxOfSub);
            //update indexes
            fromIndex = map.getOrDefault(maxOfSub, toIndex - 1) + 2;
            toIndex += fromIndex;

            if (maxOfSub > 0)
                result.add(maxOfSub);
        }

        if (fromIndex == size) fromIndex = size - 1;

        int lastMax = numbers.subList(fromIndex, size).stream().max(Integer::compareTo).get();

        System.out.println("lastMax: " + lastMax);
        if (lastMax > 0) {
            result.add(lastMax);
        }
        result = result.stream().sorted(Integer::compareTo).collect(Collectors.toList());
        //System.out.println(result);
        sss = sss.concat(result.toString().replace(", ", "").replace("]", "").replace("[", ""));
        return sss;
        //  return result.stream().reduce(0,Integer::sum);
    }

    public int findMaxSumNonAdjacent(List<Integer> numbers, int size)
    {
        int incl = numbers.get(0);
        int excl = 0;
        int excl_new;
        int i;
        System.out.println("incl = " + incl + " | excl = " + excl);

        for (i = 1; i < size; i++) {
            /* current max excluding i */
            excl_new = (incl > excl) ? incl : excl;

            /* current max including i */
            incl = excl + numbers.get(i);
            excl = excl_new;

            System.out.println("For i = " + i);
            System.out.println("incl = " + incl + " | excl = " + excl);
        }

        /* return max of incl and excl */
        return ((incl > excl) ? incl : excl);
    }

    public <K, U> Collector<Map.Entry<K, U>, ?, Map<K, U>> entriesToMap()
    {
        return Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue());
    }

    public List<List<Integer>> findMaxSumNonAdjacentStream(List<Integer> numbers, int size)
    {
        int fromIndex = 0;

//        Map<Integer, Integer> maxSumMap = IntStream
//                .range(fromIndex, size)
//                .filter(i -> numbers.get(i) > 0)
//                .mapToObj(i -> new AbstractMap.SimpleEntry<>(i, numbers.get(i)))
//                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

//        Map<Integer, Integer> maxSumMap = IntStream.range(fromIndex, size).boxed().collect(Collectors.toMap(i -> i, i -> numbers.get(i)));
        Map<Integer, Integer> maxSumMap = IntStream
                .range(fromIndex, size)
                .filter(i -> numbers.get(i) > 0)
                .boxed()
                .collect(Collectors.toMap(Function.identity(), numbers::get));

        Map<Integer, List<Integer>> indexMap = IntStream
                .range(fromIndex, size)
                .filter(i -> numbers.get(i) > 0)
                .mapToObj(i -> new AbstractMap.SimpleEntry<>(i, Collections.singletonList(numbers.get(i))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

//        Map<Integer, List<Integer>> indexMap = numbers.stream()
//                .collect(Collectors.toMap(Function.identity(), Arrays::asList));
//        Map<Integer, List<Integer>> indexMap = numbers.stream().collect(Collectors.groupingBy(Function.identity())); /*best*/
//        System.out.println("indexMap: " + indexMap);

        maxSumMap.replace(1, Math.max(numbers.get(1), numbers.get(0)));
        List<Integer> maxValList = maxSumMap
                .entrySet()
                .stream()
                .filter(entry -> {
                    System.out.println("For i = " + entry.getKey() + " | value = " + entry.getValue());
                    return entry.getKey() > 1;
                })
                .map(entry -> {
                    int index = entry.getKey();
                    int prevOne = index - 1;
                    int prevTwo = index - 2;
                    int maxVal = Math.max(maxSumMap.get(prevOne), maxSumMap.get(prevTwo) + entry.getValue());
                    boolean exclude = maxSumMap.get(prevOne) > (maxSumMap.get(prevTwo) + entry.getValue());

                    List<Integer> elements = exclude ? new ArrayList<>(indexMap.get(prevOne)) : new ArrayList<>(indexMap.get(prevTwo));
                    if (!exclude) {
                        elements.add(entry.getValue());
                        elements = elements.stream().sorted(Integer::compareTo).collect(Collectors.toList());
                    }

                    System.out.println("elements: " + elements);
                    System.out.println("value: " + entry.getValue());

                    maxSumMap.replace(index, maxVal);
                    indexMap.replace(index, elements);
                    System.out.println("mapping = " + maxVal);
                    return index;
                })
                .collect(Collectors.toList());
        System.out.println("processed list: " + maxValList);

        Integer max = maxValList
                .stream()
                .mapToInt(v -> v)
                .max().orElseThrow(NoSuchElementException::new);
        Integer maxVal = maxSumMap.get(max);

//        System.out.println("max: " + max);

        List<Integer> result = maxSumMap
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(maxVal))
                .map(i -> i.getKey())
                .collect(Collectors.toList());

//        System.out.println("result: " + result);

        Predicate<Map.Entry<Integer, List<Integer>>> containMaxList =
                mapEntry -> result.contains(mapEntry.getKey());

        return indexMap.entrySet()
                .stream()
                .filter(containMaxList)
                .map(i -> i.getValue())
                .collect(Collectors.toList());
//        return null;
    }

    public int findMaxSumNonAdjacentV2(List<Integer> numbers, int size)
    {
        int[] maxSums = new int[size];
        maxSums[0] = numbers.get(0);
        maxSums[1] = Math.max(numbers.get(1), numbers.get(0));

        System.out.println("maxSums[0] = " + maxSums[0] + " | maxSums[1] = " + maxSums[1]);
        for (int i = 2; i < size; i++) {
            maxSums[i] = Math.max(maxSums[i - 1], maxSums[i - 2] + numbers.get(i));

            System.out.println("For i = " + i + " | value = " + numbers.get(i));
            System.out.println("maxSums[" + i + "] = " + maxSums[i] +
                    " | maxSums[" + (i - 1) + "] = " + maxSums[i - 1] +
                    " | maxSums[" + (i - 2) + "] = " + maxSums[i - 2]
            );
        }
        return maxSums[size - 1];
    }

    public int findLargestSumPair(List<Integer> numbers, int size)
    {
        // Initialize first and second largest element
        int first, second;
        if (numbers.get(0) > numbers.get(1)) {
            first = numbers.get(0);
            second = numbers.get(1);
        } else {
            first = numbers.get(1);
            second = numbers.get(0);
        }
        System.out.println("first = " + first + " | second = " + second);

        // Traverse remaining array and find first and second largest
        // elements in overall array
        for (int i = 2; i < size; i++) {
            /* If current element is greater than first then update both
              first and second */
            if (numbers.get(i) > first) {
                second = first;
                first = numbers.get(i);
            }

            /* If numbers.get(i) is in between first and second then update second  */
            else if (numbers.get(i) > second && numbers.get(i) != first)
                second = numbers.get(i);

            System.out.println("For i = " + i);
            System.out.println("first = " + first + " | second = " + second);
        }
        return (first + second);
    }
}
