package com.example.bike_it.requests;

public class MapRequest {
    public String imageBase64;
    public String points;

    public MapRequest(String imageBase64, String points)
    {
        this.imageBase64 = imageBase64;
        this.points = points;
    }
}
