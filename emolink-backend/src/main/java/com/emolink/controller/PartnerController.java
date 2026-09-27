package com.emolink.controller;

import com.emolink.common.PageResult;
import com.emolink.common.Result;
import com.emolink.dto.PartnerEndDTO;
import com.emolink.service.PartnerService;
import com.emolink.vo.MatchStatusVO;
import com.emolink.vo.MessageVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;

@Validated
@RestController
@RequestMapping("/api/partner")
public class PartnerController extends BaseController {

    private final PartnerService partnerService;

    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @PostMapping("/match")
    public Result<MatchStatusVO> joinMatch(HttpServletRequest request) {
        MatchStatusVO vo = partnerService.joinMatch(currentUserId(request));
        String message = "waiting".equals(vo.getStatus()) ? "已加入匹配队列" : "匹配成功";
        return Result.success(message, vo);
    }

    @GetMapping("/match/status")
    public Result<MatchStatusVO> getMatchStatus(HttpServletRequest request) {
        return Result.success(partnerService.getMatchStatus(currentUserId(request)));
    }

    @GetMapping("/message/list")
    public Result<PageResult<MessageVO>> getMessagePage(
            @RequestParam Long matchId,
            @RequestParam(defaultValue = "1") @Min(1) long pageNum,
            @RequestParam(defaultValue = "50") @Min(1) long pageSize,
            HttpServletRequest request) {
        return Result.success(partnerService.getMessagePage(currentUserId(request), matchId, pageNum, pageSize));
    }

    @PostMapping("/match/end")
    public Result<Void> endMatch(@Valid @RequestBody PartnerEndDTO dto, HttpServletRequest request) {
        partnerService.endMatch(currentUserId(request), dto.getMatchId());
        return Result.success("已结束对话", null);
    }
}

