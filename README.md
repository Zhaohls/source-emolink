> **源码整理版**：本仓库已排除本机依赖、构建缓存和旧运行数据。先阅读 [源码整理说明](SOURCE_EXPORT.md)，再按本文启动。

# EmoLink

EmoLink 是一个面向大学生的情绪管理平台，采用前后端分离实现。系统包含情绪日记、日报周报、心理知识库、匿名树洞、伙伴信箱和管理后台。

## 目录说明

```text
EmoLink/
├── emolink-backend/   后端服务
├── emolink-frontend/  前端项目
├── docs/              项目文档
└── README.md
```

## 文档入口

建议按下面顺序阅读：

1. `docs/06-项目交付说明.md`
2. `docs/09-运行部署说明.md`
3. `docs/07-项目操作手册.md`
4. `docs/08-项目技术详解.md`

如果需要追溯原始设计资料，再看：

- `docs/00-全局架构设计.md`
- `docs/01-接口契约文档.md`
- `docs/02-数据库设计文档.md`

## 快速启动

### 1. 初始化本地数据库

初始化脚本会重建 `emolink` 数据库中的项目表，只在新建的本地演示库中运行，不要对已有业务数据库执行。

```bash
mysql -u root -p < emolink-backend/sql/mysql-init.sql
mysql -u root -p emolink < emolink-backend/sql/article-seed.sql
mysql -u root -p emolink < emolink-backend/sql/demo-seed.sql
```

演示用户为 `emolink_demo / admin123`，管理员为 `admin / admin123`。这两个账号只用于本地演示，部署前请更改密码。

### 2. 配置后端环境变量

在项目根目录打开终端，设置数据库连接和 JWT 密钥；需要 AI 功能时再配置 DeepSeek Key：

```bash
export DB_URL='jdbc:mysql://127.0.0.1:3306/emolink?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true'
export DB_USERNAME='root'
export DB_PASSWORD='你的数据库密码'
export JWT_SECRET='替换为至少32位的随机字符串'
export DEEPSEEK_API_KEY='你的 DeepSeek Key'
```

DeepSeek Key 未配置时，日记 AI 建议和周报 AI 分析不可用，其余页面可以正常使用。

### 3. 启动后端

```bash
cd emolink-backend
mvn spring-boot:run
```

### 4. 启动前端

在另一个终端中执行：

```bash
cd emolink-frontend
npm install
npm run dev -- --host 0.0.0.0 --port 18012
```

## 说明

- 上传目录使用相对路径 `uploads/`
- 文章模块已移除封面图字段
- DeepSeek 调用失败会明确提示错误，不会伪装成 AI 已生成内容
- 本项目是课程设计演示系统，不能替代专业心理咨询、诊断或治疗
