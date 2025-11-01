package com.kulvinder.livestream.domain.models.dtos;

import java.time.LocalDateTime;

import com.kulvinder.livestream.domain.models.entities.UserEntity;

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

    private UserEntity user;

    private String banner;

    private Boolean active;

    private LocalDateTime updated;

    private LocalDateTime created;
    
}
