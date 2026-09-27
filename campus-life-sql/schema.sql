-- 校园生活平台（campus-life 单体）建表脚本
-- 板块一：问答社区（问题/回答/评论/点赞/关注/签到/标签/通知）
-- 板块二：活动报名（活动/报名，Redis Lua 抢名额 + 候补补位）
-- 执行方式：MySQL 8 客户端直接执行本文件（mysql.exe 直灌 ECS 亦可）
CREATE DATABASE IF NOT EXISTS campus_life DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_life;

-- 用户表（两个板块共用）
CREATE TABLE IF NOT EXISTS tb_user (
    id          BIGINT       NOT NULL COMMENT '主键（雪花ID）',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt哈希，禁止明文）',
    nickname    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    avatar_url  VARCHAR(255) DEFAULT NULL COMMENT '头像',
    email       VARCHAR(64)  DEFAULT NULL COMMENT '邮箱',
    bio         VARCHAR(255) DEFAULT NULL COMMENT '个人简介',
    role        TINYINT      NOT NULL DEFAULT 0 COMMENT '角色：0学生 1管理员',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1正常 0封禁',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

-- 活动表
CREATE TABLE IF NOT EXISTS tb_activity (
    id                BIGINT       NOT NULL COMMENT '主键（雪花ID）',
    title             VARCHAR(100) NOT NULL COMMENT '活动标题',
    description       TEXT COMMENT '活动介绍',
    cover_url         VARCHAR(255) DEFAULT NULL COMMENT '封面图',
    location          VARCHAR(100) NOT NULL COMMENT '活动地点',
    start_time        DATETIME     NOT NULL COMMENT '活动开始时间',
    end_time          DATETIME     NOT NULL COMMENT '活动结束时间',
    signup_start_time DATETIME     NOT NULL COMMENT '报名开始时间',
    signup_end_time   DATETIME     NOT NULL COMMENT '报名截止时间',
    quota             INT          NOT NULL COMMENT '总名额',
    status            TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1报名中 2进行中 3已结束 4已取消',
    publisher_id      BIGINT       NOT NULL COMMENT '发布人',
    create_time       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_status (status),
    KEY idx_signup_time (signup_start_time, signup_end_time)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '活动表';

-- 报名表（含候补；Redis 是实时事实，本表是持久化记录）
CREATE TABLE IF NOT EXISTS tb_signup (
    id          BIGINT   NOT NULL COMMENT '主键（雪花ID）',
    activity_id BIGINT   NOT NULL COMMENT '活动ID',
    user_id     BIGINT   NOT NULL COMMENT '用户ID',
    status      TINYINT  NOT NULL DEFAULT 1 COMMENT '状态：1已报名 2候补中 3已取消',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_activity_user (activity_id, user_id),
    KEY idx_user (user_id),
    KEY idx_activity_status (activity_id, status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '报名表（含候补）';

-- ============ 板块一：问答社区 ============

-- 问题表
CREATE TABLE IF NOT EXISTS tb_question
(
    id            BIGINT       NOT NULL COMMENT '主键，雪花ID',
    user_id       BIGINT       NOT NULL COMMENT '提问者ID',
    title         VARCHAR(128) NOT NULL COMMENT '标题',
    content       TEXT         NOT NULL COMMENT '正文',
    view_count    INT          NOT NULL DEFAULT 0 COMMENT '浏览数',
    like_count    INT          NOT NULL DEFAULT 0 COMMENT '点赞数',
    answer_count  INT          NOT NULL DEFAULT 0 COMMENT '回答数',
    comment_count INT          NOT NULL DEFAULT 0 COMMENT '评论数',
    status        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1正常 0删除',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_create_time (create_time)
) ENGINE = InnoDB COMMENT ='问题表';

-- 标签表
CREATE TABLE IF NOT EXISTS tb_tag
(
    id             BIGINT      NOT NULL COMMENT '主键，雪花ID',
    name           VARCHAR(32) NOT NULL COMMENT '标签名，唯一',
    question_count INT         NOT NULL DEFAULT 0 COMMENT '关联问题数',
    create_time    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name (name)
) ENGINE = InnoDB COMMENT ='标签表';

-- 问题-标签关联表
CREATE TABLE IF NOT EXISTS tb_question_tag
(
    id          BIGINT   NOT NULL COMMENT '主键，雪花ID',
    question_id BIGINT   NOT NULL COMMENT '问题ID',
    tag_id      BIGINT   NOT NULL COMMENT '标签ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_question_tag (question_id, tag_id),
    KEY idx_tag_id (tag_id)
) ENGINE = InnoDB COMMENT ='问题标签关联表';

-- 回答表
CREATE TABLE IF NOT EXISTS tb_answer
(
    id            BIGINT   NOT NULL COMMENT '主键，雪花ID',
    question_id   BIGINT   NOT NULL COMMENT '问题ID',
    user_id       BIGINT   NOT NULL COMMENT '回答者ID',
    content       TEXT     NOT NULL COMMENT '回答内容',
    like_count    INT      NOT NULL DEFAULT 0 COMMENT '点赞数',
    comment_count INT      NOT NULL DEFAULT 0 COMMENT '评论数',
    status        TINYINT  NOT NULL DEFAULT 1 COMMENT '状态：1正常 0删除',
    create_time   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_question_id (question_id),
    KEY idx_user_id (user_id)
) ENGINE = InnoDB COMMENT ='回答表';

-- 评论表（挂在回答下）
CREATE TABLE IF NOT EXISTS tb_comment
(
    id          BIGINT       NOT NULL COMMENT '主键，雪花ID',
    answer_id   BIGINT       NOT NULL COMMENT '回答ID',
    user_id     BIGINT       NOT NULL COMMENT '评论者ID',
    content     VARCHAR(500) NOT NULL COMMENT '评论内容',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1正常 0删除',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_answer_id (answer_id)
) ENGINE = InnoDB COMMENT ='评论表';

-- 点赞表（问题/回答通用，target_type 区分）
CREATE TABLE IF NOT EXISTS tb_like
(
    id          BIGINT   NOT NULL COMMENT '主键，雪花ID',
    user_id     BIGINT   NOT NULL COMMENT '点赞者ID',
    target_id   BIGINT   NOT NULL COMMENT '目标ID（问题或回答）',
    target_type TINYINT  NOT NULL COMMENT '目标类型：1问题 2回答',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_target (user_id, target_id, target_type)
) ENGINE = InnoDB COMMENT ='点赞表';

-- 通知表
CREATE TABLE IF NOT EXISTS tb_notification
(
    id          BIGINT       NOT NULL COMMENT '主键，雪花ID',
    user_id     BIGINT       NOT NULL COMMENT '接收者ID',
    sender_id   BIGINT       NOT NULL COMMENT '触发者ID',
    type        TINYINT      NOT NULL COMMENT '类型：1被回答 2被评论 3被点赞',
    question_id BIGINT       NULL COMMENT '关联问题ID',
    target_id   BIGINT       NULL COMMENT '关联目标ID（回答/评论）',
    content     VARCHAR(255) NULL COMMENT '通知摘要',
    is_read     TINYINT      NOT NULL DEFAULT 0 COMMENT '是否已读：0未读 1已读',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_read (user_id, is_read)
) ENGINE = InnoDB COMMENT ='通知表';

-- 关注表
CREATE TABLE IF NOT EXISTS tb_follow
(
    id             BIGINT   NOT NULL COMMENT '主键，雪花ID',
    user_id        BIGINT   NOT NULL COMMENT '关注者ID',
    follow_user_id BIGINT   NOT NULL COMMENT '被关注者ID',
    create_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_follow (user_id, follow_user_id)
) ENGINE = InnoDB COMMENT ='关注表';

-- 签到表（Redis Bitmap 是实时事实，本表是持久化记录）
CREATE TABLE IF NOT EXISTS tb_checkin
(
    id           BIGINT   NOT NULL COMMENT '主键，雪花ID',
    user_id      BIGINT   NOT NULL COMMENT '签到用户ID',
    checkin_date DATE     NOT NULL COMMENT '签到日期',
    streak       INT      NOT NULL DEFAULT 1 COMMENT '截至当天的连续签到天数',
    create_time  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_date (user_id, checkin_date),
    KEY idx_date (checkin_date)
) ENGINE = InnoDB COMMENT ='签到表';
