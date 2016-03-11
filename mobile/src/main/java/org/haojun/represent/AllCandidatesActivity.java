package org.haojun.represent;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AllCandidatesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_candidates);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final RelativeLayout loadingSpinner = (RelativeLayout) findViewById(R.id.loadingSpinner);
        loadingSpinner.setVisibility(View.VISIBLE);
        Bundle extras = getIntent().getExtras();
        new AsyncTask<String, Void, List<Map<String, String>>>() {

            @Override
            protected List<Map<String, String>> doInBackground(String... params) {
                String zipCode = params[0];
                List<Map<String, String>> result =
                        "".equals(zipCode) ? new ArrayList<Map<String, String>>(0)
                                : InformationLoader.getCandidates(getApplicationContext(), zipCode);
                while (result.size() < 2) {
                    List<String> lst = new LinkedList<>();
                    try {
                        InputStream stream = getApplicationContext().getAssets().open("totalZip.txt");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            lst.add(line);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Random rand = new Random();
                    zipCode = lst.get(rand.nextInt(lst.size()));
                    result = InformationLoader.getCandidates(getApplicationContext(), zipCode);
                }
                sendToWatch(result);
                return result;
            }

            @Override
            protected void onPostExecute(List<Map<String, String>> maps) {
                super.onPostExecute(maps);

                List<Map<String, String>> candidates = new ArrayList<>();
                for (Map<String, String> candidate : maps) {
                    if (!"vote".equals(candidate.get("id")))
                        candidates.add(candidate);
                    else {
                        toolbar.setTitle(
                                String.format("All Candidates in %s, %s", candidate.get("locality"),
                                        candidate.get("state")));
                    }
                }
                loadingSpinner.setVisibility(View.GONE);
                ListView listView = (ListView) findViewById(R.id.candidateList);
                final ListAdapter candidateAdapter = new CandidateListAdapter(getApplicationContext(),
                        R.layout.individual_candidate_list_item, candidates);
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

            private void sendToWatch(List<Map<String, String>> candidates) {
                Log.d("T", String.format("Sending %d to watch", candidates.size()));
                ArrayList<Bundle> bundles = new ArrayList<>();
                for (Map<String, String> candidate : candidates) {
                    if ("vote".equals(candidate.get("id"))) {
                        Bundle bundle = new Bundle();
                        for (String key : candidate.keySet()) {
                            bundle.putString(key, candidate.get(key));
                        }
                        bundles.add(bundle);
                    } else {
                        Bundle bundle = new Bundle();
                        String bytes = candidate.get("pic");
                        String[] byteValues = bytes.substring(1, bytes.length() - 1).split(",");
                        byte[] buffer = new byte[byteValues.length];
                        for (int i = 0; i < byteValues.length; i++) {
                            buffer[i] = Byte.parseByte(byteValues[i].trim());
                        }
                        bundle.putString("name", candidate.get("name"));
                        bundle.putString("party", candidate.get("party"));
                        bundle.putString("id", "candidate");
                        bundle.putByteArray("pic", buffer);
                        bundles.add(bundle);
                    }
                }

                Intent sendIntent = new Intent(getBaseContext(), MobileToWatchService.class);
                sendIntent.putParcelableArrayListExtra("org.haojun.represent.BUNDLES", bundles);
                startService(sendIntent);
            }
        }.execute(extras.getString("zipCode"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent sendIntent = new Intent(getBaseContext(), MobileToWatchService.class);
        startService(sendIntent);
    }
}
