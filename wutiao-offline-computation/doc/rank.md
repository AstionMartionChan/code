## 排行榜逻辑梳理(2018.06.01)

### 钱包排行榜(WalletRankStatistics, 每小时更新)

TOP100今日收益: 根据所有用户今日获得的WB收益(源表中的money_gain)进行排名（只算收入部分不算支出）

TOP100累计收益: 根据所有用户累计获得的WB收益(源表中的money_gain)进行排名

每日凌晨计算截止昨日的收益取出TOP N(N较大), 放入中间表; 今日收益每小时计算输出Top N, 同时和中间表进行结合计算累计收益,
排名取TOP N(N较大)再次存入中间表, 基于此中间表生成累计收益

源表: wutiao.adl_wutiao_money_realtime

```sql
+--------------------------+------------+----------------------+--+
|         col_name         | data_type  |       comment        |
+--------------------------+------------+----------------------+--+
| uid                      | string     | 用户id                 |
| money_gain               | double     | 领币数                  |
| money_distribute         | double     | 分币数                  |
| discover_money_gain      | double     | 发现者领币数               |
| ds                       | string     | 日期分区字段, yyyy-MM-dd   |
| hour                     | string     | 时间分区，99代表当日，999代表历史  |
| # Partition Information  |            |                      |
| # col_name               | data_type  | comment              |
| ds                       | string     | 日期分区字段, yyyy-MM-dd   |
| hour                     | string     | 时间分区，99代表当日，999代表历史  |
+--------------------------+------------+----------------------+--+
```

中间表: wutiao.idl_wutiao_money_realtime

```sql
create table if not exists `wutiao.idl_wutiao_money_realtime` (
  `uid` string,
  `total_money_gain` double
)
comment '五条钱包收益中间表'
partitioned by (`ds` string COMMENT '日期分区字段, yyyy-MM-dd')
stored as parquet;
```

### 发现者排行榜(DiscovererRankStatistics, 每小时更新)

7日收益：根据发现者最近7日获得的发现者类型收益进行排名
最近7日：前6日 + 今日，前6日数值为固定值，今日数值为动态值(今日0：00~24：00)
投票数：最近7日的投票数
不显示审核力

累计收益: 根据发现者累计获得的发现者类型收益进行排名，
投票数: 累计投票数
审核力: 30日审核力, 包含当天获得的值

源表: wutiao.adl_wutiao_discover_moneygain_rank_realtime

```sql
+--------------------------+------------+---------------------+--+
|         col_name         | data_type  |       comment       |
+--------------------------+------------+---------------------+--+
| uid                      | string     | 用户uid               |
| discover_money_gain      | double     | 发现者领币数              |
| verifypoint              | bigint     | 审核力                 |
| vote_cnt                 | bigint     | 投票数                 |
| media_status             | string     | 是否自媒体               |
| ds                       | string     | 日期分区字段, yyyy-MM-dd  |
| hour                     | string     | 小时,yyyyMMddHH       |
| # Partition Information  |            |                     |
| # col_name               | data_type  | comment             |
| ds                       | string     | 日期分区字段, yyyy-MM-dd  |
| hour                     | string     | 小时,yyyyMMddHH       |
+--------------------------+------------+---------------------+--+
```

发现者收益中间表, 每次凌晨计算截止到昨日的汇总, 并将结果输出到Hive表中, 后面每小时计算当日收益时与该表结合形成累计排行榜

计算收益中间表, 需要投票需要计算的是前29天, 需要分为两步

+ 计算截止昨日的累计收益 和 投票数
+ 左外联 近29日的审核力

中间表: wutiao.idl_wutiao_discoverer_rank_realtime

```sql
create table if not exists wutiao.idl_wutiao_discoverer_rank_realtime (
    uid string,
    discover_money_gain double,
    verifypoint double,
    vote_cnt bigint
)
comment '五条发现者累计收益中间表'
partitioned by (
   ds string comment '日期分区字段, yyyy-MM-dd'
)
row format delimited fields terminated by '\001'
stored as parquet;
```

### 家庭贡献力排行榜(FamilyContributionRankStatistics, 每小时更新)

截止到最新时间，根据家庭贡献力进行排名
用户名与头像: 家庭创始人的信息
家庭成员：显示家庭成员的人数
家庭贡献力：显示所有家庭成员(最多16人)最近30天（前29天固定值+当日浮动值）获得的贡献力

前29天通过wutiao.idl_wutiao_user去获取用户在过去29天的贡献力以及家庭归属情况, 每天凌晨计算1次, 并存入Hive中间表
当天通过wutiao.odl_event_qkl去计算当天情况, 和中间表联合进行处理, 每小时生成一次结果

中间表: wutiao.idl_wutiao_family_contribution_realtime

```sql
create table if not exists `wutiao.idl_wutiao_family_contribution_realtime` (
  `uid` string COMMENT '用户编号',
  `history_contributionpoint` double COMMENT '累计贡献力',
  `familyid` string COMMENT '家庭ID'
)
comment '五条家庭排行榜中间表'
partitioned by (
    `ds` string COMMENT '日期分区字段, yyyy-MM-dd'
)
stored as parquet;
```


### 热门创作者排行榜(HotCreatorStatistics, 每小时更新)

根据自媒体用户(media_status='1')的最近7天分币汇总排行Top N, N天前对应的时间点 --> 当前计算点
比如说 计算的时候是当前9点, 那么计算区间就是7天前的9点到当前9点, 整天的按照天分区去取, 小时按小时分区去取

源表: wutiao.adl_wutiao_discover_moneygain_rank_realtime

```sql
+--------------------------+------------+---------------------+--+
|         col_name         | data_type  |       comment       |
+--------------------------+------------+---------------------+--+
| uid                      | string     | 用户uid               |
| discover_money_gain      | double     | 发现者领币数              |
| verifypoint              | bigint     | 审核力                 |
| vote_cnt                 | bigint     | 投票数                 |
| media_status             | string     | 是否自媒体               |
| ds                       | string     | 日期分区字段, yyyy-MM-dd  |
| hour                     | string     | 小时,yyyyMMddHH       |
| # Partition Information  |            |                     |
| # col_name               | data_type  | comment             |
| ds                       | string     | 日期分区字段, yyyy-MM-dd  |
| hour                     | string     | 小时,yyyyMMddHH       |
+--------------------------+------------+---------------------+--+
```