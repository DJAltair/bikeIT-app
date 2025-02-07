package com.bikeit.restendpoint.model.Dto;

import com.bikeit.restendpoint.model.PrivacyStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String username;
    private PrivacyStatus postPrivacy;
    private String imageBase64;

    public PostDto(Long id, String title, String content, LocalDateTime createdAt, String username, PrivacyStatus postPrivacy, String imageBase64) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.username = username;
        this.postPrivacy = postPrivacy;
        this.imageBase64 = imageBase64;
    }
}