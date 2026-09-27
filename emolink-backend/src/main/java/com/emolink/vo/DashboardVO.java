package com.emolink.vo;

import lombok.Data;

import java.util.List;

@Data
public class DashboardVO {

    private TodayEmotionVO todayEmotion;
    private List<WeeklyTrendPointVO> weeklyTrend;
    private List<DashboardArticleVO> recommendArticles;
    private List<DashboardHotPostVO> hotPosts;
    private Integer totalDiaryCount;
    private Integer consecutiveDays;

    @Data
    public static class TodayEmotionVO {
        private String dominantEmotion;
        private Integer diaryCount;
        private Double avgLevel;
    }

    @Data
    public static class WeeklyTrendPointVO {
        private String date;
        private Double avgLevel;
    }

    @Data
    public static class DashboardArticleVO {
        private Long id;
        private String title;
        private String category;
    }

    @Data
    public static class DashboardHotPostVO {
        private Long id;
        private String anonymousName;
        private String content;
        private Integer likeCount;
        private Integer commentCount;
    }
}
