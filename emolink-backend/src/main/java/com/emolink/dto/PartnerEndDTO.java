package com.emolink.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PartnerEndDTO {

    @NotNull(message = "匹配ID不能为空")
    private Long matchId;
}

