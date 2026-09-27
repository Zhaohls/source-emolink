package com.emolink.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmotionDiaryVO {

    private Long id;
    private String emotionType;
    private Integer emotionLevel;
    private String triggerEvent;
    private String feeling;
    private String aiSuggestion;
    private LocalDateTime createdAt;
}

