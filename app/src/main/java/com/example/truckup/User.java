package com.example.truckup;

public class User {
    public String username;
    private String profileImageUrl;
    private String phoneNumber;


    public User() {
    }

    public User(String username,String phoneNumber) {
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.phoneNumber = phoneNumber;

    }

    public String getUsername() {
        return username;
    }
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
