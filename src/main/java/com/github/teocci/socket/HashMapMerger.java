package com.github.teocci.socket;

import com.github.teocci.socket.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-May-17
 */
public class HashMapMerger
{
    public static void main(String[] args)
    {
        Map<Long, Item> itemMap = new HashMap()
        {
            {
                put(0L, new Item(2558584));
                put(1L, new Item(254243232));
                put(2L, new Item(986786));
                put(3L, new Item(672542));
                put(4L, new Item(4846));
                put(5L, new Item(76867467));
                put(6L, new Item(986786));
                put(7L, new Item(7969768));
                put(8L, new Item(68868486));
                put(9L, new Item(923));
                put(10L, new Item(986786));
                put(11L, new Item(549768));
                put(12L, new Item(796168));
                put(13L, new Item(868421));
                put(14L, new Item(923));
                put(15L, new Item(986786));
                put(16L, new Item(549768));
                put(18L, new Item(4846));
                put(19L, new Item(76867467));
                put(20L, new Item(986786));
                put(21L, new Item(7969768));
                put(22L, new Item(923));
                put(23L, new Item(4846));
                put(24L, new Item(986786));
                put(25L, new Item(549768));
            }
        };

        List<Long> toRemove = new ArrayList<>();
        itemMap.forEach((key, item) -> {
            boolean shouldFind = true;
            System.out.format("key: %s, value: %s\n", key, item);
            if (!toRemove.isEmpty()) {
                shouldFind = !toRemove.contains(key);
            }
            if (shouldFind) {
                List<Long> duplicates = findDuplicates(itemMap, key, item);
                if (!duplicates.isEmpty()) {
                    toRemove.addAll(duplicates);
                }
            }
        });

        toRemove.forEach(itemMap::remove);

        System.out.println("After deletion: ");
        itemMap.forEach((key, item) -> {
            System.out.format("key: %s, value: %s\n", key, item);
        });
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
}
