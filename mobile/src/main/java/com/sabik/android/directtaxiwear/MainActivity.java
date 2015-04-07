package com.sabik.android.directtaxiwear;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "TOASTME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


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

    public void OnButtonClick(View view)
    {
        TaxiMain taxiMain = new TaxiMain(getApplicationContext());
        taxiMain.SendHTTP("Send test");

    }
    public void SendHTTP(String message) {
        final Handler mHandler = new Handler();
        showToast(message);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //while (true) {
                try {
                    //Thread.sleep(10000);
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            // Write your code here to update the UI.
                            LocationManager locationManager = (LocationManager)
                                    getSystemService(LOCATION_SERVICE);
                            Criteria criteria = new Criteria();
                            String bestProvider = locationManager.getBestProvider(criteria, false);
                            Location location = locationManager.getLastKnownLocation(bestProvider);
                            String myLocation = "Latitude = " + location.getLatitude() + " Longitude = " + location.getLongitude();

                            //I make a log to see the results
                            Log.e("MY CURRENT LOCATION", myLocation);
                            HttpClient httpclient = new DefaultHttpClient();
                            HttpPost httppost = new HttpPost("http://taxi.d1erp.com/addorder.php");

                            try {
                                // Add your data

                                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                                nameValuePairs.add(new BasicNameValuePair("phone", "597340898"));
                                nameValuePairs.add(new BasicNameValuePair("lat", Double.toString(location.getLatitude())));
                                nameValuePairs.add(new BasicNameValuePair("lon", Double.toString(location.getLongitude())));
                                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                                // Execute HTTP Post Request
                                HttpResponse response = httpclient.execute(httppost);
                                Log.e("MY CURRENT LOCATION", "It works!");


                            } catch (ClientProtocolException e) {
                                // TODO Auto-generated catch block
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                            }
                        }
                    });
                } catch (Exception e) {
                    // TODO: handle exception
                }
                //}
            }
        }).start();
    }

    private void showToast(String message) {
        Log.d(TAG, "sendToast3");
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
