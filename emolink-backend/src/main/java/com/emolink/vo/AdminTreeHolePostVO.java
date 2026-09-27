package com.emolink.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminTreeHolePostVO {

    private Long id;
    private Long userId;
    private String username;
    private String anonymousName;
    private String content;
    private Integer likeCount;
    private Integer commentCount;
    private Integer status;
    private LocalDateTime createdAt;
}
