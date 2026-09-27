package com.emolink.vo;

import lombok.Data;

import java.util.List;

@Data
public class AdminDashboardVO {

    private Long totalUsers;
    private Long todayNewUsers;
    private Long totalDiaries;
    private Long todayDiaries;
    private Long totalArticles;
    private Long totalPosts;
    private Long pendingPosts;
    private Long totalComments;
    private Long pendingComments;
    private List<AdminUserTrendVO> recentUserTrend;
    private List<AdminEmotionDistributionVO> emotionDistribution;
}
