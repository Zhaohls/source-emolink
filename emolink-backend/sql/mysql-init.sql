-- ============================================
-- EmoLink 大学生情绪管理平台 数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS emolink 
  DEFAULT CHARACTER SET utf8mb4 
  DEFAULT COLLATE utf8mb4_general_ci;

USE emolink;

-- ============================================
-- 1. 用户表
-- ============================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名（登录账号）',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(255) DEFAULT '/avatars/default.png' COMMENT '头像路径',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `gender` TINYINT DEFAULT 0 COMMENT '性别：0-未知 1-男 2-女',
  `bio` VARCHAR(200) DEFAULT NULL COMMENT '个人简介',
  `role` VARCHAR(10) DEFAULT 'user' COMMENT '角色：user-普通用户 admin-管理员',
  `anonymous_name` VARCHAR(50) DEFAULT NULL COMMENT '匿名昵称（树洞/信箱使用）',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================
-- 2. 情绪日记表
-- ============================================
DROP TABLE IF EXISTS `emotion_diary`;
CREATE TABLE `emotion_diary` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日记ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `emotion_type` VARCHAR(20) NOT NULL COMMENT '情绪类型：happy/calm/anxious/sad/angry/fearful',
  `emotion_level` TINYINT NOT NULL COMMENT '情绪强度：1-5',
  `trigger_event` VARCHAR(500) DEFAULT NULL COMMENT '触发事件',
  `feeling` TEXT DEFAULT NULL COMMENT '内心感受',
  `ai_suggestion` TEXT DEFAULT NULL COMMENT 'AI生成的调节建议',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_user_emotion` (`user_id`, `emotion_type`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='情绪日记表';

-- ============================================
-- 3. 情绪报告表
-- ============================================
DROP TABLE IF EXISTS `emotion_report`;
CREATE TABLE `emotion_report` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '报告ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `report_type` VARCHAR(10) NOT NULL COMMENT '报告类型：daily/weekly',
  `report_date` DATE NOT NULL COMMENT '报告日期（日报=当天，周报=周一日期）',
  `summary` TEXT DEFAULT NULL COMMENT '统计摘要',
  `ai_analysis` TEXT DEFAULT NULL COMMENT 'AI分析内容',
  `emotion_data_json` JSON DEFAULT NULL COMMENT '情绪数据JSON（存储图表数据）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_report` (`user_id`, `report_type`, `report_date`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='情绪报告表';

-- ============================================
-- 4. 心理知识文章表
-- ============================================
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
  `content` TEXT NOT NULL COMMENT '文章内容（Markdown格式）',
  `category` VARCHAR(20) NOT NULL COMMENT '分类：anxiety/stress/emotion/social/growth',
  `view_count` INT DEFAULT 0 COMMENT '阅读量',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-下架 1-正常',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  FULLTEXT KEY `ft_title_content` (`title`, `content`) WITH PARSER ngram
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='心理知识文章表';

-- ============================================
-- 5. 文章收藏表
-- ============================================
DROP TABLE IF EXISTS `article_favorite`;
CREATE TABLE `article_favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_article` (`user_id`, `article_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章收藏表';

-- ============================================
-- 6. 树洞帖子表
-- ============================================
DROP TABLE IF EXISTS `tree_hole_post`;
CREATE TABLE `tree_hole_post` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '发布者用户ID',
  `anonymous_name` VARCHAR(50) NOT NULL COMMENT '匿名昵称',
  `content` TEXT NOT NULL COMMENT '帖子内容',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `comment_count` INT DEFAULT 0 COMMENT '评论数',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-审核中 1-正常 2-违规删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='树洞帖子表';

-- ============================================
-- 7. 树洞评论表
-- ============================================
DROP TABLE IF EXISTS `tree_hole_comment`;
CREATE TABLE `tree_hole_comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` BIGINT NOT NULL COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '评论者用户ID',
  `anonymous_name` VARCHAR(50) NOT NULL COMMENT '匿名昵称',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-审核中 1-正常 2-违规删除',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='树洞评论表';

-- ============================================
-- 8. 树洞点赞表
-- ============================================
DROP TABLE IF EXISTS `tree_hole_like`;
CREATE TABLE `tree_hole_like` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `post_id` BIGINT NOT NULL COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`, `user_id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='树洞点赞表';

-- ============================================
-- 9. 伙伴匹配表
-- ============================================
DROP TABLE IF EXISTS `partner_match`;
CREATE TABLE `partner_match` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '匹配ID',
  `user_a_id` BIGINT NOT NULL COMMENT '用户A的ID',
  `user_b_id` BIGINT DEFAULT NULL COMMENT '用户B的ID（等待匹配时为NULL）',
  `status` VARCHAR(10) NOT NULL DEFAULT 'waiting' COMMENT '状态：waiting/matched/ended',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `matched_at` DATETIME DEFAULT NULL COMMENT '匹配成功时间',
  `ended_at` DATETIME DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_a` (`user_a_id`),
  KEY `idx_user_b` (`user_b_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='伙伴匹配表';

-- ============================================
-- 10. 伙伴信箱消息表
-- ============================================
DROP TABLE IF EXISTS `partner_message`;
CREATE TABLE `partner_message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `match_id` BIGINT NOT NULL COMMENT '匹配ID',
  `sender_id` BIGINT NOT NULL COMMENT '发送者用户ID',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `is_read` TINYINT DEFAULT 0 COMMENT '是否已读：0-未读 1-已读',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_match_id` (`match_id`),
  KEY `idx_sender_id` (`sender_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='伙伴信箱消息表';

-- ============================================
-- 管理员默认账号（密码：admin123）
-- ============================================
INSERT INTO `user` (`username`, `password`, `nickname`, `avatar`, `role`, `status`)
VALUES ('admin', '$2a$10$DEXe3ANf0qbmeO.oCKVf9./sD9z75PsAlqpXDqcDNitSjbVD48vDK', '系统管理员', '/avatars/default.png', 'admin', 1);
