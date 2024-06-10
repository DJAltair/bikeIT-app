package com.bikeit.restendpoint.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Map {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;

    @Lob
    private String imageBase64;

    private String points;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public Map() {}
    public Map(String imageBase64, String points, User user) {
        this.imageBase64 = imageBase64;
        this.points = points;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }
}
