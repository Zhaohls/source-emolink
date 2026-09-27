package com.emolink.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminTreeHoleCommentVO {

    private Long id;
    private Long postId;
    private Long userId;
    private String username;
    private String anonymousName;
    private String content;
    private Integer status;
    private LocalDateTime createdAt;
}
