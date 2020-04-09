package com.example.thesis1;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class LeaderboardActivity extends AppCompatActivity {
    String username;
    String password;
    int score;
    int numTasks;
    private RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String currUsername;
    Integer currScore;
    ArrayList<User> allUsers = new ArrayList<>();
    private LeaderboardMainAdapter adapter;
    int taskIndex;
    ArrayList<Integer> lti = new ArrayList<>();
    User clickedUser;
    ArrayList<SustainableTask> lt = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //extract the intent data
        Intent intent = getIntent();
        username = (String) intent.getSerializableExtra("Logged in username");
        password = (String) intent.getSerializableExtra("Logged in password");
        score = (int) intent.getSerializableExtra("User score");
        //numTasks = (int) intent.getSerializableExtra("Number of logged tasks");
        lt = intent.getParcelableArrayListExtra("List of all tasks");

        recyclerView = findViewById(R.id.leaderboardMainRecyclerView);

        getUsersFromDatabase();

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
    private void getUsersFromDatabase(){
        DatabaseReference userRef = database.getReference("userDatabase/users/");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot taskSnapshot) {
                for (DataSnapshot dataValue : taskSnapshot.getChildren()) {
                    currUsername = dataValue.child("username").getValue(String.class);
                    currScore = dataValue.child("score").getValue(Integer.class);
                    User u = new User(currUsername, currScore);
                    allUsers.add(u);
                }
                ArrayList<User> users = allUsers;
                sortByScore();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
    private void sortByScore(){
        //researched sorting here https://stackoverflow.com/questions/8121176/java-sort-array-list-using-bubblesort
        User tempUser;
        if(allUsers.size() > 1){
          for(int i = 0; i < allUsers.size(); i++){
              for(int j = 0; j < allUsers.size()- i -1; j++){
                  if(allUsers.get(j).compareTo(allUsers.get(j+1)) > 0){
                      tempUser = allUsers.get(j);
                      allUsers.set(j, allUsers.get(j+1));
                      allUsers.set(j+1, tempUser);
                  }
              }
          }
        }
        Collections.reverse(allUsers);

        createLeaderboardAdapter();
    }
    private void createLeaderboardAdapter(){
        adapter = new LeaderboardMainAdapter(this, allUsers);

        adapter.setOnItemClickListener(new LeaderboardMainAdapter.ClickListener(){
            @Override
            public void onItemClick(int position, View v) {
             clickedUser = allUsers.get(position);
             moveToUserDetailsScreen(clickedUser);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(LeaderboardActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void moveToUserDetailsScreen(User clickedUser){
        //the necessary intent to go back to the home screen
        Intent intent= new Intent(this, LeaderboardClickedUserActivity.class);
        intent.putExtra("Logged in username", username);
        intent.putExtra("Clicked username", clickedUser.getUsername());
        intent.putExtra("Logged in password", password);
        intent.putExtra("User score", score);
        //intent.putExtra("Number of logged tasks", numTasks);
        intent.putParcelableArrayListExtra("List of all tasks", lt);

        startActivity(intent);
    }
}
