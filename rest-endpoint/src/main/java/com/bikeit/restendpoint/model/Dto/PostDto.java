package com.bikeit.restendpoint.model.Dto;

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

    public PostDto(Long id, String title, String content, LocalDateTime createdAt, String username) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.username = username;
    }
}