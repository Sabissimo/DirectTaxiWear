package com.sabik.android.directtaxiwear;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;


public class TaxiMain{

    private GoogleApiClient client;
    private String nodeId;
    private static final String TAG = "TOASTME";
    private Context context;
    private TextView textView;


    public TaxiMain(Context myContext,GoogleApiClient myClient, String myNodeId, TextView myTextView){
        context = myContext;
        nodeId = myNodeId;
        client = myClient;
        textView = myTextView;
    }


    public void SendHTTP(String message) {

        showToast(message);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="http://192.168.0.131/android/?cmd="+message;

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                        Log.d(TAG, "It works!");
                        replyToWatch("ok");
                        textView.setText(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
                Log.d(TAG, "Doesn't work! 1");
                replyToWatch("notok");
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void showToast(String message) {
        Log.d(TAG, "sendToast3");
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
}

