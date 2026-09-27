package com.emolink.controller;

import com.emolink.common.PageResult;
import com.emolink.common.Result;
import com.emolink.service.ArticleService;
import com.emolink.vo.ArticleVO;
import com.emolink.vo.FavoriteStatusVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;

@Validated
@RestController
@RequestMapping("/api/article")
public class ArticleController extends BaseController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/list")
    public Result<PageResult<ArticleVO>> getArticlePage(
            @RequestParam(defaultValue = "1") @Min(1) long pageNum,
            @RequestParam(defaultValue = "10") @Min(1) long pageSize,
            @RequestParam(required = false) String category,
            HttpServletRequest request) {
        return Result.success(articleService.getArticlePage(currentUserId(request), pageNum, pageSize, category));
    }

    @GetMapping("/{id}")
    public Result<ArticleVO> getArticleDetail(@PathVariable Long id, HttpServletRequest request) {
        return Result.success(articleService.getArticleDetail(currentUserId(request), id));
    }

    @GetMapping("/search")
    public Result<PageResult<ArticleVO>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") @Min(1) long pageNum,
            @RequestParam(defaultValue = "10") @Min(1) long pageSize,
            HttpServletRequest request) {
        return Result.success(articleService.searchArticles(currentUserId(request), keyword, pageNum, pageSize));
    }

    @PostMapping("/favorite/{articleId}")
    public Result<FavoriteStatusVO> toggleFavorite(@PathVariable Long articleId, HttpServletRequest request) {
        FavoriteStatusVO vo = articleService.toggleFavorite(currentUserId(request), articleId);
        return Result.success(vo.getIsFavorited() ? "收藏成功" : "取消收藏成功", vo);
    }

    @GetMapping("/favorite/list")
    public Result<PageResult<ArticleVO>> getFavoritePage(
            @RequestParam(defaultValue = "1") @Min(1) long pageNum,
            @RequestParam(defaultValue = "10") @Min(1) long pageSize,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        return Result.success(articleService.getFavoritePage(currentUserId(request), pageNum, pageSize, keyword));
    }
}
