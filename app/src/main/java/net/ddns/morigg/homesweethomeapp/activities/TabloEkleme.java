package net.ddns.morigg.homesweethomeapp.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.media.session.MediaSession;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.dialog_fragments.DisbandHome;
import net.ddns.morigg.homesweethomeapp.dialog_fragments.JoinHome;
import net.ddns.morigg.homesweethomeapp.structures.DutyStructure;
import net.ddns.morigg.homesweethomeapp.structures.MealStructure;
import net.ddns.morigg.homesweethomeapp.structures.MenuStructure;
import net.ddns.morigg.homesweethomeapp.structures.NotepadStructure;
import net.ddns.morigg.homesweethomeapp.structures.UserFullInformation;
import net.ddns.morigg.homesweethomeapp.structures.UserInformation;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerDuty;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerFriends;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerHomeRequests;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerMeals;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerMenu;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerNotepad;
import net.ddns.morigg.homesweethomeapp.utilities.NetworkUtils;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.app.PendingIntent.getActivity;
import static net.ddns.morigg.homesweethomeapp.activities.ShoppingActivity.readFromFile;
import static net.ddns.morigg.homesweethomeapp.utilities.FcmMessagingService.clearAllDatabases;

public class TabloEkleme extends AppCompatActivity implements  JoinHome.JoinHomeDialogListener, DisbandHome.DisbandHomeDialogListener {

    public static boolean isActivityAlive = false;

    @Override
    protected void onStart() {
        super.onStart();
        isActivityAlive =true;
    }

    CardView mCardNote, mCardShopping, mCardExpense, mCardMenu, mCardDuty;
    Toolbar mToolbar;
    AccountHeader accountHeader = null;
    Drawer result = null;

    private boolean isActive = true;

    List<UserInformation> friends;
    public static UserFullInformation mainUser;
    public static String _TOKEN;
    DBHandlerFriends dbFriends;

    private BroadcastReceiver mHandlerNewFriend = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new Handler().post(new Runnable() {

                @Override
                public void run()
                {
                    Log.i("NewFriend","Msg");
                    //Toast.makeText(TabloEkleme.this,"NewFriend", Toast.LENGTH_SHORT).show();
                    recreate();
                    //CreateNavigationDrawer();
                    Log.i("TabloEkleme","Update Drawer");
                }
            });

