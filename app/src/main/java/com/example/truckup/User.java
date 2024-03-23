package com.example.truckup;

public class User {
    public String username;
    private String profileImageUrl;


    public User() {
    }

    public User(String username) {
        this.username = username;
        this.profileImageUrl = profileImageUrl;

    }

    public String getUsername() {
        return username;
    }
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}
