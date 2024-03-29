package com.example.truckup;

public class Post {

    private String id;
    private String userId;
    private String imageUrl;
    private String title;
    private String description;
    private double weight;
    private double volume;
    private String packageType;
    private int packageQuantity;
    private int beltQuantity;

    public Post(String id,String userId ,String imageUrl, String title, String description, double weight, double volume, String packageType, int packageQuantity, int beltQuantity) {
        this.id = id;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.weight = weight;
        this.volume = volume;
        this.packageType = packageType;
        this.packageQuantity = packageQuantity;
        this.beltQuantity = beltQuantity;
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

    public double getWeight() {
        return weight;
    }

    public double getVolume() {
        return volume;
    }

    public String getPackageType() {
        return packageType;
    }

    public int getPackageQuantity() {
        return packageQuantity;
    }

    public int getBeltQuantity() {
        return beltQuantity;
    }
}