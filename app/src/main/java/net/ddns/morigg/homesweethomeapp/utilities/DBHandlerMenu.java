package net.ddns.morigg.homesweethomeapp.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.ddns.morigg.homesweethomeapp.structures.HomeRequestStructure;
import net.ddns.morigg.homesweethomeapp.structures.MealStructure;
import net.ddns.morigg.homesweethomeapp.structures.MenuStructure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MoriartyGG on 3.06.2018.
 */

public class DBHandlerMenu extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "sweethome_menu";

    // Contacts table name
    private static final String TABLE_NAME = "menu";

    // Contacts Table Columns names
    private static final String KEY_MENUID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_MEALIDS = "meals";


    public DBHandlerMenu(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_NOTEPAD_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_MENUID + " INTEGER PRIMARY KEY,"
                + KEY_DATE + " TEXT,"
                + KEY_MEALIDS + " TEXT " + ")";

        sqLiteDatabase.execSQL(CREATE_NOTEPAD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public void addNewRow(MenuStructure oneMenu) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_MENUID, oneMenu.ID);
        values.put(KEY_DATE, oneMenu.DATE);
        values.put(KEY_MEALIDS, oneMenu.MEALIDS);

        // Inserting Row
        db.insert(TABLE_NAME, null, values);

        db.close(); // Closing database connection

    }

    public boolean deleteRow(int delID){

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_NAME, KEY_MENUID + "=" + delID, null) > 0;
    }


    public List<MenuStructure> getAllInformation() {


        List<MenuStructure> mealList = new ArrayList<MenuStructure>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MenuStructure tmpMeal = new MenuStructure( cursor.getInt(0),cursor.getString(1),cursor.getString(2));


                // Adding contact to list
                mealList.add(tmpMeal);

            } while (cursor.moveToNext());
        }

        // return contact list
        return mealList;
    }

    public void deleteALL()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }

    public void editItem(int id,String date, String mealids)
    {
        ContentValues cv = new ContentValues();
        cv.put(KEY_DATE,date); //These Fields should be your String values of actual column names
        cv.put(KEY_MEALIDS,mealids);


        SQLiteDatabase db = this.getWritableDatabase();

        db.update(TABLE_NAME,cv,KEY_MENUID + "=" + id,null);

    }


}
