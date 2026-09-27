package com.emolink.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageVO {

    private Long id;
    private Long matchId;
    private Long senderId;
    private Boolean isSelf;
    private String content;
    private LocalDateTime createdAt;
}

