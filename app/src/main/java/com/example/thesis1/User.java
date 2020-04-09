package com.example.thesis1;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

class User implements Serializable, Comparable<User> {
    private String username;
    private String password;
    private int score;
    int numTasks;
    private ArrayList<Integer> loggedTasks;

    public User(){

    }

    public User(String username, String password, int score, ArrayList<Integer> loggedTasks){
        this.username = username;
        this.password = password;
        this.score = score;
        this.loggedTasks = loggedTasks;
    }

    public User(String username, String password, int score){
        this.username = username;
        this.password = password;
        this.score = score;
    }

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public User(String username, Integer score){
        this.username = username;
        this.score = score;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public int getScore(){
        return score;
    }

    public void setScore(int score){
        this.score = score;
    }

    public ArrayList<Integer> getLoggedTasks(){
        return loggedTasks;
    }

    public void setLoggedTasks(ArrayList<Integer> loggedTasks){
        this.loggedTasks = loggedTasks;
    }

    public int getNumTasks(){
        return numTasks;
    }

    public void setNumTasks(int numTasks){
        this.numTasks = numTasks;
    }

    @Override
    public int compareTo(User other){
        //return new User().compareTo(other.getScore());
        if(this.getScore() > other.getScore()){
            return 1;
        }else if(this.getScore() == other.getScore()){
            return 0;
        }
        return -1;
    }
}
