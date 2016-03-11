package org.haojun.represent;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MobileListenerService extends WearableListenerService {
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in MobileListenerService, got: " + messageEvent.getPath());
        if (messageEvent.getPath().equals("/name")) {
            Intent intent = new Intent(getApplicationContext(), IndividualActivity.class);
            intent.putExtra("name", new String(messageEvent.getData(), StandardCharsets.UTF_8));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (messageEvent.getPath().equals("/zipCode")) {
            Log.d("MobileListener", "detected change in accelerometer");
            Intent intent = new Intent(getApplicationContext(), AllCandidatesActivity.class);
            intent.putExtra("zipCode", "");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }

}
