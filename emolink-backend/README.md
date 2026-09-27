# 后端项目说明

这是 EmoLink 的后端工程，技术栈为 Spring Boot、MyBatis-Plus、MySQL、JWT、WebSocket 和 DeepSeek。

## 常用命令

```bash
mvn spring-boot:run
mvn clean package
java -jar target/emolink-backend-0.0.1-SNAPSHOT.jar
```

## 运行前要检查

- MySQL 是否启动
- `src/main/resources/application.yml` 中的数据库配置是否正确
- `deepseek.api.key` 是否可用

## 重点说明

- 管理端接口统一走 `/api/admin/**`
- 非管理员访问管理端接口会被拦截
- 上传目录使用相对路径 `uploads/`
- 树洞和评论只有命中敏感词才进入审核

## 建议先看的文档

- `../docs/06-项目交付说明.md`
- `../docs/08-项目技术详解.md`
- `../docs/09-运行部署说明.md`
