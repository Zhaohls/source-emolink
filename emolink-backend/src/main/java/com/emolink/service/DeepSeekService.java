package com.emolink.service;

public interface DeepSeekService {

    String getEmotionAdvice(String emotionType, int level, String event, String feeling);

    String getWeeklyAnalysis(String weeklyDataJson);
}

