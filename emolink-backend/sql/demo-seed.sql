-- EmoLink 本地演示数据，仅用于空白的本地演示数据库。
-- 账号：emolink_demo / admin123。请勿在生产环境使用。

USE emolink;

INSERT INTO `user` (`username`, `password`, `nickname`, `avatar`, `role`, `status`, `anonymous_name`)
VALUES (
  'emolink_demo',
  '$2a$10$DEXe3ANf0qbmeO.oCKVf9./sD9z75PsAlqpXDqcDNitSjbVD48vDK',
  '林知夏', '/avatars/default.png', 'user', 1, '晚风来信'
)
ON DUPLICATE KEY UPDATE `nickname` = VALUES(`nickname`), `status` = 1, `role` = 'user';

SET @demo_user_id = (SELECT `id` FROM `user` WHERE `username` = 'emolink_demo' LIMIT 1);

DELETE FROM `tree_hole_comment`
WHERE `post_id` IN (SELECT `id` FROM `tree_hole_post` WHERE `user_id` = @demo_user_id);
DELETE FROM `tree_hole_like`
WHERE `post_id` IN (SELECT `id` FROM `tree_hole_post` WHERE `user_id` = @demo_user_id);
DELETE FROM `tree_hole_post` WHERE `user_id` = @demo_user_id;
DELETE FROM `emotion_diary` WHERE `user_id` = @demo_user_id;
DELETE FROM `emotion_report` WHERE `user_id` = @demo_user_id;

INSERT INTO `emotion_diary`
  (`user_id`, `emotion_type`, `emotion_level`, `trigger_event`, `feeling`, `ai_suggestion`, `created_at`, `updated_at`)
VALUES
  (@demo_user_id, 'calm', 2, '整理好本周的课程安排', '把任务写下来之后，心里比之前踏实了一些。', '把大任务拆成几个容易开始的小步骤，完成一项就给自己一点休息时间。', DATE_SUB(CURDATE(), INTERVAL LEAST(4, WEEKDAY(CURDATE())) DAY) + INTERVAL 19 HOUR, DATE_SUB(CURDATE(), INTERVAL LEAST(4, WEEKDAY(CURDATE())) DAY) + INTERVAL 19 HOUR),
  (@demo_user_id, 'anxious', 4, '第一次在小组讨论中负责汇报', '有些担心自己讲得不够清楚，不过还是把提纲准备好了。', '先练习开场和最重要的三点。紧张时放慢语速，也可以看一眼提纲再继续。', DATE_SUB(CURDATE(), INTERVAL LEAST(3, WEEKDAY(CURDATE())) DAY) + INTERVAL 10 HOUR, DATE_SUB(CURDATE(), INTERVAL LEAST(3, WEEKDAY(CURDATE())) DAY) + INTERVAL 10 HOUR),
  (@demo_user_id, 'calm', 3, '和组员确认了分工', '沟通之后知道接下来该做什么，压力小了一些。', '把确认好的分工记在一个地方，遇到不确定的部分及时和组员核对。', DATE_SUB(CURDATE(), INTERVAL LEAST(3, WEEKDAY(CURDATE())) DAY) + INTERVAL 20 HOUR, DATE_SUB(CURDATE(), INTERVAL LEAST(3, WEEKDAY(CURDATE())) DAY) + INTERVAL 20 HOUR),
  (@demo_user_id, 'happy', 4, '完成了一项拖延了几天的任务', '进度比预想中顺利，感觉今天做得不错。', '记下这次顺利开始的原因，下次可以复用相同的准备方式。', DATE_SUB(CURDATE(), INTERVAL LEAST(2, WEEKDAY(CURDATE())) DAY) + INTERVAL 15 HOUR, DATE_SUB(CURDATE(), INTERVAL LEAST(2, WEEKDAY(CURDATE())) DAY) + INTERVAL 15 HOUR),
  (@demo_user_id, 'sad', 3, '计划没有按预期完成', '有一点失落，也提醒自己偶尔调整计划很正常。', '先区分必须完成和可以顺延的事情，再为下一步留出实际可用的时间。', DATE_SUB(CURDATE(), INTERVAL LEAST(1, WEEKDAY(CURDATE())) DAY) + INTERVAL 11 HOUR, DATE_SUB(CURDATE(), INTERVAL LEAST(1, WEEKDAY(CURDATE())) DAY) + INTERVAL 11 HOUR),
  (@demo_user_id, 'calm', 2, '晚饭后散步了一会儿', '暂时离开屏幕之后，思路清楚了一些。', '把短暂散步当作可选择的休息方式，留意它对自己状态的影响。', DATE_SUB(CURDATE(), INTERVAL LEAST(1, WEEKDAY(CURDATE())) DAY) + INTERVAL 20 HOUR, DATE_SUB(CURDATE(), INTERVAL LEAST(1, WEEKDAY(CURDATE())) DAY) + INTERVAL 20 HOUR),
  (@demo_user_id, 'calm', 2, '上午按计划完成阅读', '节奏不快，但能专注一段时间让我感觉安心。', '延续适合自己的专注时长，结束后安排短暂休息。', CURDATE() + INTERVAL 9 HOUR, CURDATE() + INTERVAL 9 HOUR),
  (@demo_user_id, 'happy', 4, '收到朋友的鼓励', '简单的问候让我觉得有人惦记，也更愿意继续完成今天的计划。', '如果愿意，可以告诉朋友这份支持对你有帮助，也给自己留一点轻松的时间。', NOW(), NOW());

