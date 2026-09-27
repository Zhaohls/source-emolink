package com.emolink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emolink.common.PageResult;
import com.emolink.dto.AdminArticleDTO;
import com.emolink.entity.Article;
import com.emolink.entity.EmotionDiary;
import com.emolink.entity.TreeHoleComment;
import com.emolink.entity.TreeHolePost;
import com.emolink.entity.User;
import com.emolink.exception.BusinessException;
import com.emolink.mapper.ArticleFavoriteMapper;
import com.emolink.mapper.ArticleMapper;
import com.emolink.mapper.EmotionDiaryMapper;
import com.emolink.mapper.TreeHoleCommentMapper;
import com.emolink.mapper.TreeHoleLikeMapper;
import com.emolink.mapper.TreeHolePostMapper;
import com.emolink.mapper.UserMapper;
import com.emolink.service.AdminService;
import com.emolink.vo.AdminArticleVO;
import com.emolink.vo.AdminDashboardVO;
import com.emolink.vo.AdminEmotionDistributionVO;
import com.emolink.vo.AdminTreeHoleCommentVO;
import com.emolink.vo.AdminTreeHolePostVO;
import com.emolink.vo.AdminUserTrendVO;
import com.emolink.vo.AdminUserVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private static final String ROLE_ADMIN = "admin";

    private final UserMapper userMapper;
    private final EmotionDiaryMapper emotionDiaryMapper;
    private final ArticleMapper articleMapper;
    private final ArticleFavoriteMapper articleFavoriteMapper;
    private final TreeHolePostMapper treeHolePostMapper;
    private final TreeHoleCommentMapper treeHoleCommentMapper;
    private final TreeHoleLikeMapper treeHoleLikeMapper;

    public AdminServiceImpl(UserMapper userMapper,
                            EmotionDiaryMapper emotionDiaryMapper,
                            ArticleMapper articleMapper,
                            ArticleFavoriteMapper articleFavoriteMapper,
                            TreeHolePostMapper treeHolePostMapper,
                            TreeHoleCommentMapper treeHoleCommentMapper,
                            TreeHoleLikeMapper treeHoleLikeMapper) {
        this.userMapper = userMapper;
        this.emotionDiaryMapper = emotionDiaryMapper;
        this.articleMapper = articleMapper;
        this.articleFavoriteMapper = articleFavoriteMapper;
        this.treeHolePostMapper = treeHolePostMapper;
        this.treeHoleCommentMapper = treeHoleCommentMapper;
        this.treeHoleLikeMapper = treeHoleLikeMapper;
    }

    @Override
    public AdminDashboardVO getDashboard() {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDate startDate = today.minusDays(6);
        LocalDateTime trendStart = startDate.atStartOfDay();

        AdminDashboardVO vo = new AdminDashboardVO();
        vo.setTotalUsers(userMapper.selectCount(Wrappers.<User>lambdaQuery().ne(User::getRole, ROLE_ADMIN)));
        vo.setTodayNewUsers(userMapper.selectCount(Wrappers.<User>lambdaQuery()
                .ne(User::getRole, ROLE_ADMIN)
                .ge(User::getCreatedAt, todayStart)));
        vo.setTotalDiaries(emotionDiaryMapper.selectCount(null));
        vo.setTodayDiaries(emotionDiaryMapper.selectCount(Wrappers.<EmotionDiary>lambdaQuery()
                .ge(EmotionDiary::getCreatedAt, todayStart)));
        vo.setTotalArticles(articleMapper.selectCount(null));
        vo.setTotalPosts(treeHolePostMapper.selectCount(null));
        vo.setPendingPosts(treeHolePostMapper.selectCount(Wrappers.<TreeHolePost>lambdaQuery()
                .eq(TreeHolePost::getStatus, 0)));
        vo.setTotalComments(treeHoleCommentMapper.selectCount(null));
        vo.setPendingComments(treeHoleCommentMapper.selectCount(Wrappers.<TreeHoleComment>lambdaQuery()
                .eq(TreeHoleComment::getStatus, 0)));
        vo.setRecentUserTrend(buildRecentUserTrend(trendStart, startDate));
        vo.setEmotionDistribution(buildEmotionDistribution());
        return vo;
    }

    @Override
    public PageResult<AdminUserVO> getUserPage(long pageNum, long pageSize, String keyword, Integer status, Long currentUserId) {
        Page<User> page = userMapper.selectPage(new Page<>(pageNum, pageSize), Wrappers.<User>lambdaQuery()
                .ne(User::getId, currentUserId)
                .and(StringUtils.isNotBlank(keyword), wrapper -> wrapper
                        .like(User::getUsername, keyword)
                        .or()
                        .like(User::getNickname, keyword))
                .eq(status != null, User::getStatus, status)
                .orderByDesc(User::getCreatedAt));

        List<Long> userIds = page.getRecords().stream().map(User::getId).toList();
        Map<Long, Long> diaryCountMap = buildDiaryCountMap(userIds);
        return PageResult.from(page, page.getRecords().stream()
                .map(user -> toAdminUserVO(user, diaryCountMap.getOrDefault(user.getId(), 0L)))
                .toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long currentUserId, Long userId, Integer status) {
        User user = getUserOrThrow(userId);
        if (Objects.equals(currentUserId, userId)) {
            throw new BusinessException(400, "不能修改自己的账号状态");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public PageResult<AdminArticleVO> getArticlePage(long pageNum, long pageSize, String category, Integer status) {
        Page<Article> page = articleMapper.selectPage(new Page<>(pageNum, pageSize), Wrappers.<Article>lambdaQuery()
                .eq(StringUtils.isNotBlank(category), Article::getCategory, category)
                .eq(status != null, Article::getStatus, status)
                .orderByDesc(Article::getCreatedAt));
        return PageResult.from(page, page.getRecords().stream().map(this::toAdminArticleVO).toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createArticle(AdminArticleDTO dto) {
        validateArticleForCreate(dto);
        Article article = new Article();
        article.setTitle(dto.getTitle().trim());
        article.setContent(dto.getContent());
        article.setCategory(dto.getCategory().trim());
        article.setViewCount(0);
        article.setStatus(dto.getStatus());
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        articleMapper.insert(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticle(Long articleId, AdminArticleDTO dto) {
        Article article = getArticleOrThrow(articleId);
        if (dto.getTitle() != null) {
            if (StringUtils.isBlank(dto.getTitle())) {
                throw new BusinessException(400, "标题不能为空");
            }
            article.setTitle(dto.getTitle().trim());
        }
        if (dto.getContent() != null) {
            if (StringUtils.isBlank(dto.getContent())) {
                throw new BusinessException(400, "内容不能为空");
            }
            article.setContent(dto.getContent());
        }
        if (dto.getCategory() != null) {
            if (StringUtils.isBlank(dto.getCategory())) {
                throw new BusinessException(400, "分类不能为空");
            }
            article.setCategory(dto.getCategory().trim());
        }
        if (dto.getStatus() != null) {
            article.setStatus(dto.getStatus());
        }
        article.setUpdatedAt(LocalDateTime.now());
        articleMapper.updateById(article);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticle(Long articleId) {
        getArticleOrThrow(articleId);
        articleFavoriteMapper.delete(Wrappers.lambdaQuery(com.emolink.entity.ArticleFavorite.class)
                .eq(com.emolink.entity.ArticleFavorite::getArticleId, articleId));
        articleMapper.deleteById(articleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticleStatus(Long articleId, Integer status) {
        Article article = getArticleOrThrow(articleId);
        article.setStatus(status);
        article.setUpdatedAt(LocalDateTime.now());
        articleMapper.updateById(article);
    }

    @Override
    public PageResult<AdminTreeHolePostVO> getTreeHolePostPage(long pageNum, long pageSize, Integer status) {
        Page<TreeHolePost> page = treeHolePostMapper.selectPage(new Page<>(pageNum, pageSize), Wrappers.<TreeHolePost>lambdaQuery()
                .eq(status != null, TreeHolePost::getStatus, status)
                .orderByDesc(TreeHolePost::getCreatedAt));
        Map<Long, String> usernameMap = buildUsernameMap(page.getRecords().stream().map(TreeHolePost::getUserId).toList());
        return PageResult.from(page, page.getRecords().stream()
                .map(post -> toAdminTreeHolePostVO(post, usernameMap.get(post.getUserId())))
                .toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditTreeHolePost(Long postId, Integer status) {
        TreeHolePost post = getTreeHolePostOrThrow(postId);
        post.setStatus(status);
        post.setUpdatedAt(LocalDateTime.now());
        treeHolePostMapper.updateById(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTreeHolePost(Long postId) {
        getTreeHolePostOrThrow(postId);
        treeHoleCommentMapper.delete(Wrappers.<TreeHoleComment>lambdaQuery().eq(TreeHoleComment::getPostId, postId));
        treeHoleLikeMapper.delete(Wrappers.<com.emolink.entity.TreeHoleLike>lambdaQuery()
                .eq(com.emolink.entity.TreeHoleLike::getPostId, postId));
        treeHolePostMapper.deleteById(postId);
    }

    @Override
    public PageResult<AdminTreeHoleCommentVO> getTreeHoleCommentPage(long pageNum, long pageSize, Integer status) {
        Page<TreeHoleComment> page = treeHoleCommentMapper.selectPage(new Page<>(pageNum, pageSize), Wrappers.<TreeHoleComment>lambdaQuery()
                .eq(status != null, TreeHoleComment::getStatus, status)
                .orderByDesc(TreeHoleComment::getCreatedAt));
        Map<Long, String> usernameMap = buildUsernameMap(page.getRecords().stream().map(TreeHoleComment::getUserId).toList());
        return PageResult.from(page, page.getRecords().stream()
                .map(comment -> toAdminTreeHoleCommentVO(comment, usernameMap.get(comment.getUserId())))
                .toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditTreeHoleComment(Long commentId, Integer status) {
        TreeHoleComment comment = getTreeHoleCommentOrThrow(commentId);
        int delta = resolveCommentCountDelta(comment.getStatus(), status);
        comment.setStatus(status);
        treeHoleCommentMapper.updateById(comment);
        updatePostCommentCount(comment.getPostId(), delta);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTreeHoleComment(Long commentId) {
        TreeHoleComment comment = getTreeHoleCommentOrThrow(commentId);
        treeHoleCommentMapper.deleteById(commentId);
        updatePostCommentCount(comment.getPostId(), Integer.valueOf(1).equals(comment.getStatus()) ? -1 : 0);
    }

    private List<AdminUserTrendVO> buildRecentUserTrend(LocalDateTime trendStart, LocalDate startDate) {
        List<Map<String, Object>> trendMaps = userMapper.selectMaps(new QueryWrapper<User>()
                .select("DATE(created_at) AS date", "COUNT(*) AS count")
                .ne("role", ROLE_ADMIN)
                .ge("created_at", trendStart)
                .groupBy("DATE(created_at)")
                .orderByAsc("DATE(created_at)"));
        Map<String, Long> countMap = trendMaps.stream()
                .collect(Collectors.toMap(map -> normalizeDate(map.get("date")), map -> toLong(map.get("count"))));
        List<AdminUserTrendVO> trend = new ArrayList<>();
        for (int index = 0; index < 7; index++) {
            String date = startDate.plusDays(index).format(DateTimeFormatter.ISO_LOCAL_DATE);
            trend.add(new AdminUserTrendVO(date, countMap.getOrDefault(date, 0L)));
        }
        return trend;
    }

    private List<AdminEmotionDistributionVO> buildEmotionDistribution() {
        List<Map<String, Object>> distributionMaps = emotionDiaryMapper.selectMaps(new QueryWrapper<EmotionDiary>()
                .select("emotion_type AS emotionType", "COUNT(*) AS count")
                .groupBy("emotion_type"));
        return distributionMaps.stream()
                .map(map -> new AdminEmotionDistributionVO(String.valueOf(map.get("emotionType")), toLong(map.get("count"))))
                .sorted(Comparator.comparing(AdminEmotionDistributionVO::getCount).reversed())
                .toList();
    }

    private Map<Long, Long> buildDiaryCountMap(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }
        return emotionDiaryMapper.selectMaps(new QueryWrapper<EmotionDiary>()
                        .select("user_id AS userId", "COUNT(*) AS diaryCount")
                        .in("user_id", userIds)
                        .groupBy("user_id"))
                .stream()
                .collect(Collectors.toMap(map -> toLong(map.get("userId")), map -> toLong(map.get("diaryCount"))));
    }

    private Map<Long, String> buildUsernameMap(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }
        return userMapper.selectList(Wrappers.<User>lambdaQuery().in(User::getId, userIds)).stream()
                .collect(Collectors.toMap(User::getId, User::getUsername, (left, right) -> left));
    }

    private AdminUserVO toAdminUserVO(User user, Long diaryCount) {
        AdminUserVO vo = new AdminUserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setDiaryCount(diaryCount);
        return vo;
    }

    private AdminArticleVO toAdminArticleVO(Article article) {
        AdminArticleVO vo = new AdminArticleVO();
        BeanUtils.copyProperties(article, vo);
        return vo;
    }

    private AdminTreeHolePostVO toAdminTreeHolePostVO(TreeHolePost post, String username) {
        AdminTreeHolePostVO vo = new AdminTreeHolePostVO();
        BeanUtils.copyProperties(post, vo);
        vo.setUsername(username);
        return vo;
    }

    private AdminTreeHoleCommentVO toAdminTreeHoleCommentVO(TreeHoleComment comment, String username) {
        AdminTreeHoleCommentVO vo = new AdminTreeHoleCommentVO();
        BeanUtils.copyProperties(comment, vo);
        vo.setUsername(username);
        return vo;
    }

    private void validateArticleForCreate(AdminArticleDTO dto) {
        if (StringUtils.isBlank(dto.getTitle())) {
            throw new BusinessException(400, "标题不能为空");
        }
        if (StringUtils.isBlank(dto.getContent())) {
            throw new BusinessException(400, "内容不能为空");
        }
        if (StringUtils.isBlank(dto.getCategory())) {
            throw new BusinessException(400, "分类不能为空");
        }
        if (dto.getStatus() == null) {
            throw new BusinessException(400, "状态不能为空");
        }
    }

    private User getUserOrThrow(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    private Article getArticleOrThrow(Long articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException(404, "文章不存在");
        }
        return article;
    }

    private TreeHolePost getTreeHolePostOrThrow(Long postId) {
        TreeHolePost post = treeHolePostMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(404, "帖子不存在");
        }
        return post;
    }

    private TreeHoleComment getTreeHoleCommentOrThrow(Long commentId) {
        TreeHoleComment comment = treeHoleCommentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(404, "评论不存在");
        }
        return comment;
    }

    private void updatePostCommentCount(Long postId, int delta) {
        if (delta == 0) {
            return;
        }
        TreeHolePost post = treeHolePostMapper.selectById(postId);
        if (post == null) {
            return;
        }
        int currentCount = post.getCommentCount() == null ? 0 : post.getCommentCount();
        post.setCommentCount(Math.max(0, currentCount + delta));
        post.setUpdatedAt(LocalDateTime.now());
        treeHolePostMapper.updateById(post);
    }

    private int resolveCommentCountDelta(Integer oldStatus, Integer newStatus) {
        boolean oldVisible = Integer.valueOf(1).equals(oldStatus);
        boolean newVisible = Integer.valueOf(1).equals(newStatus);
        if (oldVisible == newVisible) {
            return 0;
        }
        return newVisible ? 1 : -1;
    }

    private String normalizeDate(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof LocalDate localDate) {
            return localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        if (value instanceof java.sql.Date date) {
            return date.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        return String.valueOf(value);
    }

    private Long toLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }
}
