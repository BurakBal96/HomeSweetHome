package net.ddns.morigg.homesweethomeapp.dialog_fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.TextAnnotation;

import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.activities.LoginActivity;
import net.ddns.morigg.homesweethomeapp.activities.MenuActivity;
import net.ddns.morigg.homesweethomeapp.activities.TabloEkleme;
import net.ddns.morigg.homesweethomeapp.recycleradapter.MultiSpinnerSearch;
import net.ddns.morigg.homesweethomeapp.structures.KeyPairBoolData;
import net.ddns.morigg.homesweethomeapp.structures.MealStructure;
import net.ddns.morigg.homesweethomeapp.structures.UserInformation;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerFriends;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerMeals;
import net.ddns.morigg.homesweethomeapp.utilities.PackageManagerUtils;
import net.ddns.morigg.homesweethomeapp.utilities.PermissionUtils;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;
import static com.google.common.reflect.Reflection.getPackageName;
import static net.ddns.morigg.homesweethomeapp.activities.ExpenseActivity.mExpenseProgress;
import static net.ddns.morigg.homesweethomeapp.activities.TabloEkleme.mainUser;

/**
 * Created by MoriartyGG on 3.06.2018.
 */


@SuppressLint("ValidFragment")
public class MenuAdd extends DialogFragment {

    public interface AddMenuDialogListener{

        void menuOnSaveButtonClick(DialogFragment dialog);
        //void onCancelButtonClick(DialogFragment dialog);
    }

    TextView mTxtTitle, mTxtDate;
    AddMenuDialogListener addMenuDialog;
    Context context;
    Activity activity;
    MultiSpinnerSearch searchMultiSpinnerUnlimited;

    //boolean isMainUserNeed;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the SetPasswordDialogListener so we can send events to the host
            addMenuDialog = (AddMenuDialogListener) activity;
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
        View view = inflater.inflate(R.layout.dialog_menu,null);



        mTxtTitle = (TextView) view.findViewById(R.id.menuadd_title);
        mTxtDate = (TextView) view.findViewById(R.id.menuadd_date);

        Bundle mArgs = getArguments();
        final String hint = mArgs.getString("hint");
        String date = mArgs.getString("date");
        mTxtTitle.setText(hint);
        mTxtDate.setText(mArgs.getString("formattedDate"));


        searchMultiSpinnerUnlimited = (MultiSpinnerSearch) view.findViewById(R.id.menuadd_meals);

        DBHandlerMeals database = new DBHandlerMeals(context);
        List<MealStructure> meals = database.getAllInformation();

        boolean bool = (Objects.equals(hint, getString(R.string.Menu_MenuEdit)));
        List<KeyPairBoolData> listArray0 = new ArrayList<>();

        List<Integer> participants = new ArrayList<>();
        if(bool)
        {
            String[] participantsString = (mArgs.getString("mealids")).split(",");

            int i = 0;
            for(String item : participantsString)
            {
                participants.add(Integer.valueOf(item));
            }
        }


        for(int x = 0;x<meals.size();x++)
        {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(meals.get(x).ID);
            h.setName(meals.get(x).NAME);
            h.setSelected(false);

            if(bool)
            {
                int mealID = meals.get(x).ID;
                for(int j = 0; j < participants.size();j++)
                {
                    if(mealID == participants.get(j))
                        h.setSelected(true);
                }
            }
            listArray0.add(h);
        }

        searchMultiSpinnerUnlimited.setItems(listArray0, -1, new MultiSpinnerSearch.SpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        //Log.i("Selected", i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                    }
                }
            }
        },getString(R.string.Menu_SelectItems));


        // TODO MenuAdd hint
        //if(Objects.equals(hint, getString(R.string.Expense_AddHint)))
        if(Objects.equals(hint, getString(R.string.Menu_MenuAdd)))
        {
            builder.setView(view)
                    .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    })
                    .setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            addMenuDialog.menuOnSaveButtonClick(MenuAdd.this);
                        }
                    });
        }
        else if(hint == getString(R.string.Menu_MenuEdit))
        {
            builder.setView(view)
                    .setNeutralButton("İptal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .setNegativeButton("Sil", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            MenuActivity.delete = true;
                            addMenuDialog.menuOnSaveButtonClick(MenuAdd.this);

                        }
                    })
                    .setPositiveButton("Düzenle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            MenuActivity.delete = false;
                            addMenuDialog.menuOnSaveButtonClick(MenuAdd.this);
                        }
                    });
        }


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout


        return builder.create();
    }

    @SuppressLint("ValidFragment")
    public MenuAdd(Context context,Activity activity)
    {
        this.context = context;
        this.activity = activity;
    }

    public static String toISO8601UTC(Date date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        return df.format(date);
    }
}
