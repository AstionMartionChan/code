package com.kingnetdc.model

/**
 * Created by zhouml on 23/07/2018.
 */
object WithdrawStatus extends Enumeration {

    val APPLIED = Value(1, "applied")

    val SUCCESS = Value(2, "success")

    val FAILED = Value(3, "failed")

}
