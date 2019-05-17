package com.github.teocci.socket.model;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-May-17
 */
public class Item
{
    long id;

    public Item(long id)
    {
        this.id = id;
    }

    public boolean isSame(Item item)
    {
        return this.id == item.id;
    }

    public void merge(Item item)
    {
        this.id += item.id;
    }

    public String toString()
    {
        return String.valueOf(this.id);
    }
}