package com.kingnetdc.model

/**
* Created by zhouml on 13/06/2018.
*/

case class IdlExchangeRate(
    window: String,  // yyyy-MM-dd HH:mm:ss
    register: Double,
    withdrawRMB: Double,
    moneyGain: Double,
    withdrawCoin: Double
)

