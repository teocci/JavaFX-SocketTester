package com.github.teocci.socket;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Streams;
import com.google.common.primitives.Ints;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by teocci based on https://www.techiedelight.com/find-index-element-array-java/
 *
 * @author teocci@yandex.com on 2019-May-22
 */
public class SteamLearning
{
    public static void main(String[] args)
    {
//        List<Integer> numbers = Arrays.asList(4, 5, 4, 3);
//        List<Integer> numbers = Arrays.asList(1, 9, 1, 7, 7, 5, 4, 1, 6);
        List<Integer> numbers = Arrays.asList(-1, 7, 8, -5, 4, 9, -2, 3);


        Map<Integer, Integer> map = IntStream
                .range(0, numbers.size()) // IntStream
                .boxed()                // Stream<Integer>
                .collect(Collectors.toMap(i -> i, i -> numbers.get(i))); // Map<Integer, Integer>
        System.out.println(map);

        List<Integer> collection = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            collection.add(i);

        StopWatch watch = new StopWatch();
        watch.start();
        // Get a Stream containing of elements from the 2nd index to 5th index
        Stream<Integer> slice = getSlice(collection.stream(), 2, 5);
        slice.forEach(System.out::println);
        watch.stop();
        System.out.println("Code took " + (watch.getNanoTime()) + " nano seconds");

        watch.reset();
        watch.start();
        // Get a Stream containing of elements from the 2nd index to 5th index
        Stream<Integer> sliceSubList = getSliceSubList(collection.stream(), 2, 5);
        sliceSubList.forEach(System.out::println);
        watch.stop();
        System.out.println("Code took " + (watch.getNanoTime()) + " nano seconds");

        watch.reset();
        watch.start();
        // Get a Stream containing of elements from the 2nd index to 5th index
        Stream<Integer> sliceCollector = getSliceCollectors(collection.stream(), 2, 5);
        sliceCollector.forEach(System.out::println);
        watch.stop();
        System.out.println("Code took " + (watch.getNanoTime()) + " nano seconds");


        String[] array = {"A", "B", "C", "D"};

        watch.reset();
        watch.start();
        IntStream.range(0, array.length)
                .mapToObj(index -> String.format("%d -> %s", index, array[index]))
                .forEach(System.out::println);
        watch.stop();
        System.out.println("Code took " + (watch.getNanoTime()) + " nano seconds");

        watch.reset();
        watch.start();
        IntStream.range(0, array.length)
                .forEach(index -> System.out.println(index + " -> " + array[index]));
        watch.stop();
        System.out.println("Code took " + (watch.getNanoTime()) + " nano seconds");

        AtomicInteger index = new AtomicInteger();
        Arrays.stream(array)
                .map(str -> index.getAndIncrement() + " -> " + str)
                .forEach(System.out::println);

        AtomicInteger indexAlternative = new AtomicInteger();
        Arrays.stream(array)
                .forEach(str -> System.out.println(index.getAndIncrement() + " -> " + str));

        // Guava Streams Class
        Streams.mapWithIndex(Arrays.stream(array),
                (str, i) -> i + " -> " + str)
                .forEach(System.out::println);
    }

    public static int findFor(int[] a, int target)
    {
        for (int i = 0; i < a.length; i++)
            if (a[i] == target)
                return i;

        return -1;
    }

    // Generic function to find the index of an element in an object array in Java
    public static <T> int findFor(T[] a, T target)
    {
        for (int i = 0; i < a.length; i++)
            if (target.equals(a[i]))
                return i;

        return -1;
    }

    // Function to find the index of an element in a primitive array in Java
    public static int find(int[] a, int target)
    {
        return IntStream.range(0, a.length)
                .filter(i -> target == a[i])
                .findFirst()
                .orElse(-1);    // return -1 if target is not found
    }

    // Generic function to find the index of an element in an object array in Java
    public static <T> int find(T[] a, T target)
    {
        return IntStream.range(0, a.length)
                .filter(i -> target.equals(a[i]))
                .findFirst()
                .orElse(-1);    // return -1 if target is not found
    }

    // Function to find the index of an element in a primitive array in Java
    public static int findArray(int[] a, int target)
    {
        return Arrays.stream(a)                 // IntStream
                .boxed()                        // Stream<Integer>
                .collect(Collectors.toList())   // List<Integer>
                .indexOf(target);
    }

    // Generic function to find the index of an element in an object array in Java
    public static <T> int findObjectArray(T[] a, T target)
    {
        return Arrays.asList(a).indexOf(target);
    }

    // Function to find the index of an element in a primitive array in Java
    public static int findBinarySearch(int[] a, int target)
    {
        int index = Arrays.binarySearch(a, target);
        return (index < 0) ? -1 : index;
    }

    // Generic function to find the index of an element in an object array in Java
    public static <T> int findBinarySearchObjectArray(T[] a, T target)
    {
        int index = Arrays.binarySearch(a, target);
        return (index < 0) ? -1 : index;
    }

    // Function to find the index of an element in a primitive array in Java
    public static int findGuava(int[] a, int target)
    {
        return Ints.indexOf(a, target);
    }

    // Generic function to find the index of an element in an object array in Java
    public static <T> int findGuavaObjectArray(T[] a, T target)
    {
        int index = Iterators.indexOf(Iterators.forArray(a), new Predicate<T>()
        {
            public boolean apply(T input)
            {
                return input.equals(target);
            }
        });

        return index;
    }

    // Generic function to find the index of an element in an object array in Java
    public static <T> int findGuavaObjectArrayPredicates(T[] a, T target)
    {
        int index = Iterators.indexOf(Iterators.forArray(a),
                Predicates.in(Collections.singleton(target)));
        return index;
    }

    // Generic function to find the index of an element in an object array in Java
    public static <T> int findGuavaObjectArrayLambda(T[] a, T target)
    {
        return Iterators.indexOf(Iterators.forArray(a), x -> x.equals(target));
    }

    // Function to find the index of an element in a primitive array in Java
    public static int findApacheCommons(int[] a, int target)
    {
        return ArrayUtils.indexOf(a, target);
    }

    // Generic function to find the index of an element in an object array in Java
    public static <T> int findApacheCommonsObjectArray(T[] a, T target)
    {
        return ArrayUtils.indexOf(a, target);
    }

    // Generic function to get Slice of a Stream from fromIndex to toIndex
    public static <T> Stream<T> getSlice(Stream<T> stream, int fromIndex, int toIndex)
    {
        return stream
                // specify the number of elements to skip
                .skip(fromIndex)
                // specify the no. of elements the stream should be limited to
                .limit(toIndex - fromIndex + 1);
    }

    // Generic function to get Slice of a Stream from fromIndex to toIndex
    public static <T> Stream<T> getSliceSubList(Stream<T> stream, int fromIndex, int toIndex)
    {
        return stream
                .collect(Collectors.toList())
                .subList(fromIndex, toIndex + 1)
                .stream();
    }

    // Generic function to get Slice of a Stream from fromIndex to toIndex
    public static <T> Stream<T> getSliceCollectors(Stream<T> stream, int fromIndex, int toIndex)
    {
        return stream.collect(Collectors.collectingAndThen
                (
                        Collectors.toList(),
                        list -> list.stream()
                                .skip(fromIndex)
                                .limit(toIndex - fromIndex + 1)
                )
        );
    }
}
