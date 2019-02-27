package net.ddns.morigg.homesweethomeapp.dialog_fragments;



import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;


import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.activities.LoginActivity;
import net.ddns.morigg.homesweethomeapp.recycleradapter.MultiSpinnerSearch;
import net.ddns.morigg.homesweethomeapp.structures.KeyPairBoolData;
import net.ddns.morigg.homesweethomeapp.structures.UserInformation;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerFriends;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static net.ddns.morigg.homesweethomeapp.activities.TabloEkleme.mainUser;

/**
 * Created by MoriartyGG on 2.06.2018.
 */

@SuppressLint("ValidFragment")
public class DisbandHome extends DialogFragment {

    public interface DisbandHomeDialogListener {

        void disbandOnSaveButtonClick(DialogFragment dialog);
        //void onCancelButtonClick(DialogFragment dialog);
    }

    DisbandHomeDialogListener disbandHomeDialogListener;
    Context context;
    Activity activity;
    DBHandlerFriends database;
    List<UserInformation> friends;
    List<KeyPairBoolData> listArray0;
    public int newAdminId;
    Spinner spinner;

    //boolean isMainUserNeed;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the SetPasswordDialogListener so we can send events to the host
            disbandHomeDialogListener = (DisbandHomeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AddStudentDialogListener");
        }
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //type = 1;

        //isMainUserNeed = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_disband, null);





        {
            database = new DBHandlerFriends(context);
            friends = database.getAllInformation();
            listArray0 = new ArrayList<>();
            for (int x = 0; x < friends.size(); x++) {
                KeyPairBoolData h = new KeyPairBoolData();
                h.setId(friends.get(x).USER_ID);
                h.setName(friends.get(x).FIRST_NAME + " " + friends.get(x).LAST_NAME);
                h.setSelected(false);
                listArray0.add(h);
            }

            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(mainUser.USER_ID);
            h.setName(mainUser.FIRST_NAME + " " + mainUser.LAST_NAME);
            h.setSelected(false);
            listArray0.add(h);
        }

        database = new DBHandlerFriends(context);
        friends = database.getAllInformation();
        database.close();

        String[] friendUserNames = new String[friends.size()];

        for(int i = 0;i<friends.size();i++)
        {
            friendUserNames[i] = friends.get(i).USER_NAME;
        }

        spinner = (Spinner) view.findViewById(R.id.disband_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, friendUserNames);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinner.setAdapter(adapter);
        //spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //newAdminId = friends.get(spinner.getSelectedItemPosition()).USER_ID;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        builder.setView(view)
                .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setPositiveButton(getString(R.string.Navigation_ResignApply), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        disbandHomeDialogListener.disbandOnSaveButtonClick(DisbandHome.this);
                    }
                });


        return builder.create();
    }

    @SuppressLint("ValidFragment")
    public DisbandHome(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }


}
