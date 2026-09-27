package com.emolink.controller;

import com.emolink.common.PageResult;
import com.emolink.common.Result;
import com.emolink.dto.CommentDTO;
import com.emolink.dto.PostDTO;
import com.emolink.service.TreeHoleService;
import com.emolink.vo.CommentVO;
import com.emolink.vo.LikeStatusVO;
import com.emolink.vo.PostVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/api/treehole")
public class TreeHoleController extends BaseController {

    private final TreeHoleService treeHoleService;

    public TreeHoleController(TreeHoleService treeHoleService) {
        this.treeHoleService = treeHoleService;
    }

    @PostMapping("/post")
    public Result<PostVO> createPost(@Valid @RequestBody PostDTO dto, HttpServletRequest request) {
        return Result.success("发布成功", treeHoleService.createPost(currentUserId(request), dto));
    }

    @GetMapping("/post/list")
    public Result<PageResult<PostVO>> getPostPage(
            @RequestParam(defaultValue = "1") @Min(1) long pageNum,
            @RequestParam(defaultValue = "10") @Min(1) long pageSize,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        return Result.success(treeHoleService.getPostPage(currentUserId(request), pageNum, pageSize, sort, keyword));
    }

    @GetMapping("/post/{id}")
    public Result<PostVO> getPostDetail(@PathVariable Long id, HttpServletRequest request) {
        return Result.success(treeHoleService.getPostDetail(currentUserId(request), id));
    }

    @PostMapping("/comment")
    public Result<CommentVO> createComment(@Valid @RequestBody CommentDTO dto, HttpServletRequest request) {
        CommentVO comment = treeHoleService.createComment(currentUserId(request), dto);
        return Result.success(Integer.valueOf(0).equals(comment.getStatus()) ? "评论已提交，审核通过后显示" : "评论成功", comment);
    }

    @PostMapping("/like/{postId}")
    public Result<LikeStatusVO> toggleLike(@PathVariable Long postId, HttpServletRequest request) {
        LikeStatusVO vo = treeHoleService.toggleLike(currentUserId(request), postId);
        return Result.success(vo.getIsLiked() ? "点赞成功" : "取消点赞成功", vo);
    }
}
