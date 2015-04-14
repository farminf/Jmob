package com.farmin.pert.jmob;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pert on 14-Apr-15.
 */
public class DeviceActivity extends Activity {
    public String EXTRA_MESSAGE = null;
    private String serverURLfunctions = null;
    ListView lvFunctions;
    TextView tvFunction;
    TextView tvDeviceName;
    TextView TvFunctionOperations;
    private final static String jsonDeviceFunction = "dal.function.UID";
    private final static String jsonDeviceFunctionOperations = "dal.function.operation.names";
    ArrayList<HashMap<String, String>> functionList = new ArrayList<HashMap<String, String>>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_view);
        Bundle bundle = getIntent().getExtras();
        EXTRA_MESSAGE = bundle.getString("deviceUID");
        serverURLfunctions = "http://130.192.86.173/api/devices/" + EXTRA_MESSAGE + "/functions";
        tvDeviceName = (TextView) findViewById(R.id.tvDeviceName);
        tvDeviceName.setText(EXTRA_MESSAGE);
        tvFunction = (TextView) findViewById(R.id.tvFunction);
        TvFunctionOperations = (TextView) findViewById(R.id.tvFunctionOperations);
        functionList = new ArrayList<HashMap<String, String>>();


        new jsonParse().execute(serverURLfunctions);
    }
//************************************ Async***********************************
    private class jsonParse extends AsyncTask<String, Void, JSONArray> {
        private ProgressDialog Dialog = new ProgressDialog(DeviceActivity.this);

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            Dialog.setMessage("Please wait..");
            Dialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            JsonParser jparse = new JsonParser();
            JSONArray jArray = null;
            try {
                jArray = jparse.getJson(serverURLfunctions);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jArray;
        }

        @Override
        protected void onPostExecute(JSONArray jArray) {
            Dialog.dismiss();
            try {
                //devicesArray = jArray.getJSONObject();
                JSONArray functionsArray = jArray;
                for(int i = 0; i < functionsArray.length(); i++){
                    JSONObject c = functionsArray.getJSONObject(i);
                    // Storing  JSON item in a Variable
                   // JSONObject jobjtest = c.optJSONObject(jsonDeviceFunction);

                    String operations = c.getString(jsonDeviceFunctionOperations);
                    String function = c.getString(jsonDeviceFunction);
                    // Adding value HashMap key => value
                    HashMap<String, String> map = new HashMap<String, String>();
                    //map.put(jsonDeviceStatus, status);

                    map.put(jsonDeviceFunction, function);
                    map.put(jsonDeviceFunctionOperations, operations);

                    functionList.add(map);
                    lvFunctions =(ListView)findViewById(R.id.lvFunctions);
                    ListAdapter adapter = new SimpleAdapter(DeviceActivity.this, functionList,
                            R.layout.list_view2,
                            new String[] { jsonDeviceFunction , jsonDeviceFunctionOperations }, new int[] {
                            R.id.tvFunction,R.id.tvFunctionOperations});
                    lvFunctions.setAdapter(adapter);
                    lvFunctions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            String deviceFunctionUID = functionList.get(+position).get(jsonDeviceFunction);
                            String deviceFunctionOperation = functionList.get(position).get(jsonDeviceFunctionOperations);
                            Toast.makeText(DeviceActivity.this, "You Clicked at " + deviceFunctionUID + deviceFunctionOperation , Toast.LENGTH_SHORT).show();
                            onCreateDialog();
//                            Intent intent = new Intent(getApplicationContext(), DeviceActivity.class);
//                            intent.putExtra("deviceUID", deviceUID);
//                            startActivity(intent);
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public Dialog onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_operation, null))
                // Add action buttons
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }
    public HttpResponse postHTTP (String operation, String Device) throws IOException {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://www.yoursite.com/script.php");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("operation","getData"));
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        // Execute HTTP Post Request
        HttpResponse response = null;

        response = httpclient.execute(httppost);

        return response;
    }
}
