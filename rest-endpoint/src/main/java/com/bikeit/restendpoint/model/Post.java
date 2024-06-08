package com.bikeit.restendpoint.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String content;

    @Lob
    private String imageBase64;

    private PrivacyStatus postPrivacy;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public Post() {}

    public Post(String title, String content, User user, PrivacyStatus postPrivacy, String imageBase64) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.postPrivacy = postPrivacy;
        this.createdAt = LocalDateTime.now();
        this.imageBase64 = imageBase64;
    }
}
