package com.emolink.websocket;

import com.emolink.vo.MessageVO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PartnerSocketNotifier {

    private final SimpMessagingTemplate messagingTemplate;

    public PartnerSocketNotifier(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendMessage(Long userId, MessageVO messageVO) {
        messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/queue/partner/message", messageVO);
    }

    public void sendMatchNotification(Long userId, String type, Long matchId, String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", type);
        payload.put("matchId", matchId);
        payload.put("message", message);
        messagingTemplate.convertAndSendToUser(String.valueOf(userId), "/queue/partner/match", payload);
    }
}
