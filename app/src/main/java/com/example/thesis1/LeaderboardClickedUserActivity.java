package com.example.thesis1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class LeaderboardClickedUserActivity extends AppCompatActivity {
    String username;
    String clickedUsername;
    String password;
    int score;
    int numTasks;
    private RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    int taskIndex;
    ArrayList<Integer> lti = new ArrayList<>();
    User currUser;
    private LeaderboardClickedUserAdapter adapter;
    ArrayList<SustainableTask> taskList = new ArrayList<>();
    ArrayList<SustainableTask> lt = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard_clicked_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //extract the intent data
        Intent intent = getIntent();
        username = (String) intent.getSerializableExtra("Logged in username");
        clickedUsername = (String) intent.getSerializableExtra("Clicked username");
        password = (String) intent.getSerializableExtra("Logged in password");
        score = (int) intent.getSerializableExtra("User score");
        lt = intent.getParcelableArrayListExtra("List of all tasks");

        currUser = new User(username, password, score);

        recyclerView = findViewById(R.id.leaderboardClickedUserRecyclerView);

        TextView usernameTextView = findViewById(R.id.leaderboardClickedUserLabel);
        usernameTextView.setText(clickedUsername);

        getLoggedTasksFromDatabase();
    }

    private void getLoggedTasksFromDatabase(){
        DatabaseReference userRef = database.getReference("userDatabase/users/"+clickedUsername+"/loggedTasks");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot secondSnapshot) {
                for (DataSnapshot dataValue : secondSnapshot.getChildren()){
                    taskIndex = dataValue.child("taskIndex").getValue(Integer.class);
                    lti.add(taskIndex);
                }
                //set actual user's logged tasks
                currUser.setLoggedTasks(lti);
                getTasksFromIndexes();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void getTasksFromIndexes(){
        taskList.clear();
        if(currUser.getLoggedTasks()!= null){
            if(lt.size() > 0) {
                for (int num : currUser.getLoggedTasks()) {
                    SustainableTask temp = lt.get(num);
                    this.taskList.add(temp);
                }
            }
        }else{
            SustainableTask temp = new SustainableTask("No tasks yet :(", 0);
            this.taskList.add(temp);
        }
        createClickedUserScreenAdapter();
    }

    private void createClickedUserScreenAdapter(){
        adapter = new LeaderboardClickedUserAdapter(this, taskList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(LeaderboardClickedUserActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), LeaderboardActivity.class);
        myIntent.putExtra("Logged in username", username);
        myIntent.putExtra("Logged in password", password);
        myIntent.putExtra("User score", score);
        myIntent.putParcelableArrayListExtra("List of all tasks", lt);

        startActivity(myIntent);
        return true;
    }
}
