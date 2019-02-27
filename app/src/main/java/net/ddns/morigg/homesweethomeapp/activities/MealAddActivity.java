package net.ddns.morigg.homesweethomeapp.activities;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.recycleradapter.ShoppingAdapter;
import net.ddns.morigg.homesweethomeapp.structures.ShoppingItems;
import net.ddns.morigg.homesweethomeapp.structures.Shopping_OneItem;
import net.ddns.morigg.homesweethomeapp.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Objects;

import static net.ddns.morigg.homesweethomeapp.activities.TabloEkleme._TOKEN;

public class MealAddActivity extends AppCompatActivity {

    private RecyclerView recyc;
    private ShoppingAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    EditText mTxtMealName, mAddItemText, mTxtNote;
    Button mAddItem;


    boolean isChanged = false;

    ShoppingItems items = new ShoppingItems(new ArrayList<Shopping_OneItem>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_add);
        setTitle(getString(R.string.title_activity_meals));

        mTxtMealName = (EditText) findViewById(R.id.meal_name);
        mAddItemText = (EditText) findViewById(R.id.meal_newitemtext);
        mAddItem = (Button) findViewById(R.id.meal_additem);
        mTxtNote = (EditText) findViewById(R.id.meal_note);

        recyc = (RecyclerView) findViewById(R.id.meal_recyc);
        recyc.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyc.setLayoutManager(layoutManager);

        mAdapter = new ShoppingAdapter(items,this,MealAddActivity.this,"MealAddActivity");
        recyc.setAdapter(mAdapter);


        mAddItemText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && ((keyCode == KeyEvent.KEYCODE_ENTER) || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
                    //perform action
                    if(!Objects.equals(mAddItemText.getText().toString(), ""))
                    {
                        //mAdapter.addItem(0,new Shopping_OneItem(  mAddItemText.getText().toString(),0));
                        mAdapter.items.add(0,new Shopping_OneItem(  mAddItemText.getText().toString(),0));
                        mAdapter.notifyDataSetChanged();
                        mAddItemText.setText("");
                        recyc.scrollToPosition(0);
                        //ShoppingSync();
                        isChanged = true;
                    }
                    return true;
                }
                return false;
            }
        });

        mAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Objects.equals(mAddItemText.getText().toString(), ""))
                {
                    //mAdapter.addItem(0,new Shopping_OneItem(  mAddItemText.getText().toString(),0));
                    mAdapter.items.add(0,new Shopping_OneItem(  mAddItemText.getText().toString(),0));
                    mAdapter.notifyDataSetChanged();
                    mAddItemText.setText("");
                    recyc.scrollToPosition(0);
                    isChanged = true;
                    //ShoppingSync();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        if(isChanged)
        {
            if(Objects.equals(mTxtMealName.getText().toString(), ""))
            {
                new AlertDialog.Builder(MealAddActivity.this)
                        .setTitle(getString(R.string.Menu_MealNameEmptyTitle))
                        //.setMessage(getString(R.string.ShoppingList_SyncTitle))
                        .setCancelable(false)
                        .setNegativeButton(getString(R.string.signup_ok_button), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                isChanged = false;
                            }
                        })
                        .create().show();
            }
            else if(mAdapter.getItemCount() == 0)
            {
                new AlertDialog.Builder(MealAddActivity.this)
                        .setTitle(getString(R.string.Menu_MealIngredientsEmptyTitle))
                        //.setMessage(getString(R.string.ShoppingList_SyncTitle))
                        .setCancelable(false)
                        .setNegativeButton(getString(R.string.signup_ok_button), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                isChanged = false;
                            }
                        })
                        .create().show();
            }
            else
            {
                new AlertDialog.Builder(MealAddActivity.this)
                        //.setTitle(getString(R.string.signup_ok))
                        .setMessage(getString(R.string.ShoppingList_SyncTitle))
                        .setCancelable(false)
                        .setNegativeButton(getString(R.string.Notepad_Delete_Cancel), new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                isChanged = false;
                                finish();
                            }
                        })
                        .setPositiveButton(getString(R.string.signup_ok_button), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MealSync();
                                isChanged = false;
                                finish();
                            }
                        }).create().show();
            }
        }
        else
        {
            super.onBackPressed();
        }
    }

    public void MealSync()
    {
        ShoppingItems list = mAdapter.returnItems();
        //if(list.size() > 0)
        {
            try
            {
                String itemString = TextUtils.join(getString(R.string.Seperator),list.stringList);

                Log.i("items",itemString);

                if(Objects.equals(itemString, "") || itemString == null)
                {
                    return;
                }
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("name",mTxtMealName.getText().toString());
                jsonParam.put("ingredients",itemString);
                jsonParam.put("note",mTxtNote.getText().toString());
                NetworkUtils.HttpPost(getString(R.string.action_addmeal),_TOKEN,jsonParam,new Handler());


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
