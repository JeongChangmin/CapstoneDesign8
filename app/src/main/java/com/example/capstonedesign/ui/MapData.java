package com.example.capstonedesign.ui;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MapData implements Parcelable {

    ArrayList<Double> Latitude;
    ArrayList<Double> Longitude;

    protected MapData(Parcel in) {
        Latitude = (ArrayList<Double>) in.readSerializable();
        Longitude = (ArrayList<Double>) in.readSerializable();
    }

    public MapData(ArrayList<Double> Latitude, ArrayList<Double> Longitude) {
        this.Latitude = Latitude;
        this.Longitude = Longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(Latitude);
        parcel.writeSerializable(Longitude);
    }

    public ArrayList getLatitude() {
        return this.Latitude;
    }

    public ArrayList getLongitude() {
        return this.Longitude;
    }

    public static final Creator<MapData> CREATOR = new Creator<MapData>() {
        @Override
        public MapData createFromParcel(Parcel in) {
            return new MapData(in);
        }

        @Override
        public MapData[] newArray(int size) {
            return new MapData[size];
        }
    };
}
