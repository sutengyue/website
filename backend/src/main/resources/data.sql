
INSERT INTO `profile` (`name`, `title`, `bio`, `location`, `github`, `skills`) 
VALUES ('张三', '全栈工程师', '热爱技术，专注于创造有价值的产品。拥有多年软件开发经验，擅长前后端技术栈。', '北京', 'https://github.com/example', 'Java, Spring Boot, Vue.js, React, Node.js, MySQL, Redis') 
ON DUPLICATE KEY UPDATE `updated_at` = NOW();

INSERT INTO `blog` (`title`, `slug`, `content`, `summary`, `category`, `tags`, `status`, `created_at`, `updated_at`) 
VALUES ('欢迎来到我的博客', 'welcome-to-my-blog', '# 欢迎来到我的博客\n\n这是我的第一篇博客文章，很高兴能在这里与大家分享技术心得。\n\n## 关于我\n\n我是一名全栈工程师，热爱编程和技术分享。\n\n### 技术栈\n\n- Java/Spring Boot\n- Vue.js/React\n- Node.js\n- MySQL/Redis', '欢迎来到我的个人博客，这里将分享技术文章和生活感悟。', '技术', '博客,欢迎', 1, NOW(), NOW());

INSERT INTO `project` (`name`, `description`, `url`, `github_url`, `tags`, `sort_order`, `status`, `created_at`, `updated_at`) 
VALUES ('个人网站系统', '一个基于Spring Boot和Vue.js的个人网站系统，包含博客、项目展示等功能。', 'https://example.com', 'https://github.com/example/personal-website', 'Spring Boot, Vue.js, MyBatis Plus', 1, 1, NOW(), NOW());
