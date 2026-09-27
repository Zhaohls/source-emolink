package com.emolink.vo;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class WeeklyReportVO {

    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalCount;
    private List<EmotionDistributionVO> emotionDistribution;
    private List<DailyTrendVO> dailyTrend;
    private String aiAnalysis;
}

