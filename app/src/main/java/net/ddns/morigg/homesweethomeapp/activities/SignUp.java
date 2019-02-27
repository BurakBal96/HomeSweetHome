package net.ddns.morigg.homesweethomeapp.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.utilities.NetworkUtils;
import net.ddns.morigg.homesweethomeapp.utilities.Sha512;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class SignUp extends AppCompatActivity {

    TextView mTxtName, mTxtSurname, mTxtUser, mTxtPass,mTxtPass2, mTxtEmail,mTxtEmail2, mTxtHttpResult;
    EditText mTxtPhone;
    Button mBtnRegister;
    NetworkUtils networkUtils;
    int responseCode;
    ProgressBar mProgress;
    ProgressDialog progressDialog;

    CheckBox mCheckBox;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle(getResources().getText(R.string.SignUpTitle));
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);


        mBtnRegister = (Button) findViewById(R.id.register_btnKaydol);

        mTxtHttpResult = (TextView) findViewById(R.id.register_resultHttpRequest);
        mTxtName = (TextView) findViewById(R.id.register_firstName);
        mTxtSurname = (TextView) findViewById(R.id.register_lastName);
        mTxtUser = (TextView) findViewById(R.id.register_txtUser);
        mTxtPass = (TextView) findViewById(R.id.register_pass);
        mTxtPass2 = (TextView) findViewById(R.id.register_pass2);
        mTxtEmail = (TextView) findViewById(R.id.register_mail);
        mTxtEmail2 = (TextView) findViewById(R.id.register_mail2);
        mTxtPhone = (EditText) findViewById(R.id.register_phoneNumber);
        mCheckBox = (CheckBox) findViewById(R.id.signup_check);

        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SignUp.this)
                        .setTitle("Kullanıcı Sözleşmesi")
                        .setMessage(getString(R.string.agreement))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.Navigation_CreateSuccessfulOkey), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create().show();
            }
        });



        mProgress = (ProgressBar) findViewById(R.id.register_progress);
        mProgress.setVisibility(View.GONE);

        mTxtHttpResult.setMovementMethod(new ScrollingMovementMethod());

        mTxtPhone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);

        mTxtPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        networkUtils = new NetworkUtils(this);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onClick(View view) {


                String encPass;
                /* (done) TODO SignUp Pass Encryption */
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


                boolean errorNotExist = ErrorHandler();
                //(done) TODO SignUp Checker
                if(errorNotExist)
                {
                    progressDialog = new ProgressDialog(SignUp.this,
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage(getString(R.string.RegisterProgressContent));
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    final JSONObject jsonParam = new JSONObject();
                    try {
                        jsonParam.put("username", mTxtUser.getText().toString());
                        //jsonParam.put("password", mTxtPass.getText().toString());
                        jsonParam.put("password", encPass);
                        jsonParam.put("firstname", mTxtName.getText().toString());
                        jsonParam.put("lastname", mTxtSurname.getText().toString());
                        jsonParam.put("email", mTxtEmail.getText().toString());
                        jsonParam.put("phoneNumber", mTxtPhone.getText().toString());

                        handler = new Handler()
                        {
                            public void handleMessage(Message msg)
                            {
                                //Log.i("HTTPPOST_MSG","ARRIVED");
                                if(msg.getData().getInt("responseCode") != 0)
                                {
                                    try
                                    {

                                        Bundle mArgs = msg.getData();
                                        final String response = mArgs.getString("responseMessage");
                                        int responseCode = mArgs.getInt("responseCode");

                                        if(responseCode/100 == 4)
                                        {
                                            JSONObject errorJSON = new JSONObject(response);
                                            String errorMessage = "";

                                            //(Done) TODO SignUp Http Error to EditText
                                            if(errorJSON.has("FirstName")){
                                                errorMessage += errorJSON.getString("FirstName"); }
                                            if(errorJSON.has("LastName")){
                                                errorMessage += errorJSON.getString("LastName");}
                                            if(errorJSON.has("Email")){
                                                errorMessage += errorJSON.getString("Email");}
                                            if(errorJSON.has("Username")){
                                                errorMessage += errorJSON.getString("Username");}
                                            if(errorJSON.has("PhoneNumber")){
                                                errorMessage += errorJSON.getString("PhoneNumber");}
                                            if(errorJSON.has("Password")){
                                                errorMessage += errorJSON.getString("Password");}
                                            errorMessage = SignUp.responseFixer(errorMessage);
                                            //mTxtHttpResult.setText(errorMessage);
                                            final String finalErrorMessage = errorMessage;
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    mTxtHttpResult.setText(finalErrorMessage);
                                                }
                                            });
                                        }



                                        if(responseCode/100 == 2)
                                        {
                                            new AlertDialog.Builder(SignUp.this)
                                                    .setTitle(getString(R.string.signup_ok))
                                                    .setMessage(getString(R.string.signup_ok_msg))
                                                    .setCancelable(false)
                                                    .setPositiveButton(getString(R.string.signup_ok_button), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            finish();
                                                        }
                                                    }).create().show();
                                        }

                                        if(responseCode/100 != 2 && responseCode/100 != 4)
                                        {
                                            progressDialog.dismiss();
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
                                else
                                {
                                    progressDialog.dismiss();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            mTxtHttpResult.setText(getString(R.string.error_server_not_respond));
                                        }
                                    });
                                }
                            }
                        };
                        NetworkUtils.HttpPost(getString(R.string.action_register),null,jsonParam,handler);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public static String responseFixer(String response)
    {
        response = response.replace("[","");
        response = response.replace("]","\n");
        response = response.replace("\"","");
        response = response.replace(",","\n");
        return response;
    }

    public boolean ErrorHandler()
    {
        boolean x = true;
        String phoneNumber = PhoneNumberUtils.formatNumber(mTxtPhone.getText().toString(), Locale.getDefault().toString());

        if(mTxtUser.getText().length() < 3 || mTxtUser.getText().length() > 20)
        {
            x = false;
            mTxtUser.setError(getString(R.string.SignUp_Error_Username));
        }
        if(mTxtPass.getText().length() < 6)
        {
            x = false;
            mTxtPass.setError(getString(R.string.SignUp_Error_Password));
        }


        if(mTxtEmail.getText().length() < 3)
        {
            x = false;
            mTxtEmail.setError(getString(R.string.SignUp_Error_CannotBeEmpty));
        }
        if(Objects.equals(mTxtName.getText().toString(), ""))
        {
            x = false;
            mTxtName.setError(getString(R.string.SignUp_Error_CannotBeEmpty));
        }
        if(Objects.equals(mTxtSurname.getText().toString(), ""))
        {
            x = false;
            mTxtSurname.setError(getString(R.string.SignUp_Error_CannotBeEmpty));
        }
        if(phoneNumber == null)
        {
            x = false;
            mTxtPhone.setError(getString(R.string.SignUp_Error_CannotBeEmpty));
        }
        else
        {
            phoneNumber = phoneNumber.replace(" ","");
            /*if(phoneNumber.length() != 13)
            {
                x = false;
                mTxtPhone.setError(getString(R.string.SignUo_Error_PhoneFormatError));
            }*/
        }

        if(!mCheckBox.isChecked())
        {
            x = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTxtHttpResult.setText(getString(R.string.agreementerror));
                }
            });
        }


        if(!Objects.equals(mTxtPass.getText().toString(), mTxtPass2.getText().toString()))
        {
            x = false;
            mTxtPass2.setError(getString(R.string.SignUp_Error_PasswordNotMatched));
        }
        if(!Objects.equals(mTxtEmail.getText().toString(), mTxtEmail2.getText().toString()))
        {
            x = false;
            mTxtEmail2.setError(getString(R.string.SignUp_Error_EmailNotMatched));
        }
        return x;
    }

}
