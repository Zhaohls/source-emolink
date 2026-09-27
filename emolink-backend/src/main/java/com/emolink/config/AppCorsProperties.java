package com.emolink.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class AppCorsProperties {

    private final String[] allowedOriginPatterns;
    private final String uploadDir;

    public AppCorsProperties(@Value("${app.cors.allowed-origin:http://localhost:18012,http://127.0.0.1:18012}") String allowedOrigin,
                             @Value("${app.upload.dir:uploads}") String uploadDir) {
        this.allowedOriginPatterns = Arrays.stream(allowedOrigin.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toArray(String[]::new);
        this.uploadDir = uploadDir;
    }

    public String[] getAllowedOriginPatterns() {
        return Arrays.copyOf(allowedOriginPatterns, allowedOriginPatterns.length);
    }

    public String getUploadDir() {
        return uploadDir;
    }
}
