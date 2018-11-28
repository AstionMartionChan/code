```sql
create table if not exists wutiao.odl_wutiao_money_gain (
  uid string comment '用户编号',
  itemId string comment '文章编号',
  itemType string comment '文章类型',
  messageId string comment '消息ID',
  sourceId string comment '用户来源编号',
  sourceType string comment '用户来源币的类型 10发表内容/11阅读/12投票/13分享/14评论/15发现',
  actionType int comment '操作类型 1注册/2邀请/3被邀请/4好友/5举报/9其他/10发表内容/11阅读/12投票(点赞)/13分享/14评论/15发现(投票)/16邀请额外收益',
  value double comment '获取多少币',
  `time` bigint comment '分币日期 seconds',
  partitionId string comment 'kafka分区Id',
  offset string comment 'kafka offset'
)
comment '区块链领币源表'
partitioned by (
   ds string comment '日期分区字段, yyyy-MM-dd',
   hour string comment '小时分区字段, yyyyMMddHH'
)
row format delimited fields terminated by '\001'
stored as parquet
```

用户活跃榜单: 按 阅读、    点赞、   评论、     分享、  注册类型, 这5种类型的收益之和进行排名
actionType     11      12       14         13      1

发现者榜单: 按发现者的投票类型收益进行排名 actionType = 15

自媒体榜单: 按自媒体的创作类型收益进行排名 actionType = 10

排序规则: 按WB实际数值大小进行排名，数值相同时，uid越小, 排前面