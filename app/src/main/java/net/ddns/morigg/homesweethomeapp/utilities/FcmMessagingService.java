package net.ddns.morigg.homesweethomeapp.utilities;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.activities.HomeRequestsActivity;
import net.ddns.morigg.homesweethomeapp.activities.LoginActivity;
import net.ddns.morigg.homesweethomeapp.activities.TabloEkleme;
import net.ddns.morigg.homesweethomeapp.structures.DutyStructure;
import net.ddns.morigg.homesweethomeapp.structures.ExpenseStructure;
import net.ddns.morigg.homesweethomeapp.structures.HomeRequestStructure;
import net.ddns.morigg.homesweethomeapp.structures.MealStructure;
import net.ddns.morigg.homesweethomeapp.structures.MenuStructure;
import net.ddns.morigg.homesweethomeapp.structures.NotepadStructure;
import net.ddns.morigg.homesweethomeapp.structures.UserInformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static net.ddns.morigg.homesweethomeapp.activities.HomeRequestsActivity.homeRequestActivity;
import static net.ddns.morigg.homesweethomeapp.activities.ShoppingActivity.isChanged;
import static net.ddns.morigg.homesweethomeapp.activities.ShoppingActivity.isShoppingActivityActive;
import static net.ddns.morigg.homesweethomeapp.activities.TabloEkleme.isActivityAlive;
import static net.ddns.morigg.homesweethomeapp.activities.TabloEkleme.mainUser;

/**
 * Created by MoriartyGG on 3.05.2018.
 */

public class FcmMessagingService extends FirebaseMessagingService {


    public static void clearAllDatabases(Context context)
    {
        DBHandlerFriends friends = new DBHandlerFriends(context);
        DBHandlerNotepad notepad = new DBHandlerNotepad(context);
        DBHandlerHomeRequests requests = new DBHandlerHomeRequests(context);
        DBHandlerExpense expense = new DBHandlerExpense(context);
        DBHandlerMenu menu = new DBHandlerMenu(context);
        DBHandlerMeals meals = new DBHandlerMeals(context);

        friends.deleteALL();friends.close();
        notepad.deleteALL();notepad.close();
        requests.deleteALL();requests.close();
        expense.deleteALL();expense.close();
        menu.deleteALL();
        meals.deleteALL();
    }






    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //super.onMessageReceived(remoteMessage);


