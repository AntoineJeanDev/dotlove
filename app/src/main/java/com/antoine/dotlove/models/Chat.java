package com.antoine.dotlove.models;

import java.util.ArrayList;
import java.util.Date;

public class Chat {
    private Date createdAt;
    private String maleUser;
    private String femaleUser;
    private ArrayList<Message> messages;

    public Chat() {
    }

    public Chat(Date createdAt, String maleUser, String femaleUser, ArrayList<Message> messages) {
        this.createdAt = createdAt;
        this.maleUser = maleUser;
        this.femaleUser = femaleUser;
        this.messages = messages;
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

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
