package edu.uncc.chatapplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rujut on 4/20/2017.
 */

public class Trip {
    String tripName, tripLocation, imageURL, createdBy, tripID;
    ArrayList<String> memberList;

    public Trip(String tripID, String tripName, String tripLocation, String imageURL, String createdBy, ArrayList<String> memberList) {
        this.tripID = tripID;
        this.tripName = tripName;
        this.tripLocation = tripLocation;
        this.imageURL = imageURL;
        this.createdBy = createdBy;
        this.memberList = memberList;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ArrayList<String> getMemberList() {
        return memberList;
    }

    public void setMemberList(ArrayList<String> memberList) {
        this.memberList = memberList;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("tripID", tripID);
        result.put("tripName", tripName);
        result.put("tripLocation", tripLocation);
        result.put("imageURL", imageURL);
        result.put("createdBy", createdBy);
        result.put("memberList", memberList);
        return result;
    }
}
