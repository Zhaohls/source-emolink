package com.emolink.controller;

import com.emolink.common.Constants;
import com.emolink.exception.BusinessException;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseController {

    protected Long currentUserId(HttpServletRequest request) {
        Object value = request.getAttribute(Constants.CURRENT_USER_ID);
        if (value instanceof Long userId) {
            return userId;
        }
        throw new BusinessException(401, "未登录或登录已过期");
    }
}

