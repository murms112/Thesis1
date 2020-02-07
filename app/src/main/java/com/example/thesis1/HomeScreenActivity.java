package com.example.thesis1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    String relativeTaskIndex;
    Integer actualTaskIndex;
    ArrayList<Integer> lti = new ArrayList<>();
    ArrayList<SustainableTask> lt = new ArrayList<>();
    ArrayList<SustainableTask> taskList = new ArrayList<>();
    String taskTitle;
    Integer scoreValue;
    static User loggedInUser;
    Integer tempIndex;
    Integer numTasks;
    int currNumTasks = 0;
    boolean firstRun = true;
    static int currScore;
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
        TextView scoreTextView = findViewById(R.id.scoreText);
        String scoreStr = "Score: " + score;
        scoreTextView.setText(scoreStr);

        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               moveToLogTaskActivity();
            }
        });


    }
    protected void onResume(){
        super.onResume();
        if(!firstRun){
            getUserTasks();
        }
    }
    private void moveToLogTaskActivity(){
        Intent intent = new Intent (this, LogTaskActivity.class);
        //add master list of tasks to intent, probably have to implement serializable
        intent.putParcelableArrayListExtra("List of all tasks", lt);
        intent.putExtra("Logged in user", loggedInUser);
        //intent.putParcelableArrayListExtra("Users logged tasks", loggedInUser.getLoggedTasks());
        startActivity(intent);
    }
    private void createCustomAdapter(){
        adapter = new CustomAdapter(this, taskList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HomeScreenActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
    private void setUpViewableTasks(){
        taskList.clear();
        if(loggedInUser.getLoggedTasks()!= null){
            //clears the already stored score before calculating it based on what tasks have been logged
            loggedInUser.setScore(0);
            if(lt.size() > 0) {
                for (int num : loggedInUser.getLoggedTasks()) {
                    SustainableTask temp = lt.get(num);
                    addUserScore(loggedInUser.getScore(), temp.getScoreValue());
                    this.taskList.add(temp);
                }
            }
        }else{
            SustainableTask temp = new SustainableTask("No tasks yet :(", 0);
            this.taskList.add(temp);
        }
        //it can't find this textview
        TextView scoreTextView = findViewById(R.id.scoreText);
        String scoreStr = "Score: " + loggedInUser.getScore();
        scoreTextView.setText(scoreStr);
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
                    firstRun = false;
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
        DatabaseReference newRef = database.getReference("userDatabase/users/"+loggedInUser.getUsername()+"/loggedTasks");
        if(lti!=null){
            lti.clear();
        }
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
                System.out.println("You have " + numTasks + " logged tasks right now");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    //called when task is added
    private void addUserScore(int usrScore, int taskScore){
        usrScore = usrScore + taskScore;
        loggedInUser.setScore(usrScore);
        saveScoreToDatabase(loggedInUser, loggedInUser.getScore());


    }
    private static void subtractUserScore(int usrScore, int taskScore){
        usrScore = usrScore - taskScore;
        loggedInUser.setScore(usrScore);
        saveScoreToDatabase(loggedInUser, loggedInUser.getScore());
    }

    static void saveScoreToDatabase(User loggedInUser, int usrScore){
        //the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //the reference
        DatabaseReference myRef = database.getReference("userDatabase/users/"+ loggedInUser.getUsername()+"/score");

        myRef.setValue(usrScore);

    }
    void reassignTaskIndexes(final int deletedIndex){
        final DatabaseReference newRef = database.getReference("userDatabase/users/"+loggedInUser.getUsername()+"/loggedTasks");
        final ArrayList<Integer> relativeTaskIndexes = new ArrayList<>();
        final ArrayList<Integer> actualTaskIndexes = new ArrayList<>();
        //final int x;
        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot secondSnapshot) {
                for (DataSnapshot dataValue : secondSnapshot.getChildren()){
                    //if (currNumTasks != deletedIndex) {
                        relativeTaskIndex = dataValue.child(String.valueOf(currNumTasks)).getKey();
                        actualTaskIndex = dataValue.child("taskIndex").getValue(Integer.class);
                        actualTaskIndexes.add(actualTaskIndex);

                        final int x = Integer.valueOf(relativeTaskIndex);
                        if(currNumTasks > deletedIndex){
                            relativeTaskIndexes.add(x-1);
                        }
                        else{
                            relativeTaskIndexes.add(x);
                        }
                        currNumTasks++;
                    //}
                    //currNumTasks++;
                }
                //save the new indexes to database
                for(int i: relativeTaskIndexes){
                    //TODO: save the indexes properly
                    newRef.child(String.valueOf(i)).child("taskIndex").setValue(actualTaskIndexes.get(i));
                }
                final DatabaseReference deleteRef = database.getReference("userDatabase/users/"+loggedInUser.getUsername()+"/loggedTasks/" +relativeTaskIndexes.size());
                deleteRef.removeValue();
                currNumTasks = 0;

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
    void deleteTaskFromDatabase(int index, int taskScore){
        //the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //the reference
        DatabaseReference myRef = database.getReference("userDatabase/users/"+ loggedInUser.getUsername()+"/loggedTasks/"+index);
        //TODO: this is where you would do the index changing, needs to go through and move all indexes up one

        //DatabaseReference indexRef = database.getReference("userDatabase/users/"+ loggedInUser.getUsername()+"/loggedTasks/");

        subtractUserScore(loggedInUser.getScore(), taskScore);

        //deletes the task at the appropriate index
        myRef.removeValue();

        reassignTaskIndexes(index);

        //recreates the activity
        Intent intent= new Intent(HomeScreenActivity.this, HomeScreenActivity.class);
        intent.putExtra("Logged in username", loggedInUser.getUsername());
        intent.putExtra("Logged in password", loggedInUser.getPassword());
        intent.putExtra("User score", loggedInUser.getScore());
        intent.putExtra("Number of logged tasks", loggedInUser.getNumTasks());

        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }
}
