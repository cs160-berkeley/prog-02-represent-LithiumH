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
        if (bundles != null) {
            ArrayList<DataMap> dataMaps = DataMap.arrayListFromBundleArrayList(bundles);
            Log.d("T", String.format("datamaps size is %d", dataMaps.size()));
            for (DataMap dataMap : dataMaps) {
                dataMap.putLong("time", (new java.util.Date()).getTime());
            }
            PutDataMapRequest dataMapRequest = PutDataMapRequest.create("/info");
            dataMapRequest.getDataMap()
                    .putDataMapArrayList("org.haojun.represent.DATAMAPS", dataMaps);
            Wearable.DataApi.putDataItem(mApiClient, dataMapRequest.asPutDataRequest());
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NodeApi.GetConnectedNodesResult result =
                            Wearable.NodeApi.getConnectedNodes(mApiClient).await();
                    for (Node node : result.getNodes()) {
                        Wearable.MessageApi.sendMessage(mApiClient, node.getId(), "/reset",
                                new byte[0]);
                    }
                }
            }).start();
        }
        return START_NOT_STICKY;
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
