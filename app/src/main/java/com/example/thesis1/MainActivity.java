package com.example.thesis1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
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
    String salt;
    Integer numTasks;
    Integer taskScoreValue;
    String taskTitle;
    static String hexString = "e04fd020ea3a6910a2d808002b30309d";
    boolean userTaken = false;
    boolean userExists = false;
    final List<User> usersList = new ArrayList<>();
    List<Integer> lt = new ArrayList<>();
    String tempName;
    int taskIndex;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //provider code from here https://www.bswen.com/2019/11/android-How-to-solve-java.security.NoSuchProviderException-no-such-provider-SUN.html
        Provider[] secureRandomProviders = Security.getProviders("SecureRandom.SHA1PRNG");
        System.setProperty("com.warrenstrange.googleauth.rng.algorithmProvider",secureRandomProviders[0].getName());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataValues : dataSnapshot.getChildren()) {
                    un = dataValues.child("username").getValue(String.class);
                    pw = dataValues.child("password").getValue(String.class);
                    sc = dataValues.child("score").getValue(Integer.class);

                    User user;
                    if(sc != null){
                        user = new User(un, pw, sc);
                    }
                    else{
                        user = new User(un, pw);
                    }

                    usersList.add(user);
                }
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
    private void checkIfEmptyFields(String action) {
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
            // if curr user get password = salted version of logged in user's password
            if(currUser.getUsername().equals(loggedInUser.getUsername()) && checkIfCorrectPassword(loggedInUser.getPassword(), currUser.getPassword())){
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

        startActivity(intent);
    }

    //method that writes to the database
    private void writeNewUser(String username, String password, int score) {

        byte[] salt = getSalt();

        String securePassword = getSecurePassword(password, salt);

        //these lines handle saving the other user data to firebase
        myRef.child(username).child("username").setValue(username);
        //changed this back to save regular password for demo video
        myRef.child(username).child("password").setValue(securePassword);
        myRef.child(username).child("score").setValue(score);

        User signIn = new User(username, password, score);
        signUserIn(signIn);
    }

    //all salt/secure password code from here https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
    private static byte[] getSalt() {
        byte[] salt = hexStringToByteArray(hexString);
        return salt;
    }
    //researched this approach here: https://stackoverflow.com/questions/11208479/how-do-i-initialize-a-byte-array-in-java
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    private boolean checkIfCorrectPassword(String enteredPassword, String correctPassword){
        //pull down user's password from database
        //if that matches the entered password plus the hex salt, log them in
        byte[] tempSalt = hexStringToByteArray(hexString);
        if(getSecurePassword(enteredPassword, tempSalt).equals(correctPassword)){
            return true;
        }
        return false;
    }
    private static String getSecurePassword(String passwordToHash, byte[] salt)
    {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(salt);
            //Get the hash's bytes
            byte[] bytes = md.digest(passwordToHash.getBytes());
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }


}

