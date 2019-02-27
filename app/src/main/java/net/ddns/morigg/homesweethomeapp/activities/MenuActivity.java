package net.ddns.morigg.homesweethomeapp.activities;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.dialog_fragments.MenuAdd;
import net.ddns.morigg.homesweethomeapp.recycleradapter.MultiSpinnerSearch;
import net.ddns.morigg.homesweethomeapp.structures.MenuStructure;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerMenu;
import net.ddns.morigg.homesweethomeapp.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import static net.ddns.morigg.homesweethomeapp.activities.TabloEkleme._TOKEN;

public class MenuActivity extends AppCompatActivity implements MenuAdd.AddMenuDialogListener {

    private CaldroidFragment caldroidFragment;
    private CaldroidFragment dialogCaldroidFragment;

    Button mBtnNewMeal, mBtnEditMeal;
    boolean isChecked = false;
    public static boolean delete = false;
    MenuStructure selectedDay = null;


    private BroadcastReceiver mHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new Handler().post(new Runnable() {

                @Override
                public void run()
                {
                    Toast.makeText(MenuActivity.this,getString(R.string.Menu_UpdateNotif),Toast.LENGTH_SHORT).show();
                    recreate();
                }
            });

        }
    };

    private void setCustomResourceForDates() {
        /*Calendar cal = Calendar.getInstance();

        // Min date is last 7 days
        cal.add(Calendar.DATE, -7);
        Date blueDate = cal.getTime();

        // Max date is next 7 days
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);
        Date greenDate = cal.getTime();

        if (caldroidFragment != null) {
            ColorDrawable blue = new ColorDrawable(getResources().getColor(R.color.blue));
            ColorDrawable green = new ColorDrawable(Color.GREEN);
            caldroidFragment.setBackgroundDrawableForDate(blue, blueDate);
            caldroidFragment.setBackgroundDrawableForDate(green, greenDate);
            caldroidFragment.setTextColorForDate(R.color.white, blueDate);
            caldroidFragment.setTextColorForDate(R.color.white, greenDate);
        }*/
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setTitle(getString(R.string.title_activity_menu));


        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

        DBHandlerMenu databse = new DBHandlerMenu(this);
        List<MenuStructure> menu = databse.getAllInformation();


        ColorDrawable green = new ColorDrawable(getResources().getColor(R.color.LimeGreen));
        for(MenuStructure oneDay : menu)
        {
            Date greenDate = new Date(Long.valueOf(oneDay.DATE));
            caldroidFragment.setBackgroundDrawableForDate(green, greenDate);
           // caldroidFragment.setTextColorForDate(R.color.White, greenDate);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mHandler,new IntentFilter("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-MENU"));




        final SimpleDateFormat formatter = (SimpleDateFormat) android.text.format.DateFormat.getDateFormat(MenuActivity.this);
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                //Toast.makeText(getApplicationContext(), formatter.format(date),Toast.LENGTH_SHORT).show();
                Log.i("TodaysDate",formatter.format(date));
                DBHandlerMenu database = new DBHandlerMenu(MenuActivity.this);
                List<MenuStructure> list = database.getAllInformation();

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.HOUR_OF_DAY, 0);

                long timeInMilis = date.getTime();

                for(MenuStructure oneMenu : list)
                {
                    if(timeInMilis == Long.valueOf(oneMenu.DATE))
                    {
                        isChecked = true;
                        selectedDay = oneMenu;
                    }
                }

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
                if(isChecked)
                {
                    MenuAdd dialog = new MenuAdd(getBaseContext(), MenuActivity.this);
                    Bundle mArgs = new Bundle();
                    mArgs.putString("hint",getString(R.string.Menu_MenuEdit));

                    mArgs.putString("date",df.format(date));
                    mArgs.putString("formattedDate",formatter.format(date));
                    //mArgs.putLong("date",date.getTime());
                    int size = selectedDay.MEALIDS.length();
                    mArgs.putString("mealids",selectedDay.MEALIDS.substring(1,size-1));
                    Log.i("mealids",selectedDay.MEALIDS);
                    
                    dialog.setArguments(mArgs);
                    dialog.setCancelable(true);
                    dialog.show(getFragmentManager(),"Info");
                }
                else
                {
                    MenuAdd dialog = new MenuAdd(getBaseContext(), MenuActivity.this);
                    Bundle mArgs = new Bundle();
                    mArgs.putString("hint",getString(R.string.Menu_MenuAdd));
                    mArgs.putString("date",df.format(date));
                    mArgs.putString("formattedDate",formatter.format(date));
                    //mArgs.putLong("date",date.getTime());

                    dialog.setArguments(mArgs);
                    dialog.setCancelable(true);
                    dialog.show(getFragmentManager(),"Info");
                }

            }

            @Override
            public void onChangeMonth(int month, int year) {
                String text = "month: " + month + " year: " + year;
               // Toast.makeText(getApplicationContext(), text,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClickDate(Date date, View view) {
                //Toast.makeText(getApplicationContext(),"Long click " + formatter.format(date),Toast.LENGTH_SHORT).show();
                Log.i("TodaysDate",formatter.format(date));
                DBHandlerMenu database = new DBHandlerMenu(MenuActivity.this);
                List<MenuStructure> list = database.getAllInformation();

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.HOUR_OF_DAY, 0);

                long timeInMilis = date.getTime();

                for(MenuStructure oneMenu : list)
                {
                    if(timeInMilis == Long.valueOf(oneMenu.DATE))
                    {
                        isChecked = true;
                        selectedDay = oneMenu;
                    }
                }

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
                if(isChecked)
                {
                    MenuAdd dialog = new MenuAdd(getBaseContext(), MenuActivity.this);
                    Bundle mArgs = new Bundle();
                    mArgs.putString("hint",getString(R.string.Menu_MenuEdit));

                    mArgs.putString("date",df.format(date));
                    mArgs.putString("formattedDate",formatter.format(date));
                    //mArgs.putLong("date",date.getTime());
                    int size = selectedDay.MEALIDS.length();
                    mArgs.putString("mealids",selectedDay.MEALIDS.substring(1,size-1));
                    Log.i("mealids",selectedDay.MEALIDS);

                    dialog.setArguments(mArgs);
                    dialog.setCancelable(true);
                    dialog.show(getFragmentManager(),"Info");
                }
                else
                {
                    MenuAdd dialog = new MenuAdd(getBaseContext(), MenuActivity.this);
                    Bundle mArgs = new Bundle();
                    mArgs.putString("hint",getString(R.string.Menu_MenuAdd));
                    mArgs.putString("date",df.format(date));
                    mArgs.putString("formattedDate",formatter.format(date));
                    //mArgs.putLong("date",date.getTime());

                    dialog.setArguments(mArgs);
                    dialog.setCancelable(true);
                    dialog.show(getFragmentManager(),"Info");
                }
            }

            @Override
            public void onCaldroidViewCreated() {
                if (caldroidFragment.getLeftArrowButton() != null) {
                    //Toast.makeText(getApplicationContext(),"Caldroid view is created", Toast.LENGTH_SHORT) .show();
                }
            }

        };

        caldroidFragment.setCaldroidListener(listener);
        //caldroidFragment.setShowNavigationArrows(false);
        //caldroidFragment.setEnableSwipe(false);

        caldroidFragment.refreshView();


        /**
            caldroidFragment.clearDisableDates();
            caldroidFragment.clearSelectedDates();
            caldroidFragment.setMinDate(null);
            caldroidFragment.setMaxDate(null);
            caldroidFragment.setShowNavigationArrows(true);
            caldroidFragment.setEnableSwipe(true);
            caldroidFragment.refreshView();
         */


        mBtnNewMeal = (Button) findViewById(R.id.menu_addmeal);
        mBtnNewMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(MenuActivity.this, MealAddActivity.class);
                intent.putExtra("action",getString(R.string.action_addmeal));
                startActivity(intent);
            }
        });

        /**mBtnEditMeal = (Button) findViewById(R.id.menu_editmeal);
        mBtnEditMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(MenuActivity.this, MealAddActivity.class);
                intent.putExtra("action",getString(R.string.action_updatemeal));
                startActivity(intent);
            }
        });*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandler);
    }

    @Override
    public void menuOnSaveButtonClick(DialogFragment dialog) {
        Bundle mArgs = dialog.getArguments();
        Log.i("date",mArgs.getString("date"));

        String hint = mArgs.getString("hint");

        MultiSpinnerSearch mMultiSpinnerSearch = (MultiSpinnerSearch) dialog.getDialog().findViewById(R.id.menuadd_meals);

        if(Objects.equals(hint, getString(R.string.Menu_MenuAdd)))
        {
            try
            {
                JSONObject jsonParam = new JSONObject();
                JSONObject jsonMenu = new JSONObject();
                jsonMenu.put("Date",mArgs.getString("date"));

                jsonParam.put("Menu",jsonMenu);

                List<Long> mealIds = mMultiSpinnerSearch.getSelectedIds();

                if(mealIds.size() == 0)
                {
                    //TODO MenuAdd Error Handle
                }
                else
                {
                    JSONArray jsonMeals = new JSONArray(mealIds.toArray());
                    jsonParam.put("MealIds",jsonMeals);

                    NetworkUtils.HttpPost(getString(R.string.action_addmenu),_TOKEN,jsonParam,new Handler());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            if(delete)
            {
                NetworkUtils networkUtils = new NetworkUtils(this);
                networkUtils.setAction(getString(R.string.action_deletemenu));
                String URL = networkUtils.getConnectionUrl()
                        +"?menuid=" + selectedDay.ID ;
                NetworkUtils.HttpGet(_TOKEN,URL,new Handler());
            }
            else
            {
                try
                {
                    JSONObject jsonParam = new JSONObject();
                    JSONObject jsonMenu = new JSONObject();
                    jsonMenu.put("Date",mArgs.getString("date"));
                    jsonMenu.put("Id",selectedDay.ID);
                    jsonParam.put("Menu",jsonMenu);

                    List<Long> mealIds = mMultiSpinnerSearch.getSelectedIds();

                    if(mealIds.size() == 0)
                    {
                        //TODO MenuAdd Error Handle
                    }
                    else
                    {
                        JSONArray jsonMeals = new JSONArray(mealIds.toArray());
                        jsonParam.put("MealIds",jsonMeals);

                        NetworkUtils.HttpPost(getString(R.string.action_updatemenu),_TOKEN,jsonParam,new Handler());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        Intent intentNewFriend = new Intent("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-NEWFRIEND");
        LocalBroadcastManager localBroadcastManagerNewFriend = LocalBroadcastManager.getInstance(getApplicationContext());
        localBroadcastManagerNewFriend.sendBroadcast(intentNewFriend);



        //Log.i("formattedDate",mArgs.getString("formattedDate"));
    }

    public static String toISO8601UTC(Date date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        return df.format(date);
    }

}


