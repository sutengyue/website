# 个人网站系统

基于 Spring Boot 3.2.x + Spring Security 6 + MyBatis Plus 3.5 + Vue3 + Element Plus 构建的个人网站系统。

## 项目结构

```
website/
├── backend/          # 后端 Spring Boot 项目
│   ├── src/main/java/com/example/website/
│   │   ├── controller/     # REST API 控制器
│   │   ├── service/        # 业务逻辑层
│   │   ├── mapper/         # MyBatis Plus 数据访问层
│   │   ├── entity/         # 数据库实体
│   │   ├── security/       # 安全相关（JWT）
│   │   ├── config/         # 配置类
│   │   └── common/         # 通用工具类
│   └── src/main/resources/
│       ├── application.yml # 应用配置
│       ├── schema.sql      # 数据库初始化脚本
│       └── data.sql        # 初始数据
├── frontend/         # 前端 Vue3 项目
│   ├── src/
│   │   ├── views/          # 页面组件
│   │   ├── router/         # 路由配置
│   │   ├── api/            # API 接口封装
│   │   └── composables/    # 组合式函数
│   └── index.html
└── README.md
```

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2.x
- Spring Security 6
- MyBatis Plus 3.5
- JWT 认证
- MySQL 数据库
- MinIO / 本地文件存储

### 前端
- Vue 3
- Element Plus
- Vue Router
- Axios
- Marked (Markdown 解析)
- Lucide Vue (图标库)

## 功能特性

### 前端展示端
- 响应式设计
- 暗黑模式支持
- 优雅动画效果
- 个人主页（Hero区域、技能展示、项目预览、博客预览）
- 关于页（详细个人信息）
- 项目页（项目列表）
- 博客页（文章列表、详情）

### 后台管理系统
- 独立登录页面 `/admin/login`
- 个人资料编辑
- 项目作品管理（CRUD）
- 博客文章管理（Markdown 编辑和预览）
- 管理员账号管理
- 操作日志查看

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.0+

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE example_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改 `backend/src/main/resources/application.yml` 中的数据库配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/example_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 启动后端

```bash
cd backend
mvn spring-boot:run
```

### 启动前端

```bash
cd frontend
npm install
npm run dev
```

### 访问地址

- 前端展示端：http://localhost:5173
- 后台管理：http://localhost:5173/admin/login

### 默认管理员账号

- 用户名：`admin`
- 密码：`password`

## 配置说明

### 文件存储配置

默认使用本地存储，可在 `application.yml` 中配置：

```yaml
file:
  storage: local  # local 或 minio
  local:
    path: ./uploads
  minio:
    endpoint: http://localhost:9000
    access-key: minioadmin
    secret-key: minioadmin
    bucket-name: website
```

### JWT 配置

```yaml
jwt:
  secret: personal-website-jwt-secret-key-2024
  expiration: 86400000  # 24小时（毫秒）
```

## API 接口

### 公共接口（无需认证）
- `GET /api/public/profile` - 获取个人资料
- `GET /api/public/projects` - 获取项目列表
- `GET /api/public/blogs` - 获取博客列表（分页）
- `GET /api/public/blog/{slug}` - 获取博客详情

### 认证接口
- `POST /api/auth/login` - 登录

### 管理员接口（需认证）
- `GET /api/admin/profile` - 获取个人资料（管理）
- `PUT /api/admin/profile` - 更新个人资料
- `POST /api/admin/upload` - 上传文件
- 博客、项目、管理员、日志等 CRUD 接口

## 开发说明

### 代码规范
- 后端：遵循 Spring Boot 编码规范，使用 Lombok 简化代码
- 前端：使用 Vue 3 Composition API，组件化开发

### 安全特性
- JWT 无状态认证
- BCrypt 密码加密
- CSRF 防护
- 请求限流（可扩展）

## License

MIT License