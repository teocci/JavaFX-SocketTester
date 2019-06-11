package com.github.teocci.socket.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-Jun-10
 */
public class SearchResponse
{
    private List<POLine> lines;

    public SearchResponse()
    {
        lines = Stream.of(
                new POLine("1 4 3"),
                new POLine("2 5 7"),
                new POLine("3 1 4"),
                new POLine("4 8 2")
        ).collect(Collectors.toList());
    }

    public List<POLine> getDeliveryDocumentLines()
    {
        return lines;
    }
}
