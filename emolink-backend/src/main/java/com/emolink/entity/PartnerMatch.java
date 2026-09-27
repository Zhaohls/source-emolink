package com.emolink.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("partner_match")
public class PartnerMatch {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userAId;
    private Long userBId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime matchedAt;
    private LocalDateTime endedAt;
}

