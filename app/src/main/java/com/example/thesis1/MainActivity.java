package com.example.thesis1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    //the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //the reference
    DatabaseReference myRef = database.getReference("userDatabase/users");
    String testUsername = "firstUsername";
    User loggedInUser;
    String action;
    String un;
    String pw;
    Integer sc;
    Integer numTasks;
    Integer taskScoreValue;
    String taskTitle;
    boolean userTaken = false;
    boolean userExists = false;
    final List<User> usersList = new ArrayList<>();
    List<Integer> lt = new ArrayList<>();
    String tempName;
    int taskIndex;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataValues : dataSnapshot.getChildren()) {
                    //eventually make sure none of these are null - still working on this
                    un = dataValues.child("username").getValue(String.class);
                    pw = dataValues.child("password").getValue(String.class);
                    sc = dataValues.child("score").getValue(Integer.class);
                    numTasks = dataValues.child("amountOfTasks").getValue(Integer.class);

                    //what happens to num Tasks after this point???
                    User user = new User(un, pw, sc, numTasks);
                    usersList.add(user);
                }
                //getUserTasks();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        //sets up the sign in and new user buttons
        final Button newUserBtn = findViewById(R.id.newUserBtn);
        newUserBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                action = "newUser";
                checkIfEmptyFields(action);
            }
        });

        final Button signInBtn = findViewById(R.id.signInBtn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                action = "signIn";
                checkIfEmptyFields(action);
            }
        });

    }
    private void matchLoggedTasks(String tempName, ArrayList<Integer> lt){
        for(User u : usersList){
            if(u.getUsername().equals(tempName)){
                u.setLoggedTasks(lt);
            }
        }
    }

    private void createUser(){ //still working on this
        //add checking if username already exists
            //writeNewUser(loggedInUser.getUsername(), loggedInUser.getPassword());

    }

    private void checkIfEmptyFields(String action){
        EditText userText = findViewById(R.id.enterUsername);
        String username = userText.getText().toString();
        EditText passwordText = findViewById(R.id.enterPassword);
        String password = passwordText.getText().toString();
        User user = new User(username, password);
        loggedInUser = user;
        userTaken = false;
        userExists = false;

        if(username.equals("") || password.equals("")){
            Toast.makeText(this, "Oops! Looks like you left a field empty.", Toast.LENGTH_SHORT).show();
        }
        else{
            if(action.equals("newUser")){
                usernameTaken();
                if(userTaken){
                    Toast.makeText(this, "This username is taken!", Toast.LENGTH_SHORT).show();
                }
                else{
                    int score = 0;
                    writeNewUser(username, password, score);
                }
            }else if(action.equals("signIn")){
                checkIfUserExists();
                if(userExists){
                    signUserIn(loggedInUser);
                }
                else{
                    Toast.makeText(this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
    private void checkIfUserExists(){
        for (User currUser : usersList){
            if(currUser.getUsername().equals(loggedInUser.getUsername()) && (currUser.getPassword() .equals(loggedInUser.getPassword()))){
                userExists = true;
                loggedInUser.setScore(currUser.getScore());
            }
        }
    }

    private void usernameTaken(){
        for (User currUser : usersList){
            if(currUser.getUsername().equals(loggedInUser.getUsername())){
                userTaken = true;
            }
        }
    }

    private void signUserIn(User user){
        //rhis opens a new activity
        Intent intent = new Intent (this, HomeScreenActivity.class);
        intent.putExtra("Logged in username", user.getUsername());
        intent.putExtra("Logged in password", user.getPassword());
        intent.putExtra("User score", user.getScore());
        intent.putExtra("Number of logged tasks", user.getNumTasks());

        startActivity(intent);
    }

    //still working on this
    /*private List<User> retrieveUsers(){
        //i think this is in the wrong place
        final List<User> usersList = new ArrayList<User>();
        final FirebaseDatabase database1 = FirebaseDatabase.getInstance();
        // Get a reference to our posts
        DatabaseReference ref = database1.getReference("userDatabase");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataValues : dataSnapshot.getChildren()){
                    User user = dataValues.getValue(User.class);
                    usersList.add(user);
                    System.out.println(user.getUsername());
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        return usersList;
    }*/

    //method that tests writing process
    private void writeNewUserTest(){
        User user = new User(testUsername, "user");
        //writeNewUser( user.getUsername(), user.getPassword());
        /*TextView userText = findViewById(R.id.firstText);
        userText.setText(user.getUsername() + " was saved to the database");*/

        User user2 = new User("second", "user");
        //writeNewUser(user2.getUsername(), user2.getPassword());
        /*TextView userText2 = findViewById(R.id.secondText);
        userText2.setText(user2.getUsername() + " was saved to the database");*/

        User user3 = new User("third", "user");
        //writeNewUser(user3.getUsername(), user3.getPassword());
        /*TextView userText3 = findViewById(R.id.thirdText);
        userText3.setText(user3.getUsername() + " was saved to the database");*/
    }
    //method that writes to the database
    private void writeNewUser(String username, String password, int score) {
        myRef.child(username).child("username").setValue(username);
        myRef.child(username).child("password").setValue(password);
        myRef.child(username).child("score").setValue(score);
        myRef.child(username).child("amountOfTasks").setValue(0);
        //fix this
        myRef.child(username).child("loggedTasks").child("0").child("taskIndex").setValue(0);
        User signIn = new User(username, password, score, 0);
        signUserIn(signIn);
    }

    //still working on this
    private void updateUserTest(){
        testUsername = "updated username";
        //writeNewUser(testUsername, "user");
        /*TextView userText1 = findViewById(R.id.thirdText);
        userText1.setText(testUsername);*/
    }

    //researched deletion here: https://stackoverflow.com/questions/37390864/how-to-delete-from-firebase-realtime-database
    //still working on this
    private void deleteUser(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("userDatabase");

        ref.child("users").child("userid1").removeValue();
        /*TextView deletedText = findViewById(R.id.secondText);
        deletedText.setText("this text was deleted");*/
    }

    //part of API POC
    private void getAPIDataTest(){
        GetNRELDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetNRELDataService.class);
        Call<NRELData> call = service.getTestDataBySearch("Zi1fjKapaju8ji3dpqjnPGOayUMTTyzcs9djNaDx", "1617+Cole+Blvd+Golden+CO", "ELEC", 1);
        call.enqueue(new Callback<NRELData>() {
            @Override
            public void onResponse(Call<NRELData> call, Response<NRELData> response) {
                //progressDialog.dismiss();
                if(response.body().getFuel_stations() == null ){
                    Toast.makeText(MainActivity.this, "No fuel stations found at this location :( try again!", Toast.LENGTH_LONG).show();
                }
                else{
                    String city = response.body().getFuel_stations()[0].getCity();
                    /*TextView dataText = findViewById(R.id.APIDataTextView);
                    dataText.setText("City of alternative fuel from API: " + city); */
                }
            }

            @Override
            public void onFailure(Call<NRELData> call, Throwable t) {
                t.printStackTrace();
                //progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Something went wrong with data!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

