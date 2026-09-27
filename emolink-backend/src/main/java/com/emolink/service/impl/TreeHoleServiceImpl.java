package com.emolink.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emolink.common.PageResult;
import com.emolink.dto.CommentDTO;
import com.emolink.dto.PostDTO;
import com.emolink.entity.TreeHoleComment;
import com.emolink.entity.TreeHoleLike;
import com.emolink.entity.TreeHolePost;
import com.emolink.entity.User;
import com.emolink.exception.BusinessException;
import com.emolink.mapper.TreeHoleCommentMapper;
import com.emolink.mapper.TreeHoleLikeMapper;
import com.emolink.mapper.TreeHolePostMapper;
import com.emolink.mapper.UserMapper;
import com.emolink.service.SensitiveWordFilter;
import com.emolink.service.TreeHoleService;
import com.emolink.vo.CommentVO;
import com.emolink.vo.LikeStatusVO;
import com.emolink.vo.PostVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TreeHoleServiceImpl implements TreeHoleService {

    private final TreeHolePostMapper treeHolePostMapper;
    private final TreeHoleCommentMapper treeHoleCommentMapper;
    private final TreeHoleLikeMapper treeHoleLikeMapper;
    private final UserMapper userMapper;
    private final SensitiveWordFilter sensitiveWordFilter;

    public TreeHoleServiceImpl(TreeHolePostMapper treeHolePostMapper,
                               TreeHoleCommentMapper treeHoleCommentMapper,
                               TreeHoleLikeMapper treeHoleLikeMapper,
                               UserMapper userMapper,
                               SensitiveWordFilter sensitiveWordFilter) {
        this.treeHolePostMapper = treeHolePostMapper;
        this.treeHoleCommentMapper = treeHoleCommentMapper;
        this.treeHoleLikeMapper = treeHoleLikeMapper;
        this.userMapper = userMapper;
        this.sensitiveWordFilter = sensitiveWordFilter;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostVO createPost(Long userId, PostDTO dto) {
        User user = getUserOrThrow(userId);
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        TreeHolePost post = new TreeHolePost();
        post.setUserId(userId);
        post.setAnonymousName(user.getAnonymousName());
        post.setContent(dto.getContent());
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setStatus(sensitiveWordFilter.containsSensitiveWord(dto.getContent()) ? 0 : 1);
        post.setCreatedAt(now);
        post.setUpdatedAt(now);
        treeHolePostMapper.insert(post);
        return toPostVO(post, false, List.of());
    }

    @Override
    public PageResult<PostVO> getPostPage(Long userId, long pageNum, long pageSize, String sort, String keyword) {
        Page<TreeHolePost> page = treeHolePostMapper.selectPage(new Page<>(pageNum, pageSize),
                Wrappers.<TreeHolePost>lambdaQuery()
                        .eq(TreeHolePost::getStatus, 1)
                        .and(StringUtils.isNotBlank(keyword), wrapper -> wrapper
                                .like(TreeHolePost::getContent, keyword)
                                .or()
                                .like(TreeHolePost::getAnonymousName, keyword))
                        .orderByDesc("hot".equalsIgnoreCase(sort), TreeHolePost::getLikeCount)
                        .orderByDesc(!"hot".equalsIgnoreCase(sort), TreeHolePost::getCreatedAt)
                        .orderByDesc("hot".equalsIgnoreCase(sort), TreeHolePost::getCreatedAt));
        Set<Long> likedPostIds = listLikedPostIds(userId, page.getRecords().stream().map(TreeHolePost::getId).toList());
        return PageResult.from(page, page.getRecords().stream()
                .map(post -> toPostVO(post, likedPostIds.contains(post.getId()), null))
                .toList());
    }

    @Override
    public PostVO getPostDetail(Long userId, Long postId) {
        TreeHolePost post = getVisiblePostOrThrow(postId);
        List<CommentVO> comments = treeHoleCommentMapper.selectList(Wrappers.<TreeHoleComment>lambdaQuery()
                        .eq(TreeHoleComment::getPostId, postId)
                        .eq(TreeHoleComment::getStatus, 1)
                        .orderByAsc(TreeHoleComment::getCreatedAt))
                .stream()
                .map(this::toCommentVO)
                .toList();
        boolean liked = listLikedPostIds(userId, List.of(postId)).contains(postId);
        return toPostVO(post, liked, comments);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentVO createComment(Long userId, CommentDTO dto) {
        TreeHolePost post = getVisiblePostOrThrow(dto.getPostId());
        User user = getUserOrThrow(userId);

        TreeHoleComment comment = new TreeHoleComment();
        comment.setCreatedAt(java.time.LocalDateTime.now());
        comment.setPostId(dto.getPostId());
        comment.setUserId(userId);
        comment.setAnonymousName(user.getAnonymousName());
        comment.setContent(dto.getContent());
        comment.setStatus(sensitiveWordFilter.containsSensitiveWord(dto.getContent()) ? 0 : 1);
        treeHoleCommentMapper.insert(comment);

        if (Integer.valueOf(1).equals(comment.getStatus())) {
            post.setCommentCount((post.getCommentCount() == null ? 0 : post.getCommentCount()) + 1);
            post.setUpdatedAt(java.time.LocalDateTime.now());
            treeHolePostMapper.updateById(post);
        }
        return toCommentVO(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LikeStatusVO toggleLike(Long userId, Long postId) {
        TreeHolePost post = getVisiblePostOrThrow(postId);
        TreeHoleLike like = treeHoleLikeMapper.selectOne(Wrappers.<TreeHoleLike>lambdaQuery()
                .eq(TreeHoleLike::getPostId, postId)
                .eq(TreeHoleLike::getUserId, userId)
                .last("LIMIT 1"));
        int currentCount = post.getLikeCount() == null ? 0 : post.getLikeCount();
        if (like != null) {
            treeHoleLikeMapper.deleteById(like.getId());
            post.setLikeCount(Math.max(0, currentCount - 1));
            post.setUpdatedAt(java.time.LocalDateTime.now());
            treeHolePostMapper.updateById(post);
            return new LikeStatusVO(false, post.getLikeCount());
        }

        TreeHoleLike entity = new TreeHoleLike();
        entity.setPostId(postId);
        entity.setUserId(userId);
        treeHoleLikeMapper.insert(entity);
        post.setLikeCount(currentCount + 1);
        post.setUpdatedAt(java.time.LocalDateTime.now());
        treeHolePostMapper.updateById(post);
        return new LikeStatusVO(true, post.getLikeCount());
    }

    @Override
    public List<PostVO> getHotPosts(Long userId, int limit) {
        List<TreeHolePost> posts = treeHolePostMapper.selectList(Wrappers.<TreeHolePost>lambdaQuery()
                .eq(TreeHolePost::getStatus, 1)
                .orderByDesc(TreeHolePost::getLikeCount)
                .orderByDesc(TreeHolePost::getCreatedAt)
                .last("LIMIT " + limit));
        Set<Long> likedPostIds = listLikedPostIds(userId, posts.stream().map(TreeHolePost::getId).toList());
        return posts.stream()
                .map(post -> toPostVO(post, likedPostIds.contains(post.getId()), null))
                .toList();
    }

    private TreeHolePost getVisiblePostOrThrow(Long postId) {
        TreeHolePost post = treeHolePostMapper.selectById(postId);
        if (post == null || post.getStatus() == null || post.getStatus() != 1) {
            throw new BusinessException(404, "帖子不存在");
        }
        return post;
    }

    private User getUserOrThrow(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (StringUtils.isBlank(user.getAnonymousName())) {
            throw new BusinessException(500, "用户匿名昵称缺失");
        }
        return user;
    }

    private Set<Long> listLikedPostIds(Long userId, List<Long> postIds) {
        if (userId == null || postIds == null || postIds.isEmpty()) {
            return Set.of();
        }
        return treeHoleLikeMapper.selectList(Wrappers.<TreeHoleLike>lambdaQuery()
                        .eq(TreeHoleLike::getUserId, userId)
                        .in(TreeHoleLike::getPostId, postIds))
                .stream()
                .map(TreeHoleLike::getPostId)
                .collect(Collectors.toSet());
    }

    private PostVO toPostVO(TreeHolePost post, boolean liked, List<CommentVO> comments) {
        PostVO vo = new PostVO();
        BeanUtils.copyProperties(post, vo);
        vo.setIsLiked(liked);
        vo.setComments(comments);
        return vo;
    }

    private CommentVO toCommentVO(TreeHoleComment comment) {
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(comment, vo);
        return vo;
    }
}
