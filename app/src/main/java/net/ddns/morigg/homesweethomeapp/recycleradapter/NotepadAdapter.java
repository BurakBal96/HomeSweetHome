package net.ddns.morigg.homesweethomeapp.recycleradapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.activities.LoginAuthentication;
import net.ddns.morigg.homesweethomeapp.activities.SignUp;
import net.ddns.morigg.homesweethomeapp.activities.TabloEkleme;
import net.ddns.morigg.homesweethomeapp.dialog_fragments.NotepadAdd;
import net.ddns.morigg.homesweethomeapp.structures.NotepadStructure;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerNotepad;
import net.ddns.morigg.homesweethomeapp.utilities.NetworkUtils;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static net.ddns.morigg.homesweethomeapp.activities.TabloEkleme._TOKEN;

/**
 * Created by MoriartyGG on 17.04.2018.
 */

public class NotepadAdapter extends RecyclerView.Adapter<NotepadAdapter.ViewHolder> implements NotepadAdd.AddNoteDialogListener {
    public List<NotepadStructure> notlar;
    Activity activity;
    Context context;

    public NotepadAdapter(List<NotepadStructure> notlar, Context context,Activity activity){
        Collections.reverse(notlar);
        this.notlar = notlar;
        this.context = context;
        this.activity = activity;
    }

    public void addItem(int position, NotepadStructure item) {
        notlar.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        notlar.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return notlar.size();
    }


    //Useless Function, no Effect
    @Override
    public void onSaveButtonClick(DialogFragment dialog) {
        //(Done) TODO --Create This Function in NotepadActivity.java-- Menu--Edit-Save
        /**DBHandlerNotepad database = new DBHandlerNotepad(context);
        final EditText mTxtTitle = (EditText) dialog.getDialog().findViewById(R.id.notedialog_baslik);
        final EditText mTxtContent = (EditText) dialog.getDialog().findViewById(R.id.notedialog_icerik);
        final TextView mTxtID = (TextView) dialog.getDialog().findViewById(R.id.notedialog_id);


        String title = mTxtTitle.getText().toString();
        final String content = mTxtContent.getText().toString();
        int id = Integer.parseInt(mTxtID.getText().toString());

        if(!Objects.equals(title, "") && !Objects.equals(content, ""))
        {
            database.editItem(id, title, content);
            Log.i("TESTOTESTO", String.valueOf(id));


            Thread thread = new Thread(){
                @SuppressLint("TestTestTest")
                @Override
                public void run() {
                try {
                    NetworkUtils networkUtils = new NetworkUtils(context);
                    networkUtils.setAction(context.getString(R.string.action_updatenote));
                    URL url = new URL(networkUtils.getConnectionUrl());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(Integer.parseInt(context.getString(R.string.HttpConnectionTimeOut)));
                    conn.setConnectTimeout(Integer.parseInt(context.getString(R.string.HttpConnectionTimeOut)));
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Authorization", "Bearer "+ _TOKEN);
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();

                    jsonParam.put("id",Integer.parseInt(mTxtID.getText().toString()));
                    jsonParam.put("title",mTxtTitle.getText().toString());
                    jsonParam.put("content",mTxtContent.getText().toString());


                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                    bw.write(String.valueOf(jsonParam));
                    bw.flush();
                    bw.close();


                    conn.disconnect();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                }
            };

            thread.start();
            //long id = database.addNewRow(new NotepadStructure(title,content));
            //mAdapter.addItem(0, new NotepadStructure(title,content,id) );
            //recyc.scrollToPosition(0);
        }*/
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case

        public CardView mCardNote;
        public TextView mTxtBaslik, mTxtIcerik, mBtnViewOption;
        //public TextView mTxtID;

        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;

            mCardNote = (CardView) v.findViewById(R.id.not_card);
            mTxtBaslik = (TextView) v.findViewById(R.id.not_baslik);
            mTxtIcerik = (TextView) v.findViewById(R.id.not_icerik);
            mBtnViewOption = (TextView) itemView.findViewById(R.id.textViewOptions);

            //mTxtID = (TextView) v.findViewById(R.id.not_id);
        }
    }

    public NotepadAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.activity_notepad_recycler, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final NotepadStructure tmpNote = notlar.get(position);

        holder.mTxtBaslik.setText(tmpNote.Baslik);
        holder.mTxtIcerik.setText(tmpNote.Not);

        //holder.mTxtID.setText(String.valueOf(tmpNote.id));

        holder.mCardNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Card "+position+" Clicked", Toast.LENGTH_SHORT).show();
            }
        });


        holder.mBtnViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            //creating a popup menu
            PopupMenu popup = new PopupMenu(context, holder.mBtnViewOption);
            //inflating menu from xml resource
            popup.inflate(R.menu.notepad_menu);
            //adding click listener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Bundle mArgs;
                    NotepadAdd dialog;
                switch (item.getItemId()) {
                    case R.id.menu1:
                        //(Done) TODO Menu--Edit
                        //handle menu1 click


                        mArgs = new Bundle();
                        mArgs.putString("content",notlar.get(holder.getAdapterPosition()).Not);
                        mArgs.putString("title",notlar.get(holder.getAdapterPosition()).Baslik);
                        mArgs.putString("id", String.valueOf(notlar.get(holder.getAdapterPosition()).id));
                        mArgs.putString("hint",context.getString(R.string.Notepad_Edit_Hint));

                        dialog = new NotepadAdd();
                        dialog.setArguments(mArgs);
                        dialog.setCancelable(false);


                        dialog.show(activity.getFragmentManager(), "Info");

                        break;
                    case R.id.menu2:
                        //(Done) TODO Menu--Delete
                        //handle menu2 click
                        mArgs = new Bundle();
                        mArgs.putString("content",notlar.get(holder.getAdapterPosition()).Not);
                        mArgs.putString("title",notlar.get(holder.getAdapterPosition()).Baslik);
                        mArgs.putString("id", String.valueOf(notlar.get(holder.getAdapterPosition()).id));
                        mArgs.putString("hint",context.getString(R.string.Notepad_Delete_Hint));

                        dialog = new NotepadAdd();
                        dialog.setArguments(mArgs);
                        dialog.setCancelable(false);


                        dialog.show(activity.getFragmentManager(), "Info");

                        /*new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.Notepad_Delete_Title))
                            .setMessage("-----"+notlar.get(holder.getAdapterPosition()).Baslik + "-----\n\n" + notlar.get(holder.getAdapterPosition()).Not)
                            .setPositiveButton(context.getString(R.string.Notepad_Delete_Okay), new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {

                                    DBHandlerNotepad database = new DBHandlerNotepad(context);
                                    NetworkUtils networkUtils = new NetworkUtils(context);
                                    networkUtils.setAction(context.getString(R.string.action_deletenote));
                                    String URL = networkUtils.getConnectionUrl()
                                            + "?noteid=" + (notlar.get(holder.getAdapterPosition()).id);

                                    NetworkUtils.HttpGet(_TOKEN,URL,new Handler());
                                    //database.deleteRow((int) notlar.get(holder.getAdapterPosition()).id);
                                    //remove(holder.getAdapterPosition());
                                }
                            })

                            .setNegativeButton(context.getString(R.string.Notepad_Delete_Cancel),new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //do Nothing
                                }
                            })
                            .create()
                            .show();*/

                        break;
                }
                return false;
                }
            });
            //displaying the popup
            popup.show();

            }
        });
    }
}