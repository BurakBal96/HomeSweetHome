package net.ddns.morigg.homesweethomeapp.dialog_fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.ddns.morigg.homesweethomeapp.R;

/**
 * Created by MoriartyGG on 5.05.2018.
 */

public class JoinHome extends DialogFragment {
    EditText mTxtName;
    TextView mTxtHint;

    public interface JoinHomeDialogListener
    {
        void JoinHome_onSaveButtonClick(DialogFragment dialog);
    }

    JoinHomeDialogListener joinHomeListener;
    Context context;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the SetPasswordDialogListener so we can send events to the host
            joinHomeListener = (JoinHome.JoinHomeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AddStudentDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_joinhome,null);

        mTxtName = (EditText) view.findViewById(R.id.JoinHome);
        mTxtHint = (TextView) view.findViewById(R.id.JoinHomeHint);

        Bundle mArgs = getArguments();

        String hint = mArgs.getString("hint");
        String action = mArgs.getString("action");

        mTxtHint.setText(hint);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        joinHomeListener.JoinHome_onSaveButtonClick(JoinHome.this);
                    }
                })
                .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        JoinHome.this.getDialog().cancel();
                    }
                });





        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout


        return builder.create();
    }


}


