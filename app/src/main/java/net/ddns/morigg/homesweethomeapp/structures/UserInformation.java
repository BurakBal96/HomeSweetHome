package net.ddns.morigg.homesweethomeapp.structures;

import java.io.Serializable;

/**
 * Created by MoriartyGG on 29.04.2018.
 */


@SuppressWarnings("serial")
public class UserInformation implements Serializable{

    public int USER_ID;
    public int POSITION;
    public String USER_NAME;
    public String FIRST_NAME;
    public String LAST_NAME;
    public double DEBT;


    public UserInformation(int USER_ID, int POSITION, String USER_NAME, String FIRST_NAME, String LAST_NAME,double DEBT) {
        this.USER_ID = USER_ID;
        this.POSITION = POSITION;
        this.USER_NAME = USER_NAME;
        this.FIRST_NAME = FIRST_NAME;
        this.LAST_NAME = LAST_NAME;
        this.DEBT = DEBT;
    }



}
