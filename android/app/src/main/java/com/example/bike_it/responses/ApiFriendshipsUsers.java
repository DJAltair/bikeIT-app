package com.example.bike_it.responses;

public class ApiFriendshipsUsers {
    int id;
    String name = "RedSkittleFox";
    String username = "redskittlefox";
    String description;

    public enum ApiFriendshipsStatus {
        requested,
        friend,
        none
    }

    public ApiFriendshipsStatus status = ApiFriendshipsStatus.none;

    public ApiFriendshipsUsers() { }

    public ApiFriendshipsUsers(String name, String username) {
        this.id = 0;
        this.name = name;
        this.username = username;
        this.description = "";
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getDescription() { return description; }
}