            //Toast.makeText(NotepadActivity.this,"TEST",Toast.LENGTH_SHORT);
        }
    };

    private BroadcastReceiver mHandlerNewHome = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                new Handler().post(new Runnable() {

                    @Override
                    public void run()
                    {
                        if(isActive)
                        {
                            new AlertDialog.Builder(TabloEkleme.this)
                                    .setTitle(getString(R.string.Navigation_JoinSuccessfulHint))
                                    .setMessage(getString(R.string.Navigation_CreateSuccessfulContent))
                                    .setCancelable(false)
                                    .setPositiveButton(getString(R.string.Navigation_CreateSuccessfulOkey), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    }).create().show();
                        }
                        else
                        {
                            Log.i("Destroy All App","");
                            finish();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            startActivity(intent);
                            //moveTaskToBack(true);
                        }

                    }
                });
            }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablo_ekleme);

        Resources res = getResources();

        mCardNote = (CardView) findViewById(R.id.tablo_not);
        mCardShopping = (CardView) findViewById(R.id.tablo_shopping);
        mCardExpense = (CardView) findViewById(R.id.tablo_expense);
        mCardMenu = (CardView) findViewById(R.id.tablo_menu);
        mCardDuty = (CardView) findViewById(R.id.tablo_duty);
        mToolbar = (Toolbar) findViewById(R.id.tablo_toolbar);


        Intent parentIntent = getIntent();

        mainUser = (UserFullInformation) parentIntent.getSerializableExtra("MainUser");
        //friends = (List<UserInformation>) parentIntent.getSerializableExtra("FriendList");
        dbFriends = new DBHandlerFriends(this);
        friends = dbFriends.getAllInformation();
        dbFriends.close();
        _TOKEN = parentIntent.getStringExtra("TOKEN");

        LocalBroadcastManager.getInstance(this).registerReceiver(mHandlerNewFriend,new IntentFilter("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-NEWFRIEND"));
        if(mainUser.POSITION == 0)
            LocalBroadcastManager.getInstance(this).registerReceiver(mHandlerNewHome,new IntentFilter("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-NEWHOME"));


        if(mainUser.POSITION != 0)
        {
            mCardDuty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TabloEkleme.this,DutyActivity.class);
                    startActivity(intent);
                }
            });
            mCardMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TabloEkleme.this, MenuActivity.class);
                    startActivity(intent);
                }
            });
            mCardNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TabloEkleme.this, NotepadActivity.class);
                    startActivity(intent);
                }
            });

            mCardShopping.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("HandlerLeak")
                @Override
                public void onClick(View view) {

                    final ProgressDialog progressDialog = new ProgressDialog(TabloEkleme.this,
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage(getString(R.string.SynchronizeProgressContent));
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    handlerShopping = new Handler()
                    {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);

                            Bundle mArgs = msg.getData();

                            String response = mArgs.getString("responseMessage");
                            int responseCode = mArgs.getInt("responseCode");

                            if(responseCode/100 == 2)
                            {
                                try
                                {
                                    JSONObject json = new JSONObject(response);

                                    String itemString = json.getString("list");
                                    String statusString = json.getString("status");

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
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                //TODO Handle Shopping Sync Error
                            }

                            progressDialog.dismiss();
                            Intent intent = new Intent(TabloEkleme.this, ShoppingActivity.class);
                            startActivity(intent);
                        }
                    };
                    NetworkUtils networkUtils = new NetworkUtils(TabloEkleme.this);
                    networkUtils.setAction(getString(R.string.action_sync_shopping));
                    String URL = networkUtils.getConnectionUrl();
                    NetworkUtils.HttpGet(_TOKEN,URL,handlerShopping);


                }
            });

            mCardExpense.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TabloEkleme.this, ExpenseActivity.class);
                    startActivity(intent);
                }
            });
        }
        else
        {
            View.OnClickListener listener = new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(TabloEkleme.this)
                                    .setTitle(getString(R.string.NotJoinedHomeTitle))
                                    .setMessage(getString(R.string.NotJoinedHomeHint))
                                    .setCancelable(true)
                                    .setPositiveButton(getString(R.string.Navigation_CreateSuccessfulOkey), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).create().show();
                        }
                    });
                }
            };

            mCardNote.setOnClickListener(listener);
            mCardShopping.setOnClickListener(listener);
            mCardExpense.setOnClickListener(listener);
            mCardMenu.setOnClickListener(listener);
        }


        CreateNavigationDrawer();
        allSync(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;

        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandlerNewFriend);
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandlerNewHome);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandlerNewFriend);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandlerNewHome);
        isActivityAlive =false;

    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWasSelected = item.getItemId();
        if( menuItemThatWasSelected == R.id.menu_help)
        {
            Context context = this;
            String message = "Help was clicked";
            Toast.makeText(context, message , Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void JoinHome_onSaveButtonClick(final DialogFragment dialog)
    {
        NetworkUtils networkUtils = new NetworkUtils(TabloEkleme.this);
        if (mainUser.POSITION == 2)
        {
            networkUtils.setAction(getString(R.string.action_invitehomerequest));
            EditText mUserName = (EditText) dialog.getDialog().findViewById(R.id.JoinHome);
            String URL = networkUtils.getConnectionUrl()
                    + "?invitedUsername=" + mUserName.getText().toString();
            NetworkUtils.HttpGet(_TOKEN,URL,new Handler());

        }

        if (mainUser.POSITION == 0 && Objects.equals(dialog.getArguments().getString("action"), getString(R.string.Navigation_JoinHomeAction)))
        {
            networkUtils.setAction(getString(R.string.action_joinhomerequest));
            EditText mUserName = (EditText) dialog.getDialog().findViewById(R.id.JoinHome);
            String URL = networkUtils.getConnectionUrl()
                    + "?joinhomename=" + mUserName.getText().toString();

            @SuppressLint("HandlerLeak") Handler handler = new Handler()
            {
                public void handleMessage(Message msg)
                {
                    super.handleMessage(msg);
                    if(msg.getData().getInt("responseCode") == 200)
                    {
                        new AlertDialog.Builder(TabloEkleme.this)
                                .setTitle(getString(R.string.Navigation_JoinHomeRequestTitle))
                                .setMessage(getString(R.string.Navigation_JoinHomeRequestContent))
                                .setCancelable(true)
                                .setPositiveButton(getString(R.string.Navigation_CreateSuccessfulOkey), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).create().show();
                    }
                    else
                    {
                        // Not Worked
                        new AlertDialog.Builder(TabloEkleme.this)
                                .setTitle(getString(R.string.error_server_not_respond))
                                .setMessage(getString(R.string.error_server_not_respond))
                                .setCancelable(true)
                                .setPositiveButton(getString(R.string.Navigation_CreateSuccessfulOkey), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).create().show();
                    }

                }
            };
            NetworkUtils.HttpGet(_TOKEN,URL,handler);
        }


        if (mainUser.POSITION == 0 && Objects.equals(dialog.getArguments().getString("action"), getString(R.string.Navigation_CreateHomeAction)))
        {
            try
            {
                @SuppressLint("CutPasteId") EditText mHomeName = (EditText) dialog.getDialog().findViewById(R.id.JoinHome);
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("Name", mHomeName.getText().toString());

                @SuppressLint("HandlerLeak") Handler handler = new Handler() {
                    public void handleMessage(Message msg)
                    {
                        if(msg.getData().getInt("responseCode") != 0)
                        {
                            Bundle mArgs = msg.getData();

                            String response = mArgs.getString("responseMessage");
                            int responseCode = mArgs.getInt("responseCode");

                            if (responseCode / 100 == 2)
                            {
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(TabloEkleme.this)
                                                .setTitle(getString(R.string.Navigation_CreateSuccessfulHint))
                                                .setMessage(getString(R.string.Navigation_CreateSuccessfulContent))
                                                .setCancelable(false)
                                                .setPositiveButton(getString(R.string.Navigation_CreateSuccessfulOkey), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);

                                                        clearAllDatabases(TabloEkleme.this);
                                                    }
                                                }).create().show();
                                    }
                                });
                            }
                            if (responseCode / 100 == 4)
                            {
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(TabloEkleme.this)
                                                .setTitle(getString(R.string.Navigation_CreateErrorTitle))
                                                .setMessage(getString(R.string.Navigation_CreateErrorContent))
                                                .setCancelable(true)
                                                .setPositiveButton(getString(R.string.Navigation_CreateSuccessfulOkey), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                    }
                                                }).create().show();
                                    }
                                });
                            }
                        }
                        else
                        {
                            runOnUiThread(new Runnable() {
                                public void run() {

                                }
                            });
                        }
                    }
                };
                NetworkUtils.HttpPost(getString(R.string.action_createhome), _TOKEN,jsonParam,handler);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    ProgressDialog progressDialog;
    Handler handlerNotepad;
    Handler handlerShopping;
    Handler handlerMenu;
    Handler handlerMeal;
    Handler handlerDuty;
    int syncedItem = 0;
    boolean error = false;
    @SuppressLint("HandlerLeak")
    public void allSync(Context context)
    {
        syncedItem = 0;
        final File file = context.getFileStreamPath(getString(R.string.firstlogin_logname));
        if(file.exists())
        {

            progressDialog = new ProgressDialog(TabloEkleme.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.SynchronizeProgressContent));
            progressDialog.setCancelable(false);
            progressDialog.show();

            handlerDuty = new Handler()
            {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    Bundle mArgs = msg.getData();

                    String response = mArgs.getString("responseMessage");
                    int responseCode = mArgs.getInt("responseCode");

                    if(responseCode/100 == 2)
                    {
                        syncedItem++;
                        Log.i("Duty","Synced : "+syncedItem);
                        try
                        {
                            JSONArray myJSONArray = new JSONArray(response);

                            //JsonArray.length();
                            Log.i("Length", String.valueOf(myJSONArray.length()));
                            DBHandlerDuty database = new DBHandlerDuty(TabloEkleme.this);
                            database.deleteALL();

                            for(int i = 0;i < myJSONArray.length();i++)
                            {
                                JSONObject tmpNoteJSON = myJSONArray.getJSONObject(i);

                                int friendId = tmpNoteJSON.getInt("friendId");
                                JSONObject housework = tmpNoteJSON.getJSONObject("housework");
                                int id = housework.getInt("id");
                                int date = housework.getInt("day");
                                String action = housework.getString("work");

                                String user = "";
                                for(int j = 0;j<friends.size();j++)
                                {
                                    if(friendId == friends.get(j).USER_ID)
                                        user = friends.get(j).FIRST_NAME + " " + friends.get(j).LAST_NAME;
                                }

                                DutyStructure tmpDutyStructure = new DutyStructure(id,date,user,action,friendId);

                                database.addNewRow(tmpDutyStructure);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    else
                    {
                        error = true;
                    }


                    if(syncedItem == Integer.parseInt(getString(R.string.action_numberOfSyncItem)))
                    {
                        file.delete();
                        progressDialog.dismiss();
                    }


                    if(error)
                    {
                        //TODO Handle Notepad Sync Error

                        progressDialog.dismiss();
                    }
                }
            };



            handlerNotepad = new Handler()
            {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);



                    Bundle mArgs = msg.getData();

                    String response = mArgs.getString("responseMessage");
                    int responseCode = mArgs.getInt("responseCode");

                    if(responseCode/100 == 2)
                    {
                        syncedItem++;
                        Log.i("Shopping","Synced : "+syncedItem);
                        try
                        {
                            JSONArray myJSONArray = new JSONArray(response);

                            //JsonArray.length();
                            Log.i("Length", String.valueOf(myJSONArray.length()));
                            DBHandlerNotepad database = new DBHandlerNotepad(TabloEkleme.this);
                            database.deleteALL();

                            for(int i = 0;i < myJSONArray.length();i++)
                            {
                                JSONObject tmpNoteJSON = myJSONArray.getJSONObject(i);

                                NotepadStructure tmpNoteStructure = new NotepadStructure(tmpNoteJSON.getString("title"),tmpNoteJSON.getString("content"),tmpNoteJSON.getLong("id"));


                                database.addNewRow(tmpNoteStructure);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    else
                    {
                        error = true;
                    }


                    if(syncedItem == Integer.parseInt(getString(R.string.action_numberOfSyncItem)))
                    {
                        file.delete();
                        progressDialog.dismiss();
                    }


                    if(error)
                    {
                        //TODO Handle Notepad Sync Error

                        progressDialog.dismiss();
                    }
                }
            };

            handlerShopping = new Handler()
            {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    Bundle mArgs = msg.getData();

                    String response = mArgs.getString("responseMessage");
                    int responseCode = mArgs.getInt("responseCode");

                    if(responseCode/100 == 2)
                    {
                        syncedItem++;
                        Log.i("Shopping","Synced : "+syncedItem);
                        try
                        {
                            JSONObject json = new JSONObject(response);

                            String itemString = json.getString("list");
                            String statusString = json.getString("status");

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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        error = true;
                    }

                    if(syncedItem == Integer.parseInt(getString(R.string.action_numberOfSyncItem)))
                    {
                        progressDialog.dismiss();
                        file.delete();
                    }
                    if(error)
                    {
                        //TODO Handle Shopping Sync Error

                        progressDialog.dismiss();
                    }
                }
            };

            handlerMenu = new Handler()
            {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);



                    Bundle mArgs = msg.getData();

                    String response = mArgs.getString("responseMessage");
                    int responseCode = mArgs.getInt("responseCode");

                    if(responseCode/100 == 2)
                    {
                        syncedItem++;
                        Log.i("Menu","Synced : "+syncedItem);
                        try
                        {
                            JSONArray myJSONArray = new JSONArray(response);

                            //JsonArray.length();
                            Log.i("Length", String.valueOf(myJSONArray.length()));
                            DBHandlerMenu database = new DBHandlerMenu(TabloEkleme.this);
                            database.deleteALL();








                            for(int i = 0;i < myJSONArray.length();i++)
                            {
                                JSONObject json = myJSONArray.getJSONObject(i);

                                JSONObject jsonMenu = json.getJSONObject("menu");

                                int id = jsonMenu.getInt("id");
                                String date = jsonMenu.getString("date");

                                JSONArray mealIds = json.getJSONArray("mealIds");
                                String mealIdsString = mealIds.toString();

                                long timeInMilliseconds = 0;

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                try {
                                    Date mDate = sdf.parse(date);
                                    timeInMilliseconds = mDate.getTime();
                                    //System.out.println("Date in milli :: " + timeInMilliseconds);

                                    //Date date = new Date(timeInMilliseconds);
                                    //DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                                    //Log.i("TIME :",dateFormat.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                database.addNewRow(new MenuStructure(id,String.valueOf(timeInMilliseconds),mealIdsString));
                                database.close();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    else
                    {
                        error = true;
                    }


                    if(syncedItem == Integer.parseInt(getString(R.string.action_numberOfSyncItem)))
                    {
                        file.delete();
                        progressDialog.dismiss();
                    }


                    if(error)
                    {
                        //TODO Handle Notepad Sync Error

                        progressDialog.dismiss();
                    }
                }
            };

            handlerMeal = new Handler()
            {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);



                    Bundle mArgs = msg.getData();

                    String response = mArgs.getString("responseMessage");
                    int responseCode = mArgs.getInt("responseCode");

                    if(responseCode/100 == 2)
                    {
                        syncedItem++;
                        Log.i("Menu","Synced : "+syncedItem);
                        try
                        {
                            JSONArray myJSONArray = new JSONArray(response);

                            //JsonArray.length();
                            Log.i("Length", String.valueOf(myJSONArray.length()));
                            DBHandlerMeals database = new DBHandlerMeals(TabloEkleme.this);
                            database.deleteALL();








                            for(int i = 0;i < myJSONArray.length();i++)
                            {
                                JSONObject json = myJSONArray.getJSONObject(i);

                                int id = json.getInt("id");
                                String name = json.getString("name");
                                String note = json.getString("note");
                                String ingredients = json.getString("ingredients");

                                database.addNewRow(new MealStructure(id,name,note,ingredients));
                                database.close();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    else
                    {
                        error = true;
                    }


                    if(syncedItem == Integer.parseInt(getString(R.string.action_numberOfSyncItem)))
                    {
                        file.delete();
                        progressDialog.dismiss();
                    }


                    if(error)
                    {
                        //TODO Handle Notepad Sync Error

                        progressDialog.dismiss();
                    }
                }
            };








           /** handlerExpense = new Handler()
            {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);



                    Bundle mArgs = msg.getData();

                    String response = mArgs.getString("responseMessage");
                    int responseCode = mArgs.getInt("responseCode");

                    if(responseCode/100 == 2)
                    {
                        syncedItem++;
                        Log.i("Expense","Synced : "+syncedItem);
                        try
                        {
                            JSONArray myJSONArray = new JSONArray(response);

                            //JsonArray.length();
                            Log.i("Length", String.valueOf(myJSONArray.length()));
                            DBHandlerNotepad database = new DBHandlerNotepad(TabloEkleme.this);
                            database.deleteALL();

                            for(int i = 0;i < myJSONArray.length();i++)
                            {
                                JSONObject tmpNoteJSON = myJSONArray.getJSONObject(i);

                                NotepadStructure tmpNoteStructure = new NotepadStructure(tmpNoteJSON.getString("title"),tmpNoteJSON.getString("content"),tmpNoteJSON.getLong("id"));


                                database.addNewRow(tmpNoteStructure);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    else
                    {
                        error = true;
                    }


                    if(syncedItem == Integer.parseInt(getString(R.string.action_numberOfSyncItem)))
                    {
                        file.delete();
                        progressDialog.dismiss();
                    }


                    if(error)
                    {
                        //TODO Expense Sync Handle Error

                        progressDialog.dismiss();
                    }
                }
            };*/


            NetworkUtils networkUtils = new NetworkUtils(this);
            networkUtils.setAction(getString(R.string.action_sync_notepad));
            String URL = networkUtils.getConnectionUrl();
            NetworkUtils.HttpGet(_TOKEN,URL,handlerNotepad);

            networkUtils.setAction(getString(R.string.action_sync_shopping));
            URL = networkUtils.getConnectionUrl();
            NetworkUtils.HttpGet(_TOKEN,URL,handlerShopping);

            networkUtils.setAction(getString(R.string.action_sync_menu));
            URL = networkUtils.getConnectionUrl();
            NetworkUtils.HttpGet(_TOKEN,URL,handlerMenu);

            networkUtils.setAction(getString(R.string.action_sync_meals));
            URL = networkUtils.getConnectionUrl();
            NetworkUtils.HttpGet(_TOKEN,URL,handlerMeal);

            networkUtils.setAction(getString(R.string.action_sync_duty));
            URL = networkUtils.getConnectionUrl();
            NetworkUtils.HttpGet(_TOKEN,URL,handlerDuty);
        }
    }

    List<String> todaysMenu = new ArrayList<>();
    List<Integer> todaysTmpStatus = new ArrayList<>();

    public void CreateNavigationDrawer()
    {
        //TODO Navigation Drawer Build
        {

            mToolbar.setBackground(getResources().getDrawable(R.color.pink));
            PrimaryDrawerItem item1_homepage = new PrimaryDrawerItem().withName(getString(R.string.Navigation_MainPage)).withIcon(GoogleMaterial.Icon.gmd_flash_on);



            ExpandableDrawerItem item2_homeActions = null;

            ExpandableDrawerItem item3_friends = null;

            if(mainUser.POSITION == 2)
            {
                item2_homeActions = new ExpandableDrawerItem().withIcon(GoogleMaterial.Icon.gmd_home).withName(getString(R.string.Navigation_Home)).withDescription(mainUser.HOME_NAME).withSelectable(false);
                item2_homeActions.withSubItems(
                        //new PrimaryDrawerItem().withName(getString(R.string.Navigation_JoinHome)).withLevel(3),
                        /** Pos2 >> Davet Et */
                        new PrimaryDrawerItem().withName(getString(R.string.Navigation_InviteHome)).withLevel(3).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                /** Eve Davet Etme Action'ı için intent çağırılması vs. */

                                Bundle mArgs = new Bundle();
                                mArgs.putString("hint",getString(R.string.Navigation_InviteHomeHint));
                                mArgs.putString("action", getString(R.string.Navigation_InviteHomeAction));
                                JoinHome dialog = new JoinHome();
                                dialog.setArguments(mArgs);

                                dialog.show(getFragmentManager(), "Info");
                                return false;
                            }
                        }),
                        /** Pos2 >> Başvurular */
                        new PrimaryDrawerItem().withName(getString(R.string.Navigation_Apply)).withLevel(3).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                /** Eve Gelen Başvuruları Kabul Etme Action'ı için intent çağırılması vs. */

                                Intent intent = new Intent(TabloEkleme.this,HomeRequestsActivity.class);
                                startActivity(intent);

                                return false;
                            }
                        }),
                        /** Pos2 >> Evden Çıkart*/
                        new PrimaryDrawerItem().withName(getString(R.string.Navigation_Kick)).withLevel(3),
                        /** Pos2 >> Evi Sil - Yöneticinin evden ayrılması */
                        new PrimaryDrawerItem().withName(getString(R.string.Navigation_Disband)).withLevel(3).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                /** Eve Gelen Başvuruları Kabul Etme Action'ı için intent çağırılması vs. */


                                DisbandHome dialogDisband = new DisbandHome(getBaseContext(),TabloEkleme.this);
                                dialogDisband.show(getFragmentManager(), "Info");
                                return false;
                            }
                        })

                );

                List<IDrawerItem> navigation_friends = new ArrayList<IDrawerItem>();
                item3_friends = new ExpandableDrawerItem().withName(getString(R.string.Navigation_Debt)).withIcon(GoogleMaterial.Icon.gmd_attach_money).withIdentifier(19).withSelectable(false);

                for(int i = 0;i < friends.size();i++)
                {
                    UserInformation friend = friends.get(i);
                    if(friends.get(i).DEBT < 0)
                    {
                        navigation_friends.add(new PrimaryDrawerItem().withName(friend.FIRST_NAME.toUpperCase() + " " +friend.LAST_NAME.toUpperCase(Locale.getDefault())).withLevel(3).withIcon(GoogleMaterial.Icon.gmd_filter_list).withIcon(R.drawable.ic_menu_camera).withSelectable(false).withDescription(getString(R.string.Navigation_Asset) + friend.DEBT*-1));
                    }
                    else
                    {
                        navigation_friends.add(new PrimaryDrawerItem().withName(friend.FIRST_NAME.toUpperCase() + " " +friend.LAST_NAME.toUpperCase(Locale.getDefault())).withLevel(3).withIcon(GoogleMaterial.Icon.gmd_filter_list).withIcon(R.drawable.ic_menu_camera).withSelectable(false).withDescription(getString(R.string.Navigation_DebtToOne) + friend.DEBT));
                    }
                }
                item3_friends.withSubItems(navigation_friends);

            }
            else if(mainUser.POSITION == 1)
            {
                item2_homeActions = new ExpandableDrawerItem().withIcon(GoogleMaterial.Icon.gmd_home).withName(getString(R.string.Navigation_Home)).withDescription(mainUser.HOME_NAME).withSelectable(false);
                item2_homeActions.withSubItems(
                        //new PrimaryDrawerItem().withName(getString(R.string.Navigation_JoinHome)).withLevel(3),
                        //new PrimaryDrawerItem().withName(getString(R.string.Navigation_InviteHome)).withLevel(3),
                        //new PrimaryDrawerItem().withName(getString(R.string.Navigation_Disband)).withLevel(3),
                        /** Pos1 >> Evden Çık */
                        new PrimaryDrawerItem().withName(getString(R.string.Navigation_Resign)).withLevel(3).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {


                                /** Normal Kullanıcının Evden Cikmasi  */


                                new AlertDialog.Builder(TabloEkleme.this)
                                        .setTitle(getString(R.string.Navigation_ResignTitle))
                                        .setPositiveButton(getString(R.string.Navigation_ResignApply), new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i)
                                            {

                                                NetworkUtils networkUtils = new NetworkUtils(TabloEkleme.this);
                                                networkUtils.setAction(getString(R.string.action_leavehome));
                                                String URL = networkUtils.getConnectionUrl();
                                                @SuppressLint("HandlerLeak") Handler handler = new Handler()
                                                {
                                                    @Override
                                                    public void handleMessage(Message msg) {
                                                        super.handleMessage(msg);

                                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                    }
                                                };

                                                NetworkUtils.HttpGet(_TOKEN,URL,handler);
                                            }
                                        })

                                        .setNegativeButton(getString(R.string.Notepad_Delete_Cancel),new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                //do Nothing
                                            }
                                        })
                                        .create()
                                        .show();







                                return false;
                            }
                        })
                );

                List<IDrawerItem> navigation_friends = new ArrayList<IDrawerItem>();
                item3_friends = new ExpandableDrawerItem().withName(getString(R.string.Navigation_Debt)).withIcon(GoogleMaterial.Icon.gmd_attach_money).withIdentifier(19).withSelectable(false);

                for(int i = 0;i < friends.size();i++)
                {
                    UserInformation friend = friends.get(i);
                    if(friends.get(i).DEBT < 0)
                    {
                        navigation_friends.add(new PrimaryDrawerItem().withName(friend.FIRST_NAME.toUpperCase() + " " + friend.LAST_NAME.toUpperCase(Locale.getDefault())).withLevel(3).withIcon(GoogleMaterial.Icon.gmd_filter_list).withIcon(R.drawable.ic_menu_camera).withSelectable(false).withDescription("ALACAK : " + friend.DEBT*-1));
                    }
                    else
                    {
                        navigation_friends.add(new PrimaryDrawerItem().withName(friend.FIRST_NAME.toUpperCase() + " " + friend.LAST_NAME.toUpperCase(Locale.getDefault())).withLevel(3).withIcon(GoogleMaterial.Icon.gmd_filter_list).withIcon(R.drawable.ic_menu_camera).withSelectable(false).withDescription("BORC : " + friend.DEBT));
                    }
                }
                item3_friends.withSubItems(navigation_friends);

            }
            else if(mainUser.POSITION == 0)
            {
                item2_homeActions = new ExpandableDrawerItem().withIcon(GoogleMaterial.Icon.gmd_home).withName(getString(R.string.Navigation_Home)).withSelectable(false);
                item2_homeActions.withSubItems(
                        /** Pos0 >> Eve Katıl */
                        new PrimaryDrawerItem().withName(getString(R.string.Navigation_JoinHome)).withLevel(3).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                Bundle mArgs = new Bundle();
                                mArgs.putString("hint",getString(R.string.Navigation_JoinHomeHint));
                                mArgs.putString("action", getString(R.string.Navigation_JoinHomeAction));
                                JoinHome dialog = new JoinHome();
                                dialog.setArguments(mArgs);

                                dialog.show(getFragmentManager(), "Info");
                                return false;
                            }
                        }),
                        /** Pos0 >> Ev Davetleri */
                        new PrimaryDrawerItem().withName(getString(R.string.Navigation_AcceptInvite)).withLevel(3).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                /** Eve Gelen Başvuruları Kabul Etme Action'ı için intent çağırılması vs. */

                                Intent intent = new Intent(TabloEkleme.this,HomeRequestsActivity.class);
                                startActivity(intent);

                                return false;
                            }
                        }),
                        /** Pos0 >> Ev Oluştur */
                        new PrimaryDrawerItem().withName(getString(R.string.Navigation_Create)).withLevel(3).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                Bundle mArgs = new Bundle();
                                mArgs.putString("hint",getString(R.string.Navigation_CreateHint));
                                mArgs.putString("action", getString(R.string.Navigation_CreateHomeAction));
                                JoinHome dialog = new JoinHome();
                                dialog.setArguments(mArgs);

                                dialog.show(getFragmentManager(), "Info");

                                return false;
                            }
                        })
                        //new PrimaryDrawerItem().withName(getString(R.string.Navigation_InviteHome)).withLevel(3),
                        //new PrimaryDrawerItem().withName(getString(R.string.Navigation_Disband)).withLevel(3),
                        //new PrimaryDrawerItem().withName(getString(R.string.Navigation_Resign)).withLevel(3)
                );
            }






            SecondaryDrawerItem secondary1 = new SecondaryDrawerItem().withName("Secondary1");
            SecondaryDrawerItem secondary2 = new SecondaryDrawerItem().withName("Secondary2").withIcon(FontAwesome.Icon.faw_github);


            setSupportActionBar(mToolbar);


            accountHeader = new AccountHeaderBuilder()

                    .withActivity(this)
                    .withHeaderBackground(R.drawable.ic_launcher_background)
                    .withTranslucentStatusBar(true)
                    //.withCurrentProfileHiddenInList(true)
                    //.withOnlyMainProfileImageVisible(true)
                    .withSelectionListEnabled(false)
                    .addProfiles(
                            new ProfileDrawerItem().withName(mainUser.FIRST_NAME +" "+ mainUser.LAST_NAME).withEmail(mainUser.EMAIL).withIcon(GoogleMaterial.Icon.gmd_person)/*,
                        new ProfileDrawerItem().withName("Demo User").withEmail("demo@github.com").withIcon("https://avatars2.githubusercontent.com/u/3597376?v=3&s=460").withIdentifier(101),
                        new ProfileSettingDrawerItem().withName("Add Account").withDescription("Add new GitHub Account").withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_add).actionBar().paddingDp(5).colorRes(R.color.material_drawer_primary_text)).withIdentifier(100000),
                        new ProfileSettingDrawerItem().withName("Manage Account").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(100001)*/
                    )
                    .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                        @Override
                        public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                            //sample usage of the onProfileChanged listener
                            //if the clicked item has the identifier 1 add a new profile ;)
                            if (profile instanceof IDrawerItem && profile.getIdentifier() == 100000) {
                                int count = 100 + accountHeader.getProfiles().size() + 1;
                                IProfile newProfile = new ProfileDrawerItem().withNameShown(true).withName("Batman" + count).withEmail("batman" + count + "@gmail.com").withIcon("https://avatars2.githubusercontent.com/u/3597376?v=3&s=460").withIdentifier(count);
                                if (accountHeader.getProfiles() != null) {
                                    //we know that there are 2 setting elements. set the new profile above them ;)
                                    accountHeader.addProfile(newProfile, accountHeader.getProfiles().size() - 2);
                                } else {
                                    accountHeader.addProfiles(newProfile);
                                }
                            }

                            //false if you have not consumed the event and it should close the drawer
                            return false;
                        }
                    })
                    //.withSavedInstance(savedInstanceState)
                    .build();


            Drawer drawerBuilder = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(mToolbar)
                    .withActionBarDrawerToggleAnimated(true)
                    .withTranslucentStatusBar(true)
                    .withActionBarDrawerToggle(true)
                    .withAccountHeader(accountHeader)
                    .addDrawerItems(
                            new DividerDrawerItem(),
                            item1_homepage,
                            new DividerDrawerItem(),
                            item2_homeActions,
                            new DividerDrawerItem()
                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            if(drawerItem != null)
                            {
                                //Toast.makeText(TabloEkleme.this, String.valueOf(drawerItem.getIdentifier()), Toast.LENGTH_SHORT).show();
                            }
                            return false;
                        }
                    })
                    .build();

            if(mainUser.POSITION != 0 && friends.size() != 0)
            {
                drawerBuilder.addItems(item3_friends, new DividerDrawerItem());
            }
            if(mainUser.POSITION != 0)
            {
                PrimaryDrawerItem item1_sync;
                {
                    item1_sync = new PrimaryDrawerItem().withName(getString(R.string.Navigation_FullSync)).withIcon(GoogleMaterial.Icon.gmd_flash_on).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            OutputStreamWriter outputStreamWriter = null;
                            try {
                                outputStreamWriter = new OutputStreamWriter(openFileOutput(getString(R.string.firstlogin_logname), Context.MODE_PRIVATE));
                                outputStreamWriter.write("");
                                outputStreamWriter.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            allSync(TabloEkleme.this);
                            return false;
                        }
                    });
                }

                drawerBuilder.addStickyFooterItem(item1_sync);
            }
            drawerBuilder.addStickyFooterItem(
                    new PrimaryDrawerItem().withName(getString(R.string.LogOut)).withIcon(GoogleMaterial.Icon.gmd_exit_to_app).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                            return false;
                        }
                    })
            );


            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.HOUR_OF_DAY, 0);

            Date today = cal.getTime();

            DBHandlerMenu database = new DBHandlerMenu(TabloEkleme.this);
            List<MenuStructure> list = database.getAllInformation();

            long timeInMilis = today.getTime();
            Log.i("TabloEkleme", String.valueOf(timeInMilis));
            Log.i("TabloEkleme", "8    "+String.valueOf(cal.get(Calendar.DST_OFFSET)));
            MenuStructure selectedDay = null;
            for(MenuStructure oneMenu : list)
            {
                if(timeInMilis == Long.valueOf(oneMenu.DATE))
                {
                    selectedDay = oneMenu;
                }
            }


            if(selectedDay != null)
            {

                String items = "";
                int size = selectedDay.MEALIDS.length();
                String[] participantsString = (selectedDay.MEALIDS.substring(1,size-1)).split(",");
                final List<Integer> itemIDs = new ArrayList<>();
                for(String item : participantsString)
                {
                    itemIDs.add(Integer.valueOf(item));
                }

                final DBHandlerMeals databaseMeals = new DBHandlerMeals(TabloEkleme.this);
                boolean firstTour = true;
                for(MealStructure tmpMeal : databaseMeals.getAllInformation())
                {
                    for(int i = 0; i<itemIDs.size();i++)
                    {
                        if(tmpMeal.ID == itemIDs.get(i))
                        {
                            todaysMenu.add(tmpMeal.INGREDIENTS);
                            {

                                {
                                    String[] itemSplit = tmpMeal.INGREDIENTS.split(getString(R.string.Seperator));
                                    int ingredientsSize = itemSplit.length;
                                    for(int x = 0;x < ingredientsSize; x++)
                                        todaysTmpStatus.add((Integer) 0);
                                }
                            }
                            if(firstTour)
                            {
                                firstTour =false;
                                items = tmpMeal.NAME;

                            }
                            else
                                items = items + ", " + tmpMeal.NAME;
                        }

                    }
                }


                Log.i("TabloEkleme","YemekliGün");
                Log.i("TabloEkleme","TmpStatusesSize : " + todaysTmpStatus.size() + "TodaysMenuSize :" + todaysMenu.size());
                item2_homeActions = new ExpandableDrawerItem().withIcon(R.drawable.food).withName(items).withSelectable(false);
                item2_homeActions.withSubItems(
                        new PrimaryDrawerItem().withName("Alışveriş Listesine Ekle").withLevel(3).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                todaysMenu = new ArrayList<>();
                                todaysTmpStatus = new ArrayList<>();

                                todaysTmpStatus = new ArrayList<Integer>();
                                for(MealStructure tmpMeal : databaseMeals.getAllInformation())
                                {
                                    for(int i = 0; i<itemIDs.size();i++)
                                    {
                                        if(tmpMeal.ID == itemIDs.get(i))
                                        {
                                            todaysMenu.add(tmpMeal.INGREDIENTS);
                                            {

                                                {
                                                    String[] itemSplit = tmpMeal.INGREDIENTS.split(getString(R.string.Seperator));
                                                    int ingredientsSize = itemSplit.length;
                                                    for(int x = 0;x < ingredientsSize; x++)
                                                        todaysTmpStatus.add((Integer) 0);
                                                }
                                            }
                                        }

                                    }
                                }



                                String itemString = readFromFile(TabloEkleme.this,"itemString.txt");
                                String statusString = readFromFile(TabloEkleme.this,"statusString.txt");



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
                                    todaysMenu.addAll(itemArray);
                                    todaysTmpStatus.addAll(statusArray);
                                }



                                try
                                {
                                    itemString = TextUtils.join(getString(R.string.Seperator),todaysMenu);
                                    statusString = TextUtils.join(getString(R.string.Seperator),todaysTmpStatus);

                                    Log.i("items",itemString);

                                    if(Objects.equals(itemString, "") || itemString == null)
                                    {
                                        itemString ="0";statusString="0";
                                    }
                                    JSONObject jsonParam = new JSONObject();
                                    jsonParam.put("list",itemString);
                                    jsonParam.put("status",statusString);
                                    NetworkUtils.HttpPost(getString(R.string.action_updateshoppinglist),_TOKEN,jsonParam,new Handler());

                                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("itemString.txt", Context.MODE_PRIVATE));
                                    outputStreamWriter.write(itemString);
                                    outputStreamWriter.close();

                                    outputStreamWriter = new OutputStreamWriter(openFileOutput("statusString.txt", Context.MODE_PRIVATE));
                                    outputStreamWriter.write(statusString);
                                    outputStreamWriter.close();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                return false;
                            }
                        })
                );
                drawerBuilder.addItem(item2_homeActions);
            }


            /**if(result != null)
            {
                result.removeAllItems();

                if(result.isDrawerOpen())
                    result.closeDrawer();
            }*/

            result = drawerBuilder;



        }
    }


    @Override
    public void disbandOnSaveButtonClick(DialogFragment dialog) {

        final Spinner mSpinner = (Spinner) dialog.getDialog().findViewById(R.id.disband_spinner);
        DBHandlerFriends database = new DBHandlerFriends(TabloEkleme.this);
        int position = mSpinner.getSelectedItemPosition();
        Log.i("TabloEkleme","position " + String.valueOf(position));
        int id =0 ;
        if(position != -1)
            id = database.getAllInformation().get(position).USER_ID;
        NetworkUtils networkUtils = new NetworkUtils(TabloEkleme.this);
        networkUtils.setAction(getString(R.string.action_leavehome));

        String URL = networkUtils.getConnectionUrl()
                + "?newadminid=" + String.valueOf(id);
        NetworkUtils.HttpGet(_TOKEN,URL,new Handler());



        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}
