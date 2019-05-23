package com.github.teocci.socket.model;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-May-17
 */
public class Item
{
    private long id;

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

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public Item clone()
    {
        return new Item(id);
    }
}