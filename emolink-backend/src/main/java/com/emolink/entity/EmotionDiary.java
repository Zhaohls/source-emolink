package com.emolink.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("emotion_diary")
public class EmotionDiary {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String emotionType;
    private Integer emotionLevel;
    private String triggerEvent;
    private String feeling;
    private String aiSuggestion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

