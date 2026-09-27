package com.emolink.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.emolink.common.PageResult;
import com.emolink.entity.Article;
import com.emolink.entity.ArticleFavorite;
import com.emolink.entity.EmotionDiary;
import com.emolink.exception.BusinessException;
import com.emolink.mapper.ArticleFavoriteMapper;
import com.emolink.mapper.ArticleMapper;
import com.emolink.mapper.EmotionDiaryMapper;
import com.emolink.service.ArticleService;
import com.emolink.vo.ArticleVO;
import com.emolink.vo.FavoriteStatusVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final ArticleFavoriteMapper articleFavoriteMapper;
    private final EmotionDiaryMapper emotionDiaryMapper;

    public ArticleServiceImpl(ArticleMapper articleMapper,
                              ArticleFavoriteMapper articleFavoriteMapper,
                              EmotionDiaryMapper emotionDiaryMapper) {
        this.articleMapper = articleMapper;
        this.articleFavoriteMapper = articleFavoriteMapper;
        this.emotionDiaryMapper = emotionDiaryMapper;
    }

    @Override
    public PageResult<ArticleVO> getArticlePage(Long userId, long pageNum, long pageSize, String category) {
        Page<Article> page = articleMapper.selectPage(new Page<>(pageNum, pageSize), Wrappers.<Article>lambdaQuery()
                .eq(Article::getStatus, 1)
                .eq(StringUtils.isNotBlank(category), Article::getCategory, category)
                .orderByDesc(Article::getCreatedAt));
        Set<Long> favoriteIds = listFavoriteArticleIds(userId, page.getRecords().stream().map(Article::getId).toList());
        return PageResult.from(page, page.getRecords().stream()
                .map(article -> toArticleVO(article, favoriteIds.contains(article.getId())))
                .toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ArticleVO getArticleDetail(Long userId, Long articleId) {
        Article article = getArticleOrThrow(articleId);
        article.setViewCount((article.getViewCount() == null ? 0 : article.getViewCount()) + 1);
        articleMapper.updateById(article);
        boolean favorited = !listFavoriteArticleIds(userId, List.of(articleId)).isEmpty();
        return toArticleVO(article, favorited);
    }

    @Override
    public PageResult<ArticleVO> searchArticles(Long userId, String keyword, long pageNum, long pageSize) {
        Page<Article> page = articleMapper.selectPage(new Page<>(pageNum, pageSize), Wrappers.<Article>lambdaQuery()
                .eq(Article::getStatus, 1)
                .and(StringUtils.isNotBlank(keyword), wrapper -> wrapper
                        .like(Article::getTitle, keyword)
                        .or()
                        .like(Article::getContent, keyword))
                .orderByDesc(Article::getCreatedAt));
        Set<Long> favoriteIds = listFavoriteArticleIds(userId, page.getRecords().stream().map(Article::getId).toList());
        return PageResult.from(page, page.getRecords().stream()
                .map(article -> toArticleVO(article, favoriteIds.contains(article.getId())))
                .toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FavoriteStatusVO toggleFavorite(Long userId, Long articleId) {
        getArticleOrThrow(articleId);
        ArticleFavorite favorite = articleFavoriteMapper.selectOne(Wrappers.<ArticleFavorite>lambdaQuery()
                .eq(ArticleFavorite::getUserId, userId)
                .eq(ArticleFavorite::getArticleId, articleId)
                .last("LIMIT 1"));
        if (favorite != null) {
            articleFavoriteMapper.deleteById(favorite.getId());
            return new FavoriteStatusVO(false);
        }

        ArticleFavorite entity = new ArticleFavorite();
        entity.setUserId(userId);
        entity.setArticleId(articleId);
        articleFavoriteMapper.insert(entity);
        return new FavoriteStatusVO(true);
    }

    @Override
    public PageResult<ArticleVO> getFavoritePage(Long userId, long pageNum, long pageSize, String keyword) {
        long total = articleFavoriteMapper.countVisibleFavoriteArticles(userId, keyword);
        if (total == 0) {
            return PageResult.empty(pageNum, pageSize);
        }
        long offset = (pageNum - 1) * pageSize;
        List<ArticleVO> records = articleFavoriteMapper.selectVisibleFavoriteArticles(userId, offset, pageSize, keyword).stream()
                .map(article -> toArticleVO(article, true))
                .toList();
        long pages = (total + pageSize - 1) / pageSize;
        return new PageResult<>(records, total, pageNum, pageSize, pages);
    }

    @Override
    public List<ArticleVO> recommendArticles(Long userId, int limit) {
        String preferredCategory = resolvePreferredCategory(userId);
        List<Article> articles = articleMapper.selectList(Wrappers.<Article>lambdaQuery()
                .eq(Article::getStatus, 1)
                .eq(StringUtils.isNotBlank(preferredCategory), Article::getCategory, preferredCategory)
                .orderByDesc(Article::getCreatedAt)
                .last("LIMIT " + limit));
        if (articles.size() < limit) {
            List<Long> existingIds = articles.stream().map(Article::getId).toList();
            List<Article> fallback = articleMapper.selectList(Wrappers.<Article>lambdaQuery()
                    .eq(Article::getStatus, 1)
                    .notIn(!existingIds.isEmpty(), Article::getId, existingIds)
                    .orderByDesc(Article::getViewCount)
                    .last("LIMIT " + (limit - articles.size())));
            articles = new java.util.ArrayList<>(articles);
            articles.addAll(fallback);
        }
        Set<Long> favoriteIds = listFavoriteArticleIds(userId, articles.stream().map(Article::getId).toList());
        return articles.stream()
                .limit(limit)
                .map(article -> toArticleVO(article, favoriteIds.contains(article.getId())))
                .toList();
    }

    private Article getArticleOrThrow(Long articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null || article.getStatus() == null || article.getStatus() != 1) {
            throw new BusinessException(404, "文章不存在");
        }
        return article;
    }

    private Set<Long> listFavoriteArticleIds(Long userId, List<Long> articleIds) {
        if (userId == null || articleIds == null || articleIds.isEmpty()) {
            return Set.of();
        }
        return articleFavoriteMapper.selectList(Wrappers.<ArticleFavorite>lambdaQuery()
                        .eq(ArticleFavorite::getUserId, userId)
                        .in(ArticleFavorite::getArticleId, articleIds))
                .stream()
                .map(ArticleFavorite::getArticleId)
                .collect(Collectors.toSet());
    }

    private ArticleVO toArticleVO(Article article, boolean favorited) {
        ArticleVO vo = new ArticleVO();
        BeanUtils.copyProperties(article, vo);
        vo.setIsFavorited(favorited);
        return vo;
    }

    private String resolvePreferredCategory(Long userId) {
        List<EmotionDiary> diaries = emotionDiaryMapper.selectList(Wrappers.<EmotionDiary>lambdaQuery()
                .eq(EmotionDiary::getUserId, userId)
                .orderByDesc(EmotionDiary::getCreatedAt)
                .last("LIMIT 20"));
        if (diaries.isEmpty()) {
            return null;
        }
        String dominantEmotion = diaries.stream()
                .collect(Collectors.groupingBy(EmotionDiary::getEmotionType, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.<String, Long>comparingByValue().thenComparing(Map.Entry.comparingByKey(Comparator.naturalOrder())))
                .map(Map.Entry::getKey)
                .orElse(null);
        if (dominantEmotion == null) {
            return null;
        }
        return switch (dominantEmotion) {
            case "anxious", "fearful" -> "anxiety";
            case "sad", "angry" -> "emotion";
            case "happy", "calm" -> "growth";
            default -> null;
        };
    }
}
