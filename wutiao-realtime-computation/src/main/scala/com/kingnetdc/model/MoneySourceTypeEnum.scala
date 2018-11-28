package com.kingnetdc.model

/**
  * Created by zhouml on 11/06/2018.
  */
object MoneySourceTypeEnum extends Enumeration {

    // ID 不允许修改
    val register = Value(1, "注册")

    val invite = Value(2, "邀请")

    val invited = Value(3, "被邀请")

    // 邀请好友获得的额外收益
    val extraGain = Value(4, "邀请好友获得的额外收益")

    val report = Value(5, "举报")

    val issue = Value(10, "发表内容")

    val read = Value(11, "阅读")

    // 普通用户的点赞
    val vote = Value(12, "投票")

    val share = Value(13, "分享")

    val comment = Value(14, "评论")

    val discover = Value(15, "发现")

}

