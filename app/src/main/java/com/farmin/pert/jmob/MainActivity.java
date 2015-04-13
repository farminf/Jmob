package com.farmin.pert.jmob;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class MainActivity extends ActionBarActivity {

    URL url;
    String serverURL = "http://130.192.86.173/api/devices";
    Button btnGetDevices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnGetDevices = (Button) findViewById(R.id.btnGetDevices);
        btnGetDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LongOperation().execute(serverURL);
            }
        });



    }
//************************ Functions ***************************************************
//**************************************************************************************

    private class LongOperation extends AsyncTask<String, Void, Void> {
        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        //private String Error = null;
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
        protected Void doInBackground(String... args) {
            BufferedReader reader =null;
            String url = args[0];

            HttpPost httppost = new HttpPost(url);
            httppost.setHeader("Content-Type", "application/json");
            httppost.setHeader("Accept", "application/json");


            try {
                HttpResponse httpResponse = Client.execute(new HttpGet(url));
                inputStream = httpResponse.getEntity().getContent();

                // convert inputstream to string
                if(inputStream != null) {
                    result = convertInputStreamToString(inputStream);
                    Log.d("Tag", result);
                }
                else {
                    result = "Did not work!";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }



            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Dialog.dismiss();
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
