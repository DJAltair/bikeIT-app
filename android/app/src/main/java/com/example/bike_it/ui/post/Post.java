package com.example.bike_it.ui.post;

import android.graphics.Bitmap;

import com.example.bike_it.responses.ApiPostsResponse;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import java.util.Base64;

public class Post {

    private int id;

    private String title;
    private String text;
    private String date;
    private String author;

    private Bitmap image;

    public Post(ApiPostsResponse postsResponse)
    {
        this(postsResponse.getTitle(), postsResponse.getContent(), postsResponse.getCreatedAt(), postsResponse.getUsername(), postsResponse.getImage(), postsResponse.getId());
    }

    public Post(String title, String text, String date, String author, String image, int id) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.date = date;
        this.author = author;

        if(image != null)
        {
            try
            {
                byte[] decodedBytes = Base64.getDecoder().decode(image);
                this.image = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            }
            catch(Exception e)
            {
                this.image = null;
            }
        }
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() { return title; }

    public Bitmap getImage() { return image; }

    public int getId() { return id; }
}
