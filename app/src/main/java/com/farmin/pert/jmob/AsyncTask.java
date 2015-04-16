package com.farmin.pert.jmob;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pert on 15-Apr-15.
 */
public class AsyncTask extends android.os.AsyncTask<String , Void, String> {

    static JSONArray jArray = null;
    static JSONObject jObj = null;
    static String json = "";
    InputStream inputStream = null;

    public AsyncTask() {

    }

    @Override
    protected void onPreExecute() {
        //super.onPreExecute();

    }

    @Override
    protected String doInBackground(String... params) {
        String operation = params[1];
        String serverURLfunctions = params[0];
        HttpResponse httpResponse = null;
        StringEntity documentStringified = null;
        DefaultHttpClient Client = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(serverURLfunctions);
        httppost.setHeader("Content-Type", "application/json");
        httppost.setHeader("Accept", "application/json");

//        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
//        nameValuePairs.add(new BasicNameValuePair("operation",operation));
        JSONObject obj = new JSONObject();
        try {
            obj.put("operation",operation);
             documentStringified = new StringEntity(obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {

            //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httppost.setEntity(documentStringified);
            httpResponse = Client.execute(httppost);
            //httpResponse = Client.execute(new HttpGet(serverURLfunctions));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            inputStream = httpResponse.getEntity().getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            inputStream.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        // try parse the string to a JSON object
//        try {
//            jArray = new JSONArray(json);
//            jObj = new JSONObject(json);
//
//        } catch (JSONException e) {
//            Log.e("JSON Parser", "Error parsing data " + e.toString());
//        }
        return json;
    }

    @Override
    protected void onPostExecute(String jsonObj) {


    }


}




