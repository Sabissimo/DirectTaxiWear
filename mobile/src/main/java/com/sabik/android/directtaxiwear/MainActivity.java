package com.sabik.android.directtaxiwear;

import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "TOASTME";
    private GoogleApiClient client;
    private String nodeId;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.textView);

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
                                    TaxiMain taxiMain = new TaxiMainBuilder()
                                            .context(getApplicationContext())
                                            .client(client)
                                            .nodeId(nodeId)
                                            .textView(mTextView)
                                            .buildTaxi();
                                    taxiMain.SendHTTP("");
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

    public void OnButtonOnClick(View view)
    {
        TaxiMain taxiMain = new TaxiMainBuilder()
                .context(this)
                .client(client)
                .nodeId(nodeId)
                .textView(mTextView)
                .buildTaxi();
        taxiMain.SendHTTP("lightson");
        //mTextView.setText("On");
    }

    public void OnButtonOffClick(View view)
    {
        TaxiMain taxiMain = new TaxiMainBuilder()
                .context(this)
                .client(client)
                .nodeId(nodeId)
                .textView(mTextView)
                .buildTaxi();
        taxiMain.SendHTTP("lightsoff");
        //mTextView.setText("Off");
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
        client.disconnect();
        super.onPause();
    }
}
