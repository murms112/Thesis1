package com.example.thesis1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeScreenActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    //the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //the reference
    DatabaseReference myRef = database.getReference("userDatabase/users");
    String username;
    String password;
    String tempName;
    Integer taskIndex;
    ArrayList<Integer> lti = new ArrayList<>();
    ArrayList<SustainableTask> lt = new ArrayList<>();
    ArrayList<SustainableTask> taskList = new ArrayList<>();
    String taskTitle;
    Integer scoreValue;
    User loggedInUser;
    Integer tempIndex;
    Integer numTasks;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        //gets the intent that started this activity and extracts the data
        Intent intent = getIntent();
        username = (String) intent.getSerializableExtra("Logged in username");
        password = (String) intent.getSerializableExtra("Logged in password");
        int score = (int) intent.getSerializableExtra("User score");
        numTasks = (int) intent.getSerializableExtra("Number of logged tasks");

        loggedInUser = new User(username, password, score, numTasks);

        getTasksFromDatabase();

        recyclerView = findViewById(R.id.taskRecyclerView);

        final Button addTaskBtn = findViewById(R.id.addTaskBtn);

        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               moveToLogTaskActivity();
            }
        });
    }
    private void moveToLogTaskActivity(){
        Intent intent = new Intent (this, LogTaskActivity.class);
        //add master list of tasks to intent, probably have to implement serializable
        intent.putParcelableArrayListExtra("List of all tasks", lt);
        intent.putExtra("Logged in user", loggedInUser);
        startActivity(intent);
    }
    private void createCustomAdapter(){
        adapter = new CustomAdapter(this, taskList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HomeScreenActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
    private void setUpViewableTasks(){
        if(loggedInUser.getLoggedTasks()!= null){
            for(int num: loggedInUser.getLoggedTasks()){
                SustainableTask temp = lt.get(num);
                this.taskList.add(temp);
            }
        }else{
            SustainableTask temp = new SustainableTask("No tasks yet :(", 0);
            this.taskList.add(temp);
        }
        createCustomAdapter();
    }
    private void getTasksFromDatabase(){
            DatabaseReference taskRef = database.getReference("userDatabase/sustainableTasks/");
            taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot taskSnapshot) {
                    for (DataSnapshot dataValue : taskSnapshot.getChildren()){
                        scoreValue = dataValue.child("scoreValue").getValue(Integer.class);
                        taskTitle = dataValue.child("taskTitle").getValue(String.class);
                        SustainableTask t = new SustainableTask(taskTitle, scoreValue);
                        lt.add(t);
                    }
                    for(SustainableTask t : lt){
                        System.out.println("THERE'S A LOGGED TASK WITH TITLE: " + t.getTitle());
                    }
                    getUserTasks();
                    //getNumUserTasks();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });

    }
    private void getUserTasks(){
        DatabaseReference newRef = database.getReference("userDatabase/users/"+username+"/loggedTasks");
        tempName = username;
        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot secondSnapshot) {
                for (DataSnapshot dataValue : secondSnapshot.getChildren()){
                    taskIndex = dataValue.child("taskIndex").getValue(Integer.class);
                    lti.add(taskIndex);
                }
                //set actual user's logged tasks
                loggedInUser.setLoggedTasks(lti);
                for(int t : lti){
                    System.out.println("THERE'S A LOGGED TASK OF INDEX: " + t);
                }
                setUpViewableTasks();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    //i think this whole thing should happen at the very beginning - still working on this
    private void getNumUserTasks(){
        DatabaseReference secondRef = database.getReference("userDatabase/users/"+username);
        secondRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot numTaskSnapshot) {
                for (DataSnapshot dataValue : numTaskSnapshot.getChildren()){
                    numTasks = dataValue.child("amountOfTasks").getValue(Integer.class);
                }
                //set actual user's logged tasks
                loggedInUser.setNumTasks(numTasks);
                System.out.println("You have " + numTasks + " logged tasks right nooooowwwww");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    //called when task is added
    private int calculateUserScore(int usrScore, int taskScore){
        usrScore = usrScore + taskScore;
        return usrScore;
    }
}
