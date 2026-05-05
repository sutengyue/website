
CREATE TABLE IF NOT EXISTS `admin` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `nickname` VARCHAR(50),
    `email` VARCHAR(100),
    `avatar` VARCHAR(255),
    `role` VARCHAR(20) DEFAULT 'ROLE_ADMIN',
    `status` TINYINT DEFAULT 1,
    `created_at` DATETIME,
    `updated_at` DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `profile` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50),
    `avatar` VARCHAR(255),
    `title` VARCHAR(100),
    `bio` TEXT,
    `email` VARCHAR(100),
    `phone` VARCHAR(20),
    `location` VARCHAR(100),
    `github` VARCHAR(255),
    `linkedin` VARCHAR(255),
    `website` VARCHAR(255),
    `skills` TEXT,
    `education` TEXT,
    `experience` TEXT,
    `updated_at` DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `blog` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(200) NOT NULL,
    `slug` VARCHAR(200) UNIQUE,
    `content` LONGTEXT,
    `summary` TEXT,
    `cover` VARCHAR(255),
    `category` VARCHAR(50),
    `tags` VARCHAR(255),
    `views` INT DEFAULT 0,
    `status` TINYINT DEFAULT 1,
    `created_at` DATETIME,
    `updated_at` DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `project` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `cover` VARCHAR(255),
    `url` VARCHAR(255),
    `github_url` VARCHAR(255),
    `tags` VARCHAR(255),
    `sort_order` INT DEFAULT 0,
    `status` TINYINT DEFAULT 1,
    `created_at` DATETIME,
    `updated_at` DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `operation_log` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50),
    `operation` VARCHAR(100),
    `module` VARCHAR(50),
    `ip` VARCHAR(50),
    `user_agent` VARCHAR(500),
    `params` TEXT,
    `created_at` DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `admin` (`username`, `password`, `nickname`, `email`, `role`, `status`, `created_at`, `updated_at`) 
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '管理员', 'admin@example.com', 'ROLE_ADMIN', 1, NOW(), NOW());
