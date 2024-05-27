package com.bikeit.restendpoint.model.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendRequestDto {
    private Long senderId;
    private Long receiverId;
}