package com.emolink.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyTrendVO {

    private String date;
    private Double avgLevel;
    private String dominantEmotion;
}

