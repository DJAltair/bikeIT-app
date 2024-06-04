package com.example.bike_it.ui.post;

public class Post {
    private String text;
    private String date;
    private String author;

    public Post(String text, String date, String author) {
        this.text = text;
        this.date = date;
        this.author = author;
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
}
