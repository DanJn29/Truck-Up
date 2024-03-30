package com.example.truckup;

public class Post {

    private String id;
    private String userId;
    private String imageUrl;
    private String title;
    private String description;
    private int beltQuantity;
    private int packageQuantity;
    private String packageType;
    private double volume;
    private double weight;

    // Default constructor is needed for Firebase
    public Post() {}

    public Post(String id, String userId, String imageUrl, String title, String description, int beltQuantity, int packageQuantity, String packageType, double volume, double weight) {
        this.id = id;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.beltQuantity = beltQuantity;
        this.packageQuantity = packageQuantity;
        this.packageType = packageType;
        this.volume = volume;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getBeltQuantity() {
        return beltQuantity;
    }

    public int getPackageQuantity() {
        return packageQuantity;
    }

    public String getPackageType() {
        return packageType;
    }

    public double getVolume() {
        return volume;
    }
}