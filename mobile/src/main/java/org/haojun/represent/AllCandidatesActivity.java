package org.haojun.represent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.PutDataMapRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllCandidatesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_candidates);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RelativeLayout loadingSpinner = (RelativeLayout) findViewById(R.id.loadingSpinner);
        loadingSpinner.setVisibility(View.VISIBLE);
        Bundle extras = getIntent().getExtras();
        final List<Map<String, String>> result = InformationLoader.getCandidates(
                extras != null ? extras.getInt("zipCode") : 94720);
        sendToWatch(result, extras != null ? extras.getInt("zipCode") : 94720);

        loadingSpinner.setVisibility(View.GONE);
        ListView listView = (ListView) findViewById(R.id.candidateList);
        final ListAdapter candidateAdapter = new CandidateListAdapter(this,
                R.layout.individual_candidate_list_item, result);
        listView.setAdapter(candidateAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), IndividualActivity.class);
                TextView name = (TextView) view.findViewById(R.id.candidateName);
                intent.putExtra("name", name != null ? name.getText() : "");
                startActivity(intent);
            }
        });
    }

    private void sendToWatch(List<Map<String, String>> candidates, int zipCode) {
        ArrayList<Bundle> bundles = new ArrayList<>();
        int id = 0;
        for (Map<String, String> candidate : candidates) {
            Bundle bundle = new Bundle();
            Bitmap bitmap = null;
            try {
                URL profile = new URL(candidate.get("picuri"));
                bitmap = BitmapFactory.decodeStream(profile.openStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] arr = stream.toByteArray();
                bundle.putString("name", candidate.get("name"));
                bundle.putString("party", candidate.get("party"));
                bundle.putString("id", "candidate");
                bundle.putByteArray("pic", arr);
                Log.d("T", String.format("Sending %d length array to watch", arr.length));
            }
            bundles.add(bundle);
            id += 1;
        }
        Map<String, String> result = InformationLoader.getVoteData(zipCode);
        Bundle voteBundle = new Bundle();
        voteBundle.putString("id", "vote");
        for (String key : result.keySet()) {
            voteBundle.putString(key, result.get(key));
        }
        Log.d("T", String.format("Adding %d keys to voteBundle", result.size()));
        bundles.add(voteBundle);

        Intent sendIntent = new Intent(getApplicationContext(), MobileToWatchService.class);
        sendIntent.putParcelableArrayListExtra("org.haojun.represent.BUNDLES", bundles);
        startService(sendIntent);
    }
}
