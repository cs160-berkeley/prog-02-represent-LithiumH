package org.haojun.represent;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WatchToMobileService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    public WatchToMobileService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWatchApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mWatchApiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWatchApiClient.disconnect();
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        if ("candidate".equals(intent.getExtras().getString("type"))) {
            final String name = intent.getExtras().getString("name");
            final Service self = this;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NodeApi.GetConnectedNodesResult result =
                            Wearable.NodeApi.getConnectedNodes(mWatchApiClient).await();
                    for (Node node : result.getNodes()) {
                        Wearable.MessageApi.sendMessage(
                                mWatchApiClient, node.getId(), "/name",
                                name.getBytes(StandardCharsets.UTF_8)).await();
                    }
                    self.stopSelf();
                }
            }).start();
        } else if ("zipCode".equals(intent.getExtras().getString("type"))) {
            final Service self = this;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NodeApi.GetConnectedNodesResult result =
                            Wearable.NodeApi.getConnectedNodes(mWatchApiClient).await();
                    for (Node node : result.getNodes()) {
                        Wearable.MessageApi.sendMessage(
                                mWatchApiClient, node.getId(), "/zipCode",
                                "".getBytes(StandardCharsets.UTF_8)).await();
                    }
                    self.stopSelf();
                }
            }).start();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private GoogleApiClient mWatchApiClient;
}
