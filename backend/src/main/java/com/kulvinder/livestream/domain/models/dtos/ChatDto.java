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
public class ChatDto {

    private Long id;

    private LiveStreamDto liveStream;

    private GiftDto gift;

    private Boolean isGift;

    private UserDto sender;

    private String msg;

    private LocalDateTime created;

}
