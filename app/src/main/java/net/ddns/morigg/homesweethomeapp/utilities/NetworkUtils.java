package net.ddns.morigg.homesweethomeapp.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.toolbox.HttpResponse;

import net.ddns.morigg.homesweethomeapp.R;
import net.ddns.morigg.homesweethomeapp.activities.SignUp;
import net.ddns.morigg.homesweethomeapp.activities.TabloEkleme;
import net.ddns.morigg.homesweethomeapp.structures.HttpResponseStructure;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {
    static Context context;


    static String CONNECTION_URL = "";

    final static String GITHUB_BASE_URL =
            "https://api.github.com/search/repositories";

    final static String PARAM_QUERY = "q";

    /**
     * The sort field. One of stars, forks, or updated.
     * Default: results are sorted by best match if no field is specified.
     */
    final static String PARAM_SORT = "sort";
    final static String sortBy = "stars";

    public NetworkUtils(Context context)
    {
        this.context = context;
        CONNECTION_URL = context.getString(R.string.server_ip_adress) + ":" + context.getString(R.string.server_port);
    }

    public void setAction(String action)
    {
        CONNECTION_URL = context.getString(R.string.server_ip_adress) + ":" + context.getString(R.string.server_port) + action;
    }

    public String getConnectionUrl()
    {
        return CONNECTION_URL;
    }


    public static URL buildUrlForLogin(String... args) {

        Uri buildUri = Uri.parse(CONNECTION_URL).buildUpon()
                    .appendQueryParameter("username",args[0])
                    .appendQueryParameter("password",args[1])
                    .build();

        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    public static URL buildUrlForVerification(String args) {

        Uri buildUri = Uri.parse(CONNECTION_URL).buildUpon()
                .appendQueryParameter("authorization","Bearer " + args)
                .build();

        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(1000);
        urlConnection.setReadTimeout(1000);
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }


    public void ActionJSON_PureVersion(final String... args){
        Thread thread = new Thread(){
            @SuppressLint({"TestTestTest", "SetTextI18n"})
            @Override
            public void run() {
                try {
                    int responseCode;
                    NetworkUtils networkUtils = new NetworkUtils(context);

                    //networkUtils.setAction(getString(R.string.action_login));
                    URL url = new URL(networkUtils.getConnectionUrl());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //conn.setReadTimeout(Integer.parseInt(getString(R.string.HttpConnectionTimeOut)));
                    //conn.setConnectTimeout(Integer.parseInt(getString(R.string.HttpConnectionTimeOut)));
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("username", args[0]);
                    jsonParam.put("password", args[1]);

                    Log.i("JSON", jsonParam.toString());
                    Log.i("ServerURL", url.toString());

                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                    bw.write(String.valueOf(jsonParam));
                    bw.flush();
                    bw.close();

                    responseCode = conn.getResponseCode();

                    if(responseCode == 200)
                    {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line;
                        String response = "";

                        while ((line = br.readLine()) != null) {
                            response += line;
                        }
                        Log.i("RESPONSE_OK", response);

                        Log.i("InputStream", conn.getInputStream().toString());

                        Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                        Log.i("MSG" , conn.getResponseMessage());

                    }
                    if(responseCode/100 == 4)
                    {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        String line;
                        String response = "";
                        while ((line = br.readLine()) != null) {
                            response += line;
                        }

                        JSONObject errorJSON = new JSONObject(response);
                        Log.i("RESPONSE_ERROR_FULL", response);
                        //response = errorJSON.getString("Message");
                        Log.i("RESPONSE_ERROR", response);

                        String errorMessage = "";
                        errorJSON = new JSONObject(response);
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

                        Log.i("RESPONSE_ERROR", errorMessage);

                    }
                    conn.disconnect();
                }catch (SocketTimeoutException e)
                {
                    e.printStackTrace();

                }catch (ConnectTimeoutException e)
                {
                    e.printStackTrace();

                }catch (UnknownHostException e)
                {
                    e.printStackTrace();
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        thread.start();
    }





    public static void HttpPost(final String HttpAction, final String Token, final JSONObject jsonParam, final Handler handler)
    {
        Thread thread = new Thread(){
            @SuppressLint({"TestTestTest", "SetTextI18n"})
            @Override
            public void run() {

                try
                {
                    NetworkUtils networkUtils = new NetworkUtils(context);
                    networkUtils.setAction(HttpAction);
                    URL url = new URL(networkUtils.getConnectionUrl());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(Integer.parseInt(context.getString(R.string.HttpConnectionTimeOut)));
                    conn.setConnectTimeout(Integer.parseInt(context.getString(R.string.HttpConnectionTimeOut)));
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    if(Token != null)
                    { conn.setRequestProperty("Authorization", "Bearer "+ Token); }
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    Log.i("JSON", jsonParam.toString());
                    Log.i("ServerURL", url.toString());


                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                    bw.write(String.valueOf(jsonParam));
                    bw.flush();
                    bw.close();


                    int responseCode = conn.getResponseCode();

                    if(responseCode/100 == 2)
                    {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line;
                        String responseMessage = "";

                        while ((line = br.readLine()) != null) {
                            responseMessage += line;
                        }

                        Log.i("RESPONSE_CODE", String.valueOf(conn.getResponseCode()));
                        Log.i("RESPONSE_OK", responseMessage);

                        //Log.i("InputStream", conn.getInputStream().toString());
                        //Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                        //Log.i("MSG" , conn.getResponseMessage());

                        /**response[0] = new HttpResponseStructure(responseCode,responseMessage);
                        finished[0] = true;*/

                        Message msg = new Message();
                        Bundle mArgs = new Bundle();
                        mArgs.putInt("responseCode",responseCode);
                        mArgs.putString("responseMessage",responseMessage);
                        msg.setData(mArgs);
                        handler.sendMessage(msg);

                    }
                    else if ( responseCode/100 == 4)
                    {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        String line;
                        String responseMessage = "";
                        while ((line = br.readLine()) != null) {
                            responseMessage += line;
                        }

                        Log.i("RESPONSE_CODE", String.valueOf(conn.getResponseCode()));
                        Log.i("RESPONSE_ERROR_FULL", responseMessage);
                        //response = errorJSON.getString("Message");
                        //Log.i("RESPONSE_ERROR", response);
                        /**response[0] = new HttpResponseStructure(responseCode,responseMessage);
                        finished[0] = true;*/
                        Message msg = new Message();
                        Bundle mArgs = new Bundle();
                        mArgs.putInt("responseCode",responseCode);
                        mArgs.putString("responseMessage",responseMessage);
                        msg.setData(mArgs);
                        handler.sendMessage(msg);

                    }
                    else
                    {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                        String line;
                        String responseMessage = "";
                        while ((line = br.readLine()) != null) {
                            responseMessage += line;
                        }

                        Log.i("RESPONSE_CODE", String.valueOf(conn.getResponseCode()));
                        Log.i("RESPONSE_ERROR_FULL", responseMessage);
                        //response = errorJSON.getString("Message");
                        //Log.i("RESPONSE_ERROR", response);
                        /**response[0] = new HttpResponseStructure(responseCode,responseMessage);
                         finished[0] = true;*/
                        Message msg = new Message();
                        Bundle mArgs = new Bundle();
                        mArgs.putInt("responseCode",responseCode);
                        mArgs.putString("responseMessage",responseMessage);
                        msg.setData(mArgs);
                        handler.sendMessage(msg);
                    }
                    conn.disconnect();

                } catch (IOException e) {
                    e.printStackTrace();handler.sendMessage(new Message());
                }

            }
        };

        thread.start();
    }

    public static void HttpGet(final String Token, final String URL,final Handler handler)
    {
        Thread thread = new Thread()
        {
            public void run()
            {

                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, Integer.parseInt(context.getString(R.string.HttpConnectionTimeOut)));
                HttpClient httpClient = new DefaultHttpClient(httpParams);

                HttpGet httpGet = new HttpGet(URL);
                Log.i("HttpGet", URL);

                if(Token != null)
                { httpGet.addHeader("Authorization", "Bearer " + Token); }

                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                try {
                    String response = httpClient.execute(httpGet,responseHandler);
                    Log.i("GetResponse", response);

                    Message msg = new Message();
                    Bundle mArgs = new Bundle();
                    mArgs.putInt("responseCode",200);
                    mArgs.putString("responseMessage",response);
                    msg.setData(mArgs);
                    handler.sendMessage(msg);

                    //Log.i("GetResponse", response);
                } catch (IOException e) {
                    e.printStackTrace();handler.sendMessage(new Message());

                }

            }
        };
        thread.start();


    }





}