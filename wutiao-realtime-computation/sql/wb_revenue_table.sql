-- wb revenue result table
create table if not exists tbl_kingnetdc_blockchain_wbrevenue (
   `item_type` SMALLINT	 NOT NULL  COMMENT '文章类型 1:资讯, 2:视频',
   `event_time` TIMESTAMP NOT NULL  COMMENT '事件时间',
   `item_id`  VARCHAR(255)  NOT NULL COMMENT '文章id',
   `Y`  DOUBLE NOT NULL COMMENT '文章类型收益占比',
   `A` DOUBLE NOT NULL COMMENT '用户贡献力, 审核力与各种操作形成的得分',
   `total_A` DOUBLE NOT NULL COMMENT '同文章类型， 同时间段(event_time)用户贡献力, 审核力与各种操作形成的得分总和',
   `coin` DOUBLE  NOT NULL COMMENT '分币',
   `revenue` DOUBLE NOT NULL COMMENT '最终收益',
   `last_updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY `pk_kingnetdc_blockchain_wbrevenue` (`item_type`, `event_time`,  `item_id`)
)
