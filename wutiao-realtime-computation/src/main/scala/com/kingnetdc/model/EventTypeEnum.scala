package com.kingnetdc.model

object EventTypeEnum extends Enumeration {

    val LOGIN = Value(1, "login")

    val OPEN_CLIENT = Value(2, "openclient")

    val ACTIVE = Value(3, "active")

    val READ = Value(4, "read")

    val VALID_READ = Value(5, "validread")

    val PLAY = Value(6, "play")

    val WITHDRAW = Value(7, "withdraw")

    val CONTRIBUTIONINC = Value(8, "contributioninc")

    val MONEY_DISTRIBUTE = Value(9, "money_distribute")

    val ENTERFRONT = Value(10, "enterfront")

    val COMMENT = Value(11, "comment")

    val NOINTEREST = Value(12, "nointerest")

    val REPORT = Value(13, "report")

    val FAVOUR = Value(14, "favour")

    val LIKE = Value(15, "like")

    val SHARE = Value(16, "share")

    val CLICK = Value(17, "click")

    val ATTENTION = Value(18, "attention")

    val LEAVEREAD = Value(19, "leaveread")

    val SEARCH = Value(20, "search")

    val SHOWINPAGE = Value(21, "showinpage")

    val REGISTER = Value(22, "register")

}
