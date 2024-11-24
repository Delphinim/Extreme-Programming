CREATE TABLE IF NOT EXISTS `contact` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `address` varchar(200) DEFAULT NULL COMMENT '地址',
  `wechat` varchar(50) DEFAULT NULL COMMENT '微信号',
  `qq` varchar(20) DEFAULT NULL COMMENT 'QQ号',
  `weibo` varchar(50) DEFAULT NULL COMMENT '微博账号',
  `is_favorite` tinyint(1) DEFAULT 0 COMMENT '是否收藏：0-否，1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_name` (`name`),
  INDEX `idx_favorite` (`is_favorite`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联系人表';

CREATE TABLE IF NOT EXISTS `contact_phone` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `contact_id` bigint(20) NOT NULL COMMENT '联系人ID',
  `phone` varchar(20) NOT NULL COMMENT '电话号码',
  `type` varchar(20) NOT NULL COMMENT '类型：MOBILE-手机, HOME-家庭, WORK-工作, OTHER-其他',
  `description` varchar(50) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`),
  INDEX `idx_contact` (`contact_id`),
  CONSTRAINT `fk_contact_phone` FOREIGN KEY (`contact_id`) REFERENCES `contact` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联系人电话表'; 