package com.emolink.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tree_hole_post")
public class TreeHolePost {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String anonymousName;
    private String content;
    private Integer likeCount;
    private Integer commentCount;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

