package com.github.teocci.socket;

import com.github.teocci.socket.model.Item;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Stream;

import static com.github.teocci.socket.HashMapMerger.entriesToMap;
import static com.github.teocci.socket.HashMapMerger.entry;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-May-23
 */
public class DeepClone
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


        Map<Long, Item> clone = DeepClone.deepClone(itemMap);
        clone.remove(1L);
        clone.remove(2L);

        System.out.println(itemMap);
        System.out.println(clone);
    }

    private DeepClone() {}

    public static <T> T deepClone(final T input)
    {
        if (input == null) return null;

        if (input instanceof Map<?, ?>) {
            return (T) deepCloneMap((Map<?, ?>) input);
        } else if (input instanceof Collection<?>) {
            return (T) deepCloneCollection((Collection<?>) input);
        } else if (input instanceof Object[]) {
            return (T) deepCloneObjectArray((Object[]) input);
        } else if (input.getClass().isArray()) {
            return (T) clonePrimitiveArray((Object) input);
        }

        return input;
    }

    private static Object clonePrimitiveArray(final Object input)
    {
        final int length = Array.getLength(input);
        final Object output = Array.newInstance(input.getClass().getComponentType(), length);
        System.arraycopy(input, 0, output, 0, length);
        return output;
    }

    private static <E> E[] deepCloneObjectArray(final E[] input)
    {
        final E[] clone = (E[]) Array.newInstance(input.getClass().getComponentType(), input.length);
        for (int i = 0; i < input.length; i++) {
            clone[i] = deepClone(input[i]);
        }

        return clone;
    }

    private static <E> Collection<E> deepCloneCollection(final Collection<E> input)
    {
        Collection<E> clone;
        if (input instanceof LinkedList<?>) {
            clone = new LinkedList<>();
        } else if (input instanceof SortedSet<?>) {
            clone = new TreeSet<>();
        } else if (input instanceof Set) {
            clone = new HashSet<>();
        } else {
            clone = new ArrayList<>();
        }

        for (E item : input) {
            clone.add(deepClone(item));
        }

        return clone;
    }

    private static <K, V> Map<K, V> deepCloneMap(final Map<K, V> map)
    {
        Map<K, V> clone;
        if (map instanceof LinkedHashMap<?, ?>) {
            clone = new LinkedHashMap<>();
        } else if (map instanceof TreeMap<?, ?>) {
            clone = new TreeMap<>();
        } else {
            clone = new HashMap<>();
        }

        for (Map.Entry<K, V> entry : map.entrySet()) {
            clone.put(deepClone(entry.getKey()), deepClone(entry.getValue()));
        }

        return clone;
    }
}