INSERT INTO `emotion_report` (`user_id`, `report_type`, `report_date`, `summary`, `ai_analysis`)
VALUES (
  @demo_user_id,
  'weekly',
  DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY),
  '演示周报：记录了本周不同情境下的情绪变化。',
  '这周既有准备汇报时的紧张，也有完成任务后的轻松。记录显示，提前整理步骤和获得朋友支持时，心情更稳定。下周可以继续把任务拆小，并为休息留出时间。'
)
ON DUPLICATE KEY UPDATE `summary` = VALUES(`summary`), `ai_analysis` = VALUES(`ai_analysis`);

INSERT INTO `tree_hole_post`
  (`user_id`, `anonymous_name`, `content`, `like_count`, `comment_count`, `status`, `created_at`, `updated_at`)
VALUES
  (@demo_user_id, '晚风来信', '最近开始把大任务拆成小步骤，完成一点就划掉一点。事情没有突然变少，但心里没那么乱了。', 18, 2, 1, DATE_SUB(NOW(), INTERVAL LEAST(3, WEEKDAY(CURDATE())) DAY), DATE_SUB(NOW(), INTERVAL LEAST(3, WEEKDAY(CURDATE())) DAY)),
  (@demo_user_id, '海盐汽水', '今天下课后和朋友走了一小段路，聊的都是很普通的事，却觉得整个人松了下来。', 12, 1, 1, DATE_SUB(NOW(), INTERVAL LEAST(2, WEEKDAY(CURDATE())) DAY), DATE_SUB(NOW(), INTERVAL LEAST(2, WEEKDAY(CURDATE())) DAY)),
  (@demo_user_id, '云朵收集员', '第一次做小组汇报之前特别紧张，后来发现认真准备过的内容真的能帮上忙。', 9, 1, 1, DATE_SUB(NOW(), INTERVAL LEAST(1, WEEKDAY(CURDATE())) DAY), DATE_SUB(NOW(), INTERVAL LEAST(1, WEEKDAY(CURDATE())) DAY)),
  (@demo_user_id, '晚风来信', '如果今天只完成了一件重要的事，也算是向前走了一步。想把这句话留给也在赶进度的人。', 24, 3, 1, DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 5 HOUR)),
  (@demo_user_id, '纸飞机', '把手机放远一点，专心做了二十五分钟。原来开始之后没有想象中那么难。', 7, 0, 1, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR)),
  (@demo_user_id, '待处理示例', '这是一条仅供管理员演示审核流程的虚构内容。', 0, 0, 0, NOW(), NOW());

INSERT INTO `tree_hole_comment` (`post_id`, `user_id`, `anonymous_name`, `content`, `status`, `created_at`)
SELECT `id`, @demo_user_id, '海盐汽水', '谢谢你分享，今天也试着先做一小步。', 1, DATE_SUB(NOW(), INTERVAL 2 HOUR)
FROM `tree_hole_post` WHERE `user_id` = @demo_user_id AND `content` LIKE '最近开始把大任务%';
INSERT INTO `tree_hole_comment` (`post_id`, `user_id`, `anonymous_name`, `content`, `status`, `created_at`)
SELECT `id`, @demo_user_id, '云朵收集员', '这句话也提醒到我了，祝你今天顺利。', 1, DATE_SUB(NOW(), INTERVAL 1 HOUR)
FROM `tree_hole_post` WHERE `user_id` = @demo_user_id AND `content` LIKE '最近开始把大任务%';
