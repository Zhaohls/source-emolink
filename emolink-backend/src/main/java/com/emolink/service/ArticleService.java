package com.emolink.service;

import com.emolink.common.PageResult;
import com.emolink.vo.ArticleVO;
import com.emolink.vo.FavoriteStatusVO;

import java.util.List;

public interface ArticleService {

    PageResult<ArticleVO> getArticlePage(Long userId, long pageNum, long pageSize, String category);

    ArticleVO getArticleDetail(Long userId, Long articleId);

    PageResult<ArticleVO> searchArticles(Long userId, String keyword, long pageNum, long pageSize);

    FavoriteStatusVO toggleFavorite(Long userId, Long articleId);

    PageResult<ArticleVO> getFavoritePage(Long userId, long pageNum, long pageSize, String keyword);

    List<ArticleVO> recommendArticles(Long userId, int limit);
}
