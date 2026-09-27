package com.emolink.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emolink.common.Constants;
import com.emolink.common.PageResult;
import com.emolink.dto.EmotionDiaryDTO;
import com.emolink.entity.EmotionDiary;
import com.emolink.entity.EmotionReport;
import com.emolink.exception.BusinessException;
import com.emolink.mapper.EmotionDiaryMapper;
import com.emolink.mapper.EmotionReportMapper;
import com.emolink.service.DeepSeekService;
import com.emolink.service.EmotionService;
import com.emolink.vo.CalendarVO;
import com.emolink.vo.DailyReportVO;
import com.emolink.vo.DailyTrendVO;
import com.emolink.vo.EmotionDiaryVO;
import com.emolink.vo.EmotionDistributionVO;
import com.emolink.vo.EmotionTimelineVO;
import com.emolink.vo.WeeklyReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EmotionServiceImpl implements EmotionService {

    private static final Set<String> EMOTION_TYPES = Set.of("happy", "calm", "anxious", "sad", "angry", "fearful");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final EmotionDiaryMapper emotionDiaryMapper;
    private final EmotionReportMapper emotionReportMapper;
    private final DeepSeekService deepSeekService;

    public EmotionServiceImpl(EmotionDiaryMapper emotionDiaryMapper,
                              EmotionReportMapper emotionReportMapper,
                              DeepSeekService deepSeekService) {
        this.emotionDiaryMapper = emotionDiaryMapper;
        this.emotionReportMapper = emotionReportMapper;
        this.deepSeekService = deepSeekService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmotionDiaryVO createDiary(Long userId, EmotionDiaryDTO dto) {
        validateEmotionInput(dto.getEmotionType(), dto.getEmotionLevel(), dto.getFeeling());

        EmotionDiary diary = new EmotionDiary();
        LocalDateTime now = LocalDateTime.now();
        diary.setUserId(userId);
        diary.setEmotionType(dto.getEmotionType());
        diary.setEmotionLevel(dto.getEmotionLevel());
        diary.setTriggerEvent(dto.getTriggerEvent());
        diary.setFeeling(dto.getFeeling());
        diary.setCreatedAt(now);
        diary.setUpdatedAt(now);
        diary.setAiSuggestion(deepSeekService.getEmotionAdvice(
                dto.getEmotionType(),
                dto.getEmotionLevel(),
                dto.getTriggerEvent(),
                dto.getFeeling()
        ));
        emotionDiaryMapper.insert(diary);
        return toDiaryVO(diary);
    }

    @Override
    public PageResult<EmotionDiaryVO> getDiaryPage(Long userId, long pageNum, long pageSize, String emotionType,
                                                   String keyword,
                                                   LocalDate startDate, LocalDate endDate) {
        Page<EmotionDiary> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<EmotionDiary> queryWrapper = Wrappers.<EmotionDiary>lambdaQuery()
                .eq(EmotionDiary::getUserId, userId)
                .orderByDesc(EmotionDiary::getCreatedAt);
        if (StringUtils.isNotBlank(emotionType)) {
            queryWrapper.eq(EmotionDiary::getEmotionType, emotionType);
        }
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(EmotionDiary::getTriggerEvent, keyword)
                    .or()
                    .like(EmotionDiary::getFeeling, keyword)
                    .or()
                    .like(EmotionDiary::getAiSuggestion, keyword));
        }
        if (startDate != null) {
            queryWrapper.ge(EmotionDiary::getCreatedAt, startDate.atStartOfDay());
        }
        if (endDate != null) {
            queryWrapper.le(EmotionDiary::getCreatedAt, endDate.atTime(LocalTime.MAX));
        }
        Page<EmotionDiary> result = emotionDiaryMapper.selectPage(page, queryWrapper);
        return PageResult.from(result, result.getRecords().stream().map(this::toDiaryVO).toList());
    }

    @Override
    public EmotionDiaryVO getDiaryDetail(Long userId, Long diaryId) {
        return toDiaryVO(getDiaryOrThrow(userId, diaryId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDiary(Long userId, Long diaryId, EmotionDiaryDTO dto) {
        EmotionDiary diary = getDiaryOrThrow(userId, diaryId);
        String emotionType = StringUtils.defaultIfBlank(dto.getEmotionType(), diary.getEmotionType());
        Integer emotionLevel = dto.getEmotionLevel() == null ? diary.getEmotionLevel() : dto.getEmotionLevel();
        String feeling = StringUtils.defaultIfBlank(dto.getFeeling(), diary.getFeeling());
        validateEmotionInput(emotionType, emotionLevel, feeling);

        diary.setEmotionType(emotionType);
        diary.setEmotionLevel(emotionLevel);
        if (dto.getTriggerEvent() != null) {
            diary.setTriggerEvent(dto.getTriggerEvent());
        }
        diary.setFeeling(feeling);
        diary.setUpdatedAt(LocalDateTime.now());
        diary.setAiSuggestion(deepSeekService.getEmotionAdvice(
                diary.getEmotionType(),
                diary.getEmotionLevel(),
                diary.getTriggerEvent(),
                diary.getFeeling()
        ));
        emotionDiaryMapper.updateById(diary);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDiary(Long userId, Long diaryId) {
        EmotionDiary diary = getDiaryOrThrow(userId, diaryId);
        emotionDiaryMapper.deleteById(diary.getId());
    }

    @Override
    public DailyReportVO getDailyReport(Long userId, LocalDate date) {
        LocalDate targetDate = date == null ? LocalDate.now() : date;
        List<EmotionDiary> diaries = listDiaries(userId, targetDate.atStartOfDay(), targetDate.atTime(LocalTime.MAX));

        DailyReportVO vo = new DailyReportVO();
        vo.setDate(targetDate);
        vo.setTotalCount(diaries.size());
        vo.setEmotionDistribution(buildDistribution(diaries));
        vo.setEmotionTimeline(diaries.stream()
                .sorted(Comparator.comparing(EmotionDiary::getCreatedAt))
                .map(diary -> new EmotionTimelineVO(
                        diary.getCreatedAt().format(TIME_FORMATTER),
                        diary.getEmotionType(),
                        diary.getEmotionLevel()
                ))
                .toList());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WeeklyReportVO getWeeklyReport(Long userId, LocalDate startDate) {
        LocalDate weekStart = startDate == null
                ? LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                : startDate;
        LocalDate weekEnd = weekStart.plusDays(6);
        List<EmotionDiary> diaries = listDiaries(userId, weekStart.atStartOfDay(), weekEnd.atTime(LocalTime.MAX));

        WeeklyReportVO vo = new WeeklyReportVO();
        vo.setStartDate(weekStart);
        vo.setEndDate(weekEnd);
        vo.setTotalCount(diaries.size());
        vo.setEmotionDistribution(buildDistribution(diaries));
        vo.setDailyTrend(buildDailyTrend(weekStart, diaries));

        EmotionReport report = emotionReportMapper.selectOne(Wrappers.<EmotionReport>lambdaQuery()
                .eq(EmotionReport::getUserId, userId)
                .eq(EmotionReport::getReportType, Constants.REPORT_TYPE_WEEKLY)
                .eq(EmotionReport::getReportDate, weekStart)
                .last("LIMIT 1"));
        String weeklyDataJson = JSON.toJSONString(Map.of(
                "startDate", weekStart,
                "endDate", weekEnd,
                "totalCount", diaries.size(),
                "distribution", vo.getEmotionDistribution(),
                "dailyTrend", vo.getDailyTrend()
        ));

        String aiAnalysis = report != null && StringUtils.isNotBlank(report.getAiAnalysis())
                ? report.getAiAnalysis()
                : buildWeeklyAnalysis(weeklyDataJson, diaries.size());
        vo.setAiAnalysis(aiAnalysis);

        if (report == null) {
            report = new EmotionReport();
            report.setUserId(userId);
            report.setReportType(Constants.REPORT_TYPE_WEEKLY);
            report.setReportDate(weekStart);
        }
        report.setSummary(buildWeeklySummary(vo));
        report.setEmotionDataJson(weeklyDataJson);
        report.setAiAnalysis(aiAnalysis);
        if (report.getId() == null) {
            emotionReportMapper.insert(report);
        } else {
            emotionReportMapper.updateById(report);
        }
        return vo;
    }

    @Override
    public List<CalendarVO> getCalendar(Long userId, int year, int month) {
        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
        List<EmotionDiary> diaries = listDiaries(userId, monthStart.atStartOfDay(), monthEnd.atTime(LocalTime.MAX));

        Map<LocalDate, List<EmotionDiary>> grouped = diaries.stream()
                .collect(Collectors.groupingBy(diary -> diary.getCreatedAt().toLocalDate()));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new CalendarVO(
                        entry.getKey().toString(),
                        findDominantEmotion(entry.getValue()),
                        entry.getValue().size()
                ))
                .toList();
    }

    private EmotionDiary getDiaryOrThrow(Long userId, Long diaryId) {
        EmotionDiary diary = emotionDiaryMapper.selectById(diaryId);
        if (diary == null) {
            throw new BusinessException(404, "日记不存在");
        }
        if (!userId.equals(diary.getUserId())) {
            throw new BusinessException(403, "无权操作该日记");
        }
        return diary;
    }

    private List<EmotionDiary> listDiaries(Long userId, LocalDateTime start, LocalDateTime end) {
        return emotionDiaryMapper.selectList(Wrappers.<EmotionDiary>lambdaQuery()
                .eq(EmotionDiary::getUserId, userId)
                .between(EmotionDiary::getCreatedAt, start, end)
                .orderByAsc(EmotionDiary::getCreatedAt));
    }

    private EmotionDiaryVO toDiaryVO(EmotionDiary diary) {
        EmotionDiaryVO vo = new EmotionDiaryVO();
        BeanUtils.copyProperties(diary, vo);
        return vo;
    }

    private void validateEmotionInput(String emotionType, Integer emotionLevel, String feeling) {
        if (!EMOTION_TYPES.contains(emotionType)) {
            throw new BusinessException(400, "emotionType 不合法");
        }
        if (emotionLevel == null || emotionLevel < 1 || emotionLevel > 5) {
            throw new BusinessException(400, "emotionLevel 取值需在1-5之间");
        }
        if (StringUtils.isBlank(feeling)) {
            throw new BusinessException(400, "feeling 不能为空");
        }
    }

    private List<EmotionDistributionVO> buildDistribution(List<EmotionDiary> diaries) {
        if (diaries.isEmpty()) {
            return new ArrayList<>();
        }
        long total = diaries.size();
        Map<String, Long> distribution = diaries.stream()
                .collect(Collectors.groupingBy(EmotionDiary::getEmotionType, LinkedHashMap::new, Collectors.counting()));
        return distribution.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(entry -> new EmotionDistributionVO(
                        entry.getKey(),
                        entry.getValue(),
                        calculatePercentage(entry.getValue(), total)
                ))
                .toList();
    }

    private List<DailyTrendVO> buildDailyTrend(LocalDate weekStart, List<EmotionDiary> diaries) {
        Map<LocalDate, List<EmotionDiary>> grouped = diaries.stream()
                .collect(Collectors.groupingBy(diary -> diary.getCreatedAt().toLocalDate()));
        List<DailyTrendVO> result = new ArrayList<>();
        for (int index = 0; index < 7; index++) {
            LocalDate current = weekStart.plusDays(index);
            List<EmotionDiary> dailyList = grouped.getOrDefault(current, List.of());
            double avgLevel = dailyList.isEmpty()
                    ? 0.0
                    : dailyList.stream().mapToInt(EmotionDiary::getEmotionLevel).average().orElse(0.0);
            result.add(new DailyTrendVO(
                    current.toString(),
                    round(avgLevel),
                    dailyList.isEmpty() ? null : findDominantEmotion(dailyList)
            ));
        }
        return result;
    }

    private String findDominantEmotion(List<EmotionDiary> diaries) {
        return diaries.stream()
                .collect(Collectors.groupingBy(EmotionDiary::getEmotionType, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.<String, Long>comparingByValue()
                        .thenComparing(Map.Entry.comparingByKey()))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private int calculatePercentage(long count, long total) {
        if (total == 0) {
            return 0;
        }
        return BigDecimal.valueOf(count * 100.0 / total)
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    private String buildWeeklyAnalysis(String weeklyDataJson, int totalCount) {
        if (totalCount == 0) {
            return "本周还没有记录情绪日记。可以从每天花一分钟记录当下感受开始，持续几天后会更容易看到自己的情绪变化。";
        }
        return deepSeekService.getWeeklyAnalysis(weeklyDataJson);
    }

    private String buildWeeklySummary(WeeklyReportVO vo) {
        String dominantEmotion = vo.getEmotionDistribution().isEmpty()
                ? "暂无"
                : vo.getEmotionDistribution().get(0).getEmotionType();
        return String.format("本周共记录%d条日记，主要情绪为%s。", vo.getTotalCount(), dominantEmotion);
    }
}
