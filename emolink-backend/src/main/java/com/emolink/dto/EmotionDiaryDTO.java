package com.emolink.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class EmotionDiaryDTO {

    @NotBlank(message = "情绪类型不能为空")
    private String emotionType;

    @Min(value = 1, message = "情绪强度最小为1")
    @Max(value = 5, message = "情绪强度最大为5")
    private Integer emotionLevel;

    @Size(max = 500, message = "触发事件长度不能超过500")
    private String triggerEvent;

    @NotBlank(message = "内心感受不能为空")
    private String feeling;
}

