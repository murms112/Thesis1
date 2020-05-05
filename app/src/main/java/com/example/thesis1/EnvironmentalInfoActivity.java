package com.example.thesis1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

public class EnvironmentalInfoActivity extends AppCompatActivity {
    String username;
    String password;
    int score;
    int numTasks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_environmental_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //extract the intent data
        Intent intent = getIntent();
        username = (String) intent.getSerializableExtra("Logged in username");
        password = (String) intent.getSerializableExtra("Logged in password");
        score = (int) intent.getSerializableExtra("User score");
        //numTasks = (int) intent.getSerializableExtra("Number of logged tasks");

    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), HomeScreenActivity.class);
        myIntent.putExtra("Logged in username", username);
        myIntent.putExtra("Logged in password", password);
        myIntent.putExtra("User score", score);
        //myIntent.putExtra("Number of logged tasks", numTasks);

        startActivity(myIntent);
        return true;
    }
}
