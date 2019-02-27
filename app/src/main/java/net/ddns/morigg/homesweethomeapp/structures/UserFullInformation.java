package net.ddns.morigg.homesweethomeapp.structures;

/**
 * Created by MoriartyGG on 29.04.2018.
 */

public class UserFullInformation extends UserInformation {
    public String EMAIL;
    public String PHONE_NUMBER;
    public String HOME_NAME;

    public UserFullInformation(int USER_ID, int POSITION, String USER_NAME, String FIRST_NAME, String LAST_NAME, String EMAIL, String PHONE_NUMBER, String HOME_NAME, double DEBT) {
        super(USER_ID, POSITION, USER_NAME, FIRST_NAME, LAST_NAME, DEBT);
        this.EMAIL = EMAIL;
        this.PHONE_NUMBER = PHONE_NUMBER;
        this.HOME_NAME = HOME_NAME;
    }

}
