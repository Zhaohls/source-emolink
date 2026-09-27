package com.emolink.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchStatusVO {

    private String status;
    private Long matchId;
    private String partnerAnonymousName;
    private LocalDateTime matchedAt;
}

