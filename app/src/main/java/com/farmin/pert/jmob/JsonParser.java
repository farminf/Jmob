package com.farmin.pert.jmob;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by pert on 14-Apr-15.
 */
public class JsonParser {
    static JSONArray jArray = null;
    static JSONObject jObj = null;
    static String json = "";
    InputStream inputStream = null;
    String result;

    public JsonParser() {
    }
    public JSONArray getJson(String url) throws IOException {
        DefaultHttpClient Client = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        httppost.setHeader("Content-Type", "application/json");
        httppost.setHeader("Accept", "application/json");

        HttpResponse httpResponse = Client.execute(new HttpGet(url));
        inputStream = httpResponse.getEntity().getContent();
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
        try {
            jArray = new JSONArray(json);
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        // return JSON String
        return jArray;


//        // convert inputstream to string
//        if(inputStream != null) {
//            result = convertInputStreamToString(inputStream);
//            Log.d("Tag", result);
//
//        }
//        else {
//            result = "Did not work!";
//        }
//    } catch (IOException e)
//
//    {
//        e.printStackTrace();
//        result = "";
//    }
//        return null;
    }

}

