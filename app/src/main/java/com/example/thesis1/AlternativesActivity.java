package com.example.thesis1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AlternativesActivity extends AppCompatActivity {
    String username;
    String password;
    int score;
    int numTasks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternatives);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //extract the intent data
        Intent intent = getIntent();
        username = (String) intent.getSerializableExtra("Logged in username");
        password = (String) intent.getSerializableExtra("Logged in password");
        score = (int) intent.getSerializableExtra("User score");

        //sets up click listeners for choices on this screen
        TextView fuelText = findViewById(R.id.fuelText);
        TextView thriftText = findViewById(R.id.thriftText);
        TextView productText = findViewById(R.id.productText);

        fuelText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                moveToFuelActivity();
            }
        });

        thriftText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                moveToThriftActivity();
            }
        });

        productText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                moveToProductActivity();
            }
        });

    }

    public void moveToFuelActivity(){
        Intent intent= new Intent(this, FuelActivity.class);
        intent.putExtra("Logged in username", username);
        intent.putExtra("Logged in password", password);
        intent.putExtra("User score", score);

        startActivity(intent);
    }

    public void moveToThriftActivity(){
        Intent intent= new Intent(this, ThriftActivity.class);
        intent.putExtra("Logged in username", username);
        intent.putExtra("Logged in password", password);
        intent.putExtra("User score", score);

        startActivity(intent);
    }

    public void moveToProductActivity(){
        Intent intent= new Intent(this, ProductActivity.class);
        intent.putExtra("Logged in username", username);
        intent.putExtra("Logged in password", password);
        intent.putExtra("User score", score);

        startActivity(intent);
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
