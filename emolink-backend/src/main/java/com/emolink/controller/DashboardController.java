package com.emolink.controller;

import com.emolink.common.Result;
import com.emolink.service.DashboardService;
import com.emolink.vo.DashboardVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController extends BaseController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public Result<DashboardVO> getDashboard(HttpServletRequest request) {
        return Result.success(dashboardService.getDashboard(currentUserId(request)));
    }
}

