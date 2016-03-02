package org.haojun.represent;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
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
            Log.d("T", "detected change in accelerometer");
            Intent intent = new Intent(getApplicationContext(), AllCandidatesActivity.class);
            Random rand = new Random();
            // Do some checking for valid zip code
            intent.putExtra("zipCode", rand.nextInt(99499) + 500);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }

}
