package com.sabik.android.directtaxiwear;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DelayedConfirmationView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.Node;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity implements
        DelayedConfirmationView.DelayedConfirmationListener, GoogleApiClient.ConnectionCallbacks, MessageApi.MessageListener {

    private static final long CONNECTION_TIME_OUT_MS = 100;
    private static final int CONFIRMATION_REQUEST_CODE = 0;
    private static final String TAG = "TOASTME";

    private boolean mCanceled = false;
    private DelayedConfirmationView mDelayedView;

    private GoogleApiClient client;
    private String nodeId;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, Integer.toString(requestCode));
        if (requestCode == CONFIRMATION_REQUEST_CODE) {
            // Returning from ConfirmationActivity, finish this activity
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_main);
        initApi();


        mDelayedView = (DelayedConfirmationView)
                findViewById(R.id.delayed_confirm);
        mDelayedView.setTotalTimeMs(CONNECTION_TIME_OUT_MS*35);
        mDelayedView.setListener(this);
        mDelayedView.start();

    }

    @Override
    public void onConnected( Bundle bundle ) {
        Wearable.MessageApi.addListener( client, this );
    }

    @Override
    public void onConnectionSuspended( int conid ) {

    }

    private void initApi() {
        client = getGoogleApiClient(this);
        retrieveDeviceNode();
    }

    @Override
    public void onTimerFinished(View view) {
        if (mCanceled) {
            // Timer was cancelled, do nothing
            return;
        }
        sendToast();
    }

    @Override
    public void onTimerSelected(View view) {
        // Indicate that the timer should do nothing when it finishes
        mCanceled = true;

        // Show a cancellation toast
        /*Toast.makeText(this, getString(R.string.message_canceled), Toast.LENGTH_SHORT).show();*/
        finish();
    }


    /**
     * Returns a GoogleApiClient that can access the Wear API.
     * @param context
     * @return A GoogleApiClient that can make calls to the Wear API
     */
    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    /**
     * Connects to the GoogleApiClient and retrieves the connected device's Node ID. If there are
     * multiple connected devices, the first Node ID is returned.
     */
    private void retrieveDeviceNode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                }
                client.disconnect();
            }
        }).start();
    }

    /**
     * Sends a message to the connected mobile device, telling it to show a Toast.
     */
    private void sendToast() {
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(client, nodeId, getString(R.string.message_confirmed), null);
                           /* .setResultCallback(
                            new ResultCallback<MessageApi.SendMessageResult>() {
                                @Override
                                public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                    if (!sendMessageResult.getStatus().isSuccess()) {
                                        Log.d(TAG, "Failed to send message with status code: "
                                                + sendMessageResult.getStatus().getStatusCode());
                                        client.disconnect();
                                        Intent intent = new Intent(getBaseContext(), ConfirmationActivity.class);
                                        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                                                ConfirmationActivity.FAILURE_ANIMATION);
                                        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                                                getString(R.string.message_canceled));
                                        startActivityForResult(intent, CONFIRMATION_REQUEST_CODE);
                                    }
                                    else
                                    {
                                        Log.d(TAG, "Succeeded to send message with status code: "
                                                + sendMessageResult.getStatus().getStatusCode());
                                        client.disconnect();
                                        Intent intent = new Intent(getBaseContext(), ConfirmationActivity.class);
                                        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                                                ConfirmationActivity.SUCCESS_ANIMATION);

                                        startActivityForResult(intent, CONFIRMATION_REQUEST_CODE);
                                    }
                                }
                            }
                    );*/
                }
            }).start();
        }
        else
        {
            Log.d(TAG, "Device offline");
            client.disconnect();
            Intent intent = new Intent(getBaseContext(), ConfirmationActivity.class);
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                    ConfirmationActivity.FAILURE_ANIMATION);
            intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                    getString(R.string.message_canceled));
            startActivityForResult(intent, CONFIRMATION_REQUEST_CODE);
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        String message = messageEvent.getPath();
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
