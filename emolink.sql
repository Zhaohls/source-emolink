/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45)
 Source Host           : localhost:3306
 Source Schema         : emolink

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45)
 File Encoding         : 65001

 Date: 16/04/2026 15:05:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `title` varchar(200) NOT NULL COMMENT '文章标题',
  `content` text NOT NULL COMMENT '文章内容（Markdown格式）',
  `category` varchar(20) NOT NULL COMMENT '分类：anxiety/stress/emotion/social/growth',
  `view_count` int DEFAULT '0' COMMENT '阅读量',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-下架 1-正常',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  FULLTEXT KEY `ft_title_content` (`title`,`content`) /*!50100 WITH PARSER `ngram` */ 
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='心理知识文章表';

-- ----------------------------
-- Records of article
-- ----------------------------
BEGIN;
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (1, '5个简单的深呼吸练习，帮你快速缓解焦虑', '## 什么是深呼吸练习？\n\n深呼吸练习是一种简单有效的放松技巧，通过调节呼吸来激活副交感神经系统，从而降低心率、缓解肌肉紧张。\n\n### 练习一：4-7-8呼吸法\n\n1. 用鼻子吸气4秒\n2. 屏住呼吸7秒\n3. 用嘴巴缓慢呼气8秒\n4. 重复3-4次\n\n### 练习二：腹式呼吸\n\n1. 将手放在腹部\n2. 吸气时让腹部鼓起\n3. 呼气时让腹部收缩\n4. 每次练习5分钟\n\n### 练习三：方块呼吸\n\n1. 吸气4秒\n2. 屏息4秒\n3. 呼气4秒\n4. 屏息4秒\n5. 重复4-5次\n\n### 练习四：交替鼻孔呼吸\n\n用拇指按住右鼻孔，用左鼻孔吸气，然后按住左鼻孔，用右鼻孔呼气。交替进行。\n\n### 练习五：叹气式呼吸\n\n深吸一口气，然后大声叹气呼出。这种方式特别适合在感到突然焦虑时使用。\n\n> 💡 **小贴士**：坚持每天练习，效果更好！', 'anxiety', 2, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (2, '考前焦虑？试试这3个科学方法', '## 为什么会有考前焦虑？\n\n考前焦虑是一种正常的心理反应，适度的焦虑甚至可以提高我们的注意力和表现。但当焦虑过度时，就需要学会调节。\n\n### 方法一：积极自我对话\n\n将消极想法转化为积极想法：\n- ❌ \"我肯定考不好\"\n- ✅ \"我已经做了充分准备，我会尽力而为\"\n\n### 方法二：渐进式肌肉放松\n\n从脚趾开始，逐步紧绷和放松每个肌肉群：\n1. 紧绷脚趾5秒，然后放松\n2. 紧绷小腿5秒，然后放松\n3. 依次向上，直到面部肌肉\n\n### 方法三：可视化练习\n\n闭上眼睛，想象自己在考试中从容作答的场景，感受那种自信和平静。\n\n> 记住：焦虑不是你的敌人，学会与它共处。', 'anxiety', 0, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (3, '社交焦虑不可怕：大学生社交恐惧自助指南', '## 什么是社交焦虑？\n\n社交焦虑是指在社交场合中感到过度紧张、害怕被评判的心理状态。在大学生群体中非常常见。\n\n### 认识你的焦虑\n\n社交焦虑的常见表现：\n- 害怕在公共场合发言\n- 担心被别人评判\n- 回避社交活动\n- 在人多时感到不自在\n\n### 自助方法\n\n#### 1. 小步前进法\n\n不要强迫自己一下子克服所有恐惧，从小事开始：\n- 先和一个人聊天\n- 再尝试小组讨论\n- 最后尝试公开发言\n\n#### 2. 认知重建\n\n质疑你的负面想法：\n- \"别人真的在注意我吗？\"\n- \"即使犯错，真的有那么严重吗？\"\n\n#### 3. 暴露练习\n\n逐渐增加社交活动的频率和难度，让自己适应社交环境。', 'anxiety', 1, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (4, '失眠与焦虑的恶性循环，如何打破？', '## 焦虑与失眠的关系\n\n焦虑导致失眠，失眠又加重焦虑，形成恶性循环。打破这个循环需要从多个方面入手。\n\n### 睡前放松routine\n\n1. **断电时间**：睡前1小时放下手机\n2. **温水浴**：睡前洗个热水澡\n3. **轻音乐**：播放自然声音或白噪音\n4. **阅读**：读一本轻松的书\n\n### 认知行为技巧\n\n#### 担忧记录法\n\n睡前15分钟，把所有担忧写在纸上，告诉自己\"明天再处理\"。\n\n#### 身体扫描冥想\n\n从头顶开始，逐渐关注身体每个部位，感受并放松紧绷的区域。\n\n### 生活习惯调整\n\n- 固定作息时间\n- 下午2点后避免咖啡因\n- 白天适度运动\n- 卧室只用于睡觉', 'anxiety', 0, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (5, '学会说\"不\"：克服讨好型人格的焦虑', '## 什么是讨好型人格？\n\n讨好型人格是指过度关注他人感受，忽视自身需求，难以拒绝他人请求的行为模式。这种模式往往源于对被拒绝的焦虑。\n\n### 自我检测\n\n你是否经常：\n- 害怕让别人失望\n- 即使不愿意也答应别人\n- 为了维持关系而委屈自己\n- 回避冲突\n\n### 如何改变\n\n#### 1. 认识自己的需求\n\n每天写下3个\"我想要\"的事情，习惯关注自己。\n\n#### 2. 练习温和的拒绝\n\n- \"谢谢你想到我，但这次我无法参加\"\n- \"我需要一些时间考虑一下\"\n- \"这件事超出了我的能力范围\"\n\n#### 3. 建立边界\n\n明确自己的底线，并温和而坚定地维护它。', 'anxiety', 1, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (6, '大学生压力管理：时间管理四象限法', '## 为什么你总感觉时间不够用？\n\n大学生面临课程作业、社团活动、实习就业等多重任务，如果没有有效的时间管理方法，很容易产生压力和焦虑。\n\n### 艾森豪威尔矩阵\n\n将任务按照紧急性和重要性分为四个象限：\n\n| | 紧急 | 不紧急 |\n|---|---|---|\n| **重要** | ① 立即做 | ② 计划做 |\n| **不重要** | ③ 委托做 | ④ 不做 |\n\n### 实践建议\n\n1. **每周日规划下周任务**\n2. **每天晚上列出明天的TOP 3**\n3. **使用番茄工作法**：25分钟专注+5分钟休息\n4. **学会拒绝第④象限的诱惑**', 'stress', 1, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (7, '运动是最好的减压药：适合大学生的运动指南', '## 运动与心理健康\n\n研究表明，每周150分钟的中等强度运动可以显著降低焦虑和抑郁水平。运动时大脑会释放内啡肽，这是天然的\"快乐激素\"。\n\n### 推荐运动\n\n#### 🏃 有氧运动\n- 跑步/快走（20-30分钟）\n- 游泳\n- 骑自行车\n\n#### 🧘 身心运动\n- 瑜伽\n- 太极\n- 冥想步行\n\n#### 💪 力量训练\n- 俯卧撑\n- 深蹲\n- 平板支撑\n\n### 适合大学生的运动计划\n\n| 周一 | 周三 | 周五 | 周末 |\n|---|---|---|---|\n| 跑步30分钟 | 瑜伽30分钟 | 力量训练20分钟 | 户外运动60分钟 |', 'stress', 0, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (8, '期末季生存指南：如何高效复习不崩溃', '## 期末季的压力来源\n\n多门课程集中考试、论文截止日期逼近、复习资料堆积如山...这些都是大学生期末季的典型压力源。\n\n### 高效复习策略\n\n#### 1. 间隔重复法\n\n不要连续复习同一科目，用间隔的方式：\n- 第1天学习新内容\n- 第3天复习\n- 第7天再次复习\n- 第14天最终复习\n\n#### 2. 主动回忆\n\n合上书本，尝试回忆学过的内容，比反复阅读效果好3倍。\n\n#### 3. 费曼学习法\n\n用自己的话把知识讲给\"别人\"听（哪怕是对着墙讲），如果讲不清楚，说明还没真正理解。\n\n### 心理调节\n\n- 允许自己不完美\n- 每天保证7小时睡眠\n- 复习间隙做5分钟拉伸\n- 奖励自己完成的每个小目标', 'stress', 1, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (9, '学会放松：正念冥想入门指南', '## 什么是正念冥想？\n\n正念冥想是一种将注意力集中在当下的练习，不评判、不分析，只是觉察。研究证实，每天10分钟的正念练习可以减少34%的压力水平。\n\n### 入门练习\n\n#### 练习一：呼吸觉察（5分钟）\n\n1. 找一个安静的地方坐下\n2. 闭上眼睛\n3. 将注意力集中在呼吸上\n4. 当走神时，温柔地把注意力拉回来\n\n#### 练习二：身体扫描（10分钟）\n\n从头顶开始，逐渐觉察身体每个部位的感觉，不尝试改变，只是感受。\n\n#### 练习三：行走冥想（10分钟）\n\n慢慢地走路，感受脚底与地面的接触，感受每一步的动作。\n\n### 建立习惯\n\n- 选择固定的时间练习（建议早上起床后）\n- 从3分钟开始，逐渐增加\n- 使用冥想APP辅助', 'stress', 0, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (10, '你不是得了拖延症，你只是在逃避压力', '## 拖延的本质\n\n拖延并不是懒惰，而是一种情绪调节策略。当面对令人焦虑或不愉快的任务时，大脑会选择逃避来获得暂时的舒适。\n\n### 为什么你会拖延？\n\n- **完美主义**：害怕做不好，所以不开始\n- **任务太大**：不知道从何做起\n- **即时满足**：手机/游戏比学习更有吸引力\n\n### 实用对策\n\n#### 1. 两分钟规则\n\n如果一件事只需要两分钟，立刻去做。\n\n#### 2. 任务分解\n\n把大任务拆成小到\"不可能失败\"的步骤。\n\n#### 3. 环境设计\n\n- 去图书馆而不是宿舍\n- 把手机放在看不到的地方\n- 把学习材料放在桌面上\n\n#### 4. 自我同情\n\n拖延后不要自我攻击，接受已经发生的事实，重新开始。', 'stress', 0, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (11, '认识你的情绪：情绪识别与标记练习', '## 为什么要识别情绪？\n\n心理学研究发现，仅仅是给情绪\"命名\"这个动作，就能减少杏仁核（大脑中的情绪中心）的活跃程度，从而减轻情绪的强度。\n\n### 基本情绪轮\n\n人类有6种基本情绪：\n- 😊 **快乐** (Happy)\n- 😢 **悲伤** (Sad)\n- 😠 **愤怒** (Angry)\n- 😨 **恐惧** (Fear)\n- 😌 **平静** (Calm)\n- 😰 **焦虑** (Anxious)\n\n### 每日练习\n\n每天3个时间点（早、中、晚），花1分钟问自己：\n1. 我现在感受到什么情绪？\n2. 情绪强度是多少？（1-5分）\n3. 是什么引发了这个情绪？\n\n### 情绪日记的价值\n\n坚持记录情绪日记，你会发现：\n- 你的情绪模式和规律\n- 常见的情绪触发因素\n- 你的情绪调节能力在提升', 'emotion', 0, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (12, '愤怒管理：当你想发火的时候该怎么做', '## 愤怒不是问题，失控才是\n\n愤怒是一种正常的情绪，它告诉我们的边界被侵犯了。关键不是消除愤怒，而是学会以健康的方式表达它。\n\n### 紧急制动：STOP法\n\n当你感到愤怒升级时：\n- **S**top - 停下来\n- **T**ake a breath - 深呼吸\n- **O**bserve - 观察自己的感受\n- **P**roceed - 选择如何回应\n\n### 长期策略\n\n#### 1. 识别愤怒信号\n\n身体的信号：心跳加速、面部发热、拳头紧握\n\n#### 2. 理性表达\n\n使用\"我\"语句：\n- ❌ \"你总是这样粗心！\"\n- ✅ \"我感到很沮丧，因为这件事对我很重要\"\n\n#### 3. 转移能量\n\n愤怒时进行体力活动：跑步、打拳击沙袋、做俯卧撑', 'emotion', 0, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (13, '悲伤是可以的：如何与负面情绪共处', '## 你不必一直快乐\n\n在社交媒体的影响下，我们往往觉得自己应该一直保持正能量。但事实是，悲伤、失落、沮丧都是正常的人类情绪。\n\n### 允许自己悲伤\n\n- 哭泣是一种有效的情绪释放方式\n- 给自己一个\"悲伤时间\"，比如允许自己难过30分钟\n- 不要因为\"不够坚强\"而自我责备\n\n### 与负面情绪共处的技巧\n\n#### 1. 情绪观察者\n\n把自己想象为一个观察者，观察情绪如同观察天气——它来了，它会走。\n\n#### 2. 自我安抚\n\n像安慰好朋友一样安慰自己：\n- \"这种感觉很难受，但它会过去的\"\n- \"现在的我需要一些关爱\"\n\n#### 3. 寻求连接\n\n不要独自承受，找一个信任的人倾诉，或者写在日记里。\n\n### 何时需要专业帮助？\n\n如果悲伤持续超过两周，严重影响学习和生活，请寻求学校心理咨询中心的帮助。', 'emotion', 0, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (14, '情绪急救包：5个快速调节情绪的小技巧', '## 情绪SOS\n\n有时候我们需要在短时间内调节情绪，以下5个方法可以在5分钟内帮你恢复平静。\n\n### 技巧1：5-4-3-2-1接地练习\n\n关注你周围的：\n- 5样你能**看到**的东西\n- 4样你能**触摸到**的东西\n- 3样你能**听到**的声音\n- 2样你能**闻到**的气味\n- 1样你能**尝到**的味道\n\n### 技巧2：冰块法\n\n握一块冰块在手中，冰冷的刺激可以帮助你从强烈的情绪中回到当下。\n\n### 技巧3：蝴蝶拥抱\n\n双手交叉放在胸前，左右交替轻拍，就像蝴蝶的翅膀。这是一种EMDR技术，可以安抚神经系统。\n\n### 技巧4：色彩冥想\n\n想象一种让你感到平静的颜色（如蓝色），随着每次呼吸，这种颜色充满你的全身。\n\n### 技巧5：有节奏的运动\n\n原地踏步、跳绳、甩手——有节奏的运动可以调节自主神经系统。', 'emotion', 0, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (15, '建立你的情绪韧性：从脆弱到强大', '## 什么是情绪韧性？\n\n情绪韧性是指从困难或挫折中恢复的能力。它不是天生的，而是可以通过练习来培养的。\n\n### 培养情绪韧性的四个方面\n\n#### 1. 自我意识\n\n了解自己的情绪模式，知道什么会触发你、什么能安抚你。\n\n#### 2. 思维灵活性\n\n学会从多个角度看待问题：\n- 这件事最坏的结果是什么？\n- 最好的结果呢？\n- 最可能的结果是什么？\n\n#### 3. 社会连接\n\n维护2-3个深度的友谊关系，在困难时有人可以依靠。\n\n#### 4. 积极行动\n\n面对困难时，问自己\"我现在能做的最小的一步是什么？\"然后去做。\n\n### 每日韧性训练\n\n每天写下：\n- 今天我克服的一个小困难\n- 今天我感恩的三件事\n- 明天我期待的一件事', 'emotion', 0, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (16, '大学生人际关系必修课：如何建立深度友谊', '## 从相识到深交\n\n大学是建立终身友谊的黄金时期，但很多同学发现维持深度友谊比想象中困难。\n\n### 建立友谊的四个阶段\n\n1. **初识**：共同的环境（课堂、社团）\n2. **了解**：分享兴趣和经历\n3. **信任**：互相支持和保守秘密\n4. **深交**：无条件的接纳和理解\n\n### 实用技巧\n\n#### 主动出击\n- 主动邀请同学一起吃饭\n- 参加社团活动认识志同道合的人\n\n#### 倾听技巧\n- 放下手机，给予对方全部注意力\n- 用\"你的意思是...?\"来确认理解\n- 不要急着给建议，先表达理解\n\n#### 维护友谊\n- 定期联系，不要只在需要帮助时才找人\n- 记住朋友的重要日子\n- 在朋友困难时主动伸出援手', 'social', 0, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (17, '宿舍关系处理指南：四年和谐共处的秘诀', '## 宿舍矛盾的常见来源\n\n生活习惯不同（作息时间、卫生习惯）、价值观差异、缺乏沟通，这些都可能导致宿舍关系紧张。\n\n### 和谐共处原则\n\n#### 1. 建立\"宿舍公约\"\n\n开学初和室友一起制定规则：\n- 熄灯时间\n- 公共区域打扫安排\n- 是否允许带朋友来\n\n#### 2. 直接沟通\n\n不要背后抱怨，有问题直接、温和地提出：\n- \"我注意到...我感到...我希望...\"\n\n#### 3. 尊重差异\n\n每个人的生活方式不同，在不影响他人的前提下，给予对方自由。\n\n#### 4. 保持适当距离\n\n好的室友关系不等于要形影不离，每个人都需要私人空间。', 'social', 0, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (18, '如何优雅地拒绝：大学生社交边界设定', '## 为什么拒绝这么难？\n\n我们害怕拒绝别人后会被讨厌、被排斥。但事实上，一个总是说\"好\"的人，反而容易失去别人的尊重。\n\n### 三种拒绝方式\n\n#### 1. 直接拒绝\n\n\"谢谢你的邀请，但我今天需要复习，去不了。\"\n\n#### 2. 延迟回应\n\n\"让我想想，我稍后回复你。\"（给自己思考的时间）\n\n#### 3. 提供替代方案\n\n\"这个周末不行，下周末怎么样？\"\n\n### 心理建设\n\n- 拒绝一个请求≠拒绝一个人\n- 你没有义务满足所有人的期望\n- 明确的边界反而会让关系更健康', 'social', 0, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (19, '冲突解决技巧：化干戈为玉帛的沟通艺术', '## 冲突的积极意义\n\n冲突并不总是坏事。处理得当，冲突可以加深理解、促进关系成长。\n\n### 冲突解决五步法\n\n#### 步骤一：冷静\n\n在情绪激动时不要试图解决问题。给自己至少15分钟冷静下来。\n\n#### 步骤二：倾听\n\n让对方说完，不要打断。尝试理解对方的立场和感受。\n\n#### 步骤三：表达\n\n使用\"我\"语句表达自己的感受和需求：\n\"当...的时候，我感到...，我希望...\"\n\n#### 步骤四：寻找共同点\n\n找到双方的共同目标：\"我们都希望...\"\n\n#### 步骤五：达成协议\n\n提出双方都能接受的解决方案，必要时各退一步。', 'social', 0, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (20, '网络社交与真实社交：如何找到平衡', '## 数字时代的社交困境\n\n大学生平均每天使用社交媒体3-4小时，但很多人感到越社交越孤独。\n\n### 网络社交的陷阱\n\n- **社交比较**：别人的朋友圈都是光鲜亮丽的\n- **表面连接**：点赞评论不等于真实的情感链接\n- **注意力分散**：和朋友在一起却在刷手机\n\n### 找到平衡\n\n#### 1. 设定使用时间\n\n给社交媒体设定每日时间上限（建议不超过1小时）。\n\n#### 2. 线下优先\n\n约朋友面对面聊天，效果远胜于线上聊天。\n\n#### 3. 有意义的互动\n\n减少无目的的刷手机，把时间用在深度的对话和互动上。\n\n#### 4. 定期数字排毒\n\n每周选一天远离社交媒体，感受真实的生活。', 'social', 0, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (21, '大学四年自我成长路线图', '## 大学不只是学习\n\n大学四年是人生中最重要的成长阶段之一。除了专业学习，还有很多方面值得投入。\n\n### 大一：探索期\n\n- 多参加不同的社团和活动\n- 广泛阅读，发现自己的兴趣\n- 建立良好的学习习惯\n\n### 大二：深耕期\n\n- 确定自己的方向\n- 深入学习专业知识\n- 开始实习或项目经验\n\n### 大三：实践期\n\n- 积累实际项目经验\n- 准备考研/就业\n- 提升核心技能\n\n### 大四：收获期\n\n- 完成毕业项目\n- 确定人生下一步\n- 珍惜最后的大学时光\n\n### 每个阶段都重要的事\n\n- 保持身体健康\n- 维护重要的关系\n- 记录和反思自己的成长', 'growth', 1, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (22, '如何找到自己真正想做的事', '## 迷茫是正常的\n\n大学生对未来感到迷茫是非常正常的。不知道自己想做什么，不代表你有问题。\n\n### 自我探索的方法\n\n#### 1. 兴趣探索\n\n问自己：\n- 什么事情让我忘记时间？\n- 什么话题我可以聊很久？\n- 如果不用考虑收入，我会做什么？\n\n#### 2. 价值观梳理\n\n列出对你最重要的5个价值观（如自由、成就、帮助他人等），看看哪些职业符合。\n\n#### 3. 行动实验\n\n不要只是想，去尝试：\n- 参加不同的实习\n- 做志愿者\n- 学习新技能\n\n#### 4. 信息收集\n\n和已经工作的学长学姐聊聊，了解不同职业的真实情况。\n\n> 记住：找到自己想做的事不是一个瞬间的顿悟，而是一个持续探索的过程。', 'growth', 0, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (23, '培养成长型思维：从\"我不行\"到\"我正在学\"', '## 固定型思维 vs 成长型思维\n\n心理学家Carol Dweck的研究表明，我们的思维模式决定了我们的成就。\n\n### 两种思维的对比\n\n| 固定型思维 | 成长型思维 |\n|---|---|\n| \"我不擅长数学\" | \"我正在提高数学能力\" |\n| \"我失败了\" | \"我学到了什么\" |\n| \"这太难了\" | \"这需要更多练习\" |\n| \"我就是这样的人\" | \"我可以改变和成长\" |\n\n### 如何培养成长型思维\n\n#### 1. 注意你的自我对话\n\n觉察并替换消极的内在对话。\n\n#### 2. 拥抱挑战\n\n把困难看作成长的机会，而不是威胁。\n\n#### 3. 学习过程比结果重要\n\n关注你的进步和努力，而不只是最终成绩。\n\n#### 4. 从失败中学习\n\n每次失败后问自己：\"我从中学到了什么？下次可以怎样做得更好？\"', 'growth', 0, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (24, '写给内向者的社交指南', '## 内向不是缺陷\n\n内向者的能量来自独处，而外向者的能量来自社交。这只是不同的能量获取方式，没有好坏之分。\n\n### 内向者的优势\n\n- 深度思考能力\n- 倾听能力\n- 创造力\n- 专注力\n- 独立工作能力\n\n### 内向者的社交策略\n\n#### 1. 选择适合的社交方式\n\n- 小规模聚会 > 大型派对\n- 一对一交流 > 群体讨论\n- 有准备的发言 > 即兴表达\n\n#### 2. 充电时间\n\n社交活动后，给自己安排独处时间来恢复能量。\n\n#### 3. 发挥优势\n\n利用你的倾听能力和深度思考来建立有质量的关系。\n\n#### 4. 设定社交目标\n\n不要和外向者比较，按自己的节奏来。', 'growth', 0, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (25, '养成好习惯：微习惯的力量', '## 为什么好习惯难以坚持？\n\n研究表明，习惯的形成平均需要66天。失败的原因往往是目标设得太大。\n\n### 微习惯策略\n\n微习惯是指将目标缩小到\"不可能失败\"的程度：\n- 想跑步？先穿上运动鞋\n- 想阅读？先读一页\n- 想早起？先比平时早5分钟\n\n### 建立习惯的四个步骤\n\n#### 1. 提示（Cue）\n\n将新习惯和已有习惯绑定：\"每天刷完牙后做5个俯卧撑\"\n\n#### 2. 渴望（Craving）\n\n让习惯变得有吸引力：运动后奖励自己一杯奶茶\n\n#### 3. 回应（Response）\n\n让习惯变得简单：把瑜伽垫铺在床边\n\n#### 4. 奖赏（Reward）\n\n给自己即时的正反馈：记录连续天数，产生成就感\n\n### 推荐追踪方式\n\n使用日历打卡法：每完成一天画一个X，不要断链。', 'growth', 0, 1, '2026-04-15 16:42:46', '2026-04-15 16:42:46');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (27, '123', '123', 'emotion', 3, 1, '2026-04-15 23:09:27', '2026-04-16 00:08:33');
INSERT INTO `article` (`id`, `title`, `content`, `category`, `view_count`, `status`, `created_at`, `updated_at`) VALUES (28, '封面移除验证-20260416-0029', '用于验证已移除封面字段。', 'emotion', 0, 1, '2026-04-16 00:30:24', '2026-04-16 00:30:24');
COMMIT;

-- ----------------------------
-- Table structure for article_favorite
-- ----------------------------
DROP TABLE IF EXISTS `article_favorite`;
CREATE TABLE `article_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_article` (`user_id`,`article_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_article_id` (`article_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章收藏表';

-- ----------------------------
-- Records of article_favorite
-- ----------------------------
BEGIN;
INSERT INTO `article_favorite` (`id`, `user_id`, `article_id`, `created_at`) VALUES (1, 1, 1, '2026-04-15 21:01:33');
INSERT INTO `article_favorite` (`id`, `user_id`, `article_id`, `created_at`) VALUES (2, 6, 6, '2026-04-15 22:57:46');
INSERT INTO `article_favorite` (`id`, `user_id`, `article_id`, `created_at`) VALUES (4, 5, 21, '2026-04-16 01:13:23');
INSERT INTO `article_favorite` (`id`, `user_id`, `article_id`, `created_at`) VALUES (5, 8, 3, '2026-04-16 01:18:22');
COMMIT;

-- ----------------------------
-- Table structure for emotion_diary
-- ----------------------------
DROP TABLE IF EXISTS `emotion_diary`;
CREATE TABLE `emotion_diary` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日记ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `emotion_type` varchar(20) NOT NULL COMMENT '情绪类型：happy/calm/anxious/sad/angry/fearful',
  `emotion_level` tinyint NOT NULL COMMENT '情绪强度：1-5',
  `trigger_event` varchar(500) DEFAULT NULL COMMENT '触发事件',
  `feeling` text COMMENT '内心感受',
  `ai_suggestion` text COMMENT 'AI生成的调节建议',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_user_emotion` (`user_id`,`emotion_type`,`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='情绪日记表';

-- ----------------------------
-- Records of emotion_diary
-- ----------------------------
BEGIN;
INSERT INTO `emotion_diary` (`id`, `user_id`, `emotion_type`, `emotion_level`, `trigger_event`, `feeling`, `ai_suggestion`, `created_at`, `updated_at`) VALUES (1, 1, 'calm', 3, 'MySQL联调', '接口已切到真实数据库', '感谢你认真记录自己的情绪。先让自己慢下来，做三次深呼吸，给身体一点放松的空间。如果这件事一直让你难受，试着把担心写下来，再挑一件现在能做的小事开始。必要时可以和朋友、辅导员或学校心理中心聊聊。', '2026-04-15 16:44:08', '2026-04-15 16:44:08');
INSERT INTO `emotion_diary` (`id`, `user_id`, `emotion_type`, `emotion_level`, `trigger_event`, `feeling`, `ai_suggestion`, `created_at`, `updated_at`) VALUES (2, 1, 'calm', 2, '中文时间联调', '分页搜索关键字测试-情绪足迹', '感谢你认真记录自己的情绪。先让自己慢下来，做三次深呼吸，给身体一点放松的空间。如果这件事一直让你难受，试着把担心写下来，再挑一件现在能做的小事开始。必要时可以和朋友、辅导员或学校心理中心聊聊。', '2026-04-15 21:01:33', '2026-04-15 21:01:33');
INSERT INTO `emotion_diary` (`id`, `user_id`, `emotion_type`, `emotion_level`, `trigger_event`, `feeling`, `ai_suggestion`, `created_at`, `updated_at`) VALUES (3, 6, 'happy', 3, '吃了一个汉堡', '老八秘制小汉堡', '真为你开心！一个汉堡就能带来这样纯粹的快乐，说明你懂得在生活中发现小确幸呢～✨\n\n可以试试把这份愉悦延续下去：\n1. 回味刚才的满足感，给味蕾记忆点个赞\n2. 拍张阳光下的影子自拍，配上俏皮文字记录此刻\n3. 给朋友发条消息：“今天发现了个快乐配方！”\n4. 让身体记住这种轻盈感，哼着歌收拾下书桌\n\n快乐就像汉堡里的酱料，虽然看不见却让整个体验更丰富。保持这份捕捉美好的能力，明天或许还有新的“小汉堡”在等你发现呢～', '2026-04-15 22:54:13', '2026-04-15 22:54:13');
INSERT INTO `emotion_diary` (`id`, `user_id`, `emotion_type`, `emotion_level`, `trigger_event`, `feeling`, `ai_suggestion`, `created_at`, `updated_at`) VALUES (4, 8, 'happy', 4, '吃了一个小汉堡', '老八秘制小汉堡', '真为你开心！吃到一个喜欢的小汉堡，这种简单的满足感特别珍贵呢。\n\n可以试试把这份快乐“延长”一下：拿出手机拍张照，或者在本子上记下此刻的感受。当情绪低落时，翻看这些记录能帮你快速找回这种愉悦感。\n\n也可以把这份好心情分享给朋友，聊聊这个汉堡带来的小惊喜。好情绪在传递中会变得更丰富。\n\n今天剩下的时间里，不妨带着这份轻松感去做其他事情，你会发现效率都可能变高哦。记住这种通过小事获得快乐的能力，这是很宝贵的生活智慧。', '2026-04-16 00:53:02', '2026-04-16 00:53:02');
INSERT INTO `emotion_diary` (`id`, `user_id`, `emotion_type`, `emotion_level`, `trigger_event`, `feeling`, `ai_suggestion`, `created_at`, `updated_at`) VALUES (5, 8, 'calm', 3, '无聊', '无聊', '感到无聊时，其实也是大脑在提醒你：可以给生活添些新鲜感啦！试试这三个小方法：\n\n1. **微行动启动**：起身整理书桌，或出门散步10分钟，身体动起来情绪就会流动。\n\n2. **好奇心探索**：打开知识类APP随机学个冷知识，或者给朋友发条有趣的表情包。\n\n3. **创造小确幸**：泡杯花果茶，用手机拍窗外的云，记录此刻平静中的美好。\n\n无聊是灵感的种子，当你开始观察生活细节时，它会悄悄发芽🌱', '2026-04-16 00:55:04', '2026-04-16 00:55:04');
INSERT INTO `emotion_diary` (`id`, `user_id`, `emotion_type`, `emotion_level`, `trigger_event`, `feeling`, `ai_suggestion`, `created_at`, `updated_at`) VALUES (6, 5, 'happy', 5, '吃了一个秘制小汉堡', '老八秘制小汉堡', '哈哈，能因为一个汉堡这么开心，真是件特别棒的事！这种简单的快乐其实是最珍贵的情绪能量。\n\n可以试试把这份感受记下来，比如拍张照片、写两句话在手机备忘录里。下次情绪低落时翻出来看看，能帮你快速找回这种满足感。\n\n现在不妨带着这份好心情去做点喜欢的事——听首欢快的歌，或者和朋友分享这份“秘制快乐”。让好情绪像涟漪一样扩散开来，今天会因此变得更明亮呢。\n\n记住这个汉堡带来的笑容，生活中这样的小确幸还有很多，等你去发现哦！', '2026-04-16 01:10:20', '2026-04-16 01:10:20');
INSERT INTO `emotion_diary` (`id`, `user_id`, `emotion_type`, `emotion_level`, `trigger_event`, `feeling`, `ai_suggestion`, `created_at`, `updated_at`) VALUES (7, 5, 'calm', 3, '无聊', '无聊。', '感到无聊时，其实是大脑在提醒你：该给生活添点新鲜感啦！可以试试这些小事：\n\n1. **微行动启动**：起身整理书桌、给绿植浇水，小动作能唤醒活力\n2. **好奇十分钟**：选个感兴趣的话题（比如星座冷知识/手绘教程）快速探索\n3. **感官refresh**：闻闻咖啡香、听首没听过的轻音乐，激活不同感官\n\n无聊是灵感的预备铃——此刻放松的空白，或许正藏着你想尝试的新事物呢 🌱', '2026-04-16 01:12:26', '2026-04-16 01:12:26');
COMMIT;

-- ----------------------------
-- Table structure for emotion_report
-- ----------------------------
DROP TABLE IF EXISTS `emotion_report`;
CREATE TABLE `emotion_report` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报告ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `report_type` varchar(10) NOT NULL COMMENT '报告类型：daily/weekly',
  `report_date` date NOT NULL COMMENT '报告日期（日报=当天，周报=周一日期）',
  `summary` text COMMENT '统计摘要',
  `ai_analysis` text COMMENT 'AI分析内容',
  `emotion_data_json` json DEFAULT NULL COMMENT '情绪数据JSON（存储图表数据）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_report` (`user_id`,`report_type`,`report_date`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='情绪报告表';

-- ----------------------------
-- Records of emotion_report
-- ----------------------------
BEGIN;
INSERT INTO `emotion_report` (`id`, `user_id`, `report_type`, `report_date`, `summary`, `ai_analysis`, `emotion_data_json`, `created_at`) VALUES (1, 5, 'weekly', '2026-04-13', '本周共记录2条日记，主要情绪为happy。', '本周还没有记录情绪日记。可以从每天花一分钟记录当下感受开始，持续几天后会更容易看到自己的情绪变化。', '{\"endDate\": \"2026-04-19\", \"startDate\": \"2026-04-13\", \"dailyTrend\": [{\"date\": \"2026-04-13\", \"avgLevel\": 0.0}, {\"date\": \"2026-04-14\", \"avgLevel\": 0.0}, {\"date\": \"2026-04-15\", \"avgLevel\": 0.0}, {\"date\": \"2026-04-16\", \"avgLevel\": 4.0, \"dominantEmotion\": \"happy\"}, {\"date\": \"2026-04-17\", \"avgLevel\": 0.0}, {\"date\": \"2026-04-18\", \"avgLevel\": 0.0}, {\"date\": \"2026-04-19\", \"avgLevel\": 0.0}], \"totalCount\": 2, \"distribution\": [{\"count\": 1, \"percentage\": 50, \"emotionType\": \"happy\"}, {\"count\": 1, \"percentage\": 50, \"emotionType\": \"calm\"}]}', '2026-04-15 20:43:30');
INSERT INTO `emotion_report` (`id`, `user_id`, `report_type`, `report_date`, `summary`, `ai_analysis`, `emotion_data_json`, `created_at`) VALUES (2, 6, 'weekly', '2026-04-13', '本周共记录1条日记，主要情绪为happy。', '本周你的情绪整体平稳，唯一一次明显的积极情绪出现在4月15日，当天你感到“快乐”，且情绪强度较高（3.0）。这说明你拥有体验和记录快乐的能力，这是很棒的自我觉察习惯。\n\n从数据看，这一周的快乐时刻可能源于某个特定事件或人际互动——也许是完成了一项任务、一次愉快的交流，或是做了喜欢的事。试着回忆那天发生了什么，这能帮你更了解什么会带给你能量。\n\n建议下周可以：\n1. 主动安排一件类似4月15日那样让你期待的小事\n2. 继续记录情绪，尤其留意那些“平静”（0.0）的日子背后是否有满足感或放松时刻\n3. 如果愿意，可以尝试给平静的日子也添加简短的关键词记录（如“休息日”“专注学习”）\n\n你已经在照顾自己的情绪健康上迈出了第一步，继续保持这份对自己的温柔关注吧 🌱', '{\"endDate\": \"2026-04-19\", \"startDate\": \"2026-04-13\", \"dailyTrend\": [{\"date\": \"2026-04-13\", \"avgLevel\": 0.0}, {\"date\": \"2026-04-14\", \"avgLevel\": 0.0}, {\"date\": \"2026-04-15\", \"avgLevel\": 3.0, \"dominantEmotion\": \"happy\"}, {\"date\": \"2026-04-16\", \"avgLevel\": 0.0}, {\"date\": \"2026-04-17\", \"avgLevel\": 0.0}, {\"date\": \"2026-04-18\", \"avgLevel\": 0.0}, {\"date\": \"2026-04-19\", \"avgLevel\": 0.0}], \"totalCount\": 1, \"distribution\": [{\"count\": 1, \"percentage\": 100, \"emotionType\": \"happy\"}]}', '2026-04-15 22:56:58');
INSERT INTO `emotion_report` (`id`, `user_id`, `report_type`, `report_date`, `summary`, `ai_analysis`, `emotion_data_json`, `created_at`) VALUES (4, 8, 'weekly', '2026-04-13', '本周共记录2条日记，主要情绪为happy。', '本周还没有记录情绪日记。可以从每天花一分钟记录当下感受开始，持续几天后会更容易看到自己的情绪变化。', '{\"endDate\": \"2026-04-19\", \"startDate\": \"2026-04-13\", \"dailyTrend\": [{\"date\": \"2026-04-13\", \"avgLevel\": 0.0}, {\"date\": \"2026-04-14\", \"avgLevel\": 0.0}, {\"date\": \"2026-04-15\", \"avgLevel\": 0.0}, {\"date\": \"2026-04-16\", \"avgLevel\": 3.5, \"dominantEmotion\": \"happy\"}, {\"date\": \"2026-04-17\", \"avgLevel\": 0.0}, {\"date\": \"2026-04-18\", \"avgLevel\": 0.0}, {\"date\": \"2026-04-19\", \"avgLevel\": 0.0}], \"totalCount\": 2, \"distribution\": [{\"count\": 1, \"percentage\": 50, \"emotionType\": \"happy\"}, {\"count\": 1, \"percentage\": 50, \"emotionType\": \"calm\"}]}', '2026-04-16 00:50:10');
COMMIT;

-- ----------------------------
-- Table structure for partner_match
-- ----------------------------
DROP TABLE IF EXISTS `partner_match`;
CREATE TABLE `partner_match` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '匹配ID',
  `user_a_id` bigint NOT NULL COMMENT '用户A的ID',
  `user_b_id` bigint DEFAULT NULL COMMENT '用户B的ID（等待匹配时为NULL）',
  `status` varchar(10) NOT NULL DEFAULT 'waiting' COMMENT '状态：waiting/matched/ended',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `matched_at` datetime DEFAULT NULL COMMENT '匹配成功时间',
  `ended_at` datetime DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_a` (`user_a_id`),
  KEY `idx_user_b` (`user_b_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='伙伴匹配表';

-- ----------------------------
-- Records of partner_match
-- ----------------------------
BEGIN;
INSERT INTO `partner_match` (`id`, `user_a_id`, `user_b_id`, `status`, `created_at`, `matched_at`, `ended_at`) VALUES (1, 6, NULL, 'ended', '2026-04-15 22:57:49', NULL, '2026-04-15 22:57:58');
INSERT INTO `partner_match` (`id`, `user_a_id`, `user_b_id`, `status`, `created_at`, `matched_at`, `ended_at`) VALUES (2, 8, 5, 'ended', '2026-04-16 00:37:34', '2026-04-16 00:49:10', '2026-04-16 00:49:57');
INSERT INTO `partner_match` (`id`, `user_a_id`, `user_b_id`, `status`, `created_at`, `matched_at`, `ended_at`) VALUES (3, 5, 8, 'ended', '2026-04-16 01:14:59', '2026-04-16 01:16:20', '2026-04-16 01:17:34');
COMMIT;

-- ----------------------------
-- Table structure for partner_message
-- ----------------------------
DROP TABLE IF EXISTS `partner_message`;
CREATE TABLE `partner_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `match_id` bigint NOT NULL COMMENT '匹配ID',
  `sender_id` bigint NOT NULL COMMENT '发送者用户ID',
  `content` text NOT NULL COMMENT '消息内容',
  `is_read` tinyint DEFAULT '0' COMMENT '是否已读：0-未读 1-已读',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_match_id` (`match_id`),
  KEY `idx_sender_id` (`sender_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='伙伴信箱消息表';

-- ----------------------------
-- Records of partner_message
-- ----------------------------
BEGIN;
INSERT INTO `partner_message` (`id`, `match_id`, `sender_id`, `content`, `is_read`, `created_at`) VALUES (1, 2, 8, '你好啊', 0, '2026-04-16 00:49:42');
INSERT INTO `partner_message` (`id`, `match_id`, `sender_id`, `content`, `is_read`, `created_at`) VALUES (2, 2, 5, '哈哈哈哈', 0, '2026-04-16 00:49:51');
INSERT INTO `partner_message` (`id`, `match_id`, `sender_id`, `content`, `is_read`, `created_at`) VALUES (3, 3, 5, '你好', 0, '2026-04-16 01:16:47');
INSERT INTO `partner_message` (`id`, `match_id`, `sender_id`, `content`, `is_read`, `created_at`) VALUES (4, 3, 8, '哈哈哈哈哈你好', 0, '2026-04-16 01:16:59');
INSERT INTO `partner_message` (`id`, `match_id`, `sender_id`, `content`, `is_read`, `created_at`) VALUES (5, 3, 5, '1111', 0, '2026-04-16 01:17:09');
INSERT INTO `partner_message` (`id`, `match_id`, `sender_id`, `content`, `is_read`, `created_at`) VALUES (6, 3, 8, '我是帅哥', 0, '2026-04-16 01:17:26');
COMMIT;

-- ----------------------------
-- Table structure for tree_hole_comment
-- ----------------------------
DROP TABLE IF EXISTS `tree_hole_comment`;
CREATE TABLE `tree_hole_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '评论者用户ID',
  `anonymous_name` varchar(50) NOT NULL COMMENT '匿名昵称',
  `content` text NOT NULL COMMENT '评论内容',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-审核中 1-正常 2-违规删除',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='树洞评论表';

-- ----------------------------
-- Records of tree_hole_comment
-- ----------------------------
BEGIN;
INSERT INTO `tree_hole_comment` (`id`, `post_id`, `user_id`, `anonymous_name`, `content`, `status`, `created_at`) VALUES (1, 1, 2, '闪亮的雨滴#5458', '我现在想去死', 1, '2026-04-15 17:00:44');
INSERT INTO `tree_hole_comment` (`id`, `post_id`, `user_id`, `anonymous_name`, `content`, `status`, `created_at`) VALUES (2, 2, 3, '明亮的微风#1069', '我不想活了', 2, '2026-04-15 18:30:20');
INSERT INTO `tree_hole_comment` (`id`, `post_id`, `user_id`, `anonymous_name`, `content`, `status`, `created_at`) VALUES (3, 3, 4, '快乐的蝴蝶#9196', '我不想活了', 2, '2026-04-15 18:30:32');
INSERT INTO `tree_hole_comment` (`id`, `post_id`, `user_id`, `anonymous_name`, `content`, `status`, `created_at`) VALUES (5, 6, 5, '可爱的月亮#1374', '哈哈哈哈哈', 1, '2026-04-15 23:15:32');
INSERT INTO `tree_hole_comment` (`id`, `post_id`, `user_id`, `anonymous_name`, `content`, `status`, `created_at`) VALUES (6, 8, 7, '勇敢的花朵#4564', '这条评论需要审核', 1, '2026-04-15 23:21:01');
INSERT INTO `tree_hole_comment` (`id`, `post_id`, `user_id`, `anonymous_name`, `content`, `status`, `created_at`) VALUES (7, 9, 7, '勇敢的花朵#4564', '这是一条正常评论', 1, '2026-04-15 23:24:59');
INSERT INTO `tree_hole_comment` (`id`, `post_id`, `user_id`, `anonymous_name`, `content`, `status`, `created_at`) VALUES (8, 9, 7, '勇敢的花朵#4564', '我想自杀', 2, '2026-04-15 23:24:59');
INSERT INTO `tree_hole_comment` (`id`, `post_id`, `user_id`, `anonymous_name`, `content`, `status`, `created_at`) VALUES (9, 10, 7, '勇敢的花朵#4564', '今天天气不错', 1, '2026-04-15 23:43:36');
INSERT INTO `tree_hole_comment` (`id`, `post_id`, `user_id`, `anonymous_name`, `content`, `status`, `created_at`) VALUES (10, 10, 7, '勇敢的花朵#4564', '我想自杀', 2, '2026-04-15 23:43:36');
INSERT INTO `tree_hole_comment` (`id`, `post_id`, `user_id`, `anonymous_name`, `content`, `status`, `created_at`) VALUES (11, 10, 7, '勇敢的花朵#4564', '我想自 杀', 1, '2026-04-15 23:43:36');
INSERT INTO `tree_hole_comment` (`id`, `post_id`, `user_id`, `anonymous_name`, `content`, `status`, `created_at`) VALUES (12, 12, 5, '可爱的月亮#1374', '哈哈哈哈哈', 1, '2026-04-16 01:13:49');
COMMIT;

-- ----------------------------
-- Table structure for tree_hole_like
-- ----------------------------
DROP TABLE IF EXISTS `tree_hole_like`;
CREATE TABLE `tree_hole_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`,`user_id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='树洞点赞表';

-- ----------------------------
-- Records of tree_hole_like
-- ----------------------------
BEGIN;
INSERT INTO `tree_hole_like` (`id`, `post_id`, `user_id`, `created_at`) VALUES (2, 6, 5, '2026-04-15 23:15:22');
INSERT INTO `tree_hole_like` (`id`, `post_id`, `user_id`, `created_at`) VALUES (4, 12, 5, '2026-04-16 01:13:56');
COMMIT;

-- ----------------------------
-- Table structure for tree_hole_post
-- ----------------------------
DROP TABLE IF EXISTS `tree_hole_post`;
CREATE TABLE `tree_hole_post` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '发布者用户ID',
  `anonymous_name` varchar(50) NOT NULL COMMENT '匿名昵称',
  `content` text NOT NULL COMMENT '帖子内容',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `comment_count` int DEFAULT '0' COMMENT '评论数',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-审核中 1-正常 2-违规删除',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='树洞帖子表';

-- ----------------------------
-- Records of tree_hole_post
-- ----------------------------
BEGIN;
INSERT INTO `tree_hole_post` (`id`, `user_id`, `anonymous_name`, `content`, `like_count`, `comment_count`, `status`, `created_at`, `updated_at`) VALUES (1, 2, '闪亮的雨滴#5458', '这是验收帖子，请忽略。', 0, 2, 1, '2026-04-15 17:00:44', '2026-04-16 01:20:06');
INSERT INTO `tree_hole_post` (`id`, `user_id`, `anonymous_name`, `content`, `like_count`, `comment_count`, `status`, `created_at`, `updated_at`) VALUES (2, 3, '明亮的微风#1069', '修复后树洞验收帖子', 0, 0, 1, '2026-04-15 18:30:20', '2026-04-15 18:30:20');
INSERT INTO `tree_hole_post` (`id`, `user_id`, `anonymous_name`, `content`, `like_count`, `comment_count`, `status`, `created_at`, `updated_at`) VALUES (3, 4, '快乐的蝴蝶#9196', '修复后树洞验收帖子', 0, 0, 1, '2026-04-15 18:30:32', '2026-04-15 18:30:32');
INSERT INTO `tree_hole_post` (`id`, `user_id`, `anonymous_name`, `content`, `like_count`, `comment_count`, `status`, `created_at`, `updated_at`) VALUES (4, 1, '闪亮的月亮#3406', '树洞分页搜索关键字测试', 0, 0, 1, '2026-04-15 21:01:33', '2026-04-15 21:01:33');
INSERT INTO `tree_hole_post` (`id`, `user_id`, `anonymous_name`, `content`, `like_count`, `comment_count`, `status`, `created_at`, `updated_at`) VALUES (6, 7, '勇敢的花朵#4564', '这是一条正常帖子', 1, 1, 1, '2026-04-15 22:08:32', '2026-04-15 23:15:32');
INSERT INTO `tree_hole_post` (`id`, `user_id`, `anonymous_name`, `content`, `like_count`, `comment_count`, `status`, `created_at`, `updated_at`) VALUES (7, 5, '可爱的月亮#1374', '123', 0, 0, 1, '2026-04-15 23:15:07', '2026-04-15 23:15:07');
INSERT INTO `tree_hole_post` (`id`, `user_id`, `anonymous_name`, `content`, `like_count`, `comment_count`, `status`, `created_at`, `updated_at`) VALUES (8, 7, '勇敢的花朵#4564', '评论审核规则验证帖子', 0, 1, 1, '2026-04-15 23:21:01', '2026-04-15 23:21:01');
INSERT INTO `tree_hole_post` (`id`, `user_id`, `anonymous_name`, `content`, `like_count`, `comment_count`, `status`, `created_at`, `updated_at`) VALUES (9, 7, '勇敢的花朵#4564', '评论审核规则验证帖子2', 0, 1, 1, '2026-04-15 23:24:59', '2026-04-15 23:24:59');
INSERT INTO `tree_hole_post` (`id`, `user_id`, `anonymous_name`, `content`, `like_count`, `comment_count`, `status`, `created_at`, `updated_at`) VALUES (10, 7, '勇敢的花朵#4564', 'sensitive-word集成验证帖子', 0, 2, 1, '2026-04-15 23:43:36', '2026-04-15 23:43:36');
INSERT INTO `tree_hole_post` (`id`, `user_id`, `anonymous_name`, `content`, `like_count`, `comment_count`, `status`, `created_at`, `updated_at`) VALUES (11, 5, '可爱的月亮#1374', '我想去死', 0, 0, 2, '2026-04-16 00:11:20', '2026-04-16 01:19:47');
INSERT INTO `tree_hole_post` (`id`, `user_id`, `anonymous_name`, `content`, `like_count`, `comment_count`, `status`, `created_at`, `updated_at`) VALUES (12, 5, '可爱的月亮#1374', '哈哈哈哈', 1, 1, 1, '2026-04-16 01:13:36', '2026-04-16 01:13:56');
INSERT INTO `tree_hole_post` (`id`, `user_id`, `anonymous_name`, `content`, `like_count`, `comment_count`, `status`, `created_at`, `updated_at`) VALUES (13, 5, '可爱的月亮#1374', '我想杀人', 0, 0, 2, '2026-04-16 01:14:27', '2026-04-16 01:19:44');
COMMIT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名（登录账号）',
  `password` varchar(100) NOT NULL COMMENT '密码（BCrypt加密）',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT '/avatars/default.png' COMMENT '头像路径',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `gender` tinyint DEFAULT '0' COMMENT '性别：0-未知 1-男 2-女',
  `bio` varchar(200) DEFAULT NULL COMMENT '个人简介',
  `role` varchar(10) DEFAULT 'user' COMMENT '角色：user-普通用户 admin-管理员',
  `anonymous_name` varchar(50) DEFAULT NULL COMMENT '匿名昵称（树洞/信箱使用）',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用 1-正常',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `email`, `gender`, `bio`, `role`, `anonymous_name`, `status`, `created_at`, `updated_at`) VALUES (1, 'mysql_test_user', '$2a$10$rzgYlMrio4FHri2DWttQde7xYHg/MDNvnkDXOgPwd/ohN.Og/d96G', 'MySQL联调用户', '/uploads/avatar/1/e9fb6a83b80d437698440247dc884864.tiff', NULL, 0, NULL, 'user', '闪亮的月亮#3406', 1, '2026-04-15 16:43:49', '2026-04-15 16:43:49');
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `email`, `gender`, `bio`, `role`, `anonymous_name`, `status`, `created_at`, `updated_at`) VALUES (2, 'codex_accept_1776243643662', '$2a$10$KldnF7CyHlhF9V4/3iG6NuSt5xhNimY/pNDoTkoyuEsyio7Jmi2Zi', '验收账号', '/avatars/default.png', NULL, 0, NULL, 'user', '闪亮的雨滴#5458', 1, '2026-04-15 17:00:43', '2026-04-15 17:00:43');
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `email`, `gender`, `bio`, `role`, `anonymous_name`, `status`, `created_at`, `updated_at`) VALUES (3, 'codex_fix_1776249019121', '$2a$10$4uPTvM34SyTb3YCa6iYgUORS9sGlIB5NxpP1UR4PHUoZlwLn6fX4u', '修复验收账号', '/avatars/default.png', NULL, 0, NULL, 'user', '明亮的微风#1069', 1, '2026-04-15 18:30:19', '2026-04-15 18:30:19');
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `email`, `gender`, `bio`, `role`, `anonymous_name`, `status`, `created_at`, `updated_at`) VALUES (4, 'codex_fix_1776249031438', '$2a$10$oZh0LAvfn7jkVLVOMw9V2uJMta2udTV6uaGkPPXWvrlJtXiTYpl9K', '修复验收账号', '/avatars/default.png', NULL, 0, NULL, 'user', '快乐的蝴蝶#9196', 1, '2026-04-15 18:30:31', '2026-04-15 18:30:31');
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `email`, `gender`, `bio`, `role`, `anonymous_name`, `status`, `created_at`, `updated_at`) VALUES (5, 'zhangsan', '$2a$10$G.UuF71vxhVNZ9Fa51OXQ.OT60P4lbTEPkEp/aJ0Gv8uyjOkotTxi', '张三', '/avatars/default.png', NULL, 0, NULL, 'user', '可爱的月亮#1374', 1, '2026-04-15 20:42:36', '2026-04-15 20:42:36');
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `email`, `gender`, `bio`, `role`, `anonymous_name`, `status`, `created_at`, `updated_at`) VALUES (6, 'admin', '$2a$10$DEXe3ANf0qbmeO.oCKVf9./sD9z75PsAlqpXDqcDNitSjbVD48vDK', '系统管理员', '/avatars/admin.png', NULL, 0, NULL, 'admin', NULL, 1, '2026-04-15 22:06:25', '2026-04-15 22:08:01');
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `email`, `gender`, `bio`, `role`, `anonymous_name`, `status`, `created_at`, `updated_at`) VALUES (7, 'mac', '$2a$10$Zy3Ty8VBbcN83Ke4njlrVugTap54imZMaBIXDB/yXGv7kFz/neGSa', 'Phase9用户', '/avatars/default.png', NULL, 0, NULL, 'user', '勇敢的花朵#4564', 1, '2026-04-15 22:07:37', '2026-04-15 22:07:37');
INSERT INTO `user` (`id`, `username`, `password`, `nickname`, `avatar`, `email`, `gender`, `bio`, `role`, `anonymous_name`, `status`, `created_at`, `updated_at`) VALUES (8, 'lisi', '$2a$10$1Aw3VmZM7JWI55B/cAv4gOSCq7Ft6SiqmRxWqgMO4KE0cPN3Zjd1.', '李四', '/uploads/avatar/8/107ab7e6e283436e92b4190e90f6f5db.png', '', 1, '', 'user', '安静的雨滴#8608', 1, '2026-04-16 00:37:19', '2026-04-16 00:37:19');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
