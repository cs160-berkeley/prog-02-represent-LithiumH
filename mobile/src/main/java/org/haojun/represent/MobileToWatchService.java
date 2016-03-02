package org.haojun.represent;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

public class MobileToWatchService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    @Override
    public void onCreate() {
        super.onCreate();
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mApiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int intentId) {
        ArrayList<Bundle> bundles = intent.getParcelableArrayListExtra("org.haojun.represent.BUNDLES");
        Log.d("T", String.format("picture array from bundle length: %d", bundles.get(0).getByteArray("pic").length));
        if (bundles != null) {
            ArrayList<DataMap> dataMaps = DataMap.arrayListFromBundleArrayList(bundles);
            for (DataMap dataMap : dataMaps) {
                if (dataMap.getString("id").equals("candidate")) {
                    Log.d("T", String.format("Each candidate's pic length: %d", dataMap.getByteArray("pic").length));
//                    Asset asset = Asset.createFromBytes(dataMap.getByteArray("pic"));
//                    dataMap.remove("pic");
//                    dataMap.putAsset("pic", asset);
                }
                dataMap.putLong("time", (new java.util.Date()).getTime());
            }
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create("/info");
            dataMapRequest.getDataMap()
                    .putDataMapArrayList("org.haojun.represent.DATAMAPS", dataMaps);
            Wearable.DataApi.putDataItem(mApiClient, dataMapRequest.asPutDataRequest());
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private GoogleApiClient mApiClient;

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
