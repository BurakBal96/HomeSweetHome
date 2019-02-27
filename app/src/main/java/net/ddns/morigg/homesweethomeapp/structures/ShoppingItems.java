package net.ddns.morigg.homesweethomeapp.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MoriartyGG on 16.05.2018.
 */

public class ShoppingItems {

    public List<Shopping_OneItem> items = new ArrayList<Shopping_OneItem>();

    public List<String> stringList;
    public List<Integer> statusList;

    public ShoppingItems(List<Shopping_OneItem> items)
    {
        stringList = new ArrayList<String>();
        statusList = new ArrayList<Integer>();
        for(int i = 0; i < items.size();i++)
        {
            stringList.add(items.get(i).item);
            statusList.add(items.get(i).status);
        }
    }

    public ShoppingItems(List<String> stringList, List<Integer> statusList)
    {
        this.stringList = stringList;
        this.statusList = statusList;
    }

    public void add(Shopping_OneItem item)
    {
        stringList.add(item.item);
        statusList.add(item.status);
    }

    public void add(Integer position,Shopping_OneItem item)
    {

        stringList.add(position,item.item);
        statusList.add(position,item.status);
        //items.add(new Shopping_OneItem(item,0));
    }

    public void remove(Integer position)
    {
        stringList.remove((int) position);
        statusList.remove((int) position);
    }

    public int size()
    {
        return stringList.size();
    }

    public Shopping_OneItem get(Integer position)
    {
        return new Shopping_OneItem(stringList.get(position),statusList.get(position));
    }

    public void setStatus(int position, int status)
    {
        statusList.set(position, status);
    }

    public void swapItems(Shopping_OneItem item1, int position1, Shopping_OneItem item2, int position2)
    {
        Shopping_OneItem tmpItem = item1;

        stringList.set(position1,item2.item);
        statusList.set(position1,item2.status);

        stringList.set(position2,tmpItem.item);
        statusList.set(position2,tmpItem.status);

    }

    public int indexOf(String item)
    {
        int index = stringList.indexOf(item);
        return index;
    }
}

