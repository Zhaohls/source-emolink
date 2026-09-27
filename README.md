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

### 后端

```bash
cd emolink-backend
mvn spring-boot:run
```

### 前端

```bash
cd emolink-frontend
npm install
npm run dev -- --host 0.0.0.0 --port 18012
```

### 数据库

```bash
mysql -u root -p < emolink-backend/sql/mysql-init.sql
mysql -u root -p emolink < emolink-backend/sql/article-seed.sql
```

## 默认管理员账号

- 用户名：`admin`
- 密码：`admin123`

## 说明

- 上传目录使用相对路径 `uploads/`
- 文章模块已移除封面图字段
- DeepSeek 调用失败会直接报错，不再返回默认文案
