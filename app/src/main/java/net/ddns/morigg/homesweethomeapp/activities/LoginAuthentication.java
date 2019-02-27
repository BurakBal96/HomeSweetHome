package net.ddns.morigg.homesweethomeapp.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.structures.UserFullInformation;
import net.ddns.morigg.homesweethomeapp.structures.UserInformation;
import net.ddns.morigg.homesweethomeapp.utilities.NetworkUtils;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;

import static net.ddns.morigg.homesweethomeapp.activities.TabloEkleme._TOKEN;
import static net.ddns.morigg.homesweethomeapp.activities.TabloEkleme.mainUser;

public class LoginAuthentication extends AppCompatActivity {

    UserInformation mainUser;
    String _token;
    NetworkUtils networkUtils;
    Button mBtnVerific, mbtnSendEmail;
    TextView mTxtVerific, mTxtVerificError;
    int responseCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_authentication);

        mBtnVerific = (Button) findViewById(R.id.login_btnVerification);
        mbtnSendEmail = (Button) findViewById(R.id.login_btnSendEmail);
        mTxtVerific = (TextView) findViewById(R.id.login_txtVerification);
        mTxtVerificError = (TextView) findViewById(R.id.login_txtVerificationError);
        networkUtils = new NetworkUtils(this);

        mainUser = (UserFullInformation) getIntent().getSerializableExtra("MainUser");


        _token = null; //getIntent().getStringExtra("TOKEN");

        VerificationActionJSON();

        mBtnVerific.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EmailVerify();
            }

            // Access the RequestQueue through your singleton class.
            //MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        });

        mbtnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VerificationActionJSON();
                mTxtVerificError.setText(getString(R.string.EmailGonderildiBildirimi));
            }

            // Access the RequestQueue through your singleton class.
            //MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        });
    }


    public void VerificationActionJSON(final String... args)
    {
        //TODO CHECK THIS METHOD
        networkUtils.setAction(getString(R.string.action_verification));
        String URL = networkUtils.getConnectionUrl()
                +"?userId="+ mainUser.USER_ID;
        NetworkUtils.HttpGet(null,URL,new Handler());
    }


    public void EmailVerify(final String... args){

        try
        {
            @SuppressLint("HandlerLeak") Handler handler = new Handler()
            {
                public void handleMessage(Message msg)
                {
                    super.handleMessage(msg);
                    if(msg.getData().getInt("responseCode") != 0)
                    {
                        try
                        {
                            Bundle mArgs = msg.getData();

                            String response = mArgs.getString("responseMessage");
                            int responseCode = mArgs.getInt("responseCode");

                            if(responseCode/100 == 2)
                            {
                                //Intent intent = new Intent(LoginAuthentication.this, MainActivity.class);
                                Intent intent = new Intent(LoginAuthentication.this, TabloEkleme.class);

                                intent.putExtra("TOKEN", response.substring(1,response.length()-1));
                                intent.putExtra("MainUser", mainUser);
                                startActivity(intent);
                                finish();
                            }
                            else if(responseCode/100 == 4)
                            {
                                JSONObject errorJSON = new JSONObject(response);

                                String errorMessage = "";
                                if(errorJSON.has("VerificationCode")){
                                    errorMessage += errorJSON.getString("VerificationCode");}
                                if(errorJSON.has("Verification Code")){
                                    errorMessage += errorJSON.getString("Verification Code");}

                                errorMessage = SignUp.responseFixer(errorMessage);
                                final String finalErrorMessage = errorMessage;
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        mTxtVerificError.setText(finalErrorMessage);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            };
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("verificationcode",mTxtVerific.getText().toString());
            jsonParam.put("UserId",mainUser.USER_ID);

            //networkUtils.setAction(getString(R.string.action_verification));
            //String URL = networkUtils.getConnectionUrl();
            NetworkUtils.HttpPost(getString(R.string.action_verify),_token,jsonParam,handler);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
