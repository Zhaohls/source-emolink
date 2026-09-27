package com.emolink.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeStatusVO {

    private Boolean isLiked;
    private Integer likeCount;
}

