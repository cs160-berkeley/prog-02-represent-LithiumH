package org.haojun.represent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

}
