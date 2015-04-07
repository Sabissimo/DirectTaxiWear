package com.sabik.android.directtaxiwear;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
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

/**
 * Created by Sabik on 4/6/2015.
 */
public class TaxiMain{

    private static final String TAG = "TOASTME";
    private static Context mycontext;

    public TaxiMain(Context mContext){
        mycontext = mContext;
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
                            LocationManager locationManager = (LocationManager)mycontext.getSystemService(mycontext.LOCATION_SERVICE);
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
        Toast.makeText(mycontext, message, Toast.LENGTH_LONG).show();
    }
}
