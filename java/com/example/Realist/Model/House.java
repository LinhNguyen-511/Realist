package com.example.dttapp.Model;
import android.location.Location;
import android.util.Log;

import java.io.Serializable;

public class House implements Serializable{
    private String id, image, price, description, numBed, numBath, size, zip, city, latitude,
            longitude, createdDate, distance;

    private double myLatitude;
    private double myLongitude;

    private static final String TAG = "House";
    public House() {

    }

    public void setImage (String image) {
        this.image = image;
    }
    public void setPrice (String price) {
        this.price = price;
    }
    public void setZip (String zip) {
        this.zip = zip;
    }
    public void setCity (String city) {
        this.city = city;
    }
    public void setDescription (String description) {
        this.description = description;
    }
    public void setLatitude (String latitude) {
        this.latitude = latitude;
    }
    public void setLongitude (String longitude) {
        this.longitude = longitude;
    }
    public void setCreatedDate (String createdDate) {
        this.createdDate = createdDate;
    }
    public void setNumBed (String numBed) {
        this.numBed = numBed;
    }
    public void setNumBath (String numBath) {
        this.numBath = numBath;
    }
    public void setSize (String size) {
        this.size = size;
    }
    public void setDistance(Location myLocation) {
        myLatitude = myLocation.getLatitude();
        myLongitude = myLocation.getLongitude();
        Location location = new Location("");
        location.setLatitude(Double.parseDouble(latitude));
        location.setLongitude(Double.parseDouble(longitude));
        float distanceInMeters = myLocation.distanceTo(location);
        float distanceInKms = distanceInMeters / 1000;
        this.distance = distanceInKms + " km";
        Log.d(TAG, "House: set distance");
    }


    public String getImage() {
        String url = "https://intern.docker-dev.d-tt.nl" + image;
        return url;
    }
    public String getPrice() {
        return price;
    }
    public String getZip() {
        return zip;
    }
    public String getCity() {
        return city;
    }
    public String getDescription() {
        return description;
    }
    public String getLatitude() {
        return latitude;
    }
    public String getLongitude() {
        return longitude;
    }
    public String getCreatedDate() {
        return createdDate;
    }
    public String getNumBed () {
        return numBed;
    }
    public String getNumBath () {
        return numBath;
    }
    public String getSize () {
        return size;
    }
    public String getDistance () {
        return distance;
    }
    public double getMyLat() {
        return myLatitude;
    }
    public double getMyLong() {
        return myLongitude;
    }
    public String getAddress() {
        return city + zip;
    }
}
