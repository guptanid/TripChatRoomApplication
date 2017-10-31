package edu.uncc.chatapplication;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rujut on 4/20/2017.
 */

public class Trip {
    String tripName, tripLocation, imageURL, createdByID, createdByName, tripID;
    ArrayList<String> memberList;
//    ArrayList<MyPlaces> placesList;

    public Trip(String tripID, String tripName, String tripLocation, String imageURL, String createdByID, String createdByName, ArrayList<String> memberList) {
        this.tripID = tripID;
        this.tripName = tripName;
        this.tripLocation = tripLocation;
        this.imageURL = imageURL;
        this.createdByID = createdByID;
        this.createdByName = createdByName;
        this.memberList = memberList;
//        this.placesList = placesList;
    }

    public Trip() {
    }

    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getTripLocation() {
        return tripLocation;
    }

    public void setTripLocation(String tripLocation) {
        this.tripLocation = tripLocation;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCreatedByID() {
        return createdByID;
    }

    public void setCreatedByID(String createdByID) {
        this.createdByID = createdByID;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public ArrayList<String> getMemberList() {
        return memberList;
    }

    public void setMemberList(ArrayList<String> memberList) {
        this.memberList = memberList;
    }

//    public ArrayList<MyPlaces> getPlacesList() {
//        return placesList;
//    }
//
//    public void setPlacesList(ArrayList<MyPlaces> placesList) {
//        this.placesList = placesList;
//    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("tripID", tripID);
        result.put("tripName", tripName);
        result.put("tripLocation", tripLocation);
        result.put("imageURL", imageURL);
        result.put("createdByID", createdByID);
        result.put("createdByName", createdByName);
        result.put("memberList", memberList);
//        result.put("placesList", placesList);
        return result;
    }
}
