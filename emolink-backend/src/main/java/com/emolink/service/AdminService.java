package com.emolink.service;

import com.emolink.common.PageResult;
import com.emolink.dto.AdminArticleDTO;
import com.emolink.vo.AdminArticleVO;
import com.emolink.vo.AdminDashboardVO;
import com.emolink.vo.AdminTreeHoleCommentVO;
import com.emolink.vo.AdminTreeHolePostVO;
import com.emolink.vo.AdminUserVO;

public interface AdminService {

    AdminDashboardVO getDashboard();

    PageResult<AdminUserVO> getUserPage(long pageNum, long pageSize, String keyword, Integer status, Long currentUserId);

    void updateUserStatus(Long currentUserId, Long userId, Integer status);

    PageResult<AdminArticleVO> getArticlePage(long pageNum, long pageSize, String category, Integer status);

    void createArticle(AdminArticleDTO dto);

    void updateArticle(Long articleId, AdminArticleDTO dto);

    void deleteArticle(Long articleId);

    void updateArticleStatus(Long articleId, Integer status);

    PageResult<AdminTreeHolePostVO> getTreeHolePostPage(long pageNum, long pageSize, Integer status);

    void auditTreeHolePost(Long postId, Integer status);

    void deleteTreeHolePost(Long postId);

    PageResult<AdminTreeHoleCommentVO> getTreeHoleCommentPage(long pageNum, long pageSize, Integer status);

    void auditTreeHoleComment(Long commentId, Integer status);

    void deleteTreeHoleComment(Long commentId);
}
