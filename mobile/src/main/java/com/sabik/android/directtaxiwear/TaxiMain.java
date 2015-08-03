package com.sabik.android.directtaxiwear;

import android.content.Context;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Sabik on 4/6/2015.
 */
public class TaxiMain{

    private GoogleApiClient client;
    private String nodeId;
    private static final String TAG = "TOASTME";
    private static Context context;
    private static String messageglobal;

    public TaxiMain(Context myContext,GoogleApiClient myClient, String myNodeId){
        context = myContext;
        nodeId = myNodeId;
        client = myClient;
    }


    public void SendHTTP(String message) {
        final Handler mHandler = new Handler();

        messageglobal = message;

        showToast(message);

        final TextView mTextView = (TextView) findViewById(R.id.text);


        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://www.google.com";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        mTextView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTextView.setText("That didn't work!");
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    // TODO Auto-generated method stub
                    // Write your code here to update the UI.
                    //I make a log to see the results
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet("http://192.168.0.131/android/?cmd="+messageglobal);

                    try {
                        // Add your data

                        // Execute HTTP Post Request
                        HttpResponse response = httpclient.execute(httpGet);
                        Log.d(TAG, "It works!");
                        replyToWatch("ok");


                    } catch (ClientProtocolException e) {
                        Log.d(TAG, "Doesn't work! 1");
                        replyToWatch("notok");
                    } catch (IOException e) {
                        Log.d(TAG, "Doesn't work! 2");
                        replyToWatch("notok");
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Doesn't work! 3");
                    replyToWatch("notok");
                }

                //testingt();
            }
        }).start();
    }

    private void showToast(String message) {
        Log.d(TAG, "sendToast3");
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    private void replyToWatch(final String isItOk) {
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Wearable.MessageApi.sendMessage(client, nodeId, isItOk, null);
                }
            }).start();
        }
        else
        {
            Log.d(TAG, "Device offline");
        }
    }

    public void testingt(){
        try {
            String urlParameters  = "param1=a&param2=b&param3=c";
            byte[] postData       = urlParameters.getBytes( Charset.forName( "UTF-8" ));
            int    postDataLength = postData.length;
            String request        = "http://taxi.d1erp.com/addorder.php";
            URL    url            = new URL( request );
            HttpURLConnection cox= (HttpURLConnection) url.openConnection();
            cox.setDoOutput( true );
            cox.setDoInput ( true );
            cox.setInstanceFollowRedirects( false );
            cox.setRequestMethod( "POST" );
            cox.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
            cox.setRequestProperty( "charset", "utf-8");
            cox.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            cox.setUseCaches( false );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}

