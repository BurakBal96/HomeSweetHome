package net.ddns.morigg.homesweethomeapp.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import net.ddns.morigg.homesweethomeapp.structures.NotepadStructure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MoriartyGG on 20.04.2018.
 */

public class DBHandlerNotepad extends SQLiteOpenHelper {


    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "sweethome";

    // Contacts table name
    private static final String TABLE_NAME = "notepad";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";

    public DBHandlerNotepad(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_NOTEPAD_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_CONTENT + " TEXT " + ")";

        sqLiteDatabase.execSQL(CREATE_NOTEPAD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(sqLiteDatabase);
    }


    public void addNewRow(NotepadStructure newNote) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_TITLE, newNote.Baslik);
        values.put(KEY_CONTENT, newNote.Not);
        values.put(KEY_ID,newNote.id);


        // Inserting Row
        db.insert(TABLE_NAME, null, values);

        db.close(); // Closing database connection
    }


    public boolean deleteRow(int delID){

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_NAME, KEY_ID + "=" + delID, null) > 0;
    }

    public List<NotepadStructure> getAllInformation() {


        List<NotepadStructure> studentList = new ArrayList<NotepadStructure>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                NotepadStructure note = new NotepadStructure( cursor.getString(1),cursor.getString(2) );
                note.id = (Integer.parseInt(cursor.getString(0)));

                // Adding contact to list
                studentList.add(note);

            } while (cursor.moveToNext());
        }

        // return contact list
        return studentList;
    }

    public void editItem(int id,String title, String content)
    {
        ContentValues cv = new ContentValues();
        cv.put(KEY_TITLE,title); //These Fields should be your String values of actual column names
        cv.put(KEY_CONTENT,content);

        SQLiteDatabase db = this.getWritableDatabase();

        db.update(TABLE_NAME,cv,KEY_ID + "=" + id,null);

    }

    public void deleteALL()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }

}
