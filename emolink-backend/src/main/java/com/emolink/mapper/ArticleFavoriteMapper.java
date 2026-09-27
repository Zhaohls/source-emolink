package com.emolink.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.emolink.entity.Article;
import com.emolink.entity.ArticleFavorite;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ArticleFavoriteMapper extends BaseMapper<ArticleFavorite> {

    @Select("""
            <script>
            SELECT a.*
            FROM article_favorite af
            INNER JOIN article a ON a.id = af.article_id
            WHERE af.user_id = #{userId}
              AND a.status = 1
              <if test="keyword != null and keyword != ''">
                AND (
                  a.title LIKE CONCAT('%', #{keyword}, '%')
                  OR a.content LIKE CONCAT('%', #{keyword}, '%')
                )
              </if>
            ORDER BY af.created_at DESC
            LIMIT #{offset}, #{pageSize}
            </script>
            """)
    List<Article> selectVisibleFavoriteArticles(@Param("userId") Long userId,
                                                @Param("offset") long offset,
                                                @Param("pageSize") long pageSize,
                                                @Param("keyword") String keyword);

    @Select("""
            <script>
            SELECT COUNT(1)
            FROM article_favorite af
            INNER JOIN article a ON a.id = af.article_id
            WHERE af.user_id = #{userId}
              AND a.status = 1
              <if test="keyword != null and keyword != ''">
                AND (
                  a.title LIKE CONCAT('%', #{keyword}, '%')
                  OR a.content LIKE CONCAT('%', #{keyword}, '%')
                )
              </if>
            </script>
            """)
    long countVisibleFavoriteArticles(@Param("userId") Long userId,
                                      @Param("keyword") String keyword);
}
