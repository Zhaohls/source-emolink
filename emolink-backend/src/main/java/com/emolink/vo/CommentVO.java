package com.emolink.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentVO {

    private Long id;
    private String anonymousName;
    private String content;
    private Integer status;
    private LocalDateTime createdAt;
}
