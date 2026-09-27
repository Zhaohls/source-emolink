package com.emolink.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmotionDistributionVO {

    private String emotionType;
    private Long count;
    private Integer percentage;
}

