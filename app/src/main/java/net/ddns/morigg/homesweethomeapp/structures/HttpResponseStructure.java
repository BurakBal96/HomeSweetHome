package net.ddns.morigg.homesweethomeapp.structures;

/**
 * Created by MoriartyGG on 13.05.2018.
 */

public class HttpResponseStructure {
    public String responseMessage;
    public int responseCode;

    public HttpResponseStructure(int responseCode, String responseMessage)
    {
        this.responseMessage = responseMessage;
        this.responseCode = responseCode;
    }
}
