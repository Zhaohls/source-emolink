package com.emolink.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.emolink.entity.EmotionDiary;
import com.emolink.mapper.EmotionDiaryMapper;
import com.emolink.service.ArticleService;
import com.emolink.service.DashboardService;
import com.emolink.service.TreeHoleService;
import com.emolink.vo.ArticleVO;
import com.emolink.vo.DashboardVO;
import com.emolink.vo.PostVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final EmotionDiaryMapper emotionDiaryMapper;
    private final ArticleService articleService;
    private final TreeHoleService treeHoleService;

    public DashboardServiceImpl(EmotionDiaryMapper emotionDiaryMapper,
                                ArticleService articleService,
                                TreeHoleService treeHoleService) {
        this.emotionDiaryMapper = emotionDiaryMapper;
        this.articleService = articleService;
        this.treeHoleService = treeHoleService;
    }

    @Override
    public DashboardVO getDashboard(Long userId) {
        DashboardVO vo = new DashboardVO();
        vo.setTodayEmotion(buildTodayEmotion(userId));
        vo.setWeeklyTrend(buildWeeklyTrend(userId));
        vo.setRecommendArticles(buildRecommendArticles(userId));
        vo.setHotPosts(buildHotPosts(userId));
        vo.setTotalDiaryCount(Math.toIntExact(emotionDiaryMapper.selectCount(Wrappers.<EmotionDiary>lambdaQuery()
                .eq(EmotionDiary::getUserId, userId))));
        vo.setConsecutiveDays(calculateConsecutiveDays(userId));
        return vo;
    }

    private DashboardVO.TodayEmotionVO buildTodayEmotion(Long userId) {
        LocalDate today = LocalDate.now();
        List<EmotionDiary> diaries = emotionDiaryMapper.selectList(Wrappers.<EmotionDiary>lambdaQuery()
                .eq(EmotionDiary::getUserId, userId)
                .between(EmotionDiary::getCreatedAt, today.atStartOfDay(), today.atTime(LocalTime.MAX)));
        DashboardVO.TodayEmotionVO todayEmotion = new DashboardVO.TodayEmotionVO();
        todayEmotion.setDiaryCount(diaries.size());
        todayEmotion.setAvgLevel(diaries.isEmpty() ? 0.0 : round(diaries.stream().mapToInt(EmotionDiary::getEmotionLevel).average().orElse(0.0)));
        todayEmotion.setDominantEmotion(diaries.isEmpty()
                ? null
                : diaries.stream()
                .collect(Collectors.groupingBy(EmotionDiary::getEmotionType, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.<String, Long>comparingByValue().thenComparing(Map.Entry.comparingByKey()))
                .map(Map.Entry::getKey)
                .orElse(null));
        return todayEmotion;
    }

    private List<DashboardVO.WeeklyTrendPointVO> buildWeeklyTrend(Long userId) {
        LocalDate weekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);
        List<EmotionDiary> diaries = emotionDiaryMapper.selectList(Wrappers.<EmotionDiary>lambdaQuery()
                .eq(EmotionDiary::getUserId, userId)
                .between(EmotionDiary::getCreatedAt, weekStart.atStartOfDay(), weekEnd.atTime(LocalTime.MAX)));
        Map<LocalDate, List<EmotionDiary>> grouped = diaries.stream()
                .collect(Collectors.groupingBy(diary -> diary.getCreatedAt().toLocalDate()));

        List<DashboardVO.WeeklyTrendPointVO> points = new ArrayList<>();
        for (int index = 0; index < 7; index++) {
            LocalDate current = weekStart.plusDays(index);
            List<EmotionDiary> currentList = grouped.getOrDefault(current, List.of());
            DashboardVO.WeeklyTrendPointVO point = new DashboardVO.WeeklyTrendPointVO();
            point.setDate(current.toString());
            point.setAvgLevel(currentList.isEmpty() ? 0.0 : round(currentList.stream()
                    .mapToInt(EmotionDiary::getEmotionLevel)
                    .average()
                    .orElse(0.0)));
            points.add(point);
        }
        return points;
    }

    private List<DashboardVO.DashboardArticleVO> buildRecommendArticles(Long userId) {
        List<ArticleVO> articles = articleService.recommendArticles(userId, 3);
        return articles.stream().map(article -> {
            DashboardVO.DashboardArticleVO vo = new DashboardVO.DashboardArticleVO();
            vo.setId(article.getId());
            vo.setTitle(article.getTitle());
            vo.setCategory(article.getCategory());
            return vo;
        }).toList();
    }

    private List<DashboardVO.DashboardHotPostVO> buildHotPosts(Long userId) {
        List<PostVO> posts = treeHoleService.getHotPosts(userId, 3);
        return posts.stream().map(post -> {
            DashboardVO.DashboardHotPostVO vo = new DashboardVO.DashboardHotPostVO();
            vo.setId(post.getId());
            vo.setAnonymousName(post.getAnonymousName());
            vo.setContent(post.getContent());
            vo.setLikeCount(post.getLikeCount());
            vo.setCommentCount(post.getCommentCount());
            return vo;
        }).toList();
    }

    private int calculateConsecutiveDays(Long userId) {
        List<EmotionDiary> diaries = emotionDiaryMapper.selectList(Wrappers.<EmotionDiary>lambdaQuery()
                .eq(EmotionDiary::getUserId, userId)
                .orderByDesc(EmotionDiary::getCreatedAt));
        Set<LocalDate> uniqueDates = diaries.stream()
                .map(diary -> diary.getCreatedAt().toLocalDate())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (uniqueDates.isEmpty() || !uniqueDates.iterator().next().equals(LocalDate.now())) {
            return 0;
        }
        int streak = 0;
        LocalDate cursor = LocalDate.now();
        while (uniqueDates.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }
}
