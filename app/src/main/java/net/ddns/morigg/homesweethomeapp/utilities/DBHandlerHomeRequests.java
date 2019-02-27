package net.ddns.morigg.homesweethomeapp.utilities;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.ddns.morigg.homesweethomeapp.structures.HomeRequestStructure;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;

/**
 * Created by MoriartyGG on 3.05.2018.
 */

public class DBHandlerHomeRequests extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "sweethome_requests";

    // Contacts table name
    private static final String TABLE_NAME = "homerequests";

    // Contacts Table Columns names
    private static final String KEY_USERID = "id";
    private static final String KEY_USERNAME = "UserName";
    private static final String KEY_NAMESURNAME = "NameSurname";


    public DBHandlerHomeRequests(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_NOTEPAD_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_USERID + " INTEGER PRIMARY KEY,"
                + KEY_USERNAME + " TEXT,"
                + KEY_NAMESURNAME + " TEXT " + ")";

        sqLiteDatabase.execSQL(CREATE_NOTEPAD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public void addNewRow(HomeRequestStructure newRequest) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_USERID, newRequest.UserId);
        values.put(KEY_USERNAME, newRequest.UserName);
        values.put(KEY_NAMESURNAME, newRequest.NameSurname);

        // Inserting Row
        db.insert(TABLE_NAME, null, values);

        db.close(); // Closing database connection

    }

    public boolean deleteRow(int delID){

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_NAME, KEY_USERID + "=" + delID, null) > 0;
    }


    public List<HomeRequestStructure> getAllInformation() {


        List<HomeRequestStructure> requestList = new ArrayList<HomeRequestStructure>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                HomeRequestStructure tmpRequest = new HomeRequestStructure( cursor.getString(2),cursor.getString(1),cursor.getInt(0));


                // Adding contact to list
                requestList.add(tmpRequest);

            } while (cursor.moveToNext());
        }

        // return contact list
        return requestList;
    }

    public void deleteALL()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }


}
