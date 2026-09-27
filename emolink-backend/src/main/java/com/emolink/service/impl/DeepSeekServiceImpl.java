package com.emolink.service.impl;

import com.emolink.exception.BusinessException;
import com.emolink.service.DeepSeekService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeepSeekServiceImpl implements DeepSeekService {

    private final RestTemplate restTemplate;

    @Value("${deepseek.api.key:}")
    private String apiKey;

    @Value("${deepseek.api.url}")
    private String apiUrl;

    @Value("${deepseek.api.model}")
    private String model;

    public DeepSeekServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getEmotionAdvice(String emotionType, int level, String event, String feeling) {
        String prompt = String.format(
                "你是一名专业的心理咨询师，擅长帮助大学生管理情绪。用户当前情绪为【%s】，强度为%d/5。%n" +
                        "触发事件：%s%n内心感受：%s%n%n请给出温暖、积极、可操作的情绪调节建议，200字以内，语气自然。",
                getEmotionLabel(emotionType), level, defaultText(event), defaultText(feeling)
        );
        return callDeepSeek(prompt);
    }

    @Override
    public String getWeeklyAnalysis(String weeklyDataJson) {
        String prompt = String.format(
                "你是一名心理健康分析师。以下是一名大学生一周的情绪记录数据（JSON）：%n%s%n%n" +
                        "请分析：1. 本周整体情绪状态和波动趋势；2. 主要情绪触发因素；3. 下周建议。控制在300字以内，语气温暖积极。",
                weeklyDataJson
        );
        return callDeepSeek(prompt);
    }

    private String callDeepSeek(String prompt) {
        if (StringUtils.isBlank(apiKey)) {
            throw new BusinessException(500, "DeepSeek API Key 未配置，请检查 application.yml 的 deepseek.api.key");
        }

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", List.of(
                    Map.of("role", "system", "content", "你是一位温暖可靠的心理健康助手。"),
                    Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("stream", false);
            requestBody.put("max_tokens", 500);
            requestBody.put("temperature", 0.7);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, new HttpEntity<>(requestBody, headers), Map.class);
            Map body = response.getBody();
            if (body == null) {
                throw new BusinessException(502, "DeepSeek 服务返回为空");
            }
            Object choicesObject = body.get("choices");
            if (!(choicesObject instanceof List<?> choices) || choices.isEmpty()) {
                throw new BusinessException(502, "DeepSeek 服务返回格式异常：choices 为空");
            }
            Object firstChoice = choices.get(0);
            if (!(firstChoice instanceof Map<?, ?> choiceMap)) {
                throw new BusinessException(502, "DeepSeek 服务返回格式异常：choice 无效");
            }
            Object messageObject = choiceMap.get("message");
            if (!(messageObject instanceof Map<?, ?> messageMap)) {
                throw new BusinessException(502, "DeepSeek 服务返回格式异常：message 无效");
            }
            Object content = messageMap.get("content");
            String answer = content == null ? "" : String.valueOf(content).trim();
            if (StringUtils.isBlank(answer)) {
                throw new BusinessException(502, "DeepSeek 服务返回内容为空");
            }
            return answer;
        } catch (BusinessException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new BusinessException(502, "DeepSeek 服务调用失败，请检查 API Key 或网络连接");
        } catch (Exception exception) {
            throw new BusinessException(502, "DeepSeek 服务处理失败：" + exception.getMessage());
        }
    }

    private String getEmotionLabel(String type) {
        return switch (type) {
            case "happy" -> "开心";
            case "calm" -> "平静";
            case "anxious" -> "焦虑";
            case "sad" -> "悲伤";
            case "angry" -> "愤怒";
            case "fearful" -> "恐惧";
            default -> "复杂";
        };
    }

    private String defaultText(String text) {
        return StringUtils.defaultIfBlank(text, "未填写");
    }
}
