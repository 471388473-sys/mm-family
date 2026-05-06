-- ============================================
-- POS服务平台 数据库建表脚本
-- 数据库: mm_family
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- ============================================

CREATE DATABASE IF NOT EXISTS `mm_family` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `mm_family`;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `openid` varchar(64) DEFAULT NULL COMMENT '微信openid',
  `union_id` varchar(64) DEFAULT NULL COMMENT '微信unionid',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(512) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint DEFAULT '1' COMMENT '状态：1-正常 0-禁用',
  `last_login_at` datetime DEFAULT NULL COMMENT '最后登录时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`),
  UNIQUE KEY `uk_union_id` (`union_id`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户用户表';

-- ----------------------------
-- 2. 管理员表
-- ----------------------------
DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(64) NOT NULL COMMENT '用户名',
  `password` varchar(128) NOT NULL COMMENT '密码（bcrypt加密存储）',
  `real_name` varchar(64) DEFAULT NULL COMMENT '真实姓名',
  `role` varchar(20) DEFAULT 'service' COMMENT '角色：admin/service',
  `status` tinyint DEFAULT '1' COMMENT '状态：1-正常 0-禁用',
  `last_login_at` datetime DEFAULT NULL COMMENT '最后登录时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- ----------------------------
-- 3. POS机领取申请表
-- ----------------------------
DROP TABLE IF EXISTS `pos_application`;
CREATE TABLE `pos_application` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `user_id` bigint NOT NULL COMMENT '申请用户ID',
  `name` varchar(64) NOT NULL COMMENT '申请人姓名',
  `phone` varchar(20) NOT NULL COMMENT '申请人手机号',
  `address` varchar(512) NOT NULL COMMENT '收货地址',
  `status` tinyint DEFAULT '1' COMMENT '状态：1-已提交 2-已处理',
  `processed_by` bigint DEFAULT NULL COMMENT '处理人ID',
  `processed_at` datetime DEFAULT NULL COMMENT '处理时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_phone` (`phone`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='POS机领取申请表';

-- ----------------------------
-- 4. 机具表
-- ----------------------------
DROP TABLE IF EXISTS `machine`;
CREATE TABLE `machine` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '机具ID',
  `machine_code` varchar(64) NOT NULL COMMENT '机具编码（SN码）',
  `product_id` bigint DEFAULT NULL COMMENT '产品ID',
  `customer_id` bigint DEFAULT NULL COMMENT '绑定客户ID',
  `bind_phone` varchar(20) DEFAULT NULL COMMENT '绑定手机号（冗余字段）',
  `status` tinyint DEFAULT '0' COMMENT '状态：0-未绑定 1-已绑定',
  `bind_at` datetime DEFAULT NULL COMMENT '绑定时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_machine_code` (`machine_code`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_bind_phone` (`bind_phone`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='机具表';

-- ----------------------------
-- 5. 机具交易量表
-- ----------------------------
DROP TABLE IF EXISTS `machine_transaction`;
CREATE TABLE `machine_transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `machine_id` bigint NOT NULL COMMENT '机具ID',
  `machine_code` varchar(64) DEFAULT NULL COMMENT '机具编码（冗余）',
  `transaction_amount` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '本次导入交易量（元）',
  `total_transaction` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '导入后累计交易量（元）',
  `points_earned` int NOT NULL DEFAULT '0' COMMENT '本次获得积分',
  `import_batch` varchar(64) DEFAULT NULL COMMENT '导入批次号',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_machine_id` (`machine_id`),
  KEY `idx_machine_code` (`machine_code`),
  KEY `idx_import_batch` (`import_batch`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='机具交易量表';

-- ----------------------------
-- 6. 积分账户表
-- ----------------------------
DROP TABLE IF EXISTS `points_account`;
CREATE TABLE `points_account` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '账户ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `points` int NOT NULL DEFAULT '0' COMMENT '当前积分总额',
  `balance` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '当前余额（元）',
  `total_exchanged` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '累计兑换金额（元）',
  `total_withdrawn` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '累计提现金额（元）',
  `total_lottery_winnings` int NOT NULL DEFAULT '0' COMMENT '累计抽奖获得积分',
  `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分账户表';

-- ----------------------------
-- 7. 积分变动记录表
-- ----------------------------
DROP TABLE IF EXISTS `points_record`;
CREATE TABLE `points_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `type` varchar(20) NOT NULL COMMENT '类型：import/exchange/lottery/adjust',
  `points_change` int NOT NULL DEFAULT '0' COMMENT '积分变动值',
  `balance_change` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '余额变动值',
  `points_after` int NOT NULL DEFAULT '0' COMMENT '变动后积分余额',
  `balance_after` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '变动后余额',
  `description` varchar(256) DEFAULT NULL COMMENT '变动描述',
  `ref_id` bigint DEFAULT NULL COMMENT '关联业务ID',
  `ref_type` varchar(32) DEFAULT NULL COMMENT '关联业务类型',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID，系统自动则为null',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '变动时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分变动记录表';

-- ----------------------------
-- 8. 提现记录表
-- ----------------------------
DROP TABLE IF EXISTS `withdrawal_record`;
CREATE TABLE `withdrawal_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '提现记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `amount` decimal(15,2) NOT NULL COMMENT '提现金额（元）',
  `fixed_fee` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '固定手续费（元）',
  `fee_rate` decimal(5,4) NOT NULL DEFAULT '0.0000' COMMENT '提现时费率',
  `rate_fee` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '费率计算费用（元）',
  `total_fee` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '总费用（元）',
  `actual_amount` decimal(15,2) NOT NULL COMMENT '实际到账金额（元）',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-待审核 1-审核通过 2-审核拒绝 3-已打款',
  `reject_reason` varchar(256) DEFAULT NULL COMMENT '拒绝原因',
  `transaction_no` varchar(64) DEFAULT NULL COMMENT '打款交易号',
  `approved_by` bigint DEFAULT NULL COMMENT '审核人ID',
  `approved_at` datetime DEFAULT NULL COMMENT '审核时间',
  `paid_by` bigint DEFAULT NULL COMMENT '打款确认人ID',
  `paid_at` datetime DEFAULT NULL COMMENT '打款时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提现记录表';

-- ----------------------------
-- 9. 系统配置表
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(64) NOT NULL COMMENT '配置键',
  `config_value` varchar(1024) DEFAULT NULL COMMENT '配置值',
  `description` varchar(256) DEFAULT NULL COMMENT '配置说明',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ----------------------------
-- 10. 抽奖活动配置表
-- ----------------------------
DROP TABLE IF EXISTS `lottery_config`;
CREATE TABLE `lottery_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `enabled` tinyint NOT NULL DEFAULT '1' COMMENT '活动开关：0-关闭 1-开启',
  `title` varchar(128) DEFAULT '积分抽奖' COMMENT '活动名称',
  `threshold` int NOT NULL DEFAULT '1000' COMMENT '抽奖阈值（积分）',
  `daily_limit` int NOT NULL DEFAULT '3' COMMENT '每人每日抽奖上限',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='抽奖活动配置表';

-- ----------------------------
-- 11. 抽奖奖项表
-- ----------------------------
DROP TABLE IF EXISTS `lottery_prize`;
CREATE TABLE `lottery_prize` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '奖项ID',
  `name` varchar(64) NOT NULL COMMENT '奖项名称，如"一等奖"',
  `points` int NOT NULL DEFAULT '0' COMMENT '奖励积分数',
  `probability` decimal(6,4) NOT NULL DEFAULT '0.0000' COMMENT '中奖概率，0-1之间',
  `total_count` int NOT NULL DEFAULT '-1' COMMENT '奖品总量，-1表示不限量',
  `remaining_count` int NOT NULL DEFAULT '-1' COMMENT '剩余数量，-1表示不限量',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序值，升序',
  `enabled` tinyint NOT NULL DEFAULT '1' COMMENT '是否启用：0-禁用 1-启用',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_enabled` (`enabled`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='抽奖奖项表';

-- ----------------------------
-- 12. 抽奖记录表
-- ----------------------------
DROP TABLE IF EXISTS `lottery_record`;
CREATE TABLE `lottery_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '抽奖记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `prize_id` bigint NOT NULL COMMENT '中奖奖项ID',
  `prize_name` varchar(64) NOT NULL COMMENT '奖项名称（冗余）',
  `prize_points` int NOT NULL DEFAULT '0' COMMENT '奖励积分数',
  `points_before` int NOT NULL DEFAULT '0' COMMENT '抽奖前积分',
  `points_after` int NOT NULL DEFAULT '0' COMMENT '抽奖后积分',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '抽奖时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='抽奖记录表';

-- ----------------------------
-- 13. 用户每日抽奖统计表
-- ----------------------------
DROP TABLE IF EXISTS `lottery_daily`;
CREATE TABLE `lottery_daily` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `date` date NOT NULL COMMENT '日期',
  `draw_count` int NOT NULL DEFAULT '0' COMMENT '当日已抽次数',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`, `date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户每日抽奖统计表';

-- ----------------------------
-- 14. 公告表
-- ----------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(128) NOT NULL COMMENT '标题',
  `content` text COMMENT '内容（富文本HTML）',
  `summary` varchar(256) DEFAULT NULL COMMENT '摘要',
  `type` varchar(20) NOT NULL DEFAULT 'notice' COMMENT '类型：important/notice/activity',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-草稿 1-已发布',
  `is_top` tinyint NOT NULL DEFAULT '0' COMMENT '是否置顶：0-否 1-是',
  `published_at` datetime DEFAULT NULL COMMENT '发布时间',
  `created_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_top` (`is_top`),
  KEY `idx_published_at` (`published_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公告表';

-- ----------------------------
-- 15. 激活产品表
-- ----------------------------
DROP TABLE IF EXISTS `activation_product`;
CREATE TABLE `activation_product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '产品ID',
  `name` varchar(128) NOT NULL COMMENT '产品名称',
  `description` varchar(512) DEFAULT NULL COMMENT '产品描述',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序值',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='激活产品表';

-- ----------------------------
-- 16. 激活步骤表
-- ----------------------------
DROP TABLE IF EXISTS `activation_step`;
CREATE TABLE `activation_step` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '步骤ID',
  `product_id` bigint NOT NULL COMMENT '产品ID',
  `title` varchar(128) NOT NULL COMMENT '步骤标题',
  `description` varchar(512) DEFAULT NULL COMMENT '步骤说明',
  `image_url` varchar(512) DEFAULT NULL COMMENT '引导图片URL',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序值，升序',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='激活步骤表';

-- ----------------------------
-- 17. 激活所需材料表
-- ----------------------------
DROP TABLE IF EXISTS `activation_material`;
CREATE TABLE `activation_material` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '材料ID',
  `product_id` bigint NOT NULL COMMENT '产品ID',
  `name` varchar(64) NOT NULL COMMENT '材料名称',
  `required` tinyint NOT NULL DEFAULT '1' COMMENT '是否必填：0-选填 1-必填',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序值',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='激活所需材料表';

-- ----------------------------
-- 18. 常见问题表
-- ----------------------------
DROP TABLE IF EXISTS `faq`;
CREATE TABLE `faq` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'FAQ ID',
  `question` varchar(256) NOT NULL COMMENT '问题',
  `answer` text COMMENT '回答',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序值，升序',
  `enabled` tinyint NOT NULL DEFAULT '1' COMMENT '是否启用：0-禁用 1-启用',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_enabled` (`enabled`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='常见问题表';

-- ----------------------------
-- 19. 导入批次表
-- ----------------------------
DROP TABLE IF EXISTS `import_batch`;
CREATE TABLE `import_batch` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '批次ID',
  `batch_no` varchar(64) NOT NULL COMMENT '批次号',
  `type` varchar(32) NOT NULL COMMENT '导入类型：machine/transaction',
  `total_rows` int NOT NULL DEFAULT '0' COMMENT '导入总行数',
  `success_rows` int NOT NULL DEFAULT '0' COMMENT '成功行数',
  `fail_rows` int NOT NULL DEFAULT '0' COMMENT '失败行数',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-处理中 1-完成 2-失败',
  `error_detail` text COMMENT '失败明细，JSON格式',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导入批次表';

-- ----------------------------
-- 20. 短信验证码日志表
-- ----------------------------
DROP TABLE IF EXISTS `sms_log`;
CREATE TABLE `sms_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `code` varchar(8) NOT NULL COMMENT '验证码',
  `scene` varchar(32) NOT NULL COMMENT '场景：pos_apply/login/other',
  `ip` varchar(64) DEFAULT NULL COMMENT '请求IP',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-未使用 1-已使用 2-已过期',
  `expire_at` datetime NOT NULL COMMENT '过期时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`),
  KEY `idx_phone` (`phone`),
  KEY `idx_scene` (`scene`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短信验证码日志表';

-- ----------------------------
-- 21. 操作日志表
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(64) DEFAULT NULL COMMENT '操作人姓名',
  `module` varchar(32) NOT NULL COMMENT '模块：customer/machine/points/withdrawal/announcement/lottery/activation/faq/config',
  `action` varchar(32) NOT NULL COMMENT '操作：create/update/delete/import/export/approve/reject',
  `target_id` bigint DEFAULT NULL COMMENT '操作对象ID',
  `detail` text COMMENT '操作详情，JSON格式',
  `ip` varchar(64) DEFAULT NULL COMMENT '操作IP',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_module` (`module`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入默认管理员账号 (密码: admin123，BCRYPT加密)
INSERT INTO `admin_user` (`username`, `password`, `real_name`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt.zH/0y', '系统管理员', 'admin', 1),
('service', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt.zH/0y', '客服', 'service', 1);

-- 插入默认系统配置
INSERT INTO `system_config` (`config_key`, `config_value`, `description`) VALUES
('exchange_rate', '100', '积分兑换比例，100积分兑换1元'),
('min_withdraw', '100', '最低提现金额（元）'),
('fixed_fee', '2', '提现固定手续费（元）'),
('fee_rate', '0.005', '提现费率（0.5%）'),
('cs_wechat_id', 'pos_service', '客服微信号'),
('cs_phone', '400-888-8888', '客服热线'),
('cs_work_time', '周一至周五 9:00-18:00', '客服工作时间');

-- 插入默认抽奖配置
INSERT INTO `lottery_config` (`enabled`, `title`, `threshold`, `daily_limit`) VALUES
(1, '积分抽奖', 1000, 3);

-- 插入默认抽奖奖项
INSERT INTO `lottery_prize` (`name`, `points`, `probability`, `total_count`, `remaining_count`, `sort`, `enabled`) VALUES
('一等奖', 500, 0.0100, 10, 10, 1, 1),
('二等奖', 200, 0.0500, 50, 50, 2, 1),
('三等奖', 100, 0.1000, 100, 100, 3, 1),
('四等奖', 50, 0.2000, -1, -1, 4, 1),
('五等奖', 20, 0.3000, -1, -1, 5, 1),
('谢谢参与', 0, 0.3100, -1, -1, 6, 1);

-- 插入示例公告
INSERT INTO `announcement` (`title`, `content`, `summary`, `type`, `status`, `is_top`, `published_at`, `created_by`) VALUES
('欢迎使用POS服务平台', '<p>欢迎使用我们的POS服务平台，您可以通过本平台领取POS机、管理交易积分等功能。</p>', '平台使用说明', 'important', 1, 1, NOW(), 1);

-- 插入示例激活产品
INSERT INTO `activation_product` (`name`, `description`, `sort`) VALUES
('传统POS机', '传统有线POS机，适合固定收银场景', 1),
('移动POS机', '便携式移动POS机，支持无线连接', 2),
('智能POS机', '智能安卓系统POS机，功能丰富', 3);

-- 插入示例FAQ
INSERT INTO `faq` (`question`, `answer`, `sort`, `enabled`) VALUES
('如何领取POS机？', '点击首页"免费领取POS机"按钮，填写收货信息后提交申请，我们会在1-3个工作日内审核并安排发货。', 1, 1),
('积分如何获得？', '您每日的POS机交易量会按一定比例转换为积分，交易量越多，获得的积分越多。', 2, 1),
('积分如何提现？', '积分达到一定数量后，可以在"积分兑换"页面将积分兑换为余额，余额可申请提现到银行卡。', 3, 1);