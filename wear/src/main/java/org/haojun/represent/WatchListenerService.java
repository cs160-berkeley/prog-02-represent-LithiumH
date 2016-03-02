package org.haojun.represent;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;

/** This is a service running on the back ground to receive all the data from the mobile
 *
 */
public class WatchListenerService extends WearableListenerService {

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d("T", "data received from mobile");
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED &&
                    event.getDataItem().getUri().getPath().equals("/info")) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                ArrayList<DataMap> dataMaps = dataMapItem.getDataMap()
                        .getDataMapArrayList("org.haojun.represent.DATAMAPS");
                ArrayList<Bundle> bundles = new ArrayList<>();
                for (DataMap dataMap : dataMaps) {
                    bundles.add(dataMap.toBundle());
                }
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                mainIntent.putParcelableArrayListExtra("org.haojun.represent.BUNDLES", bundles);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
            }
        }
    }
}
