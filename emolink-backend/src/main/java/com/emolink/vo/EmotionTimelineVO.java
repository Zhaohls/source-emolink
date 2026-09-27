package com.emolink.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmotionTimelineVO {

    private String time;
    private String emotionType;
    private Integer emotionLevel;
}

