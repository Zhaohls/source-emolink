package com.emolink.websocket;

import com.emolink.common.Constants;
import com.emolink.util.JwtUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    public WebSocketAuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = resolveToken(accessor);
            if (token == null || !jwtUtil.validateToken(token)) {
                throw new IllegalArgumentException("WebSocket Token无效或已过期");
            }
            accessor.setUser(new StompPrincipal(String.valueOf(jwtUtil.getUserIdFromToken(token))));
        }
        return message;
    }

    private String resolveToken(StompHeaderAccessor accessor) {
        List<String> authHeaders = accessor.getNativeHeader(Constants.AUTHORIZATION);
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String value = authHeaders.get(0);
            if (value != null && value.startsWith(Constants.BEARER_PREFIX)) {
                return value.substring(Constants.BEARER_PREFIX.length());
            }
            return value;
        }
        List<String> tokenHeaders = accessor.getNativeHeader("token");
        if (tokenHeaders != null && !tokenHeaders.isEmpty()) {
            return tokenHeaders.get(0);
        }
        Object token = accessor.getSessionAttributes() == null ? null : accessor.getSessionAttributes().get("token");
        return token == null ? null : String.valueOf(token);
    }
}

