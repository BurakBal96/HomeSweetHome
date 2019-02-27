package net.ddns.morigg.homesweethomeapp.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.structures.HttpResponseStructure;
import net.ddns.morigg.homesweethomeapp.structures.UserInformation;
import net.ddns.morigg.homesweethomeapp.structures.UserFullInformation;
import net.ddns.morigg.homesweethomeapp.utilities.DBHandlerFriends;
import net.ddns.morigg.homesweethomeapp.utilities.NetworkUtils;
import net.ddns.morigg.homesweethomeapp.utilities.Sha512;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    NetworkUtils networkUtils;
    ProgressDialog progressDialog;

    ProgressBar mProgress;
    private EditText mTxtUser, mTxtPass;
    private TextView mTxtHttpResult;
    private Button mBtnLogin, mBtnForgot, mBtnSignUp;

    private Button mBtnMain;
    CheckBox mChkPass;

    SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(getResources().getText(R.string.LoginTitle));

        mBtnLogin = (Button) findViewById(R.id.btnLogin);
        mBtnForgot = (Button) findViewById(R.id.btnForgotPass);
        mBtnSignUp = (Button) findViewById(R.id.btnSignUp);


        mProgress = (ProgressBar) findViewById(R.id.login_progress);
        mProgress.setVisibility(View.GONE);
        mProgress.showContextMenu();


        mTxtUser = (EditText) findViewById(R.id.textUser); mTxtUser.setSelected(false);
        mTxtPass = (EditText) findViewById(R.id.textPass);
        mTxtHttpResult = (TextView) findViewById(R.id.login_resultHttpRequest);
        mTxtHttpResult.setMovementMethod(new ScrollingMovementMethod());

        networkUtils = new NetworkUtils(this);

        mChkPass = (CheckBox) findViewById(R.id.checkPass);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();
        checkSharedPreferences();

        /* (Done)TODO Login Button Function */
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            //Sifremi Hatirla
            if(mChkPass.isChecked()){
                mEditor.putString(getString(R.string.checkPass), "True");
                mEditor.commit();

                String name = mTxtUser.getText().toString();
                mEditor.putString(getString(R.string.textUser), name);
                mEditor.commit();

                String pass = mTxtPass.getText().toString();
                mEditor.putString(getString(R.string.textPass), pass);
                mEditor.commit();
            }
            else
            {
                mEditor.putString(getString(R.string.checkPass), "False");
                mEditor.commit();


                mEditor.putString(getString(R.string.textUser), "");
                mEditor.commit();


                mEditor.putString(getString(R.string.textPass), "");
                mEditor.commit();
            }


            /* (done) TODO Pass Encryption */
            String encPass;
            {
                byte[] md5input = mTxtPass.getText().toString().getBytes();
                BigInteger md5Data =null;
                try{
                    md5Data = new BigInteger(1, Sha512.encryptMD5(md5input));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                encPass = md5Data.toString(16);
            }
            //Log.i("PASS",mTxtPass.getText().toString());


            progressDialog = new ProgressDialog(LoginActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.LoginProgressContent));
            progressDialog.setCancelable(false);
            progressDialog.show();
            //LoginActionJSON(args);

            JSONObject jsonParam = new JSONObject();
            try {
                jsonParam.put("username", mTxtUser.getText().toString());
                //jsonParam.put("password", mTxtPass.getText().toString());
                jsonParam.put("password", encPass);
                jsonParam.put("deviceid", FirebaseInstanceId.getInstance().getToken());

                networkUtils.setAction(getString(R.string.action_login));
                @SuppressLint("HandlerLeak") Handler handler = new Handler()
                {
                    public void handleMessage(Message msg)
                    {
                        super.handleMessage(msg);
                        if (msg.getData().getInt("responseCode") != 0)
                        {
                            Bundle mArgs = msg.getData();
                            String response = mArgs.getString("responseMessage");
                            int responseCode = mArgs.getInt("responseCode");


                            ActionLogin(response,responseCode);
                            progressDialog.dismiss();
                        }
                        else
                        {
                            runOnUiThread(new Runnable() {
                            public void run() {
                            progressDialog.dismiss();
                            mTxtHttpResult.setText(getString(R.string.error_server_not_respond));
                        }
                    });
                    }

                    }
                };
                NetworkUtils.HttpPost(getString(R.string.action_login),null,jsonParam,handler);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            }
        });

        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignUp.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

            }
        });


        mBtnMain = (Button) findViewById(R.id.btnMain);
        mBtnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Intent intent = new Intent(LoginActivity.this, TabloEkleme.class);
                intent.putExtra("TOKEN","");

                intent.putExtra("MainUser", new UserFullInformation(0,2,"USER NAME","FIRST","LAST NAME","test@mail.com","+90","Home Sweet Home",0));
                intent.putExtra("FriendList",new ArrayList<UserInformation>());

                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemThatWasSelected = item.getItemId();
        if( menuItemThatWasSelected == R.id.menu_help)
        {
            Context context = LoginActivity.this;
            String message = "Help was clicked";
            Toast.makeText(this, message , Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    void checkSharedPreferences(){
        String checkBox = mPreferences.getString(getString(R.string.checkPass), "False");
        String name = mPreferences.getString(getString(R.string.textUser), "");
        String pass = mPreferences.getString(getString(R.string.textPass), "");

        mTxtUser.setText(name);
        mTxtPass.setText(pass);

        if(checkBox.equals("True")){
            mChkPass.setChecked(true);
        }else{
            mChkPass.setChecked(false);
        }
    }


    public void ActionLogin(final String response, int responseCode)
    {
        try {
            if (responseCode == 202) {
                                      /* TODO HTTP_OK -> MainIntent'e Gitme */
                //Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                Intent intent = new Intent(LoginActivity.this, LoginAuthentication.class);
                JSONObject responseJSON = new JSONObject(response);
                JSONObject userJSON = responseJSON.getJSONObject("user");
                UserInformation mainUser = new UserFullInformation(
                        userJSON.getInt("id"),
                        userJSON.getInt("position"),
                        userJSON.getString("username"),
                        userJSON.getString("firstName"),
                        userJSON.getString("lastName"),
                        responseJSON.getString("email"),
                        responseJSON.getString("phoneNumber"),
                        responseJSON.getString("homeName"),
                        0
                );

                intent.putExtra("MainUser",mainUser);

                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
            }

            if (responseCode == 200) {
                //(Done)TODO Friends Okuma //// ResponseCode 200 olmalı
                JSONObject responseJSON = new JSONObject(response);


                JSONObject userJSON = responseJSON.getJSONObject("user");

                UserInformation mainUser;
                List<UserInformation> friends = new ArrayList<UserInformation>();

                //UserInformation[] friends = null;
                {
                    mainUser = new UserFullInformation(
                            userJSON.getInt("id"),
                            userJSON.getInt("position"),
                            userJSON.getString("username"),
                            userJSON.getString("firstName"),
                            userJSON.getString("lastName"),
                            responseJSON.getString("email"),
                            responseJSON.getString("phoneNumber"),
                            responseJSON.getString("homeName"),
                            0
                    );
                    int numberOfFrinds = responseJSON.getInt("numberOfFriends");
                    if (numberOfFrinds != 0) {

                        DBHandlerFriends database = new DBHandlerFriends(this);
                        database.deleteALL();

                        JSONArray friendsJSON = responseJSON.getJSONArray("friends");

                        for (int i = 0; i < numberOfFrinds; i++) {

                            JSONObject tmpFriendXJSON = friendsJSON.getJSONObject(i);

                            UserInformation tmpFriend = new UserInformation(
                                    tmpFriendXJSON.getInt("id"),
                                    tmpFriendXJSON.getInt("position"),
                                    tmpFriendXJSON.getString("username"),
                                    tmpFriendXJSON.getString("firstName"),
                                    tmpFriendXJSON.getString("lastName"),
                                    tmpFriendXJSON.getDouble("debt")
                            );

                            database.addNewRow(tmpFriend);

                        }
                    }
                }

                Intent intent = new Intent(LoginActivity.this, TabloEkleme.class);

                intent.putExtra("TOKEN", responseJSON.getString("token"));
                intent.putExtra("MainUser", mainUser);
                //intent.putExtra("FriendList", (Serializable) friends);

                //Bundle tmpBundle = new Bundle();


                Log.i("Token", responseJSON.getString("token"));

                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
            }

            if (responseCode / 100 == 4) {
                JSONObject errorJSON = new JSONObject(response);

                String errorMessage = "";
                if (errorJSON.has("FirstName")) {
                    errorMessage += errorJSON.getString("FirstName");
                }
                if (errorJSON.has("LastName")) {
                    errorMessage += errorJSON.getString("LastName");
                }
                if (errorJSON.has("Email")) {
                    errorMessage += errorJSON.getString("Email");
                }
                if (errorJSON.has("Username")) {
                    errorMessage += errorJSON.getString("Username");
                }
                if (errorJSON.has("PhoneNumber")) {
                    errorMessage += errorJSON.getString("PhoneNumber");
                }
                if (errorJSON.has("Password")) {
                    errorMessage += errorJSON.getString("Password");
                }
                errorMessage = SignUp.responseFixer(errorMessage);


                final String finalErrorMessage = errorMessage;
                runOnUiThread(new Runnable() {
                    public void run() {
                        mTxtHttpResult.setText(finalErrorMessage);
                    }
                });
            }

            if(responseCode/100 != 2 && responseCode/100 != 4)
            {
                runOnUiThread(new Runnable() {
                    public void run() {
                        mTxtHttpResult.setText(response);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

