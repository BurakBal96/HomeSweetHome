package net.ddns.morigg.homesweethomeapp.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.ddns.morigg.homesweethomeapp.structures.ExpenseStructure;

import java.util.ArrayList;
import java.util.List;


public class DBHandlerExpense extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "sweethome_expense";

    // Contacts table name
    private static final String TABLE_NAME = "expense";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String E_TYPE = "type";
    private static final String LAST_UPDATED = "lastupdated";
    private static final String COST = "cost";
    private static final String AUTHOR = "author";
    private static final String PARTICIPANTS = "participants";


    public DBHandlerExpense(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_EXPENSE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_CONTENT + " TEXT,"
                + E_TYPE + " TEXT,"
                + LAST_UPDATED + " TEXT,"
                + COST + " REAL, "
                + AUTHOR + " TEXT, "
                + PARTICIPANTS + " TEXT " +")";

        sqLiteDatabase.execSQL(CREATE_EXPENSE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public void addNewRow(ExpenseStructure expense) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, expense.ID);
        values.put(KEY_TITLE, expense.TITLE);
        values.put(KEY_CONTENT,expense.CONTENT);
        values.put(E_TYPE,expense.E_TYPE);
        values.put(LAST_UPDATED, expense.LAST_UPDATED);
        values.put(COST,expense.COST);
        values.put(AUTHOR,expense.AUTHOR);
        values.put(PARTICIPANTS,expense.PARTICIPANTS);


        // Inserting Row
        db.insert(TABLE_NAME, null, values);

        db.close(); // Closing database connection
    }

    public boolean deleteRow(int delID){

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_NAME, KEY_ID + "=" + delID, null) > 0;
    }


    public List<ExpenseStructure> getAllInformation() {


        List<ExpenseStructure> expenseList = new ArrayList<ExpenseStructure>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                ExpenseStructure expense = new ExpenseStructure(
                        cursor.getInt(0),
                        cursor.getDouble(5),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(6),
                        cursor.getString(7));
                // Adding contact to list
                expenseList.add(expense);

            } while (cursor.moveToNext());
        }

        // return contact list
        return expenseList;
    }

    public void editItem(ExpenseStructure expense)
    {
        ContentValues values = new ContentValues();

        int id = expense.ID;

        values.put(KEY_TITLE, expense.TITLE);
        values.put(KEY_CONTENT,expense.CONTENT);
        values.put(E_TYPE,expense.E_TYPE);
        values.put(LAST_UPDATED, expense.LAST_UPDATED);
        values.put(COST,expense.COST);
        values.put(AUTHOR,expense.AUTHOR);
        values.put(PARTICIPANTS,expense.PARTICIPANTS);

        SQLiteDatabase db = this.getWritableDatabase();

        db.update(TABLE_NAME,values,KEY_ID + "=" + id,null);

    }

    public void deleteALL()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }

}
