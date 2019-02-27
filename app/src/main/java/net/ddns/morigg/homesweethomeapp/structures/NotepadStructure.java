package net.ddns.morigg.homesweethomeapp.structures;

/**
 * Created by MoriartyGG on 17.04.2018.
 */

public class NotepadStructure {
    public String Baslik, Not;
    public long id;

    public NotepadStructure(String Baslik, String Not,long id)
    {

        this.Baslik = Baslik;
        this.Not = Not;
        this.id = id;
    }

    public NotepadStructure(String Baslik, String Not)
    {
        this.Baslik = Baslik;
        this.Not = Not;
    }
}
