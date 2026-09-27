package com.emolink.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUserVO {

    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String email;
    private Integer gender;
    private String role;
    private Integer status;
    private Long diaryCount;
    private LocalDateTime createdAt;
}
