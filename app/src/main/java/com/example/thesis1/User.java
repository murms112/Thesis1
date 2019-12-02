package com.example.thesis1;

import java.io.Serializable;
import java.util.ArrayList;

class User implements Serializable {
    private String username;
    private String password;
    private int score;
    int numTasks;
    private ArrayList<Integer> loggedTasks;

    public User(){

    }

    public User(String username, String password, int score, ArrayList<Integer> loggedTasks, int numTasks){
        this.username = username;
        this.password = password;
        this.score = score;
        this.loggedTasks = loggedTasks;
        this.numTasks = numTasks;
    }

    public User(String username, String password, int score, int numTasks){
        this.username = username;
        this.password = password;
        this.score = score;
        this.numTasks = numTasks;
    }

    public User(String username, String password){
        this.username = username;
        this.password = password;
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

}
