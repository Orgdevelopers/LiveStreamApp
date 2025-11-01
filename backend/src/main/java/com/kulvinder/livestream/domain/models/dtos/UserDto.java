package com.kulvinder.livestream.domain.models.dtos;

import java.time.LocalDateTime;

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
    private String password;
    private String profile_pic;

    private LocalDateTime updated;
    private LocalDateTime created;

}
