package com.emolink.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class PostDTO {

    @NotBlank(message = "帖子内容不能为空")
    @Size(max = 2000, message = "帖子内容长度不能超过2000")
    private String content;
}

