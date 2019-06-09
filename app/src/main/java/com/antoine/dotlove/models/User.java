package com.antoine.dotlove.models;

import java.util.ArrayList;

public class User {
    private String displayName;
    private String photoUrl;
    private String gender;
    private String uid;
    private ArrayList<String> techLanguages = new ArrayList<>();

    public User() {
    }


    public User(String displayName, String photoUrl, String gender, String uid, ArrayList<String> techLanguages) {
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.gender = gender;
        this.uid = uid;
        this.techLanguages = techLanguages;
    }

    public ArrayList<String> getTechLanguages() {
        return techLanguages;
    }

    public void setTechLanguages(ArrayList<String> techLanguages) {
        this.techLanguages = techLanguages;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
