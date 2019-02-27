package net.ddns.morigg.homesweethomeapp.structures;

/**
 * Created by MoriartyGG on 27.05.2018.
 */

public class ExpenseStructure {

        public int ID;
        public String TITLE,CONTENT,E_TYPE,LAST_UPDATED,AUTHOR, PARTICIPANTS;
        public double COST;

        public ExpenseStructure(int ID, double COST, String TITLE, String CONTENT, String E_TYPE, String LAST_UPDATED, String AUTHOR, String PARTICIPANTS)
        {
            this.ID = ID;
            this.COST = COST;
            this.TITLE = TITLE;
            this.CONTENT = CONTENT;
            this.E_TYPE = E_TYPE;
            this.LAST_UPDATED = LAST_UPDATED;
            this.AUTHOR = AUTHOR;
            this.PARTICIPANTS = PARTICIPANTS;
        }

}

