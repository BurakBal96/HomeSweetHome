package net.ddns.morigg.homesweethomeapp.structures;

import net.ddns.morigg.homesweethomeapp.activities.DutyActivity;

public class DutyStructure {

    public int id;
    public int date;
    public String user;
    public String action;
    public int userid;

    public DutyStructure(int id, int date, String user, String action, int userId)
    {
        this.id = id;
        this.date = date;
        this.user = user;
        this.action = action;
        this.userid = userId;
    }


}
