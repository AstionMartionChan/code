## 区块链五条项目  

* ### bdl信息层 ###
    * 从odl抽取数据
    * 包含用户信息、设备信息和事件信息等

* ### idl中间层 ###
    * 从bdl抽取数据
    * 以用户、版本、渠道为维度统计用户行为等

* ### adl分析层 ###
    * 活跃用户行为统计
    * 全部用户行为统计
    * 播放阅读事件统计

* ### createtable ###
    * hivetab
        * 创建hive表
    * mysqltab
        * 创建mysql表

* ### report ###
    * 以idl或adl为数据源
    * 离线报表

* ### realtime ###
    * 以odl、idl或adl为数据源
    * 实时报表