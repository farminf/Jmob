package com.farmin.pert.jmob;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {

    ListView lvDevices;
    TextView tvDeviceUID;
    TextView tvDeviceDriver;
    ArrayList<HashMap<String, String>> deviceList = new ArrayList<HashMap<String, String>>();
    String serverURLDevices = "http://130.192.86.173/api/devices";
    Button btnGetDevices;
    private final static String jsonDeviceStatus = "dal.device.status";
    private final static String jsonDeviceUID = "dal.device.UID";
    private final static String jsonDeviceDriver = "dal.device.driver";
    JSONArray devicesArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceList = new ArrayList<HashMap<String, String>>();
        tvDeviceUID = (TextView) findViewById(R.id.tvDeviceUID);
        tvDeviceDriver = (TextView) findViewById(R.id.tvDeviceUID);

        // Button for get devices list
        btnGetDevices = (Button) findViewById(R.id.btnGetDevices);
        btnGetDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new jsonParse().execute(serverURLDevices);
            }
        });



    }
//************************ Functions ***************************************************
//**************************************************************************************

    private class jsonParse extends AsyncTask<String, Void, JSONArray> {
        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
        String data ="";
        InputStream inputStream = null;
        String result = "";
        //String json=args[1];

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            Dialog.setMessage("Please wait..");
            Dialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... args) {
            JsonParser jparse = new JsonParser();
            JSONObject jobj = null;
            JSONArray jArray = null;
            try {
                jArray = jparse.getJson(serverURLDevices);
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
                devicesArray = jArray;
                for(int i = 0; i < devicesArray.length(); i++){
                    JSONObject c = devicesArray.getJSONObject(i);
                    // Storing  JSON item in a Variable
                    //String status = c.getString(jsonDeviceStatus);
                    String uid = c.getString(jsonDeviceUID);
                    String driver = c.getString(jsonDeviceDriver);
                    // Adding value HashMap key => value
                    HashMap<String, String> map = new HashMap<String, String>();
                    //map.put(jsonDeviceStatus, status);
                    map.put(jsonDeviceUID, uid);
                    map.put(jsonDeviceDriver, driver);
                    deviceList.add(map);
                    lvDevices =(ListView)findViewById(R.id.lvDevices);
                    ListAdapter adapter = new SimpleAdapter(MainActivity.this, deviceList,
                            R.layout.list_view,
                            new String[] {jsonDeviceUID, jsonDeviceDriver }, new int[] {
                            R.id.tvDeviceUID, R.id.tvDeviceDriver});
                    lvDevices.setAdapter(adapter);
                    lvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            String deviceUID = deviceList.get(+position).get("dal.device.UID");
                            Toast.makeText(MainActivity.this, "You Clicked at " + deviceUID, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), DeviceActivity.class);
                            intent.putExtra("deviceUID", deviceUID);
                            startActivity(intent);
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



//*************** Menu Option*************************


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch  (item.getItemId()) {

            //noinspection SimplifiableIfStatement
            case R.id.action_settings:
                return true;
            case R.id.action_exit:
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
