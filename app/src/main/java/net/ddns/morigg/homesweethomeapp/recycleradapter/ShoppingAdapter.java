package net.ddns.morigg.homesweethomeapp.recycleradapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.activities.ShoppingActivity;
import net.ddns.morigg.homesweethomeapp.structures.ShoppingItems;
import net.ddns.morigg.homesweethomeapp.structures.Shopping_OneItem;
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

/**
 * Created by MoriartyGG on 16.05.2018.
 */

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ViewHolder> {

    public ShoppingItems items;
    Activity activity;
    Context context;
    int lastUncheckedIndex = 0;
    String ActivityName;



    public ShoppingAdapter(ShoppingItems items, Context context, Activity activity, String ActivityName)
    {
        this.items = items;
        this.activity = activity;
        this.context = context;
        this.ActivityName = ActivityName;
    }

    public void addItem(int position, Shopping_OneItem item)
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
        public CheckBox mCheckBox;


        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;

            mCheckBox = (CheckBox) v.findViewById(R.id.shopping_recyc_check);
            mCheckBox.setChecked(false);

            //mTxtID = (TextView) v.findViewById(R.id.not_id);
        }


    }

    public ShoppingItems returnItems()
    {
        return items;
    }

    public ShoppingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.activity_shopping_recycler, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        Shopping_OneItem tmpItem = items.get(position);


        holder.mCheckBox.setText(tmpItem.item);
        if(tmpItem.status == 1)
        {
            holder.mCheckBox.setChecked(true);
        }
        else
        {
            holder.mCheckBox.setChecked(false);
            if(position > lastUncheckedIndex )
            {
                lastUncheckedIndex = position;
            }
        }

        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Objects.equals(ActivityName, "ShoppingActivity"))
                {
                    if(holder.mCheckBox.isChecked())
                    {
                        String itemName = holder.mCheckBox.getText().toString();

                    /*Log.i("item",itemName);
                    Log.i("position :", String.valueOf(position));
                    Log.i("index :", String.valueOf(index));*/

                        items.remove( position);
                        items.add(new Shopping_OneItem(itemName,1) );

                        notifyItemRemoved(position);
                        notifyDataSetChanged();
                    }
                    else
                    {
                        items.setStatus(position,0);
                        String itemName = holder.mCheckBox.getText().toString();

                        items.remove(position);
                        items.add(0,new Shopping_OneItem(itemName,0));

                        notifyItemRemoved(position);
                        notifyDataSetChanged();
                    }

                    ShoppingActivity.isChanged = true;
                }
                if(Objects.equals(ActivityName, "MealAddActivity"))
                {
                    items.remove( position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();

                }

            }
        });

        holder.mCheckBox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Handle long click
                // Return true to indicate the click was handled
                Toast.makeText(context, "TEST", Toast.LENGTH_SHORT).show();

                /*public void onCreateContextMenu(final ContextMenu menu,
                final View v, final ContextMenu.ContextMenuInfo menuInfo) {
                    ;
                }*/


                return true;
            }
        });
    }

    public ShoppingItems getItems()
    {
        return items;
    }

   /* public void swapItems(int position1, int position2)
    {
        String string1 = items.stringList.get(position1);
        int status1 = items.statusList.get(position1);

        String string2 = items.stringList.get(position2);
        int status2 = items.statusList.get(position2);



    }*/
}
