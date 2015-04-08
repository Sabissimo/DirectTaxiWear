package com.sabik.android.directtaxiwear;


import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ListenerService extends WearableListenerService {

    private static final long CONNECTION_TIME_OUT_MS = 100;

    private GoogleApiClient client;
    private String nodeId;

    private static final String TAG = "TOASTME";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        initApi();
        String message = messageEvent.getPath();
        TaxiMain taxiMain = new TaxiMain(this, client, nodeId);
        taxiMain.SendHTTP(message);
    }

    private void initApi() {
        client = getGoogleApiClient(this);
        retrieveDeviceNode();
    }

    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    private void retrieveDeviceNode() {
        client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
        Log.d(TAG, "start");
        NodeApi.GetConnectedNodesResult result =
                Wearable.NodeApi.getConnectedNodes(client).await();
        List<Node> nodes = result.getNodes();
        if (nodes.size() > 0) {
            nodeId = nodes.get(0).getId();
        }
        Log.d(TAG,"finish");
        client.disconnect();
    }
}

