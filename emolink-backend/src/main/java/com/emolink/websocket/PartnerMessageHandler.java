package com.emolink.websocket;

import com.emolink.dto.MessageDTO;
import com.emolink.service.PartnerService;
import com.emolink.util.JwtUtil;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.security.Principal;

import javax.validation.Valid;

@Validated
@Controller
public class PartnerMessageHandler {

    private final PartnerService partnerService;
    private final JwtUtil jwtUtil;

    public PartnerMessageHandler(PartnerService partnerService, JwtUtil jwtUtil) {
        this.partnerService = partnerService;
        this.jwtUtil = jwtUtil;
    }

    @MessageMapping("/partner/send")
    public void handleMessage(Principal principal, SimpMessageHeaderAccessor headerAccessor, @Valid @Payload MessageDTO messageDTO) {
        Long senderId = resolveSenderId(principal, headerAccessor);
        partnerService.sendMessage(senderId, messageDTO);
    }

    private Long resolveSenderId(Principal principal, SimpMessageHeaderAccessor headerAccessor) {
        if (principal != null) {
            return Long.parseLong(principal.getName());
        }
        if (headerAccessor.getSessionAttributes() != null) {
            Object tokenObject = headerAccessor.getSessionAttributes().get("token");
            if (tokenObject != null) {
                String token = String.valueOf(tokenObject);
                if (jwtUtil.validateToken(token)) {
                    return jwtUtil.getUserIdFromToken(token);
                }
            }
        }
        throw new IllegalArgumentException("WebSocket用户身份缺失");
    }
}
