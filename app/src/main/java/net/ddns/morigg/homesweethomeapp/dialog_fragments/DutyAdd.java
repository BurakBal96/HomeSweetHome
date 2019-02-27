package net.ddns.morigg.homesweethomeapp.dialog_fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.structures.UserInformation;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerFriends;

import java.util.ArrayList;
import java.util.List;

import static net.ddns.morigg.homesweethomeapp.activities.TabloEkleme.mainUser;


@SuppressLint("ValidFragment")
public class DutyAdd extends DialogFragment {
    Spinner spinner;
    EditText mTxtAction;
    EditText mTxtDate;

    public interface DutyAddListener
    {
        void DutyAdd_onSaveButtonClick(DialogFragment dialog);
    }

    DutyAddListener dutyAddListener;
    Context context;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the SetPasswordDialogListener so we can send events to the host
            dutyAddListener = (DutyAdd.DutyAddListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AddStudentDialogListener");
        }
    }


    List<UserInformation> friends = new ArrayList<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_duty,null);

        mTxtAction = (EditText) view.findViewById(R.id.duty_dialog_action);
        mTxtDate = (EditText) view.findViewById(R.id.duty_dialog_date);
        spinner = (Spinner) view.findViewById(R.id.duty_dialog_spinner);


        DBHandlerFriends database = new DBHandlerFriends(context);
        friends = database.getAllInformation();


        String[] friendUserNames = new String[friends.size() +1];

        for(int i = 0;i<friends.size();i++)
        {
            friendUserNames[i] = friends.get(i).USER_NAME;
        }
        friendUserNames[friends.size()] = mainUser.USER_NAME;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, friendUserNames);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinner.setAdapter(adapter);
        /**Bundle mArgs = getArguments();

        String hint = mArgs.getString("hint");
        String action = mArgs.getString("action");

        mTxtHint.setText(hint);*/

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dutyAddListener.DutyAdd_onSaveButtonClick(DutyAdd.this);
                    }
                })
                .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DutyAdd.this.getDialog().cancel();
                    }
                });




        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout


        return builder.create();
    }

    @SuppressLint("ValidFragment")
    public DutyAdd(Context context)
    {
        this.context = context;
    }

}


