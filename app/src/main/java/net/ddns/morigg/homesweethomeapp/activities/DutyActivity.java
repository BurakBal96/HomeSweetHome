package net.ddns.morigg.homesweethomeapp.activities;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.dialog_fragments.DutyAdd;
import net.ddns.morigg.homesweethomeapp.recycleradapter.DutyAdapter;
import net.ddns.morigg.homesweethomeapp.recycleradapter.MultiSpinnerSearch;
import net.ddns.morigg.homesweethomeapp.recycleradapter.NotepadAdapter;
import net.ddns.morigg.homesweethomeapp.structures.DutyStructure;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerDuty;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerFriends;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerNotepad;
import net.ddns.morigg.homesweethomeapp.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

import static net.ddns.morigg.homesweethomeapp.activities.TabloEkleme._TOKEN;
import static net.ddns.morigg.homesweethomeapp.activities.TabloEkleme.mainUser;

public class DutyActivity extends AppCompatActivity implements DutyAdd.DutyAddListener {

    int DayOfMonth = 0;
    private RecyclerView recyc;
    private DutyAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    DBHandlerDuty database;
    Button mBtnAdd;
    List<DutyStructure> items;

    private BroadcastReceiver mHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new Handler().post(new Runnable() {

                @Override
                public void run()
                {
                    Toast.makeText(DutyActivity.this,"Görevler Güncellendi",Toast.LENGTH_SHORT).show();

                    //recreate();
                    mAdapter.items = database.getAllInformation();
                    mAdapter.notifyDataSetChanged();
                }
            });

            //Toast.makeText(NotepadActivity.this,"TEST",Toast.LENGTH_SHORT);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandler);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duty);

        setTitle(R.string.title_activity_duty);
        LocalBroadcastManager.getInstance(this).registerReceiver(mHandler,new IntentFilter("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-DUTY"));


        database = new DBHandlerDuty(this);

        recyc = (RecyclerView) findViewById(R.id.duty_recycler);
        recyc.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyc.setLayoutManager(layoutManager);

        //mAdapter= new NotepadAdapter(arrayListData,this);
        items = database.getAllInformation();
        Log.i("size",items.size()+"");
        mAdapter = new DutyAdapter( items,this,DutyActivity.this);
        recyc.setAdapter(mAdapter);

        mBtnAdd = (Button) findViewById(R.id.duty_add);

        if(mainUser.POSITION == 2)
        {
            mBtnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DutyAdd dialog = new DutyAdd(DutyActivity.this);
                    dialog.setCancelable(false);
                    dialog.show(getFragmentManager(), "Info");
                }
            });

            mBtnAdd.setVisibility(View.VISIBLE);
        }


        /**final Calendar takvim = Calendar.getInstance();
        int yil = takvim.get(Calendar.YEAR);
        int ay = takvim.get(Calendar.MONTH);
        int gun = takvim.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // ay değeri 0 dan başladığı için (Ocak=0, Şubat=1,..,Aralık=11)
                        // değeri 1 artırarak gösteriyoruz.
                        month += 1;
                        // year, month ve dayOfMonth değerleri seçilen tarihin değerleridir.
                        // Edittextte bu değerleri gösteriyoruz.
                        //etTarih.setText(dayOfMonth + "/" + month + "/" + year);
                        //Log.i("tarih","gun"+year+"\nay"+month+"\nyil"+dayOfMonth);

                        DayOfMonth = dayOfMonth;

                    }
                }, yil, 0, 1);


        dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Seç", dpd);
        dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", dpd);
        dpd.show();*/


    }

    @Override
    public void DutyAdd_onSaveButtonClick(DialogFragment dialog) {
        boolean correct = true;

        final EditText mTxtDate = (EditText) dialog.getDialog().findViewById(R.id.duty_dialog_date);
        final EditText mTxtAction = (EditText) dialog.getDialog().findViewById(R.id.duty_dialog_action);
        final Spinner mSpinner = (Spinner) dialog.getDialog().findViewById(R.id.duty_dialog_spinner);

        DBHandlerFriends database = new DBHandlerFriends(DutyActivity.this);
        int position = mSpinner.getSelectedItemPosition();

        int id =0 ;
        if(position == database.getAllInformation().size())
            id = mainUser.USER_ID;
        else if(position != -1)
            id = database.getAllInformation().get(position).USER_ID;
        else
            correct = false;

        int date = Integer.parseInt(mTxtDate.getText().toString());

        if(date > 31 || date < 1)
            correct = false;


        if(correct)
        {

            try
            {
                JSONObject jsonParam = new JSONObject();
                JSONObject duty = new JSONObject();

                duty.put("Day",date);
                duty.put("Work",mTxtAction.getText().toString());

                jsonParam.put("Housework",duty);
                jsonParam.put("FriendId",id);

                Log.i("Json",jsonParam.toString());

                NetworkUtils.HttpPost(getString(R.string.action_addduty),_TOKEN,jsonParam,new Handler());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
