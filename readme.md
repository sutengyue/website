# 企业文档系统

基于 JDK17 + Spring Cloud Alibaba + Vue3 的企业文档管理系统，支持RBAC权限控制、验证码登录、文件上传解析与全文索引。

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2.0
- Spring Cloud Alibaba 2023.0.1.0
- Spring Security + JWT
- Spring Data JPA
- Spring Data Redis
- Spring Data Elasticsearch
- MySQL 8.0+
- Elasticsearch 8.12.0

### 前端
- Vue 3.4+
- Vue Router 4.3+
- Element Plus 2.6+
- Axios 1.6+

## 功能特性

1. **RBAC权限控制**
   - 用户管理
   - 角色管理
   - 权限管理
   - 基于角色的访问控制

2. **验证码登录**
   - 图片验证码生成
   - Redis缓存验证码
   - JWT token认证

3. **文件管理**
   - 支持主流格式上传（doc, docx, xls, xlsx, pdf, txt, md, json, xml, html）
   - 文件内容解析
   - 全文索引（Elasticsearch）
   - 文档搜索

## 项目结构

```
corp_document/
├── doc-server/                    # 后端服务
│   ├── src/main/java/com/example/docserver/
│   │   ├── controller/           # 控制器
│   │   ├── service/              # 服务层
│   │   ├── repository/           # 数据访问层
│   │   ├── entity/               # 实体类
│   │   ├── dto/                  # 数据传输对象
│   │   ├── config/               # 配置类
│   │   ├── security/             # 安全相关
│   │   └── util/                 # 工具类
│   ├── src/main/resources/
│   │   └── application.yml       # 应用配置
│   └── pom.xml                   # Maven配置
├── doc-frontend/                 # 前端项目
│   ├── src/
│   │   ├── views/                # 页面视图
│   │   ├── router/               # 路由配置
│   │   ├── api/                  # API接口
│   │   ├── App.vue               # 根组件
│   │   └── main.js               # 入口文件
│   ├── index.html
│   ├── vite.config.js            # Vite配置
│   └── package.json              # 依赖配置
├── scripts/                      # 部署脚本
│   ├── install_elasticsearch.bat # Elasticsearch一键安装
│   ├── start_all.bat             # 启动所有服务
│   └── init_mysql.sql            # 数据库初始化脚本
└── README.md
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8.0+（账号密码：root/root）
- Redis（无密码）

### 部署步骤

1. **初始化数据库**

```bash
mysql -u root -p < scripts/init_mysql.sql
```

2. **启动Elasticsearch**（Windows）

```bash
scripts/install_elasticsearch.bat
```

3. **启动后端服务**

```bash
cd doc-server
mvn spring-boot:run
```

4. **启动前端服务**

```bash
cd doc-frontend
npm install
npm run dev
```

### 访问地址

- 后端服务: http://localhost:8080
- 前端页面: http://localhost:5173

### 测试账号

- 管理员: admin / admin123
- 普通用户: user / user123

## API接口

### 认证接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/auth/captcha | 获取验证码 |
| POST | /api/auth/login | 用户登录 |

### 文档接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/documents | 获取文档列表 |
| POST | /api/documents/upload | 上传文档 |
| GET | /api/documents/{id} | 获取文档详情 |
| DELETE | /api/documents/{id} | 删除文档 |
| POST | /api/documents/search | 搜索文档 |

## 项目配置

### 后端配置 (application.yml)

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/example_db
    username: root
    password: root
  data:
    redis:
      host: localhost
      port: 6379
    elasticsearch:
      uris: http://localhost:9200
```

## 许可证

MIT License