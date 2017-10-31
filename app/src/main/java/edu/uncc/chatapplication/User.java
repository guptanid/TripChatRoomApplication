package edu.uncc.chatapplication;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by NidhiGupta on 4/16/2017.
 */

public class User implements Serializable {
    String firstName, lastName, email, gender, imageUrl, userId;

    ArrayList<String> friendIdList;

    public ArrayList<String> getFriendIdList() {
        return friendIdList;
    }

    public void setFriendIdList(ArrayList<String> friendIdList) {
        this.friendIdList = friendIdList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("firstName", firstName);
        result.put("lastName", lastName);
        result.put("email", email);
        result.put("gender", gender);
        result.put("imageUrl", imageUrl);
      //  result.put("FriendsList",friendIdList);
        return result;
    }
}
