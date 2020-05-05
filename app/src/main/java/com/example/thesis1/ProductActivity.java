package com.example.thesis1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ProductActivity extends AppCompatActivity {
    String username;
    String password;
    int score;
    int numTasks;
    ArrayList<Product> products = new ArrayList<>();
    ProgressDialog progressDialog;
    private ProductAdapter adapter;
    private RecyclerView recyclerView;
    //the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //the reference
    DatabaseReference productRef = database.getReference("userDatabase/sustainableProducts/");
    String productName;
    String productLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //extract the intent data
        Intent intent = getIntent();
        username = (String) intent.getSerializableExtra("Logged in username");
        password = (String) intent.getSerializableExtra("Logged in password");
        score = (int) intent.getSerializableExtra("User score");
        //numTasks = (int) intent.getSerializableExtra("Number of logged tasks");

        recyclerView = findViewById(R.id.productRecyclerView);

        getProductsFromDatabase();

    }

    private void getProductsFromDatabase(){
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot productSnapshot) {
                for (DataSnapshot dataValue : productSnapshot.getChildren()) {
                    productName = dataValue.child("productName").getValue(String.class);
                    productLink = dataValue.child("productLink").getValue(String.class);
                    Product p = new Product(productName, productLink);
                    products.add(p);
                }
                //firstRun = false;
                buildProductAdapter(products);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
    private void buildProductAdapter(ArrayList<Product> products){
        Collections.shuffle(products);
        adapter = new ProductAdapter(this, products);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ProductActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), AlternativesActivity.class);
        myIntent.putExtra("Logged in username", username);
        myIntent.putExtra("Logged in password", password);
        myIntent.putExtra("User score", score);
        //myIntent.putExtra("Number of logged tasks", numTasks);

        startActivity(myIntent);
        return true;
    }
}
