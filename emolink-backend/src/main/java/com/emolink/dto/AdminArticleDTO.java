package com.emolink.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Data
public class AdminArticleDTO {

    @Size(max = 200, message = "title: 标题长度不能超过200个字符")
    private String title;

    private String content;

    @Size(max = 20, message = "category: 分类长度不能超过20个字符")
    private String category;

    @Min(value = 0, message = "status: 状态值不合法")
    @Max(value = 1, message = "status: 状态值不合法")
    private Integer status;
}
