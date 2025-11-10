package com.kulvinder.livestream.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.kulvinder.livestream.domain.models.dtos.ChatDto;
import com.kulvinder.livestream.domain.models.dtos.LiveStreamDto;
import com.kulvinder.livestream.domain.models.entities.LiveStreamEntity;
import com.kulvinder.livestream.mappers.Mapper;

@Service
public class StreamBroadcastService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private Mapper<LiveStreamEntity, LiveStreamDto> mapper;

    public void broadcastStreamUpdate(LiveStreamEntity entity) {
        broadcastStreamUpdate(mapper.MapTo(entity));
    }

    public void broadcastStreamUpdate(LiveStreamDto dto) {
        messagingTemplate.convertAndSend("/updates/streams", dto);
    }

    public void broadcastChatMessage(Long streamId, ChatDto msg) {
        messagingTemplate.convertAndSend("/liveStreams/" + streamId + "/chat", msg);
    }

    public <T> void convertAndSend(String destination, T payload){
        messagingTemplate.convertAndSend(destination, payload);
    }
}
