package com.antoine.dotlove.models;

import java.util.ArrayList;
import java.util.Date;

public class Chat {
    private Date createdAt;
    private String maleUser;
    private String maleDisplayName;
    private String femaleUser;
    private String femaleDisplayName;
    private ArrayList<Message> messages;
    private String id;

    public Chat(String id, Date createdAt, String maleUser, String maleDisplayName, String femaleUser, String femaleDisplayName, ArrayList<Message> messages) {
        this.id = id;
        this.createdAt = createdAt;
        this.maleUser = maleUser;
        this.maleDisplayName = maleDisplayName;
        this.femaleUser = femaleUser;
        this.femaleDisplayName = femaleDisplayName;
        this.messages = messages;
    }

    public Chat() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaleDisplayName() {
        return maleDisplayName;
    }

    public void setMaleDisplayName(String maleDisplayName) {
        this.maleDisplayName = maleDisplayName;
    }

    public String getFemaleDisplayName() {
        return femaleDisplayName;
    }

    public void setFemaleDisplayName(String femaleDisplayName) {
        this.femaleDisplayName = femaleDisplayName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getMaleUser() {
        return maleUser;
    }

    public void setMaleUser(String maleUser) {
        this.maleUser = maleUser;
    }

    public String getFemaleUser() {
        return femaleUser;
    }

    public void setFemaleUser(String femaleUser) {
        this.femaleUser = femaleUser;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public Message getMessage(int position) {
        return messages.get(position);
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
