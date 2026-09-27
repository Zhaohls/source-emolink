package com.emolink.websocket;

import com.emolink.config.AppCorsProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;
    private final WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;
    private final JwtHandshakeHandler jwtHandshakeHandler;
    private final AppCorsProperties appCorsProperties;

    public WebSocketConfig(WebSocketAuthInterceptor webSocketAuthInterceptor,
                           WebSocketHandshakeInterceptor webSocketHandshakeInterceptor,
                           JwtHandshakeHandler jwtHandshakeHandler,
                           AppCorsProperties appCorsProperties) {
        this.webSocketAuthInterceptor = webSocketAuthInterceptor;
        this.webSocketHandshakeInterceptor = webSocketHandshakeInterceptor;
        this.jwtHandshakeHandler = jwtHandshakeHandler;
        this.appCorsProperties = appCorsProperties;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(appCorsProperties.getAllowedOriginPatterns())
                .setHandshakeHandler(jwtHandshakeHandler)
                .addInterceptors(webSocketHandshakeInterceptor);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthInterceptor);
    }
}
