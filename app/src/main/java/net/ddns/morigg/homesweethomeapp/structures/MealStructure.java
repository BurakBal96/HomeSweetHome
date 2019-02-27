package net.ddns.morigg.homesweethomeapp.structures;

/**
 * Created by MoriartyGG on 3.06.2018.
 */

public class MealStructure {

    public int ID;
    public String NAME, NOTE, INGREDIENTS;

    public MealStructure(int id, String name, String note, String ingredients)
    {
        ID = id;
        NAME = name;
        NOTE = note;
        INGREDIENTS = ingredients;
    }

}
