-- 宠物护理助手数据库初始化脚本
-- 使用前请先安装 MySQL 8.0+，并执行本脚本

CREATE DATABASE IF NOT EXISTS pet_care DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE pet_care;

-- 说明：表结构由 Spring Boot JPA 的 ddl-auto: update 自动创建
-- 本脚本仅创建数据库，表会在应用首次启动时自动生成

-- 如需手动建表，可取消以下注释执行：

-- CREATE TABLE pet (
--   id BIGINT NOT NULL AUTO_INCREMENT,
--   name VARCHAR(255),
--   species VARCHAR(255),
--   breed VARCHAR(255),
--   birth_date DATE,
--   gender VARCHAR(255),
--   image_url VARCHAR(255),
--   PRIMARY KEY (id)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- CREATE TABLE health_record (
--   id BIGINT NOT NULL AUTO_INCREMENT,
--   pet_id BIGINT,
--   care_type VARCHAR(255),
--   weight DOUBLE,
--   temperature DOUBLE,
--   mental_state VARCHAR(255),
--   note VARCHAR(1000),
--   record_date DATE,
--   PRIMARY KEY (id),
--   FOREIGN KEY (pet_id) REFERENCES pet(id)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
--
-- CREATE TABLE reminder (
--   id BIGINT NOT NULL AUTO_INCREMENT,
--   pet_id BIGINT,
--   title VARCHAR(255),
--   reminder_date DATE,
--   completed BOOLEAN,
--   expired BOOLEAN,
--   care_type VARCHAR(255),
--   PRIMARY KEY (id),
--   FOREIGN KEY (pet_id) REFERENCES pet(id)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
