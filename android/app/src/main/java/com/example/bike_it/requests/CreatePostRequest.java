package com.example.bike_it.requests;

public class CreatePostRequest {
    public String title;
    public String content;
    public String postPrivacy = "PUBLIC";
    public String imageBase64;

    public CreatePostRequest(String title, String content, String imageBase64)
    {
        this.title = title;
        this.content = content;
        this.imageBase64 = imageBase64;
    }

}
