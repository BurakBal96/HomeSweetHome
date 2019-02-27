package net.ddns.morigg.homesweethomeapp.activities;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.FaceAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.TextAnnotation;

import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.dialog_fragments.ExpenseAdd;
import net.ddns.morigg.homesweethomeapp.recycleradapter.ExpenseAdapter;
import net.ddns.morigg.homesweethomeapp.recycleradapter.MultiSpinnerSearch;
import net.ddns.morigg.homesweethomeapp.structures.ExpenseStructure;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerExpense;
import net.ddns.morigg.homesweethomeapp.utilities.NetworkUtils;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static net.ddns.morigg.homesweethomeapp.activities.TabloEkleme._TOKEN;

public class ExpenseActivity extends AppCompatActivity implements ExpenseAdd.AddExpenseDialogListener{

    public static ProgressBar mExpenseProgress;
    FloatingActionButton mFloatBtn;
    RecyclerView recyc;
    ExpenseAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    Button mVisionButton;

    private BroadcastReceiver mHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new Handler().post(new Runnable() {

                @Override
                public void run()
                {
                    Toast.makeText(ExpenseActivity.this,getString(R.string.Expense_UpdateNotif),Toast.LENGTH_SHORT).show();

                    //recreate();
                    DBHandlerExpense database = new DBHandlerExpense(ExpenseActivity.this);
                    mAdapter.items = database.getAllInformation();
                    mAdapter.notifyDataSetChanged();
                    database.close();

                }
            });

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);


        mFloatBtn = (FloatingActionButton) findViewById(R.id.expense_add);
        mFloatBtn.setAlpha(0.4f);

        LocalBroadcastManager.getInstance(this).registerReceiver(mHandler,new IntentFilter("net.ddns.morigg.homesweethomeapp_FCM-MESSAGE-EXPENSE"));

        mExpenseProgress =  (ProgressBar) findViewById(R.id.expense_progress);
        mFloatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle mArgs = new Bundle();
                mArgs.putString("hint",getString(R.string.Expense_AddHint));
                ExpenseAdd dialog = new ExpenseAdd(getBaseContext(),ExpenseActivity.this);
                dialog.setArguments(mArgs);
                dialog.setCancelable(false);
                dialog.show(getFragmentManager(), "Info");

            }
        });

        DBHandlerExpense database = new DBHandlerExpense(this);
        recyc= (RecyclerView) findViewById(R.id.expense_recyclerview);
        recyc.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyc.setLayoutManager(layoutManager);

        mAdapter = new ExpenseAdapter( database.getAllInformation(),this,ExpenseActivity.this);
        recyc.setAdapter(mAdapter);

        recyc.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyc, new ClickListener() {
            //TODO ExpenseRecyclerView Click Handle
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
                //Toast.makeText(ExpenseActivity.this, "Long press on position :"+position,Toast.LENGTH_SHORT).show();

                ExpenseStructure item = mAdapter.items.get(position);

                Bundle mArgs = new Bundle();
                mArgs.putString("hint",getString(R.string.Expense_EditHint));
                mArgs.putString("date",item.LAST_UPDATED);
                mArgs.putString("author",item.AUTHOR);
                mArgs.putString("title",item.TITLE);
                mArgs.putString("content",item.CONTENT);
                mArgs.putString("e_type",item.E_TYPE);
                mArgs.putString("id", String.valueOf(item.ID));
                mArgs.putString("cost", String.valueOf(item.COST));
                mArgs.putString("participants",item.PARTICIPANTS);
                ExpenseAdd dialog = new ExpenseAdd(getBaseContext(),ExpenseActivity.this);
                dialog.setArguments(mArgs);
                dialog.setCancelable(false);
                dialog.show(getFragmentManager(), "Info");


            }
        }));

    }



    public interface ClickListener{
        public void onClick(View view,int position);
        public void onLongClick(View view,int position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandler);
    }


    @Override
    public void onSaveButtonClick(DialogFragment dialog) {

        boolean error = false;
        final EditText mTxtTitle = (EditText) dialog.getDialog().findViewById(R.id.expensedialog_title);
        final EditText mTxtContent = (EditText) dialog.getDialog().findViewById(R.id.expensedialog_content);
        final EditText mTxtCost = (EditText) dialog.getDialog().findViewById(R.id.expensedialog_cost);
        final Spinner mSpinner = (Spinner) dialog.getDialog().findViewById(R.id.expensedialog_type);
        final MultiSpinnerSearch mMultiSpinner = (MultiSpinnerSearch) dialog.getDialog().findViewById(R.id.expense_userList);

        Double cost = 0.0;

        List<Long> participants = mMultiSpinner.getSelectedIds();

        if(participants.size() == 0)
        {
            error = true;
            mTxtCost.setError(getString(R.string.Expense_CostError));
            //TODO Handle Error
        }

        try
        {
            cost = Double.parseDouble(mTxtCost.getText().toString());
        } catch (NumberFormatException e)
        {
            error = true;
            mTxtCost.setError(getString(R.string.Expense_CostError));
            Toast.makeText(ExpenseActivity.this,getString(R.string.Expense_CostError),Toast.LENGTH_SHORT).show();
        }

        if(Objects.equals(mTxtTitle.getText().toString(), ""))
        {
            error = true;
            mTxtTitle.setError(getString(R.string.Expense_CostError));
            Toast.makeText(ExpenseActivity.this,getString(R.string.Expense_CostError),Toast.LENGTH_SHORT).show();
        }

        // TODO ERROR HANDLE

        Log.i("Error", String.valueOf(error));
        Log.i("ParticipantSize", String.valueOf(participants.size()));
        if(!error)
        {
            Bundle mArgs = dialog.getArguments();

            if(Objects.equals(mArgs.getString("hint"), getString(R.string.Expense_AddHint)))
            {
                try
                {
                    int type  = mSpinner.getSelectedItemPosition() + 1 ;

                    JSONObject jsonParam = new JSONObject();
                    JSONObject expense = new JSONObject();

                    JSONArray jsonArray = new JSONArray(participants.toArray());
                    expense.put("EType",type);
                    expense.put("Cost",cost);
                    expense.put("Title",mTxtTitle.getText().toString());
                    expense.put("Content",mTxtContent.getText().toString());

                    jsonParam.put("expense",expense);
                    jsonParam.put("participants",jsonArray);

                    Log.i("Json",jsonParam.toString());

                    NetworkUtils.HttpPost(getString(R.string.action_addexpense),_TOKEN,jsonParam,new Handler());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if(Objects.equals(mArgs.getString("hint"), getString(R.string.Expense_EditHint)))
            {
                //TODO Edit Expense
                for(Long i : participants)
                {
                    Log.i("participants",String.valueOf(i));
                }

                try
                {
                    int type  = mSpinner.getSelectedItemPosition() + 1 ;

                    JSONObject jsonParam = new JSONObject();
                    JSONObject expense = new JSONObject();

                    JSONArray jsonArray = new JSONArray(participants.toArray());
                    expense.put("EType",type);
                    expense.put("Cost",cost);
                    expense.put("Title",mTxtTitle.getText().toString());
                    expense.put("Content",mTxtContent.getText().toString());
                    expense.put("Id",mArgs.getString("id"));

                    jsonParam.put("expense",expense);
                    jsonParam.put("participants",jsonArray);

                    NetworkUtils.HttpPost(getString(R.string.action_updateexpense),_TOKEN,jsonParam,new Handler());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }





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




    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

}
