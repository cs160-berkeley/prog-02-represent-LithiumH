package org.haojun.represent;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.List;
import java.util.Map;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                Log.d("T", String.format("Zip Code entry is : %s",zipCodeString));
                int zipCode = Integer.parseInt(zipCodeString);
                Intent intent = new Intent(getApplicationContext(), AllCandidatesActivity.class);
                intent.putExtra("zipCode", zipCode);
                startActivity(intent);
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zipCodeString = "94720";
                int zipCode = Integer.parseInt(zipCodeString);
                Intent intent = new Intent(getApplicationContext(), AllCandidatesActivity.class);
                intent.putExtra("zipCode", zipCode);
                startActivity(intent);
            }
        });
    }
}
