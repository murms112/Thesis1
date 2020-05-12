package com.example.thesis1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class LogTaskActivity extends AppCompatActivity {
    ArrayList<SustainableTask> lt = new ArrayList<>();
    private RecyclerView recyclerView;
    private AddTaskAdapter adapter;
    private static User loggedInUser;
    private static int numTasks = 0;
    //researched making activity static here https://stackoverflow.com/questions/16402844/call-finish-from-static-method/16402875
    public static Activity activity = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_task);

        //gets the intent that started this activity and extracts the data
        Intent intent = getIntent();
        lt = intent.getParcelableArrayListExtra("List of all tasks");
        loggedInUser = (User) intent.getSerializableExtra("Logged in user");
        recyclerView = findViewById(R.id.allTaskRecyclerView);
        createAddTaskAdapter();
        activity = this;
        getUserTasks();
    }

    //pulls list of user tasks to calculate how many there are
    private void getUserTasks(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference newRef = database.getReference("userDatabase/users/"+loggedInUser.getUsername()+"/loggedTasks");
        //clears whatever is stored as of now to start with a fresh pull from firebase
        if(loggedInUser.getLoggedTasks()!=null){
            loggedInUser.getLoggedTasks().clear();
            numTasks = 0;
        }
        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot secondSnapshot) {
                for (DataSnapshot dataValue : secondSnapshot.getChildren()){
                    int taskIndex = dataValue.child("taskIndex").getValue(Integer.class);
                    loggedInUser.getLoggedTasks().add(taskIndex);
                    numTasks++;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void createAddTaskAdapter(){
        adapter = new AddTaskAdapter(this, lt);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(LogTaskActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    //adds the new task in a new index of the array in the database depending on how many there currently are
    static void addTaskToDatabase(int index, String clickedTaskIndex){
        //the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //the reference
        DatabaseReference myRef = database.getReference("userDatabase/users/"+ loggedInUser.getUsername()+"/loggedTasks");
        myRef.child(String.valueOf(numTasks)).child("taskIndex").setValue(index);

        //finishes the activity
        activity.finish();

    }
}
