package net.ddns.morigg.homesweethomeapp.activities;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.dialog_fragments.NotepadAdd;
import net.ddns.morigg.homesweethomeapp.recycleradapter.NotepadAdapter;
import net.ddns.morigg.homesweethomeapp.structures.NotepadStructure;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerNotepad;
import net.ddns.morigg.homesweethomeapp.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static net.ddns.morigg.homesweethomeapp.activities.TabloEkleme._TOKEN;

public class NotepadActivity extends AppCompatActivity implements NotepadAdd.AddNoteDialogListener {

    private RecyclerView recyc;
    private NotepadAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    DBHandlerNotepad database;
    FloatingActionButton mFloatBtn;

    private List<NotepadStructure> arrayListData = new ArrayList<NotepadStructure>();

    private BroadcastReceiver mHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new Handler().post(new Runnable() {

                @Override
                public void run()
                {
                    Toast.makeText(NotepadActivity.this,getString(R.string.Notepad_UpdatedNotif),Toast.LENGTH_SHORT).show();

                    //recreate();
                    List<NotepadStructure> list = database.getAllInformation();
                    Collections.reverse(list);
                    mAdapter.notlar = list;
                    mAdapter.notifyDataSetChanged();
                }
            });

            //Toast.makeText(NotepadActivity.this,"TEST",Toast.LENGTH_SHORT);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad);

        LocalBroadcastManager.getInstance(this).registerReceiver(mHandler,new IntentFilter("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-NOTEPAD"));
        Resources res = getResources();

        database = new DBHandlerNotepad(this);

        recyc = (RecyclerView) findViewById(R.id.not_recyclerview);
        recyc.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyc.setLayoutManager(layoutManager);

        //mAdapter= new NotepadAdapter(arrayListData,this);
        mAdapter = new NotepadAdapter( database.getAllInformation(),this,NotepadActivity.this);
        recyc.setAdapter(mAdapter);



        mFloatBtn = (FloatingActionButton) findViewById(R.id.not_ekleme);
        mFloatBtn.setAlpha(0.4f);

        mFloatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(NotepadActivity.this, "Action Button Clicked", Toast.LENGTH_SHORT).show();
                Bundle mArgs = new Bundle();
                mArgs.putString("hint",getString(R.string.Notepad_Add_Hint));
                NotepadAdd dialog = new NotepadAdd();
                dialog.setArguments(mArgs);
                dialog.setCancelable(false);
                dialog.show(getFragmentManager(), "Info");

            }
        });


        recyc.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyc, new ClickListener() {
            //(DoNothing)TODO NotepadRecyclerView Click Handle
            @Override
            public void onClick(View view, final int position) {
                //Values are passing to activity & to fragment as well
                //Toast.makeText(NotepadActivity.this, "Single Click on position :"+position, Toast.LENGTH_SHORT).show();
                //ImageView picture=(ImageView)view.findViewById(R.id.picture);

                /*picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(NotepadActivity.this, "Single Click on Image :"+position,
                                Toast.LENGTH_SHORT).show();
                    }
                });*/
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(NotepadActivity.this, "Long press on position :"+position,Toast.LENGTH_SHORT).show();
            }
        }));
    }

    @Override
    public void onSaveButtonClick(DialogFragment dialog) {
        final EditText mTxtTitle = (EditText) dialog.getDialog().findViewById(R.id.notedialog_baslik);
        final EditText mTxtContent = (EditText) dialog.getDialog().findViewById(R.id.notedialog_icerik);
        final TextView mTxtID = (TextView) dialog.getDialog().findViewById(R.id.notedialog_id);

        String title = mTxtTitle.getText().toString();
        String content = mTxtContent.getText().toString();
        String oldID_String = mTxtID.getText().toString();
        int oldID = -1;
        if(!Objects.equals(oldID_String, ""))
        {
            oldID = Integer.parseInt(mTxtID.getText().toString());
        }



        if(oldID== -1)
        {
            if(!Objects.equals(title, "") && !Objects.equals(content, ""))
            {

                final NotepadStructure tmpNotepad = new NotepadStructure(title,content);
                /*long id = database.addNewRow(new NotepadStructure(title,content));
                mAdapter.addItem(0, new NotepadStructure(title,content,id) );
                recyc.scrollToPosition(0);*/

                try
                {
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("title", tmpNotepad.Baslik);
                    jsonParam.put("content", tmpNotepad.Not);

                    Handler handler = new Handler();
                    NetworkUtils.HttpPost(getString(R.string.action_addnote),_TOKEN,jsonParam,handler);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            try
            {
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("id",Integer.parseInt(mTxtID.getText().toString()));
                jsonParam.put("title",mTxtTitle.getText().toString());
                jsonParam.put("content",mTxtContent.getText().toString());

                Handler handler = new Handler();
                NetworkUtils.HttpPost(getString(R.string.action_updatenote),_TOKEN,jsonParam,handler);

                runOnUiThread(new Runnable() {
                    public void run() {
                        new Handler().post(new Runnable() {

                            @Override
                            public void run()
                            {
                                recreate();
                            }
                        });
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Bundle mArgs = dialog.getArguments();
        String hint = mArgs.getString("hint");

        if(hint == getString(R.string.Notepad_Delete_Hint))
        {

            DBHandlerNotepad database = new DBHandlerNotepad(this);
            NetworkUtils networkUtils = new NetworkUtils(this);
            networkUtils.setAction(getString(R.string.action_deletenote));
            String URL = networkUtils.getConnectionUrl()
                    + "?noteid=" + mArgs.getString("id");

            NetworkUtils.HttpGet(_TOKEN,URL,new Handler());
            //database.deleteRow((int) notlar.get(holder.getAdapterPosition()).id);
            //remove(holder.getAdapterPosition());
        }


    }




    @Override
    protected void onPause() {
        super.onPause();

        //We cannot unregister this BroadcastManager
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandler);
    }

    public interface ClickListener{
        public void onClick(View view,int position);
        public void onLongClick(View view,int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){

            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
