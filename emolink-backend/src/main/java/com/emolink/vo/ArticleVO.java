package com.emolink.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleVO {

    private Long id;
    private String title;
    private String content;
    private String category;
    private Integer viewCount;
    private Boolean isFavorited;
    private LocalDateTime createdAt;
}
