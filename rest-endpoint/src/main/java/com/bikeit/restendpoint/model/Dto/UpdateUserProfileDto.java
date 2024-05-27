package com.bikeit.restendpoint.model.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserProfileDto {
    private String name;
    private String description;
}
