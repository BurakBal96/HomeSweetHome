package net.ddns.morigg.homesweethomeapp.recycleradapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.activities.ShoppingActivity;
import net.ddns.morigg.homesweethomeapp.dialog_fragments.DutyAdd;
import net.ddns.morigg.homesweethomeapp.dialog_fragments.NotepadAdd;
import net.ddns.morigg.homesweethomeapp.structures.DutyStructure;
import net.ddns.morigg.homesweethomeapp.structures.ShoppingItems;
import net.ddns.morigg.homesweethomeapp.structures.Shopping_OneItem;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerDuty;
import net.ddns.morigg.homesweethomeapp.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static net.ddns.morigg.homesweethomeapp.activities.TabloEkleme._TOKEN;
import static net.ddns.morigg.homesweethomeapp.activities.TabloEkleme.mainUser;

public class DutyAdapter extends RecyclerView.Adapter<DutyAdapter.ViewHolder> {

    public List<DutyStructure> items;
    Activity activity;
    Context context;



    public DutyAdapter(List<DutyStructure> items, Context context, Activity activity)
    {
        this.items = items;
        this.activity = activity;
        this.context = context;
    }

    public void addItem(int position, DutyStructure item)
    {
        items.add(position,item);
        notifyItemInserted(position);
    }

    public void remove(int HolderPosition,int ListIndex)
    {
        if(ListIndex != -1)
            items.remove(ListIndex);
        notifyItemRemoved(HolderPosition);
    }



    public int getItemCount()
    {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mTxtAction, mTxtDate, mTxtAuthor, mBtnViewOption;


        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;

            mTxtAction = (TextView) v.findViewById(R.id.duty_recyc_action);
            mTxtDate = (TextView) v.findViewById(R.id.duty_recyc_date);
            mTxtAuthor = (TextView) v.findViewById(R.id.duty_recyc_author);
            mBtnViewOption = (TextView) itemView.findViewById(R.id.duty_ViewOptions);

        }


    }


    public DutyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.activity_duty_recycler, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        DutyStructure tmpItem = items.get(position);

        holder.mTxtAction.setText(tmpItem.action);
        holder.mTxtDate.setText(String.valueOf(tmpItem.date));
        holder.mTxtAuthor.setText(tmpItem.user);

        if(mainUser.POSITION == 2)
        {
            holder.mBtnViewOption.setVisibility(View.VISIBLE);

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
                            DutyAdd dialog;
                            switch (item.getItemId()) {
                                case R.id.menu1:
                                    //(Done) TODO Menu--Edit
                                    //handle menu1 click


                                    /**
                                    mArgs = new Bundle();
                                     mArgs.putString("content",notlar.get(holder.getAdapterPosition()).Not);
                                     mArgs.putString("title",notlar.get(holder.getAdapterPosition()).Baslik);
                                     mArgs.putString("id", String.valueOf(notlar.get(holder.getAdapterPosition()).id));
                                     mArgs.putString("hint",context.getString(R.string.Notepad_Edit_Hint));

                                     dialog = new NotepadAdd();
                                     dialog.setArguments(mArgs);
                                     dialog.setCancelable(false);


                                     dialog.show(activity.getFragmentManager(), "Info");
                                     */

                                    break;
                                case R.id.menu2:
                                    //(Done) TODO Menu--Delete
                                    //handle menu2 click
                                    /** mArgs = new Bundle();
                                     mArgs.putString("content",notlar.get(holder.getAdapterPosition()).Not);
                                     mArgs.putString("title",notlar.get(holder.getAdapterPosition()).Baslik);
                                     mArgs.putString("id", String.valueOf(notlar.get(holder.getAdapterPosition()).id));
                                     mArgs.putString("hint",context.getString(R.string.Notepad_Delete_Hint));

                                     dialog = new NotepadAdd();
                                     dialog.setArguments(mArgs);
                                     dialog.setCancelable(false);


                                     dialog.show(activity.getFragmentManager(), "Info");*/

                                    new AlertDialog.Builder(context)
                                        .setTitle(items.get(holder.getAdapterPosition()).date + ". gündeki "
                                        + "\"" + items.get(holder.getAdapterPosition()).action + "\"" + " işi silinsin mi?"
                                        )
                                        .setPositiveButton(context.getString(R.string.signup_ok_button), new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i)
                                            {

                                                //DBHandlerDuty database = new DBHandlerDuty(context);
                                                NetworkUtils networkUtils = new NetworkUtils(context);
                                                networkUtils.setAction(context.getString(R.string.action_deleteduty));
                                                String URL = networkUtils.getConnectionUrl()
                                                        + "?houseworkId=" + (items.get(holder.getAdapterPosition()).id);

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
                                        .show();

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

}
