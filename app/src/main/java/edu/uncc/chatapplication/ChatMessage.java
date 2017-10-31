package edu.uncc.chatapplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rujut on 4/21/2017.
 */

public class ChatMessage {
    private String messageText, imageURL;
    private String messageUser;
    private String messageTime, messageID;


    public ChatMessage(String messageText, String imageURL, String messageUser, String messageTime, String messageID) {
        this.messageText = messageText;
        this.imageURL = imageURL;
        this.messageUser = messageUser;
        this.messageTime = messageTime;
        this.messageID = messageID;
    }

    public ChatMessage() {
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("messageText", messageText);
        result.put("imageURL", imageURL);
        result.put("messageUser", messageUser);
        result.put("messageTime", messageTime);
        return result;
    }
}
