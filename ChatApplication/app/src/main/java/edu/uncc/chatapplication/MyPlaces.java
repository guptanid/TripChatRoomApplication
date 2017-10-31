package edu.uncc.chatapplication;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rujut on 4/28/2017.
 */

public class MyPlaces implements Serializable{
    private String name, address, ID;
    private double latitude, longitude;

    public MyPlaces(String name, String address, String ID, double latitide, double longitude) {
        this.name = name;
        this.address = address;
        this.ID = ID;
        this.latitude = latitide;
        this.longitude = longitude;
    }

    public MyPlaces() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("ID", ID);
        result.put("name", name);
        result.put("address", address);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        return result;
    }
}
