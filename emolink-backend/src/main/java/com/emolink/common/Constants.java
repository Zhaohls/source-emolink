package com.emolink.common;

import java.util.List;

public final class Constants {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String CURRENT_USER_ID = "currentUserId";
    public static final String DEFAULT_AVATAR = "/avatars/default.png";
    public static final String DEFAULT_SUCCESS_MESSAGE = "success";
    public static final String STATUS_WAITING = "waiting";
    public static final String STATUS_MATCHED = "matched";
    public static final String STATUS_ENDED = "ended";
    public static final String STATUS_NONE = "none";
    public static final String REPORT_TYPE_DAILY = "daily";
    public static final String REPORT_TYPE_WEEKLY = "weekly";
    public static final List<String> PUBLIC_APIS = List.of(
            "/api/user/register",
            "/api/user/login"
    );

    private Constants() {
    }
}

