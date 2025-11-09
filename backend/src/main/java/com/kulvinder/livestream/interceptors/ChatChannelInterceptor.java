package com.kulvinder.livestream.interceptors;

import com.kulvinder.livestream.domain.models.dtos.WebSocketChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ChatChannelInterceptor implements ChannelInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ChatChannelInterceptor.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, String> sessionStreamMap = new ConcurrentHashMap<>();
    private final Pattern chatPattern = Pattern.compile("/liveStreams/(\w+)/chat");


    @Autowired
    public ChatChannelInterceptor(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();

        switch (accessor.getCommand()) {
            case SUBSCRIBE:
                handleSubscription(accessor, sessionId);
                break;
            case DISCONNECT:
                handleDisconnect(sessionId);
                break;
            default:
                break;
        }
        return message;
    }

    private void handleSubscription(StompHeaderAccessor accessor, String sessionId) {
        String destination = accessor.getDestination();
        if (destination != null) {
            Matcher matcher = chatPattern.matcher(destination);
            if (matcher.matches()) {
                String streamId = matcher.group(1);
                sessionStreamMap.put(sessionId, streamId);
                logger.info("User {} subscribed to stream {}", sessionId, streamId);

                WebSocketChatMessage chatMessage = new WebSocketChatMessage("JOIN", sessionId, "User joined");
                messagingTemplate.convertAndSend(destination, chatMessage);
            }
        }
    }

    private void handleDisconnect(String sessionId) {
        String streamId = sessionStreamMap.remove(sessionId);
        if (streamId != null) {
            logger.info("User {} disconnected from stream {}", sessionId, streamId);
            String destination = String.format("/liveStreams/%s/chat", streamId);

            WebSocketChatMessage chatMessage = new WebSocketChatMessage("LEAVE", sessionId, "User left");
            messagingTemplate.convertAndSend(destination, chatMessage);
        }
    }
}
