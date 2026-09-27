package com.emolink.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("emotion_report")
public class EmotionReport {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String reportType;
    private LocalDate reportDate;
    private String summary;
    private String aiAnalysis;
    private String emotionDataJson;
    private LocalDateTime createdAt;
}

