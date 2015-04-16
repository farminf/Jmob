package com.farmin.pert.jmob;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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
import java.util.concurrent.ExecutionException;

/**
 * Created by pert on 14-Apr-15.
 */
public class DeviceActivity extends Activity {
    public String EXTRA_MESSAGE = null;
    private String serverURLfunctions = null;
    ListView lvFunctions;
    TextView tvFunction;
    TextView tvDeviceName;
    TextView tvFunctionProperty;
    TextView TvFunctionOperations;
    TextView tvResponse;
    private final static String jsonDeviceFunction = "dal.function.UID";
    private final static String jsonDeviceFunctionOperations = "dal.function.operation.names";
    private final static String jsonDeviceFunctionProperty = "dal.function.property.names";
    ArrayList<HashMap<String, String>> functionList = new ArrayList<HashMap<String, String>>();
    AlertDialog.Builder builder;



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
        tvFunctionProperty = (TextView) findViewById(R.id.tvFunctionProperty);
        tvResponse = (TextView) findViewById(R.id.tvResponse);
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
                    String property = c.getString(jsonDeviceFunctionProperty);
                    // Adding value HashMap key => value
                    HashMap<String, String> map = new HashMap<String, String>();
                    //map.put(jsonDeviceStatus, status);

                    map.put(jsonDeviceFunction, function);
                    map.put(jsonDeviceFunctionOperations, operations);
                    map.put(jsonDeviceFunctionProperty, property);

                    functionList.add(map);
                    lvFunctions =(ListView)findViewById(R.id.lvFunctions);
                    ListAdapter adapter = new SimpleAdapter(DeviceActivity.this, functionList,
                            R.layout.list_view2,
                            new String[] { jsonDeviceFunction , jsonDeviceFunctionOperations , jsonDeviceFunctionProperty }, new int[] {
                            R.id.tvFunction,R.id.tvFunctionOperations , R.id.tvFunctionProperty});
                    lvFunctions.setAdapter(adapter);
                    lvFunctions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            String deviceFunctionUID = functionList.get(+position).get(jsonDeviceFunction);
                            String deviceFunctionOperation = functionList.get(position).get(jsonDeviceFunctionOperations);
                            String functionUIDNoSpace = deviceFunctionUID.replaceAll("\\s","%20");
                            //Toast.makeText(DeviceActivity.this, "You Clicked at " + deviceFunctionUID + deviceFunctionOperation , Toast.LENGTH_SHORT).show();
                            onCreateDialog(functionUIDNoSpace);
                            builder.show();

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

    public void onCreateDialog(String functionUIDURL) {
        builder = new AlertDialog.Builder(new ContextThemeWrapper(DeviceActivity.this,R.style.dialog ));
        final String URL = "http://130.192.86.173/api/functions/" + functionUIDURL;
        // Get the layout inflater
        //LayoutInflater inflater = getLayoutInflater();
        final EditText inputOPT = new EditText(getApplicationContext());
        inputOPT.findViewById(R.id.EToperation);

        builder.setView(inputOPT);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
       // builder.setView(inflater.inflate(R.layout.dialog_operation, null))
                // Add action buttons
                builder.setMessage("Insert your Operation")
                       .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int id) {
//                               try {
                               Editable input = inputOPT.getText();
                               String customMSGInput = input.toString();
                               Log.d("ttt",customMSGInput);
                               //JsonParser jparsen = new JsonParser();
                               //JSONArray newArray = jparsen.getJson("http://130.192.86.173/api/functions/ZigBee:Smart%20Info:ah.app.3521399293212622892-1:EnergyMeter");
                               //postHTTP("getCurrent", "ZigBee:Smart%20Info:ah.app.3521399293212622892-1:EnergyMeter");
                               AsyncTask<String, Void, String> gg = new com.farmin.pert.jmob.AsyncTask().execute(URL ,customMSGInput );
                               String answer = null;
                               try {
                                   answer = gg.get();
                               } catch (InterruptedException e) {
                                   e.printStackTrace();
                               } catch (ExecutionException e) {
                                   e.printStackTrace();
                               }
                               Toast.makeText(getApplicationContext(),answer,Toast.LENGTH_LONG).show();
                               tvResponse.setText(answer);
//                               } catch (IOException e) {
//                                   e.printStackTrace();
//                               }
                           }
                       })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
         builder.create();
    }

}
