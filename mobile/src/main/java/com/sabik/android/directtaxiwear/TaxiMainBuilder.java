package com.sabik.android.directtaxiwear;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Sabik on 4/6/2015.
 */
public class TaxiMainBuilder
{
    private GoogleApiClient mClient;
    private String mNodeId;
    private Context mContext;
    private TextView mTextView;

    public TaxiMainBuilder() { }

    public TaxiMain buildTaxi()
    {
        return new TaxiMain(mContext, mClient, mNodeId, mTextView);
    }

    public TaxiMainBuilder context(Context mContext)
    {
        this.mContext = mContext;
        return this;
    }

    public TaxiMainBuilder client(GoogleApiClient mClient)
    {
        this.mClient = mClient;
        return this;
    }

    public TaxiMainBuilder nodeId(String mNodeId)
    {
        this.mNodeId = mNodeId;
        return this;
    }

    public TaxiMainBuilder textView(TextView mTextView)
    {
        this.mTextView = mTextView;
        return this;
    }
}
