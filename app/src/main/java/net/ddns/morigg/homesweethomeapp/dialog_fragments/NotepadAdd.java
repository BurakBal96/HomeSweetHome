package net.ddns.morigg.homesweethomeapp.dialog_fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.ddns.morigg.homesweethomeapp.R;

import java.util.Objects;

/**
 * Created by MoriartyGG on 20.04.2018.
 */

public class NotepadAdd extends DialogFragment  {

    EditText mTxtContent, mTxtTitle ;
    TextView mTxtID,mTxtHint;

    public interface AddNoteDialogListener{

        void onSaveButtonClick(DialogFragment dialog);
        //void onCancelButtonClick(DialogFragment dialog);

    }

    AddNoteDialogListener addNoteListener;
    Context context;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the SetPasswordDialogListener so we can send events to the host
            addNoteListener = (AddNoteDialogListener) activity;
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

        View view = inflater.inflate(R.layout.dialog_notepad,null);

        mTxtID = (TextView) view.findViewById(R.id.notedialog_id);
        mTxtTitle = (EditText) view.findViewById(R.id.notedialog_baslik);
        mTxtContent = (EditText) view.findViewById(R.id.notedialog_icerik);
        mTxtHint = (TextView) view.findViewById(R.id.notepad_hint);


        Bundle mArgs = getArguments();
        String hint = mArgs.getString("hint");
        mTxtHint.setText(hint);

        if(Objects.equals(hint, getString(R.string.Notepad_Edit_Hint)))
        {
            String content = mArgs.getString("content");
            String title = mArgs.getString("title");
            String id = mArgs.getString("id");

            mTxtID.setText(String.valueOf(id));
            mTxtContent.setText(content);
            mTxtTitle.setText(title);

            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            addNoteListener.onSaveButtonClick(NotepadAdd.this);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            NotepadAdd.this.getDialog().cancel();
                        }
                    });
        }
        else if(Objects.equals(hint, getString(R.string.Notepad_Add_Hint)))
        {
            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            addNoteListener.onSaveButtonClick(NotepadAdd.this);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            NotepadAdd.this.getDialog().cancel();
                        }
                    });
        }
        else if (Objects.equals(hint, getString(R.string.Notepad_Delete_Hint)))
        {
            String content = mArgs.getString("content");
            String title = mArgs.getString("title");
            String id = mArgs.getString("id");

            mTxtID.setText(String.valueOf(id));
            mTxtContent.setText(content);
            mTxtTitle.setText(title);

            mTxtContent.setFocusable(false);
            mTxtTitle.setFocusable(false);
            mTxtContent.setFocusableInTouchMode(false);
            mTxtTitle.setFocusableInTouchMode(false);

            builder.setView(view)
                    // Add action buttons
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            addNoteListener.onSaveButtonClick(NotepadAdd.this);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            NotepadAdd.this.getDialog().cancel();
                        }
                    });
        }



        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout


        return builder.create();
    }

    @SuppressLint("ValidFragment")
    public NotepadAdd(Bundle mArgs)
    {

    }

    public NotepadAdd()
    {

    }

}
