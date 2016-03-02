package org.haojun.represent;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.Map;

public class IndividualActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras == null || extras.getString("name").equals("")) {
            return;
        }
        Map<String, String> candidate =
                InformationLoader.getDetailedCandidate(extras.getString("name"));
        CollapsingToolbarLayout toolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(candidate.get("name"));
        Log.d("T", String.format("Name: %s, Party: %s", candidate.get("name"), candidate.get("party")));
        int color =
                candidate.get("party").equals("Democrat") ? ContextCompat.getColor(this, R.color.democratBlue)
                        : candidate.get("party").equals("Republican") ? ContextCompat.getColor(this, R.color.republicanRed)
                        : ContextCompat.getColor(this, R.color.independentGreen);
        toolbarLayout.setExpandedTitleColor(color);
        toolbarLayout.setCollapsedTitleTextColor(color);

        TextView email = (TextView) findViewById(R.id.candidateEmail);
        TextView website = (TextView) findViewById(R.id.candidateWebsite);

        Drawable imgSrc = null;
        if (email != null && website != null) {
            try {
                URL thumb_u = new URL(candidate.get("picuri"));
                imgSrc= Drawable.createFromStream(thumb_u.openStream(), "src");
            } catch (IOException e) {
                e.printStackTrace();
            }
            email.setText(candidate.get("email"));
            website.setText(candidate.get("website"));
        }
        toolbarLayout.setBackground(imgSrc);

        TextView term = (TextView) findViewById(R.id.term);
        TextView committee = (TextView) findViewById(R.id.committee);
        TextView bills = (TextView) findViewById(R.id.bills);
        term.setText(candidate.get("term"));
        committee.setText(candidate.get("committee"));
        bills.setText(candidate.get("bills"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
