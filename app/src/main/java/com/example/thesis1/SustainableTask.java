package com.example.thesis1;

import android.os.Parcel;
import android.os.Parcelable;

//parcelable information from here https://developer.android.com/reference/android/os/Parcelable
class SustainableTask implements Parcelable {

    private String title;
    private int scoreValue;

    public SustainableTask(Parcel in){
        this.title = in.readString();
        this.scoreValue = in.readInt();

    }

    public SustainableTask(String title, int scoreValue){
        this.title = title;
        this.scoreValue = scoreValue;
    }
    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public int getScoreValue(){
        return scoreValue;
    }

    public void setScoreValue(int scoreValue){
        this.scoreValue = scoreValue;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeInt(scoreValue);
    }

    private void readFromParcel(Parcel in) {
        in.writeInt(scoreValue);
        in.writeString(title);
    }

    public static final Parcelable.Creator<SustainableTask> CREATOR
            = new Parcelable.Creator<SustainableTask>() {
        public SustainableTask createFromParcel(Parcel in) {
            return new SustainableTask(in);
        }

        public SustainableTask[] newArray(int size) {
            return new SustainableTask[size];
        }
    };
}
