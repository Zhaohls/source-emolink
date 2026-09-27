package com.emolink.interceptor;

import com.emolink.common.Constants;
import com.emolink.common.Result;
import com.emolink.entity.User;
import com.emolink.mapper.UserMapper;
import com.emolink.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;

    public JwtInterceptor(JwtUtil jwtUtil, ObjectMapper objectMapper, UserMapper userMapper) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        this.userMapper = userMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String header = request.getHeader(Constants.AUTHORIZATION);
        if (header == null || !header.startsWith(Constants.BEARER_PREFIX)) {
            writeUnauthorized(response, "未登录或Token缺失");
            return false;
        }

        String token = header.substring(Constants.BEARER_PREFIX.length());
        if (!jwtUtil.validateToken(token)) {
            writeUnauthorized(response, "Token无效或已过期");
            return false;
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userMapper.selectById(userId);
        if (user == null) {
            writeUnauthorized(response, "用户不存在或登录已失效");
            return false;
        }
        if (!Integer.valueOf(1).equals(user.getStatus())) {
            writeForbidden(response, "账号已被禁用");
            return false;
        }
        String requestUri = request.getRequestURI();
        if ((requestUri.startsWith("/api/admin/") || "/api/admin".equals(requestUri))
                && !"admin".equals(user.getRole())) {
            writeForbidden(response, "无权访问，仅管理员可操作");
            return false;
        }

        request.setAttribute(Constants.CURRENT_USER_ID, userId);
        return true;
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, message)));
    }

    private void writeForbidden(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(403, message)));
    }
}
