package com.emolink.vo;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DailyReportVO {

    private LocalDate date;
    private Integer totalCount;
    private List<EmotionDistributionVO> emotionDistribution;
    private List<EmotionTimelineVO> emotionTimeline;
}