        if(remoteMessage.getData().size() > 0)
        {
            Log.i("FCM_MESSAGE",remoteMessage.getData().toString());
            Log.v("FCM_MESSAGE",remoteMessage.getData().toString());

            String FcmType = remoteMessage.getData().get("FcmType");


            //Position = 2
            if(Objects.equals(FcmType, "JoinHomeRequest"))
            {

                int UserId = Integer.parseInt(remoteMessage.getData().get("RequesterId"));
                String NameSurname = remoteMessage.getData().get("RequesterName") + " " + remoteMessage.getData().get("RequesterLastName");
                String UserName = remoteMessage.getData().get("RequesterUsername");

                HomeRequestStructure tmpRequest = new HomeRequestStructure(NameSurname, UserName, UserId);
                DBHandlerHomeRequests database = new DBHandlerHomeRequests(this);


                database.addNewRow(tmpRequest);
                database.close();

                if(isActivityAlive)
                {
                    Intent intentRequest = new Intent("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-NEWREQUEST");
                    LocalBroadcastManager localBroadcastManagerRequest = LocalBroadcastManager.getInstance(getApplicationContext());
                    localBroadcastManagerRequest.sendBroadcast(intentRequest);
                }

            }

            //Position = 0
            if(Objects.equals(FcmType, "InviteHomeRequest"))
            {
                int HomeId = Integer.parseInt(remoteMessage.getData().get("InvitedHomeId"));
                String NameSurname = remoteMessage.getData().get("InviterFirstName") + " " + remoteMessage.getData().get("InviterLastName");
                String UserName = remoteMessage.getData().get("InviterUsername");

                HomeRequestStructure tmpRequest = new HomeRequestStructure(NameSurname, UserName, HomeId);
                DBHandlerHomeRequests database = new DBHandlerHomeRequests(this);
                database.addNewRow(tmpRequest);
                database.close();

                if(isActivityAlive)
                {
                    Intent intentRequest = new Intent("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-NEWREQUEST");
                    LocalBroadcastManager localBroadcastManagerRequest = LocalBroadcastManager.getInstance(getApplicationContext());
                    localBroadcastManagerRequest.sendBroadcast(intentRequest);
                }
            }



            /** {NewNote={"Content":"icerik","Title":"baslik","Id":0}, FcmType=NotepadAdd}  */
            if(Objects.equals(FcmType, "NotepadAdd"))
            {
                try {
                    JSONObject json = new JSONObject(remoteMessage.getData().get("NewNote"));

                    int id = Integer.parseInt(json.getString("Id"));
                    String title = json.getString("Title");
                    String content = json.getString("Content");

                    NotepadStructure tmpNotepad = new NotepadStructure(title,content,id);
                    DBHandlerNotepad database = new DBHandlerNotepad(this);
                    database.addNewRow(tmpNotepad);
                    database.close();

                    if(isActivityAlive)
                    {
                        Intent intentNotepad = new Intent("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-NOTEPAD");
                        LocalBroadcastManager localBroadcastManagerNotepad = LocalBroadcastManager.getInstance(getApplicationContext());
                        localBroadcastManagerNotepad.sendBroadcast(intentNotepad);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            /** {FcmType=NotepadUpdate, UpdatedNote={"Content":"enyeni icerik","Title":"en yeni baslik bu","Id":14}} */
            if(Objects.equals(FcmType, "NotepadUpdate"))
            {
                try {

                    //Server ile eldeki notpad arşivini senkronla, notepad activity'si açık ise recreate et
                    JSONObject json = new JSONObject(remoteMessage.getData().get("UpdatedNote"));

                    int id = Integer.parseInt(json.getString("Id"));
                    String title = json.getString("Title");
                    String content = json.getString("Content");


                    DBHandlerNotepad database = new DBHandlerNotepad(this);
                    database.editItem(id,title,content);
                    database.close();

                    if(isActivityAlive)
                    {
                        Intent intentNotepad = new Intent("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-NOTEPAD");
                        LocalBroadcastManager localBroadcastManagerNotepad = LocalBroadcastManager.getInstance(getApplicationContext());
                        localBroadcastManagerNotepad.sendBroadcast(intentNotepad);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            /** {DeletedNote={"Content":"enyeni icerik","Title":"en yeni baslik bu","Id":14}, FcmType=NotepadDelete} */
            if(Objects.equals(FcmType, "NotepadDelete"))
            {

                int id = Integer.parseInt(remoteMessage.getData().get("DeletedNote"));

                DBHandlerNotepad database = new DBHandlerNotepad(this);
                database.deleteRow(id);
                database.close();

                if(isActivityAlive)
                {
                    Intent intentNotepad = new Intent("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-NOTEPAD");
                    LocalBroadcastManager localBroadcastManagerNotepad = LocalBroadcastManager.getInstance(getApplicationContext());
                    localBroadcastManagerNotepad.sendBroadcast(intentNotepad);
                }

            }

            if(Objects.equals(FcmType, "NewFriend"))
            {
                try
                {
                    DBHandlerFriends database = new DBHandlerFriends(this);

                    JSONObject json = new JSONObject(remoteMessage.getData().get("Friend"));

                    int id = Integer.parseInt(json.getString("Id"));
                    int position = Integer.parseInt(json.getString("Position"));
                    String username = json.getString("Username");
                    String firstName = json.getString("FirstName");
                    String lastName = json.getString("LastName");
                    Double debt = Double.valueOf(json.getString("Debt"));

                    UserInformation newFriend = new UserInformation(id,position,username,firstName,lastName,debt);

                    database.addNewRow(newFriend);
                    database.close();

                    if(isActivityAlive)
                    {
                        Intent intentNewFriend = new Intent("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-NEWFRIEND");
                        LocalBroadcastManager localBroadcastManagerNewFriend = LocalBroadcastManager.getInstance(getApplicationContext());
                        localBroadcastManagerNewFriend.sendBroadcast(intentNewFriend);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            if(Objects.equals(FcmType, "LeaveHome"))
            {
                int id = Integer.parseInt(remoteMessage.getData().get("LeaverId"));
                if(id == mainUser.USER_ID)
                {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else
                {
                    DBHandlerFriends database = new DBHandlerFriends(this);
                    database.deleteRow(id);
                    database.close();

                    if(isActivityAlive)
                    {
                        Intent intentNewFriend = new Intent("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-NEWFRIEND");
                        LocalBroadcastManager localBroadcastManagerNewFriend = LocalBroadcastManager.getInstance(getApplicationContext());
                        localBroadcastManagerNewFriend.sendBroadcast(intentNewFriend);
                    }

                }

            }

            if(Objects.equals(FcmType, "AllFriends"))
            {
                clearAllDatabases(this);


                OutputStreamWriter outputStreamWriter = null;
                try {
                    outputStreamWriter = new OutputStreamWriter(this.openFileOutput(getString(R.string.firstlogin_logname), Context.MODE_PRIVATE));
                    outputStreamWriter.write("");
                    outputStreamWriter.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(isActivityAlive)
                {
                    Intent intentNewHome = new Intent("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-NEWHOME");
                    LocalBroadcastManager localBroadcastManagerNewHome = LocalBroadcastManager.getInstance(getApplicationContext());
                    localBroadcastManagerNewHome.sendBroadcast(intentNewHome);
                }

            }

            if(Objects.equals(FcmType, "ShoppingListUpdate"))
            {


                try
                {
                    JSONObject json = new JSONObject(remoteMessage.getData().get("UpdatedShoppingList"));

                    String itemString = json.getString("List");
                    String statusString = json.getString("Status");



                    if( isShoppingActivityActive && isChanged )
                    {

                    }
                    else
                    {
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("itemString.txt", Context.MODE_PRIVATE));
                        outputStreamWriter.write(itemString);
                        outputStreamWriter.close();

                        outputStreamWriter = new OutputStreamWriter(openFileOutput("statusString.txt", Context.MODE_PRIVATE));
                        outputStreamWriter.write(statusString);
                        outputStreamWriter.close();
                    }

                    if(isActivityAlive)
                    {
                        Intent intentShopping = new Intent("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-SHOPPING");
                        LocalBroadcastManager localBroadcastManagerShopping = LocalBroadcastManager.getInstance(getApplicationContext());
                        intentShopping.putExtra("Items",itemString);
                        intentShopping.putExtra("Status",statusString);
                        localBroadcastManagerShopping.sendBroadcast(intentShopping);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            if(Objects.equals(FcmType, "AddExpense"))
            {
                try {
                    JSONObject json = new JSONObject(remoteMessage.getData().get("Content"));

                    String[] action = getResources().getStringArray(R.array.expense_actions);

                    int id = json.getInt("Id");
                    Double cost = json.getDouble("Cost");
                    int typeInt = json.getInt("EType")-1;
                    if(typeInt == 5)
                        typeInt = 3;
                    String type = action[typeInt];
                    String title = json.getString("Title");
                    String content = json.getString("Content");
                    String LastUpdated = json.getString("LastUpdated");
                    long timeInMilliseconds = 0;
                    String author = remoteMessage.getData().get("Author");
                    JSONArray jsonArray = new JSONArray(remoteMessage.getData().get("Participants"));
                    //Log.i("JsonArray",jsonArray.toString());

                    String participants = jsonArray.toString();
                    participants = participants.substring(1,participants.length() -1);
                    //Log.i("participants",participants);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    try {
                        Date mDate = sdf.parse(LastUpdated);
                        timeInMilliseconds = mDate.getTime();
                        //System.out.println("Date in milli :: " + timeInMilliseconds);

                        //Date date = new Date(timeInMilliseconds);
                        //DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                        //Log.i("TIME :",dateFormat.format(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    ExpenseStructure expense = new ExpenseStructure(id,cost,title,content,type,String.valueOf(timeInMilliseconds),author,participants);

                    DBHandlerExpense database = new DBHandlerExpense(this);
                    database.addNewRow(expense);
                    database.close();

                    if(isActivityAlive)
                    {
                        Intent intentExpense = new Intent("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-EXPENSE");
                        LocalBroadcastManager localBroadcastManagerExpense = LocalBroadcastManager.getInstance(getApplicationContext());
                        localBroadcastManagerExpense.sendBroadcast(intentExpense);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


            if(Objects.equals(FcmType, "UpdateExpense"))
            {
                try {
                    JSONObject json = new JSONObject(remoteMessage.getData().get("Content"));

                    String[] action = getResources().getStringArray(R.array.expense_actions);

                    int id = json.getInt("Id");
                    Double cost = json.getDouble("Cost");
                    String type = action[json.getInt("EType")-1];
                    String title = json.getString("Title");
                    String content = json.getString("Content");
                    String LastUpdated = json.getString("LastUpdated");
                    long timeInMilliseconds = 0;
                    String author = remoteMessage.getData().get("Author");
                    JSONArray jsonArray = new JSONArray(remoteMessage.getData().get("Participants"));
                    //Log.i("JsonArray",jsonArray.toString());

                    String participants = jsonArray.toString();
                    participants = participants.substring(1,participants.length() -1);
                    //Log.i("participants",participants);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    try {
                        Date mDate = sdf.parse(LastUpdated);
                        timeInMilliseconds = mDate.getTime();
                        //System.out.println("Date in milli :: " + timeInMilliseconds);

                        //Date date = new Date(timeInMilliseconds);
                        //DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                        //Log.i("TIME :",dateFormat.format(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    ExpenseStructure expense = new ExpenseStructure(id,cost,title,content,type,String.valueOf(timeInMilliseconds),author,participants);

                    DBHandlerExpense database = new DBHandlerExpense(this);
                    database.editItem(expense);
                    //database.addNewRow(expense);
                    database.close();

                    if(isActivityAlive)
                    {
                        Intent intentExpense = new Intent("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-EXPENSE");
                        LocalBroadcastManager localBroadcastManagerExpense = LocalBroadcastManager.getInstance(getApplicationContext());
                        localBroadcastManagerExpense.sendBroadcast(intentExpense);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            if(Objects.equals(FcmType, "DeleteExpense"))
            {
                DBHandlerExpense database = new DBHandlerExpense(this);

                int id = Integer.parseInt(remoteMessage.getData().get("ExpenseId"));
                database.deleteRow(id);
                //database.addNewRow(expense);
                database.close();

                if(isActivityAlive)
                {
                    Intent intentExpense = new Intent("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-EXPENSE");
                    LocalBroadcastManager localBroadcastManagerExpense = LocalBroadcastManager.getInstance(getApplicationContext());
                    localBroadcastManagerExpense.sendBroadcast(intentExpense);
                }
            }

            if(Objects.equals(FcmType, "GiveMoney"))
            {
                //GiveMoney
                //TakeMoney
                int id = Integer.parseInt(remoteMessage.getData().get("ToId"));
                Double debt = Double.valueOf(remoteMessage.getData().get("NewDebt"));

                DBHandlerFriends database = new DBHandlerFriends(this);
                database.editItem(id,debt);
                database.close();

                if(isActivityAlive)
                {

                }

                Intent intentNewFriend = new Intent("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-NEWFRIEND");
                LocalBroadcastManager localBroadcastManagerNewFriend = LocalBroadcastManager.getInstance(getApplicationContext());
                localBroadcastManagerNewFriend.sendBroadcast(intentNewFriend);

            }
            if( Objects.equals(FcmType, "TakeMoney"))
            {
                int id = Integer.parseInt(remoteMessage.getData().get("FromId"));
                Double debt = Double.valueOf(remoteMessage.getData().get("NewDebt"));

                DBHandlerFriends database = new DBHandlerFriends(this);
                database.editItem(id,debt);
                database.close();

                if(isActivityAlive)
                {
                    Intent intentNewFriend = new Intent("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-NEWFRIEND");
                    LocalBroadcastManager localBroadcastManagerNewFriend = LocalBroadcastManager.getInstance(getApplicationContext());
                    localBroadcastManagerNewFriend.sendBroadcast(intentNewFriend);
                }
            }

            if(Objects.equals(FcmType, "AddMeal"))
            {
                try {
                    JSONObject json = new JSONObject(remoteMessage.getData().get("Meal"));

                    int id = json.getInt("Id");
                    String name = json.getString("Name");
                    String note = json.getString("Note");
                    String ingredients = json.getString("Ingredients");

                    DBHandlerMeals database = new DBHandlerMeals(this);
                    database.addNewRow(new MealStructure(id,name,note,ingredients));
                    database.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            if(Objects.equals(FcmType, "AddMeal"))
            {
                try {
                    JSONObject json = new JSONObject(remoteMessage.getData().get("Meal"));

                    int id = json.getInt("Id");
                    String name = json.getString("Name");
                    String note = json.getString("Note");
                    String ingredients = json.getString("Ingredients");

                    DBHandlerMeals database = new DBHandlerMeals(this);
                    database.addNewRow(new MealStructure(id,name,note,ingredients));
                    database.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }



            if(Objects.equals(FcmType, "AddMenu"))
            {
                try {
                    JSONObject json = new JSONObject(remoteMessage.getData().get("Menu"));

                    int id = json.getInt("Id");
                    String date = json.getString("Date");

                    JSONArray mealIds = new JSONArray(remoteMessage.getData().get("MealIds"));
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



                    DBHandlerMenu database = new DBHandlerMenu(this);
                    database.addNewRow(new MenuStructure(id,String.valueOf(timeInMilliseconds),mealIdsString));
                    database.close();

                    if(isActivityAlive)
                    {
                        Intent intentMenu = new Intent("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-MENU");
                        LocalBroadcastManager localBroadcastManagerMenu = LocalBroadcastManager.getInstance(getApplicationContext());
                        localBroadcastManagerMenu.sendBroadcast(intentMenu);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(Objects.equals(FcmType, "UpdateMenu"))
            {
                try {
                    JSONObject json = new JSONObject(remoteMessage.getData().get("Menu"));

                    int id = json.getInt("Id");
                    String date = json.getString("Date");

                    JSONArray mealIds = new JSONArray(remoteMessage.getData().get("MealIds"));
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



                    DBHandlerMenu database = new DBHandlerMenu(this);
                    database.editItem(id,String.valueOf(timeInMilliseconds),mealIdsString);
                    //database.addNewRow(new MenuStructure(id,String.valueOf(timeInMilliseconds),mealIdsString));
                    database.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(Objects.equals(FcmType, "DeleteMenu"))
            {
                int id = Integer.parseInt(remoteMessage.getData().get("MenuId"));
                DBHandlerMenu database = new DBHandlerMenu(this);
                database.deleteRow(id);
                database.close();

                if(isActivityAlive)
                {
                    Intent intentMenu = new Intent("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-MENU");
                    LocalBroadcastManager localBroadcastManagerMenu = LocalBroadcastManager.getInstance(getApplicationContext());
                    localBroadcastManagerMenu.sendBroadcast(intentMenu);
                }
            }

            if(Objects.equals(FcmType, "AddHousework"))
            {
                int userId =  Integer.parseInt(remoteMessage.getData().get("FriendId"));
                DBHandlerFriends databasefriends = new DBHandlerFriends(this);
                List<UserInformation> friends = databasefriends.getAllInformation();
                databasefriends.close();
                String author = "";
                if(userId == mainUser.USER_ID)
                    author = mainUser.FIRST_NAME + " " + mainUser.LAST_NAME;
                else
                for(int i = 0;i<friends.size();i++)
                {
                    if(userId == friends.get(i).USER_ID)
                        author = friends.get(i).FIRST_NAME + " " + friends.get(i).LAST_NAME;
                }

                try {
                    JSONObject json = new JSONObject(remoteMessage.getData().get("Housework"));

                    String action = json.getString("Work");
                    int id = json.getInt("Id");
                    int day = json.getInt("Day");

                    DutyStructure duty = new DutyStructure(id,day,author,action,userId);

                    DBHandlerDuty database = new DBHandlerDuty(this);
                    database.addNewRow(duty);
                    database.close();

                    Log.i("AllInfo",id+","+day+","+author+","+action+","+userId);
                    if(isActivityAlive)
                    {
                        Intent intentDuty = new Intent("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-DUTY");
                        LocalBroadcastManager localBroadcastManagerDuty = LocalBroadcastManager.getInstance(getApplicationContext());
                        localBroadcastManagerDuty.sendBroadcast(intentDuty);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            if(Objects.equals(FcmType, "DeleteHousework"))
            {
                int id = Integer.parseInt(remoteMessage.getData().get("HouseworkId"));

               DBHandlerDuty database = new DBHandlerDuty(this);
               database.deleteRow(id);

                if(isActivityAlive)
                {
                    Intent intentDuty = new Intent("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-DUTY");
                    LocalBroadcastManager localBroadcastManagerDuty = LocalBroadcastManager.getInstance(getApplicationContext());
                    localBroadcastManagerDuty.sendBroadcast(intentDuty);
                }

            }

        }
    }
}
