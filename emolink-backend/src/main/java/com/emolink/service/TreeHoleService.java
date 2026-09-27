package com.emolink.service;

import com.emolink.common.PageResult;
import com.emolink.dto.CommentDTO;
import com.emolink.dto.PostDTO;
import com.emolink.vo.CommentVO;
import com.emolink.vo.LikeStatusVO;
import com.emolink.vo.PostVO;

import java.util.List;

public interface TreeHoleService {

    PostVO createPost(Long userId, PostDTO dto);

    PageResult<PostVO> getPostPage(Long userId, long pageNum, long pageSize, String sort, String keyword);

    PostVO getPostDetail(Long userId, Long postId);

    CommentVO createComment(Long userId, CommentDTO dto);

    LikeStatusVO toggleLike(Long userId, Long postId);

    List<PostVO> getHotPosts(Long userId, int limit);
}
