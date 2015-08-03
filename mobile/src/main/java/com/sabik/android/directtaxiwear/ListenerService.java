package com.sabik.android.directtaxiwear;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

public class ListenerService extends WearableListenerService {

    private GoogleApiClient client;
    private String nodeId;
    private static final String TAG = "TOASTME";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        final String message = messageEvent.getPath();

        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d(TAG, "Client connect");
                        Wearable.NodeApi.getConnectedNodes(client).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                            @Override
                            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                                if (getConnectedNodesResult.getStatus().isSuccess() && getConnectedNodesResult.getNodes().size() > 0) {
                                    nodeId = getConnectedNodesResult.getNodes().get(0).getId();
                                    //View view = new View();
                                    TaxiMain taxiMain = new TaxiMainBuilder()
                                            .context(getApplicationContext())
                                            .client(client)
                                            .nodeId(nodeId)
                                            .buildTaxi();

                                    taxiMain.SendHTTP(message);
                                }
                            }
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int conId) {
                    }
                })
                .addApi(Wearable.API)
                .build();
        client.connect();
    }
}

