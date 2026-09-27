package com.emolink.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class MessageDTO {

    @NotNull(message = "匹配ID不能为空")
    private Long matchId;

    @NotBlank(message = "消息内容不能为空")
    @Size(max = 1000, message = "消息内容长度不能超过1000")
    private String content;
}

