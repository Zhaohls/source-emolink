package com.emolink.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminArticleVO {

    private Long id;
    private String title;
    private String content;
    private String category;
    private Integer viewCount;
    private Integer status;
    private LocalDateTime createdAt;
}
