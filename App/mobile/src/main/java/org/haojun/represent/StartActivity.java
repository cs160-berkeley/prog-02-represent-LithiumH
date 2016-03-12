package org.haojun.represent;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.location.LocationListener;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;

public class StartActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "3ynlv5gW3qtM0KywlQvpVRQnZ";
    private static final String TWITTER_SECRET = "uR68D4psCJLmEqp1A8BL1sO7vx3dHfpVepZNlAoDZlR1OqDNI2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_start);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton searchButton = (FloatingActionButton) findViewById(R.id.enter_zip);
        final FloatingActionButton locationButton = (FloatingActionButton) findViewById(R.id.enter_location);
        final EditText zipCodeEntry = (EditText) findViewById(R.id.zip_code_entry);
        final AppCompatActivity self = this;

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zipCodeString = zipCodeEntry.getText().toString();
                Log.d("T", String.format("Zip Code entry is : %s", zipCodeString));
                Intent intent = new Intent(getApplicationContext(), AllCandidatesActivity.class);
                intent.putExtra("zipCode", zipCodeString.trim());
                startActivity(intent);
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
                String a = LocationManager.GPS_PROVIDER;
                LocationListener listener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();
                        new AsyncTask<Double, Void, String>() {
                            @Override
                            protected String doInBackground(Double... params) {
                                double lat = params[0];
                                double lon = params[1];
                                String zipCode = "";
                                try {
                                    JSONObject json = InformationLoader.getFromURL(
                                            "https://maps.googleapis.com/maps/api/geocode/json",
                                            String.format("latlng=%.5f,%.5f", lat, lon),
                                            String.format("key=%s", InformationLoader.getKey(
                                                    getApplicationContext(), "google")));
                                    JSONArray locations = json.getJSONArray("results");
                                    JSONArray components = new JSONArray();
                                    for (int i = 0; i < locations.length() && components.length() <= 0; i++) {
                                        JSONArray types = locations.getJSONObject(i).getJSONArray("types");
                                        for (int j = 0; j < types.length(); j++) {
                                            if ("postal_code".equals(types.getString(j))) {
                                                components = locations.getJSONObject(i).getJSONArray("address_components");
                                                break;
                                            }
                                        }
                                    }
                                    for (int i = 0; i < components.length(); i++) {
                                        if ("postal_code".equals(components.getJSONObject(i).getJSONArray("types").getString(0)))
                                            zipCode = components.getJSONObject(i).getString("short_name");
                                    }

                                } catch (JSONException e) {
                                    Log.e("locationButton", e.getMessage());
                                }
                                return zipCode;
                            }

                            @Override
                            protected void onPostExecute(String s) {
                                super.onPostExecute(s);
                                Log.d("T", String.format("result is :%s", s));
                                Intent intent = new Intent(getApplicationContext(), AllCandidatesActivity.class);
                                intent.putExtra("zipCode", s);
                                startActivity(intent);
                            }
                        }.execute(lat, lon);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) { }

                    @Override
                    public void onProviderEnabled(String provider) { }

                    @Override
                    public void onProviderDisabled(String provider) { }
                };
                Looper looper = Looper.getMainLooper();
                manager.requestSingleUpdate(a, listener, looper);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
