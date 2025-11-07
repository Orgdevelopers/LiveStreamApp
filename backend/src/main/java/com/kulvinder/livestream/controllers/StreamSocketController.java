package com.kulvinder.livestream.controllers;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

import com.kulvinder.livestream.domain.models.dtos.ChatDto;
import com.kulvinder.livestream.domain.models.dtos.LiveStreamDto;
import com.kulvinder.livestream.domain.models.dtos.UserDto;
import com.kulvinder.livestream.domain.models.entities.LiveStreamEntity;
import com.kulvinder.livestream.domain.models.entities.UserEntity;
import com.kulvinder.livestream.domain.services.LivestreamServices;
import com.kulvinder.livestream.domain.services.StreamBroadcastService;
import com.kulvinder.livestream.domain.services.UserServices;
import com.kulvinder.livestream.mappers.Mapper;

@CrossOrigin("*")
@Controller
public class StreamSocketController {

    // utils
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    // mappers
    @Autowired
    private Mapper<LiveStreamEntity, LiveStreamDto> liveStreamMapper;
    @Autowired
    private Mapper<UserEntity, UserDto> userMapper;

    // services
    @Autowired
    private LivestreamServices livestreamServices;
    @Autowired
    private StreamBroadcastService streamBroadcastService;
    @Autowired
    private UserServices userServices;

    // msg mapping for live chat
    @MessageMapping("/liveStreams/{id}/chat")
    public void HandleLiveChat(@DestinationVariable Long id, ChatDto sentDto) {
        Optional<LiveStreamEntity> stream = livestreamServices.findById(id);
        Optional<UserEntity> sender = userServices.findById(sentDto.getSender().getId());
        if (stream.isPresent() && sender.isPresent()) {
            ChatDto chat = ChatDto.builder()
                        .id(id)
                        .liveStream(liveStreamMapper.MapTo(stream.get()))
                        .sender(userMapper.MapTo(sender.get()))
                        .msg(sentDto.getMsg())
                        .created(LocalDateTime.now())
                        .build();

            streamBroadcastService.broadcastChatMessage(id, chat);

        }else{
            System.out.println(stream.isPresent()+"          "+ sender.isPresent());
        }

    }

}
