package net.ddns.morigg.homesweethomeapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.recycleradapter.HomeRequestAdapter;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerHomeRequests;

public class HomeRequestsActivity extends AppCompatActivity {

    public static boolean homeRequestActivity = false;

    private RecyclerView recyc;
    private HomeRequestAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    DBHandlerHomeRequests database;

    private BroadcastReceiver mHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new Handler().post(new Runnable() {

                @Override
                public void run()
                {
                    Toast.makeText(HomeRequestsActivity.this,getString(R.string.Notepad_UpdatedNotif),Toast.LENGTH_SHORT).show();

                    //recreate();
                    mAdapter.homeRequests = database.getAllInformation();
                    mAdapter.notifyDataSetChanged();
                }
            });

            //Toast.makeText(NotepadActivity.this,"TEST",Toast.LENGTH_SHORT);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_accept);
        //setContentView(R.layout.activity_home_accept_recycler);

        database = new DBHandlerHomeRequests(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mHandler,new IntentFilter("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-NEWREQUEST"));

        recyc = (RecyclerView) findViewById(R.id.HomeAccept_recyc);
        recyc.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyc.setLayoutManager(mLayoutManager);

        mAdapter = new HomeRequestAdapter(database.getAllInformation(),this, HomeRequestsActivity.this);
        recyc.setAdapter(mAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        homeRequestActivity = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        homeRequestActivity = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandler);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        getParent().recreate();
    }
}
