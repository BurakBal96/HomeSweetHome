package net.ddns.morigg.homesweethomeapp.structures;

import java.util.jar.Attributes;

/**
 * Created by MoriartyGG on 4.05.2018.
 */

public class HomeRequestStructure {
    public String NameSurname, UserName;
    public int UserId;

    public HomeRequestStructure(String NameSurname, String UserName, int UserId)
    {
        this.NameSurname = NameSurname;
        this.UserName = UserName;
        this.UserId = UserId;
    }



}
