package com.example.thesis1;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

public class LogTaskActivity extends AppCompatActivity {
    ArrayList<SustainableTask> lt = new ArrayList<>();
    private RecyclerView recyclerView;
    private AddTaskAdapter adapter;
    private static User loggedInUser;
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
    }

    private void createAddTaskAdapter(){
        adapter = new AddTaskAdapter(this, lt);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(LogTaskActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    static void addTaskToDatabase(int index, String clickedTaskIndex){
        //the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //the reference
        DatabaseReference myRef = database.getReference("userDatabase/users/"+ loggedInUser.getUsername()+"/loggedTasks");

        myRef.child(clickedTaskIndex).child("taskIndex").setValue(index);
        //myRef.child(username).child("password").setValue(password);
        //myRef.child(username).child("score").setValue(score);

    }
}
