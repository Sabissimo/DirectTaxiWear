package com.sabik.android.directtaxiwear;

import android.content.Context;
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

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

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
import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity {

    private static final long CONNECTION_TIME_OUT_MS = 100;

    private String nodeId;

    private static final String TAG = "TOASTME";
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initApi();
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
        TaxiMain taxiMain = new TaxiMain(this, client, nodeId);
        taxiMain.SendHTTP("Send test");
        //replyToWatch();

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                Log.d(TAG,"start");
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                }
                Log.d(TAG,"finish");
                client.disconnect();
            }
        }).start();
    }

    private void replyToWatch() {
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(client, nodeId, "message received", null);
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
        }
    }
}
