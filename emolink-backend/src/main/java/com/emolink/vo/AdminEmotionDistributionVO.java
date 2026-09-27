package com.emolink.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminEmotionDistributionVO {

    private String emotionType;
    private Long count;
}
