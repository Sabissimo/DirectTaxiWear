package com.sabik.android.directtaxiwear;


import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ListenerService extends WearableListenerService {

    private static final String TAG = "TOASTME";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.d(TAG, "Message received");
        String message = messageEvent.getPath();
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}

