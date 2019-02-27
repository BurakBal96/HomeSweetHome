package net.ddns.morigg.homesweethomeapp.recycleradapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.activities.ExpenseActivity;
import net.ddns.morigg.homesweethomeapp.dialog_fragments.ExpenseAdd;
import net.ddns.morigg.homesweethomeapp.dialog_fragments.NotepadAdd;
import net.ddns.morigg.homesweethomeapp.structures.ExpenseStructure;
import net.ddns.morigg.homesweethomeapp.structures.Shopping_OneItem;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerExpense;
import net.ddns.morigg.homesweethomeapp.utilities.NetworkUtils;

import org.w3c.dom.Text;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static net.ddns.morigg.homesweethomeapp.activities.TabloEkleme._TOKEN;

/**
 * Created by MoriartyGG on 27.05.2018.
 */

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    public List<ExpenseStructure> items;
    Activity activity;
    Context context;
    TextView mTxtAuthor,mTxtTitle,mTxtType,mTxtCost;



    public ExpenseAdapter(List<ExpenseStructure> items, Context context, Activity activity)
    {
        Collections.reverse(items);
        this.items = items;
        this.activity = activity;
        this.context = context;
    }



    public int getItemCount()
    {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        //public CheckBox mCheckBox;
        TextView mBtnViewOption;



        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;

            mTxtAuthor = (TextView) v.findViewById(R.id.expense_recyc_author);
            mTxtTitle = (TextView) v.findViewById(R.id.expense_recyc_title);
            mTxtType = (TextView) v.findViewById(R.id.expense_recyc_type);
            mTxtCost = (TextView) v.findViewById(R.id.expense_recyc_cost);
            mBtnViewOption = (TextView) itemView.findViewById(R.id.expense_ViewOptions);
            //mCheckBox = (CheckBox) v.findViewById(R.id.shopping_recyc_check);
            //mCheckBox.setChecked(false);

        }


    }

    public List<ExpenseStructure>  returnItems()
    {
        return items;
    }

    public ExpenseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.activity_expense_recycler, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        //ExpenseStructure tmpItem = items.get(position);

        mTxtAuthor.setText(items.get(position).AUTHOR);
        mTxtCost.setText(String.valueOf(items.get(position).COST));
        mTxtTitle.setText(items.get(position).TITLE);
        mTxtType.setText(items.get(position).E_TYPE);


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
                        //NotepadAdd dialog;
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                // (Done) TODO Menu--Edit
                                //handle menu1 click

                                ExpenseStructure expenseItem = items.get(holder.getAdapterPosition());

                                mArgs = new Bundle();
                                mArgs.putString("hint",context.getString(R.string.Expense_EditHint));
                                mArgs.putString("date",expenseItem.LAST_UPDATED);
                                mArgs.putString("author",expenseItem.AUTHOR);
                                mArgs.putString("title",expenseItem.TITLE);
                                mArgs.putString("content",expenseItem.CONTENT);
                                mArgs.putString("e_type",expenseItem.E_TYPE);
                                mArgs.putString("id", String.valueOf(expenseItem.ID));
                                mArgs.putString("cost", String.valueOf(expenseItem.COST));
                                mArgs.putString("participants",expenseItem.PARTICIPANTS);
                                ExpenseAdd dialog = new ExpenseAdd(context,activity);
                                dialog.setArguments(mArgs);
                                dialog.setCancelable(false);
                                dialog.show(activity.getFragmentManager(), "Info");

                                break;
                            case R.id.menu2:
                                //TODO Menu--Delete
                                //handle menu2 click


                                new AlertDialog.Builder(context)
                                        .setTitle(context.getString(R.string.Expense_DeleteTitle))

                                        .setPositiveButton(context.getString(R.string.Notepad_Delete_Okay), new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i)
                                            {

                                                NetworkUtils networkUtils = new NetworkUtils(context);
                                                networkUtils.setAction(context.getString(R.string.action_deleteexpense));
                                                String URL = networkUtils.getConnectionUrl()
                                                        + "?expenseid=" + (items.get(holder.getAdapterPosition()).ID);

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



                                /**mArgs = new Bundle();
                                mArgs.putString("content",notlar.get(holder.getAdapterPosition()).Not);
                                mArgs.putString("title",notlar.get(holder.getAdapterPosition()).Baslik);
                                mArgs.putString("id", String.valueOf(notlar.get(holder.getAdapterPosition()).id));
                                mArgs.putString("hint",context.getString(R.string.Notepad_Delete_Hint));

                                dialog = new NotepadAdd();
                                dialog.setArguments(mArgs);
                                dialog.setCancelable(false);


                                dialog.show(activity.getFragmentManager(), "Info");*/

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
