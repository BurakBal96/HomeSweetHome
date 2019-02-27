package net.ddns.morigg.homesweethomeapp.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.recycleradapter.ShoppingAdapter;
import net.ddns.morigg.homesweethomeapp.structures.ShoppingItems;
import net.ddns.morigg.homesweethomeapp.structures.Shopping_OneItem;
import net.ddns.morigg.homesweethomeapp.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.ddns.morigg.homesweethomeapp.activities.TabloEkleme._TOKEN;

public class ShoppingActivity extends AppCompatActivity {

    public static boolean isChanged = false;
    private RecyclerView recyc;
    private ShoppingAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    EditText mAddItemText;
    Button mAddItem;

    boolean dialogCreated = false;

    public static boolean isShoppingActivityActive = true;

    ShoppingItems items = new ShoppingItems(new ArrayList<Shopping_OneItem>());

    private BroadcastReceiver mHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            new Handler().post(new Runnable() {

                @Override
                public void run()
                {
                    if(isChanged && isShoppingActivityActive)
                    {
                        if(!dialogCreated)
                        {
                            new AlertDialog.Builder(ShoppingActivity.this)
                                    .setTitle(getString(R.string.ShoppingList_NewListTitle))
                                    .setMessage(getString(R.string.ShoppingList_NewListHint))
                                    .setCancelable(false)
                                    .setNegativeButton(getString(R.string.Notepad_Delete_Cancel), new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogCreated = false;
                                        }
                                    })
                                    .setPositiveButton(getString(R.string.signup_ok_button), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            try
                                            {
                                                String itemString = intent.getStringExtra("Items");
                                                String statusString = intent.getStringExtra("Status");

                                                if(!Objects.equals(itemString, "0") && !Objects.equals(itemString, ""))
                                                {
                                                    String[] itemSplit = itemString.split(getString(R.string.Seperator));
                                                    String[] statusSplit = statusString.split(getString(R.string.Seperator));

                                                    List<String> itemArray = new ArrayList<String>();
                                                    List<Integer> statusArray = new ArrayList<Integer>();
                                                    int x = 0;
                                                    for(String item : itemSplit)
                                                    {
                                                        itemArray.add(item);
                                                        statusArray.add(Integer.valueOf(statusSplit[x]));
                                                        x++;
                                                    }
                                                    items = new ShoppingItems(itemArray,statusArray);

                                                    mAdapter.items = items;
                                                    dialogCreated = false;

                                                }

                                                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("itemString.txt", Context.MODE_PRIVATE));
                                                outputStreamWriter.write(itemString);
                                                outputStreamWriter.close();

                                                outputStreamWriter = new OutputStreamWriter(openFileOutput("statusString.txt", Context.MODE_PRIVATE));
                                                outputStreamWriter.write(statusString);
                                                outputStreamWriter.close();
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            mAdapter.notifyDataSetChanged();
                                            isChanged = false;
                                        }
                                    }).create().show();
                            dialogCreated = true;
                        }

                    }
                    else
                    {
                        recreate();
                    }

                }
            });

            //Toast.makeText(NotepadActivity.this,"TEST",Toast.LENGTH_SHORT);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        setTitle(getString(R.string.title_activity_shopping));

        LocalBroadcastManager.getInstance(this).registerReceiver(mHandler,new IntentFilter("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-SHOPPING"));
        isShoppingActivityActive = true;

        mAddItemText = (EditText) findViewById(R.id.shopping_item_add);
        mAddItem = (Button) findViewById(R.id.shopping_add);

        recyc = (RecyclerView) findViewById(R.id.shopping_recyc);
        recyc.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyc.setLayoutManager(layoutManager);

        String itemString = readFromFile(this,"itemString.txt");
        String statusString = readFromFile(this,"statusString.txt");

        if(!Objects.equals(itemString, "0") && !Objects.equals(itemString, ""))
        {
            String[] itemSplit = itemString.split(getString(R.string.Seperator));
            String[] statusSplit = statusString.split(getString(R.string.Seperator));

            List<String> itemArray = new ArrayList<String>();
            List<Integer> statusArray = new ArrayList<Integer>();
                int i = 0;
            for(String item : itemSplit)
            {
                itemArray.add(item);
                statusArray.add(Integer.valueOf(statusSplit[i]));
                i++;
            }

            items = new ShoppingItems(itemArray,statusArray);
        }


        mAdapter = new ShoppingAdapter(items,this,ShoppingActivity.this, "ShoppingActivity");
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
    protected void onPause() {
        super.onPause();
        isShoppingActivityActive = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isShoppingActivityActive = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandler);
    }

    public static String readFromFile(Context context, String filename) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public void ShoppingSync()
    {
        ShoppingItems list = mAdapter.returnItems();
        //if(list.size() > 0)
        {
            try
            {
                String itemString = TextUtils.join(getString(R.string.Seperator),list.stringList);
                String statusString = TextUtils.join(getString(R.string.Seperator),list.statusList);

                Log.i("items",itemString);

                if(Objects.equals(itemString, "") || itemString == null)
                {
                    itemString ="0";statusString="0";
                }
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("list",itemString);
                jsonParam.put("status",statusString);
                NetworkUtils.HttpPost(getString(R.string.action_updateshoppinglist),_TOKEN,jsonParam,new Handler());

                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("itemString.txt", Context.MODE_PRIVATE));
                outputStreamWriter.write(itemString);
                outputStreamWriter.close();

                outputStreamWriter = new OutputStreamWriter(this.openFileOutput("statusString.txt", Context.MODE_PRIVATE));
                outputStreamWriter.write(statusString);
                outputStreamWriter.close();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressLint("RestrictedApi")
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shopping, menu);

        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }


        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_save:
                isChanged = false;
                ShoppingSync();
                return true;
            case R.id.menu_remove_purchased:
                ShoppingItems list = mAdapter.returnItems();
                //if(list.size() > 0)
                List<String> stringList = list.stringList;
                List<Integer> statusList = list.statusList;

                Iterator<String> iterator2 = list.stringList.iterator();

                for(Iterator<Integer> iterator = statusList.iterator(); iterator.hasNext(); )
                {
                    iterator2.next();
                    Integer value = iterator.next();
                    if(value == 1)
                    {
                        iterator.remove();
                        iterator2.remove();
                    }

                    items = new ShoppingItems(stringList,statusList);
                    mAdapter = new ShoppingAdapter(items,this,ShoppingActivity.this, "ShoppingActivity");
                    recyc.setAdapter(mAdapter);
                }
                return true;
            case R.id.menu_notification:

                NetworkUtils networkUtils = new NetworkUtils(ShoppingActivity.this);
                networkUtils.setAction(getString(R.string.action_shoppingnotification));
                String URL = networkUtils.getConnectionUrl();
                NetworkUtils.HttpGet(_TOKEN,URL,new Handler());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    @Override
    public void onBackPressed() {
        if(isChanged)
        {
            new AlertDialog.Builder(ShoppingActivity.this)
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
                            ShoppingSync();
                            isChanged = false;
                            finish();
                        }
                    }).create().show();
        }
        else
        {
            super.onBackPressed();
        }

    }
}
