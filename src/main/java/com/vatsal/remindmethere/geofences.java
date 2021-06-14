package com.vatsal.remindmethere;

public class geofences {

    String id;
   // LatLng Latlng;
    String location;
     int radius;
     int toggle;

    public geofences() {
    }

    public geofences(String id, String location, int radius, int  toggle) {
        this.id = id;
       // this.Lat = Lat;
      //  this.Lng = Lat;
        this.location = location;
        this.radius = radius;
        this.toggle = toggle;
    }

    public geofences(String location, int radius, int toggle) {
        this.location = location;
        this.radius = radius;
        this.toggle = toggle;
    }

    public String getID() {
        return this.id;
    }

    public void setID(String id) {
        this.id = id;
    }

//    public LatLng getLatlng() {
//        return this.Latlng;
//    }
//
//    public void setLatlng(LatLng Latlng) {
//        this.Latlng = Latlng;
//    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getRadius() {
        return this.radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getToggle() {
        return this.toggle;
    }

    public void setToggle(int toggle) {
        this.toggle = toggle;

    }
}