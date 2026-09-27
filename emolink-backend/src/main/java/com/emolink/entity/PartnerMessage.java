package com.emolink.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("partner_message")
public class PartnerMessage {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long matchId;
    private Long senderId;
    private String content;
    private Integer isRead;
    private LocalDateTime createdAt;
}

