package com.example.capstonedesign.view;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Postinfo implements Parcelable {
    private String title;
    private ArrayList<String> contents;
    private String publisher;
    //private String photoUri;
    private Date createAt;
    private List<Double> latitude;
    private List<Double> longitude;

    public Postinfo() {};

    public Postinfo(String title, ArrayList<String> contents, String publisher, Date createAt){
        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
        this.createAt = createAt;
    }

    public Postinfo(String title, ArrayList<String> contents, String publisher, Date createAt,List<Double> latitude, List<Double> longitude){
        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
        this.createAt = createAt;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Postinfo(String title, ArrayList<String> contents, String publisher, String photoUri, Date createAt, List<Double> latitude, List<Double> longitude){
        this.title = title;
        this.contents = contents;
        this.publisher = publisher;
        //this.photoUri = photoUri;
        this.createAt = createAt;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected Postinfo(Parcel in) {
        title = in.readString();
        publisher = in.readString();
       // photoUri = in.readString();
    }

    public static final Creator<Postinfo> CREATOR = new Creator<Postinfo>() {
        @Override
        public Postinfo createFromParcel(Parcel in) {
            return new Postinfo(in);
        }

        @Override
        public Postinfo[] newArray(int size) {
            return new Postinfo[size];
        }
    };

    public String getTitle(){return this.title;}
    public void setTitle(String title) {this.title = title;}

    public ArrayList<String> getContents(){return this.contents;}
    public void setContents(ArrayList<String> contents) {this.contents = contents;}

    public String getPublisher(){return this.publisher;}
    public void setPublisher(String publisher) {this.publisher = publisher;}

   /* public String getPhotoUri(){return this.photoUri;}
    public void setPhotoUri(String photoUri) {this.photoUri = photoUri;}*/

    public Date getCreateAt(){return this.createAt;}
    public void setCreateAt(Date createAt) {this.createAt = createAt;}

    public List getLatitude(){return this.latitude;}
    public void setLatitude(List<Double> latitude){this.latitude = latitude;}

    public List getLongitude(){return this.longitude;}
    public void setLongitude(List<Double> longitude){this.longitude = longitude;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(publisher);
    }
}
