package net.ddns.morigg.homesweethomeapp.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.ddns.morigg.homesweethomeapp.structures.DutyStructure;
import net.ddns.morigg.homesweethomeapp.structures.UserInformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DBHandlerDuty extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "duty";

    // Contacts table name
    private static final String TABLE_NAME = "duty";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USER = "user";
    private static final String KEY_USERID = "userid";
    private static final String KEY_DATE = "date";
    private static final String KEY_ACTION = "action";

    public DBHandlerDuty(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_NOTEPAD_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_USER + " INTEGER,"
                + KEY_USERID + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_ACTION + " TEXT " + ")";

        sqLiteDatabase.execSQL(CREATE_NOTEPAD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public void addNewRow(DutyStructure duty) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, duty.id);
        values.put(KEY_USER, duty.user);
        values.put(KEY_USERID,duty.userid);
        values.put(KEY_DATE,duty.date);
        values.put(KEY_ACTION, duty.action);


        // Inserting Row
        db.insert(TABLE_NAME, null, values);

        db.close(); // Closing database connection
    }

    public boolean deleteRow(int delID){

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_NAME, KEY_ID + "=" + delID, null) > 0;
    }


    public List<DutyStructure> getAllInformation() {


        List<DutyStructure> friendList = new ArrayList<DutyStructure>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                DutyStructure friend = new DutyStructure(
                        cursor.getInt(0),
                        cursor.getInt(3),
                        cursor.getString(1),
                        cursor.getString(4),
                        cursor.getInt(2)
                );
                // Adding contact to list
                friendList.add(friend);

            } while (cursor.moveToNext());
        }
        Collections.sort(friendList, new Comparator<DutyStructure>() {
            public int compare(DutyStructure o1, DutyStructure o2) {
                return String.format("%02d",o1.date).compareTo(String.format("%02d",o2.date));
            }
        });

        // return contact list
        return friendList;
    }

    public void editItem(int id,Double debt)
    {
        /**ContentValues cv = new ContentValues();
        cv.put(KEY_DEBT, debt); //These Fields should be your String values of actual column names


        SQLiteDatabase db = this.getWritableDatabase();

        db.update(TABLE_NAME,cv,KEY_ID + "=" + id,null);*/

    }

    public void deleteALL()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }

}
