package net.ddns.morigg.homesweethomeapp.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.ddns.morigg.homesweethomeapp.structures.HomeRequestStructure;
import net.ddns.morigg.homesweethomeapp.structures.MealStructure;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MoriartyGG on 3.06.2018.
 */

public class DBHandlerMeals extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "sweethome_heals";

    // Contacts table name
    private static final String TABLE_NAME = "meals";

    // Contacts Table Columns names
    private static final String KEY_MEALID = "id";
    private static final String KEY_MEALNAME = "name";
    private static final String KEY_MEALNOTE = "note";
    private static final String KEY_MEALINGR = "ingredients";


    public DBHandlerMeals(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_NOTEPAD_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_MEALID + " INTEGER PRIMARY KEY,"
                + KEY_MEALNAME + " TEXT,"
                + KEY_MEALNOTE + " TEXT,"
                + KEY_MEALINGR + " TEXT " + ")";

        sqLiteDatabase.execSQL(CREATE_NOTEPAD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public void addNewRow(MealStructure oneMeal) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_MEALID, oneMeal.ID);
        values.put(KEY_MEALNAME, oneMeal.NAME);
        values.put(KEY_MEALNOTE, oneMeal.NOTE);
        values.put(KEY_MEALINGR, oneMeal.INGREDIENTS);

        // Inserting Row
        db.insert(TABLE_NAME, null, values);

        db.close(); // Closing database connection

    }

    public boolean deleteRow(int delID){

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_NAME, KEY_MEALID + "=" + delID, null) > 0;
    }


    public List<MealStructure> getAllInformation() {


        List<MealStructure> mealList = new ArrayList<MealStructure>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MealStructure tmpMeal = new MealStructure( cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3));


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


}
