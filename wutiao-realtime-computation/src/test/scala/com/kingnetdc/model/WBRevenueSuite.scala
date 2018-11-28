package com.kingnetdc.model

import com.kingnetdc.UnitSpecs

/**
  * Created by zhouml on 18/05/2018.
  */
// scalastyle:off
class WBRevenueSuite extends UnitSpecs {

  val fixture = {
    new {
      // 1, 2 同为类型2并且同时段
      val wbRevenueLog1 =
        """{"all_comment":1000,"all_likes":1420,"all_read":200,"all_share":12382,"all_view":20000,"coin":"121.921420801991","contributions":{211:"521",212:"534"},"item_id":"111001","item_type":2,"media_id":"1002","plat_avg_comment_rate": 0.23,"plat_avg_like_rate": 0.12,"plat_avg_read_rate": 0.22,"plat_avg_share_rate": 0.12,"time":1526354640,"verifys":{1111:"122",1112:"112",1113:"105",1114:"109"}}"""

      val wbRevenueLog2 =
        """{"all_comment":1200,"all_likes":100,"all_read":200,"all_share":986,"all_view":2343,"coin":"163.07857919800895","contributions":{211:"631",212:"734"},"item_id":"111002","item_type":2,"media_id":"1003","plat_avg_comment_rate": 0.19,"plat_avg_like_rate": 0.22,"plat_avg_read_rate": 0.32,"plat_avg_share_rate": 0.42,"time":1526354640,"verifys":{1111:"132",1112:"142",1113:"105",1114:"109"}}"""

      // 3, 4 同为类型1并且同时段
      val wbRevenueLog3 =
        """{"all_comment":800,"all_likes":1420,"all_read":200,"all_share":12382,"all_view":20000,"coin":"170.0147104492775","contributions":{211:"521",212:"534"},"item_id":"111003","item_type":1,"media_id":"1002","plat_avg_comment_rate": 0.23,"plat_avg_like_rate": 0.12,"plat_avg_read_rate": 0.22,"plat_avg_share_rate": 0.12,"time":1526354640,"verifys":{1111:"122",1112:"112",1113:"105",1114:"109"}}"""
      val wbRevenueLog4 =
        """{"all_comment":210,"all_likes":100,"all_read":200,"all_share":986,"all_view":2343,"coin":"114.9852895507225","contributions":{211:"631",212:"534"},"item_id":"111004","item_type":1,"media_id":"1003","plat_avg_comment_rate": 0.19,"plat_avg_like_rate": 0.22,"plat_avg_read_rate": 0.32,"plat_avg_share_rate": 0.42,"time":1526354640,"verifys":{1111:"132",1112:"142",1113:"105",1114:"109"}}"""

      // 5, 6 同为类型3, 同时段但是与1,2,3,4不同时段
      val wbRevenueLog5 =
        """{"all_comment":1200,"all_likes":110,"all_read":200,"all_share":986,"all_view":2143,"coin":"407.57353506149605","contributions":{211:"631",212:"734"},"item_id":"111013","item_type":3,"media_id":"1003","plat_avg_comment_rate": 0.19,"plat_avg_like_rate": 0.22,"plat_avg_read_rate": 0.32,"plat_avg_share_rate": 0.42,"time":1526354700,"verifys":{1111:"132",1112:"142",1113:"115",1114:"109"}}"""
      val wbRevenueLog6 =
        """{"all_comment":750,"all_likes":100,"all_read":200,"all_share":986,"all_view":2343,"coin":"352.4264649385039","contributions":{211:"631",212:"734"},"item_id":"111093","item_type":3,"media_id":"1003","plat_avg_comment_rate": 0.19,"plat_avg_like_rate": 0.22,"plat_avg_read_rate": 0.32,"plat_avg_share_rate": 0.42,"time":1526354700,"verifys":{1111:"132",1112:"142",1113:"105",1114:"109"}}"""    }
  }

  "wbRevenueLog1" should "pass check" in {
     import fixture._
     val revenue = WBRevenueLog.parse(wbRevenueLog1).get
     // Y
     revenue.revenuePercentageByType shouldBe 1900 * 0.15

//     log1.K1 shouldBe 0.1
//     log1.K2 shouldBe 0.5916666666666667
//     log1.K3 shouldBe 0.5
//     log1.K4 shouldBe 2

     // A
     revenue.scoreBasedOnOperation shouldBe 4797.075
  }

  "wbRevenueLog2" should "pass check" in {
    import fixture._
    val revenue = WBRevenueLog.parse(wbRevenueLog2).get
    // Y
    revenue.revenuePercentageByType shouldBe 1900 * 0.15

//    log2.K1 shouldBe 0.2667520273154076
//    log2.K2 shouldBe  0.19400147441120552
//    log2.K3 shouldBe 2
//    log2.K4 shouldBe 1.0019714245066358

    revenue.scoreBasedOnOperation shouldBe 6416.42928831021
  }


  "wbRevenueLog3 wbRevenueLog4" should "pass revenue check" in {
    import fixture._
    val revenue3 = WBRevenueLog.parse(wbRevenueLog3).get
    val revenue4 = WBRevenueLog.parse(wbRevenueLog4).get

    revenue3.revenuePercentageByType shouldBe 285.0
    revenue3.scoreBasedOnOperation shouldBe 4797.075

    revenue4.revenuePercentageByType shouldBe 285.0
    revenue4.scoreBasedOnOperation shouldBe 3244.3843030635603

    val group = (revenue3.scoreBasedOnOperation + revenue4.scoreBasedOnOperation)

    (revenue3.scoreBasedOnOperation / group) * revenue3.revenuePercentageByType shouldBe revenue3.coin
    (revenue4.scoreBasedOnOperation / group) * revenue4.revenuePercentageByType shouldBe revenue4.coin
  }

  "wbRevenueLog5 wbRevenueLog6" should "pass revenue check" in {
    import fixture._
    val revenue5 = WBRevenueLog.parse(wbRevenueLog5).get
    val revenue6 = WBRevenueLog.parse(wbRevenueLog6).get

    revenue5.revenuePercentageByType shouldBe 760.0
    revenue5.scoreBasedOnOperation shouldBe 6744.893740417306

    revenue6.revenuePercentageByType shouldBe 760.0
    revenue6.scoreBasedOnOperation shouldBe 5832.27038272358

    val group = (revenue5.scoreBasedOnOperation + revenue6.scoreBasedOnOperation)

    (revenue5.scoreBasedOnOperation / group) * revenue5.revenuePercentageByType shouldBe revenue5.coin

    (revenue6.scoreBasedOnOperation / group) * revenue6.revenuePercentageByType shouldBe revenue6.coin
  }


  "wbRevenueLog1 wbRevenueLog2" should "pass revenue check" in {
    import fixture._
    val log1 = WBRevenueLog.parse(wbRevenueLog1).get
    val log2 = WBRevenueLog.parse(wbRevenueLog2).get

    val groupA = log1.scoreBasedOnOperation + log2.scoreBasedOnOperation

    val revenueLog1 = (log1.scoreBasedOnOperation / groupA) * log1.revenuePercentageByType
    log1.coin shouldBe revenueLog1

    val revenueLog2 = (log2.scoreBasedOnOperation / groupA) * log2.revenuePercentageByType
    log2.coin shouldBe revenueLog2
  }

}
// scalastyle:on
