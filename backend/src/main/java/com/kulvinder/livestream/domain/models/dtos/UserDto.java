package com.kulvinder.livestream.domain.models.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class UserDto {

    private Long id;

    private String username;

    @JsonIgnore
    private String password;
    
    private String profile_pic;

    private Integer coins;

    @JsonIgnore
    private LocalDateTime updated;
    
    @JsonIgnore
    private LocalDateTime created;

}
