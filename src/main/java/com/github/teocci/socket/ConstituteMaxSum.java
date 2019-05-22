package com.github.teocci.socket;

import com.github.teocci.socket.model.Ideone;
import com.github.teocci.socket.model.Item;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-May-17
 */
public class ConstituteMaxSum
{
    public static void main(String[] args)
    {
//        List<Integer> numbers = Arrays.asList(4, 5, 4, 3);
//        List<Integer> numbers = Arrays.asList(1, 9, 1, 7, 7, 5, 4, 1, 6);
        List<Integer> numbers = Arrays.asList(-1, 7, 8, -5, 4, 9, -2, 3);

        String sss = "";
        String ddd = "";
        Ideone mm = new Ideone();
//        int maxi = mm.findMaxSumNonAdjacentStream(numbers, numbers.size());
//        String maxi = mm.maximumm(numbers, sss);

        List<List<Integer>> maxi = mm.findMaxSumNonAdjacentStream(numbers, numbers.size());
        System.out.println(Collections.singletonList(maxi));
//        sss = sss.concat("" + maxi);
//        System.out.println(sss);
    }
}
