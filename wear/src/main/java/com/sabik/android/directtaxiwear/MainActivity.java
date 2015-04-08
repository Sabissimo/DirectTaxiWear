package com.sabik.android.directtaxiwear;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DelayedConfirmationView;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;


public class MainActivity extends Activity {

    private static final long CONNECTION_TIME_OUT_MS = 100;
    private static final int CONFIRMATION_REQUEST_CODE = 0;
    private static final int FAILED_REQUEST_CODE = 1;
    private static final String TAG = "TOASTME";
    private boolean mCanceled = false;
    private DelayedConfirmationView mDelayedView;
    private GoogleApiClient client;
    private MessageApi.MessageListener messageListener;
    private String nodeId;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, Integer.toString(requestCode));
        if (requestCode == CONFIRMATION_REQUEST_CODE || requestCode == FAILED_REQUEST_CODE) {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_main);

        mDelayedView = (DelayedConfirmationView) findViewById(R.id.delayed_confirm);
        mDelayedView.setTotalTimeMs(CONNECTION_TIME_OUT_MS * 35);
        mDelayedView.setListener(new DelayedConfirmationView.DelayedConfirmationListener() {
            @Override
            public void onTimerFinished(View view) {
                if (mCanceled) {
                    return;
                }
                sendToast();
            }

            @Override
            public void onTimerSelected(View view) {
                mCanceled = true;
                finish();
            }
        });
        mDelayedView.start();

        messageListener = new MessageApi.MessageListener() {
            @Override
            public void onMessageReceived(MessageEvent messageEvent) {
                String message = messageEvent.getPath();
                Log.d(TAG, "Message received = " + message);
                if(message.equals("ok")) {
                    Intent intent = new Intent(getBaseContext(), ConfirmationActivity.class);
                    intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                            ConfirmationActivity.SUCCESS_ANIMATION);

                    startActivityForResult(intent, CONFIRMATION_REQUEST_CODE);
                }
                else if (message.equals("notok")){
                    Intent intent = new Intent(getBaseContext(), ConfirmationActivity.class);

                    intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                            ConfirmationActivity.FAILURE_ANIMATION);
                    intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                            getString(R.string.message_canceled));
                    startActivityForResult(intent, FAILED_REQUEST_CODE);
                }
            }
        };

        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Wearable.MessageApi.addListener(client, messageListener);
                        Wearable.NodeApi.getConnectedNodes(client).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                            @Override
                            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                                if (getConnectedNodesResult.getStatus().isSuccess() && getConnectedNodesResult.getNodes().size() > 0) {
                                    nodeId = getConnectedNodesResult.getNodes().get(0).getId();
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
    }

    private void sendToast() {
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Wearable.MessageApi.sendMessage(client, nodeId, getString(R.string.message_confirmed), null)
                            .setResultCallback(
                            new ResultCallback<MessageApi.SendMessageResult>() {
                                @Override
                                public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                    if (!sendMessageResult.getStatus().isSuccess()) {
                                        Log.d(TAG, "Failed to send message with status code: "
                                                + sendMessageResult.getStatus().getStatusCode());
                                        Intent intent = new Intent(getBaseContext(), ConfirmationActivity.class);
                                        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                                                ConfirmationActivity.FAILURE_ANIMATION);
                                        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                                                getString(R.string.message_canceled));
                                        startActivityForResult(intent, FAILED_REQUEST_CODE);
                                    }
                                    else
                                    {
                                        Log.d(TAG, "Succeeded to send message with status code: "
                                                + sendMessageResult.getStatus().getStatusCode());
                                    }
                                }
                            }
                    );
                    //client.disconnect();
                }
            }).start();
        }
        else
        {
            Log.d(TAG, "Device offline");
            Intent intent = new Intent(getBaseContext(), ConfirmationActivity.class);
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                    ConfirmationActivity.FAILURE_ANIMATION);
            intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                    getString(R.string.message_canceled));
            startActivityForResult(intent, FAILED_REQUEST_CODE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        int connectionResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (connectionResult != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.showErrorDialogFragment(connectionResult, this, 0, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
        } else {
            client.connect();
        }
    }

    @Override
    protected void onPause() {
        Wearable.MessageApi.removeListener(client, messageListener);
        client.disconnect();
        super.onPause();
    }
}
