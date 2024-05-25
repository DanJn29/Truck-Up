package com.example.truckup;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

public class Post implements Serializable {

    private String username;

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
    private String unit;
    private String date;
    private boolean isFavorite;
    private String unloadingDate;
    private String loadingLocation;
    private String unloadingLocation;

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
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public int getBeltQuantity() {
        return beltQuantity;
    }
    public void setBeltQuantity(int beltQuantity) {
        this.beltQuantity = beltQuantity;
    }

    public int getPackageQuantity() {
        return packageQuantity;
    }
    public void setPackageQuantity(int packageQuantity) {
        this.packageQuantity = packageQuantity;
    }

    public String getPackageType() {
        return packageType;
    }
    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public double getVolume() {
        return volume;
    }
    public void setVolume(double volume) {
        this.volume = volume;
    }
    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public boolean isFavorite() {
        return isFavorite;
    }
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
    public String getUnloadingDate() {
        return unloadingDate;
    }
    public void setUnloadingDate(String unloadingDate) {
        this.unloadingDate = unloadingDate;
    }
    public String getLoadingLocation() {
        return loadingLocation;
    }
    public void setLoadingLocation(String loadingLocation) {
        this.loadingLocation = loadingLocation;
    }
    public String getUnloadingLocation() {
        return unloadingLocation;
    }
    public void setUnloadingLocation(String unloadingLocation) {
        this.unloadingLocation = unloadingLocation;
    }
    public String getLoadingLocationAddress(Context context) {
        String[] coordinates = loadingLocation.split(",");
        double latitude = Double.parseDouble(coordinates[0]);
        double longitude = Double.parseDouble(coordinates[1]);

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && !addresses.isEmpty()) {
            Address address = addresses.get(0);
            return address.getAddressLine(0);
        }

        return null;
    }

    public String getUnLoadingLocationAddress(Context context) {
        String[] coordinates = unloadingLocation.split(",");
        double latitude = Double.parseDouble(coordinates[0]);
        double longitude = Double.parseDouble(coordinates[1]);

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && !addresses.isEmpty()) {
            Address address = addresses.get(0);
            return address.getAddressLine(0);
        }

        return null;
    }


}