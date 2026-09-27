package com.emolink.websocket;

import com.emolink.util.JwtUtil;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
public class JwtHandshakeHandler extends DefaultHandshakeHandler {

    private final JwtUtil jwtUtil;

    public JwtHandshakeHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        Object tokenObject = attributes.get("token");
        if (tokenObject == null) {
            return super.determineUser(request, wsHandler, attributes);
        }
        String token = String.valueOf(tokenObject);
        if (!jwtUtil.validateToken(token)) {
            return super.determineUser(request, wsHandler, attributes);
        }
        return new StompPrincipal(String.valueOf(jwtUtil.getUserIdFromToken(token)));
    }
}

