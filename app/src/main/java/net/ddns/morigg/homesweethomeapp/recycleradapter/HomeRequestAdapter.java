package net.ddns.morigg.homesweethomeapp.recycleradapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.StringRequest;

import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.activities.LoginActivity;
import net.ddns.morigg.homesweethomeapp.activities.TabloEkleme;
import net.ddns.morigg.homesweethomeapp.structures.HomeRequestStructure;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerHomeRequests;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerNotepad;
import net.ddns.morigg.homesweethomeapp.utilities.NetworkUtils;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static net.ddns.morigg.homesweethomeapp.activities.TabloEkleme._TOKEN;
import static net.ddns.morigg.homesweethomeapp.activities.TabloEkleme.mainUser;

/**
 * Created by MoriartyGG on 4.05.2018.
 */

public class HomeRequestAdapter extends RecyclerView.Adapter<HomeRequestAdapter.ViewHolder> {
    public List<HomeRequestStructure> homeRequests;

    Context context;
    Activity activity;

    public HomeRequestAdapter(List<HomeRequestStructure> homeRequests, Context context, Activity activity)
    {
        this.homeRequests = homeRequests;
        this.context = context;
        this.activity = activity;
    }

    public void addItem(int position, HomeRequestStructure item) {
        homeRequests.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        homeRequests.remove(position);
        notifyItemRemoved(position);
    }

    public int getItemCount() {
        return homeRequests.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder  {

        // each data item is just a string in this case

        public TextView mTxtNameSurname, mTxtUserName;
        public ImageView mImgAccept, mImgRefuse;
        //public TextView mTxtID;

        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;

            mTxtNameSurname = (TextView) v.findViewById(R.id.HomeAccept_recyc_NameSurname);
            mTxtUserName = (TextView) v.findViewById(R.id.HomeAccept_recyc_UserName);
            mImgAccept = (ImageView) v.findViewById(R.id.HomeAccept_recyc_imgaccept);
            mImgRefuse = (ImageView) v.findViewById(R.id.HomeAccept_recyc_imgrefuse);

            //mTxtID = (TextView) v.findViewById(R.id.not_id);
        }



    }

    public HomeRequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.activity_home_accept_recycler, parent, false);

        // set the view's size, margins, paddings and layout parameters
        HomeRequestAdapter.ViewHolder vh = new HomeRequestAdapter.ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final HomeRequestStructure tmpRequest = homeRequests.get(position);

        holder.mTxtNameSurname.setText(tmpRequest.NameSurname);
        holder.mTxtUserName.setText(tmpRequest.UserName);


        holder.mImgAccept.setOnClickListener(new View.OnClickListener()
        {
             @Override
             public void onClick(View view) {
                 if(mainUser.POSITION == 0)
                 {

                     NetworkUtils networkUtils= new NetworkUtils(context);
                     networkUtils.setAction(context.getString(R.string.action_invitehomeaccept));
                     String URL = networkUtils.getConnectionUrl()
                             +"?invitedHomeId=" + String.valueOf(homeRequests.get(holder.getAdapterPosition()).UserId)
                             +"&isAccepted=true";

                     NetworkUtils.HttpGet(_TOKEN,URL,new Handler());
                     clearAllDatabases();
                     remove(holder.getAdapterPosition());

                     Handler handler = new Handler(Looper.getMainLooper());
                     handler.post(new Runnable() {
                         @Override
                         public void run() {
                             new AlertDialog.Builder(context)
                                     .setTitle(context.getString(R.string.Navigation_JoinHomeAcceptTitle))
                                     .setMessage(context.getString(R.string.Navigation_JoinHomeAcceptContent))
                                     .setCancelable(false)
                                     .setPositiveButton(context.getString(R.string.Navigation_CreateSuccessfulOkey), new DialogInterface.OnClickListener() {
                                         @Override
                                         public void onClick(DialogInterface dialogInterface, int i) {



                                             Intent intent = new Intent(activity.getApplicationContext(), LoginActivity.class);
                                             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                             activity.startActivity(intent);


                                             /*
                                             Activity parent = activity.getParent();
                                             parent.finish();
                                             activity.finish();
                                             Intent intent = new Intent(context,LoginActivity.class);
                                             activity.startActivity(intent);*/

                                         }
                                     }).create().show();
                         }
                     });


                    //homeRequests.get(holder.getAdapterPosition()).UserId;

                 }
                 else if ( mainUser.POSITION == 2)
                 {
                     NetworkUtils networkUtils= new NetworkUtils(context);
                     networkUtils.setAction(context.getString(R.string.action_joinhomeaccept));
                     String URL = networkUtils.getConnectionUrl()
                             +"?requesterId=" + String.valueOf(homeRequests.get(holder.getAdapterPosition()).UserId)
                             +"&isAccepted=true";
                     NetworkUtils.HttpGet(_TOKEN,URL,new Handler());

                     DBHandlerHomeRequests database = new DBHandlerHomeRequests(context);
                     database.deleteRow(homeRequests.get(holder.getAdapterPosition()).UserId);
                     remove(holder.getAdapterPosition());
                 }

             }
        });

        holder.mImgRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBHandlerHomeRequests database = new DBHandlerHomeRequests(context);
                database.deleteRow(homeRequests.get(holder.getAdapterPosition()).UserId);
                remove(holder.getAdapterPosition());
            }
        });

        //holder.mTxtID.setText(String.valueOf(tmpNote.id));

    }

    public void requestAction(View v)
    {
        v.getId();
        int action = R.id.HomeAccept_recyc_imgaccept;
        Log.i("View :", String.valueOf(v.getId()));
        Log.i("action", String.valueOf(action));
    }


    public void clearAllDatabases()
    {
        DBHandlerHomeRequests dbHomeRequests = new DBHandlerHomeRequests(context);
        dbHomeRequests.deleteALL();

        DBHandlerNotepad dbNotpad = new DBHandlerNotepad(context);
        dbNotpad.deleteALL();
    }

}
