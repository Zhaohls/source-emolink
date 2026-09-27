package com.emolink.controller;

import com.emolink.common.PageResult;
import com.emolink.common.Result;
import com.emolink.dto.EmotionDiaryDTO;
import com.emolink.service.EmotionService;
import com.emolink.vo.CalendarVO;
import com.emolink.vo.DailyReportVO;
import com.emolink.vo.EmotionDiaryVO;
import com.emolink.vo.WeeklyReportVO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/emotion")
public class EmotionController extends BaseController {

    private final EmotionService emotionService;

    public EmotionController(EmotionService emotionService) {
        this.emotionService = emotionService;
    }

    @PostMapping("/diary")
    public Result<EmotionDiaryVO> createDiary(@Valid @RequestBody EmotionDiaryDTO dto, HttpServletRequest request) {
        return Result.success("保存成功", emotionService.createDiary(currentUserId(request), dto));
    }

    @GetMapping("/diary/list")
    public Result<PageResult<EmotionDiaryVO>> getDiaryPage(
            @RequestParam(defaultValue = "1") @Min(1) long pageNum,
            @RequestParam(defaultValue = "10") @Min(1) long pageSize,
            @RequestParam(required = false) String emotionType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletRequest request) {
        return Result.success(emotionService.getDiaryPage(currentUserId(request), pageNum, pageSize, emotionType, keyword, startDate, endDate));
    }

    @GetMapping("/diary/{id}")
    public Result<EmotionDiaryVO> getDiaryDetail(@PathVariable Long id, HttpServletRequest request) {
        return Result.success(emotionService.getDiaryDetail(currentUserId(request), id));
    }

    @PutMapping("/diary/{id}")
    public Result<Void> updateDiary(@PathVariable Long id, @RequestBody EmotionDiaryDTO dto, HttpServletRequest request) {
        emotionService.updateDiary(currentUserId(request), id, dto);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/diary/{id}")
    public Result<Void> deleteDiary(@PathVariable Long id, HttpServletRequest request) {
        emotionService.deleteDiary(currentUserId(request), id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/report/daily")
    public Result<DailyReportVO> getDailyReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpServletRequest request) {
        return Result.success(emotionService.getDailyReport(currentUserId(request), date));
    }

    @GetMapping("/report/weekly")
    public Result<WeeklyReportVO> getWeeklyReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            HttpServletRequest request) {
        return Result.success(emotionService.getWeeklyReport(currentUserId(request), startDate));
    }

    @GetMapping("/calendar")
    public Result<List<CalendarVO>> getCalendar(
            @RequestParam int year,
            @RequestParam int month,
            HttpServletRequest request) {
        return Result.success(emotionService.getCalendar(currentUserId(request), year, month));
    }
}
