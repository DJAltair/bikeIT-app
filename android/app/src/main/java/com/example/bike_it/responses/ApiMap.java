package com.example.bike_it.responses;

public class ApiMap {
    int id = 0;
    String name;

    public ApiMap(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }

    public String getName() { return name; }
}
