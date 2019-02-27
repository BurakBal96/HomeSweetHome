package net.ddns.morigg.homesweethomeapp.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.ddns.morigg.homesweethomeapp.structures.UserInformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MoriartyGG on 18.05.2018.
 */

public class DBHandlerFriends extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "sweethomefriends";

    // Contacts table name
    private static final String TABLE_NAME = "friends";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_POSITION = "position";
    private static final String KEY_DEBT = "debt";
    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";

    public DBHandlerFriends(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_NOTEPAD_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_USERNAME + " TEXT,"
                + KEY_FIRSTNAME + " TEXT,"
                + KEY_LASTNAME + " TEXT,"
                + KEY_DEBT + " TEXT,"
                + KEY_POSITION + " INTEGER " + ")";

        sqLiteDatabase.execSQL(CREATE_NOTEPAD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public void addNewRow(UserInformation newFriend) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, newFriend.USER_ID);
        values.put(KEY_USERNAME, newFriend.USER_NAME);
        values.put(KEY_FIRSTNAME,newFriend.FIRST_NAME);
        values.put(KEY_LASTNAME,newFriend.LAST_NAME);
        values.put(KEY_DEBT, newFriend.DEBT);
        values.put(KEY_POSITION,newFriend.POSITION);


        // Inserting Row
        db.insert(TABLE_NAME, null, values);

        db.close(); // Closing database connection
    }

    public boolean deleteRow(int delID){

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_NAME, KEY_ID + "=" + delID, null) > 0;
    }


    public List<UserInformation> getAllInformation() {


        List<UserInformation> friendList = new ArrayList<UserInformation>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                UserInformation friend = new UserInformation(
                        cursor.getInt(0),
                        cursor.getInt(5),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4)
                );
                // Adding contact to list
                friendList.add(friend);

            } while (cursor.moveToNext());
        }

        // return contact list
        return friendList;
    }

    public void editItem(int id,Double debt)
    {
        ContentValues cv = new ContentValues();
        cv.put(KEY_DEBT, debt); //These Fields should be your String values of actual column names


        SQLiteDatabase db = this.getWritableDatabase();

        db.update(TABLE_NAME,cv,KEY_ID + "=" + id,null);

    }

    public void deleteALL()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }

}
