package com.emolink.service;

import com.emolink.common.PageResult;
import com.emolink.dto.EmotionDiaryDTO;
import com.emolink.vo.CalendarVO;
import com.emolink.vo.DailyReportVO;
import com.emolink.vo.EmotionDiaryVO;
import com.emolink.vo.WeeklyReportVO;

import java.time.LocalDate;
import java.util.List;

public interface EmotionService {

    EmotionDiaryVO createDiary(Long userId, EmotionDiaryDTO dto);

    PageResult<EmotionDiaryVO> getDiaryPage(Long userId, long pageNum, long pageSize, String emotionType,
                                            String keyword,
                                            LocalDate startDate, LocalDate endDate);

    EmotionDiaryVO getDiaryDetail(Long userId, Long diaryId);

    void updateDiary(Long userId, Long diaryId, EmotionDiaryDTO dto);

    void deleteDiary(Long userId, Long diaryId);

    DailyReportVO getDailyReport(Long userId, LocalDate date);

    WeeklyReportVO getWeeklyReport(Long userId, LocalDate startDate);

    List<CalendarVO> getCalendar(Long userId, int year, int month);
}
