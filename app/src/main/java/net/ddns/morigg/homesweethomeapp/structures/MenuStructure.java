package net.ddns.morigg.homesweethomeapp.structures;

/**
 * Created by MoriartyGG on 3.06.2018.
 */

public class MenuStructure {

    public String DATE, MEALIDS;
    public int ID;

    public MenuStructure(int ID, String DATE, String MEALIDS)
    {
        this.ID = ID;
        this.DATE = DATE;
        this.MEALIDS = MEALIDS;
    }

}
