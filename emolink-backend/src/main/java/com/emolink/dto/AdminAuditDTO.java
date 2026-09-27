package com.emolink.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class AdminAuditDTO {

    @NotNull(message = "status: 状态不能为空")
    @Min(value = 1, message = "status: 状态值不合法")
    @Max(value = 2, message = "status: 状态值不合法")
    private Integer status;
}
