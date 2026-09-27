package com.emolink.controller;

import com.emolink.common.PageResult;
import com.emolink.common.Result;
import com.emolink.dto.AdminArticleDTO;
import com.emolink.dto.AdminArticleStatusDTO;
import com.emolink.dto.AdminAuditDTO;
import com.emolink.dto.AdminUserStatusDTO;
import com.emolink.service.AdminService;
import com.emolink.vo.AdminArticleVO;
import com.emolink.vo.AdminDashboardVO;
import com.emolink.vo.AdminTreeHoleCommentVO;
import com.emolink.vo.AdminTreeHolePostVO;
import com.emolink.vo.AdminUserVO;
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

@Validated
@RestController
@RequestMapping("/api/admin")
public class AdminController extends BaseController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public Result<AdminDashboardVO> getDashboard() {
        return Result.success(adminService.getDashboard());
    }

    @GetMapping("/user/list")
    public Result<PageResult<AdminUserVO>> getUserPage(
            @RequestParam(defaultValue = "1") @Min(1) long pageNum,
            @RequestParam(defaultValue = "10") @Min(1) long pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        return Result.success(adminService.getUserPage(pageNum, pageSize, keyword, status, currentUserId(request)));
    }

    @PutMapping("/user/status/{userId}")
    public Result<Void> updateUserStatus(@PathVariable Long userId,
                                         @Valid @RequestBody AdminUserStatusDTO dto,
                                         HttpServletRequest request) {
        adminService.updateUserStatus(currentUserId(request), userId, dto.getStatus());
        return Result.success("操作成功", null);
    }

    @GetMapping("/article/list")
    public Result<PageResult<AdminArticleVO>> getArticlePage(
            @RequestParam(defaultValue = "1") @Min(1) long pageNum,
            @RequestParam(defaultValue = "10") @Min(1) long pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        return Result.success(adminService.getArticlePage(pageNum, pageSize, category, status));
    }

    @PostMapping("/article")
    public Result<Void> createArticle(@Valid @RequestBody AdminArticleDTO dto) {
        adminService.createArticle(dto);
        return Result.success("新增成功", null);
    }

    @PutMapping("/article/{id}")
    public Result<Void> updateArticle(@PathVariable Long id, @Valid @RequestBody AdminArticleDTO dto) {
        adminService.updateArticle(id, dto);
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/article/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        adminService.deleteArticle(id);
        return Result.success("删除成功", null);
    }

    @PutMapping("/article/status/{id}")
    public Result<Void> updateArticleStatus(@PathVariable Long id, @Valid @RequestBody AdminArticleStatusDTO dto) {
        adminService.updateArticleStatus(id, dto.getStatus());
        return Result.success("操作成功", null);
    }

    @GetMapping("/treehole/post/list")
    public Result<PageResult<AdminTreeHolePostVO>> getTreeHolePostPage(
            @RequestParam(defaultValue = "1") @Min(1) long pageNum,
            @RequestParam(defaultValue = "10") @Min(1) long pageSize,
            @RequestParam(required = false) Integer status) {
        return Result.success(adminService.getTreeHolePostPage(pageNum, pageSize, status));
    }

    @PutMapping("/treehole/post/audit/{id}")
    public Result<Void> auditTreeHolePost(@PathVariable Long id, @Valid @RequestBody AdminAuditDTO dto) {
        adminService.auditTreeHolePost(id, dto.getStatus());
        return Result.success("审核成功", null);
    }

    @DeleteMapping("/treehole/post/{id}")
    public Result<Void> deleteTreeHolePost(@PathVariable Long id) {
        adminService.deleteTreeHolePost(id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/treehole/comment/list")
    public Result<PageResult<AdminTreeHoleCommentVO>> getTreeHoleCommentPage(
            @RequestParam(defaultValue = "1") @Min(1) long pageNum,
            @RequestParam(defaultValue = "10") @Min(1) long pageSize,
            @RequestParam(required = false) Integer status) {
        return Result.success(adminService.getTreeHoleCommentPage(pageNum, pageSize, status));
    }

    @PutMapping("/treehole/comment/audit/{id}")
    public Result<Void> auditTreeHoleComment(@PathVariable Long id, @Valid @RequestBody AdminAuditDTO dto) {
        adminService.auditTreeHoleComment(id, dto.getStatus());
        return Result.success("审核成功", null);
    }

    @DeleteMapping("/treehole/comment/{id}")
    public Result<Void> deleteTreeHoleComment(@PathVariable Long id) {
        adminService.deleteTreeHoleComment(id);
        return Result.success("删除成功", null);
    }
}
