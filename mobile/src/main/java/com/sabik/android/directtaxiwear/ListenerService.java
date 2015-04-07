package com.sabik.android.directtaxiwear;


import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ListenerService extends WearableListenerService {

    private static final String TAG = "TOASTME";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        String message = messageEvent.getPath();
        TaxiMain taxiMain = new TaxiMain(getApplicationContext());
        taxiMain.SendHTTP(message);
    }
}

