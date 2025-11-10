package com.kulvinder.livestream.domain.models.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveStreamDto {
    private Long id;

    private UserDto user;

    private String banner;


    private Boolean active;

    private LocalDateTime updated;
    
    private Integer viewerCount;

    private LocalDateTime created;
    
}
