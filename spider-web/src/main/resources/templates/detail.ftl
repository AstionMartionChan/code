<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title></title>
    <link href="/spider-web/shopdetail.css" rel="stylesheet" type="text/css">
    <style type="text/css">
        table
        {
            border-collapse: collapse;
            margin: 0 auto;
            text-align: center;
            width:100%;
            border:0;
            margin:0;
            padding:0;
            height:100%;
        }
        table td
        {
            border: 1px solid #cad9ea;
            color: #666;
            height: 30px;
        }
    </style>
    <script src="/spider-web/jquery-1.9.1.min.js"></script>
    <script src="/spider-web/common.js"></script>
    <script type="text/javascript">
        $(document).ready(function(){
            var showproduct = {
                "boxid":"showbox",
                "sumid":"showsum",
                "boxw":400,
                "boxh":400,
                "sumw":60,//列表每个宽度,该版本中请把宽高填写成一样
                "sumh":60,//列表每个高度,该版本中请把宽高填写成一样
                "sumi":7,//列表间隔
                "sums":5,//列表显示个数
                "sumsel":"sel",
                "sumborder":1,//列表边框，没有边框填写0，边框在css中修改
                "lastid":"showlast",
                "nextid":"shownext"
            };//参数定义
            $.ljsGlasses.pcGlasses(showproduct);//方法调用，务必在加载完后执行

            $(function(){

                $('.tabs a').click(function(){

                    var $this=$(this);
                    $('.panel').hide();
                    $('.tabs a.active').removeClass('active');
                    $this.addClass('active').blur();
                    var panel=$this.attr("href");
                    $(panel).show();
                    return false;  //告诉浏览器  不要纸箱这个链接
                })//end click


                $(".tabs li:first a").click()   //web 浏览器，单击第一个标签吧



            })//end ready

            $(".centerbox li").click(function(){
                $("li").removeClass("now");
                $(this).addClass("now")

            });

            $.ajax({
                url: "/spider-web/getById?id=${id}",
                type: "GET",
                dataType: "json",
                success: function (data) {
                    $("#showbox").append('<img src="' + data.img_url + '" />')
                    $("#title").text(data.title);
                    $("#op_price").text("¥" + data.op_price);
                    $("#p_price").text("¥" + data.p_price);
                    $("#firstbuy").attr("href", data.url);

                    // 优惠券
                    var couponStr = "";
                    $.each(data.couponList, function(index, coupon) {
                        couponStr += ("满"+coupon.name+"减"+coupon.content+"  ")
                    });
                    $("#coupon").text(couponStr);

                    // 促销
                    var saleStr = "";
                    $.each(data.salesList, function(index, sale) {
                        saleStr += "<font>" + sale.name + "： " + sale.content + " </font><br/>"
                    });
                    $("#sale").append("促销活动：<br/>" + saleStr);

                    // 参数
                    var paramStr = "";
                    $.each(data.paramsList, function (index, param) {
                        paramStr += "<tr><td>" + param.name + "</td><td>" + param.content + "</td></tr>"
                    });
                    $("#params").append(paramStr);
                }
            });


            $.ajax({
                url: "/spider-web/getPriceHistory?id=${id}",
                type: "GET",
                dataType: "json",
                success: function (data){

                    var dom = document.getElementById("panel02");
                    var myChart = echarts.init(dom);
                    var app = {};
                    option = null;
                    option = {
                        title: {
                            text: '价格趋势图'
                        },
                        tooltip: {
                            trigger: 'axis'
                        },
                        legend: {
                            data:['原价','促销价格']
                        },
                        grid: {
                            left: '3%',
                            right: '4%',
                            bottom: '3%',
                            containLabel: true
                        },
                        toolbox: {
                            feature: {
                                saveAsImage: {}
                            }
                        },
                        xAxis: {
                            type: 'category',
                            boundaryGap: false,
                            data: []
                        },
                        yAxis: {
                            type: 'value'
                        },
                        series: [
                            {
                                name:'原价',
                                type:'line',
                                smooth: true,
                                itemStyle: {
                                    normal: {
                                        label: {
                                            show: true,
                                            position: 'insideLeft'
                                        }
                                    }
                                },
                                data:[]
                            },
                            {
                                name:'促销价格',
                                type:'line',
                                smooth: true,
                                itemStyle: {
                                    normal: {
                                        label: {
                                            show: true,
                                            position: 'insideRight'
                                        }
                                    }
                                },
                                data:[]
                            }
                        ]
                    };

                    option.series[0].data = data.opList
                    option.series[1].data = data.pList
                    option.xAxis.data = data.dateList

                    if (option && typeof option === "object") {
                        myChart.setOption(option, true);
                    }

                }
            });





        });

    </script>

    <script type="text/javascript" src="http://echarts.baidu.com/gallery/vendors/echarts/echarts.min.js"></script>
</head>

<body>
<!-----header部分------->
<div class="header">
    <div class="top">
        <div class="top1">
            <a href="welcome.html">登录</a>
            <a href="register.html">注册</a>
            <a href="#"><img src="images/index_img/top1.jpg">我的购物车</a>
            <a href="#"><img src="images/index_img/top3.jpg">联系我们</a>
        </div>
    </div>
    <!-----logo_search部分------->
    <div class="logobg">
        <div class="center">
            <div class="logo">
                <img src="images/index_img/logo.gif" width="249" height="55">
            </div>
            <form id="searchForm">
                <input type="text" id="searchTxt">
                <input type="submit" value="搜  索" id="search_btn">
            </form>
        </div>
    </div>
    <!-----主导航部分------->
    <div class="bottom">
        <div class="menu"><a href="#">全部商品分类</a></div>
        <div class="nav">
            <a href="index.html" class="now">首页</a>
            <a href="tuangou.html">团购促销</a>
            <a href="mingshihuicui.html">名师荟萃</a>
            <a href="yipinyizhan.html">艺品驿站</a>
            <a href="western.html">欧式摆件</a>
        </div>
    </div>


</div>
<!-----header结束------->
<!-----商品详情部分------->
<div class="shopdetails">
    <!-------放大镜-------->
    <div id="leftbox">
        <div id="showbox">

        </div><!--展示图片盒子-->
        <div id="showsum"></div><!--展示图片里边-->

    </div>
    <!----中间----->

    <div class="centerbox">
        <p class="imgname" id="title"></p>
        <p class="Aprice">价格：<samp id="op_price"></samp></p>
        <p class="price">促销价：<samp id="p_price"></samp></p>
        <p class="youhui">优惠券：<samp id="coupon"></samp></p>
        <#--<p class="kefu">客服：</p>-->
        <#--<ul>
            <li class="kuanshi">款式：</li>
            <li class="now shopimg"><a href="#" title="熊猫套装"><img src="images/shopdetail/kuanshi01.jpg" width="45"></a></li>
            <li class="shopimg"><a href="#" title="铁塔套装"><img src="images/shopdetail/kuanshi02.jpg" width="45"></a></li>
            <li class="shopimg"><a href="#" title="创意胡子"><img src="images/shopdetail/kuanshi03.jpg" width="45"></a></li>
            <li class="shopimg"><a href="#" title="四色小猫"><img src="images/shopdetail/kuanshi04.jpg" width="45"></a></li>
        </ul>-->
        <p class="chima" id="sale" style="width: 600px; margin-left: 0px; font-family: '微软雅黑'"></p>
        <p class="buy"><a href="" target="_blank" id="firstbuy">跳转京东商品页</a></p>
    </div>

</div>
<!-----商品详情部分结束------->
<!-----商品详情评价部分------->
<div class="evaluate">

    <div class="tabbedPanels">
        <ul class="tabs">
            <li><a href="#panel01">商品规格</a></li>
            <li><a href="#panel02" class="active">商品价格趋势图</a></li>
        </ul>

        <div class="panelContainer">
            <div class="panel" id="panel01">
                <p class="sell">商品规格</p>
                <table id="params">
                    <tr>
                        <td>参数</td>
                        <td>规格</td>
                    </tr>


                </table>
            </div>

            <div class="panel" id="panel02" style="width: 1100px;height:400px;">
                <p class="sell">买家评价</p>

            </div>


        </div>

    </div>

</div>

<!-----商品详情评价部结束分------->

<!----bottom_页脚部分----->
<div class="backf">
    <div id="footer">
        <ul>
            <li class="sy">支付方式</li>
            <li><a href="#">在线支付</a></li>
            <li><a href="#">货到付款</a></li>
            <li><a href="#">发票说明</a></li>
            <li><a href="#">余额宝</a></li>

        </ul>
        <ul>
            <li class="sy">购物指南</li>
            <li><a href="#">免费注册</a></li>
            <li><a href="#">申请会员</a></li>
            <li><a href="#">开通支付宝</a></li>
            <li><a href="#">支付宝充值</a></li>
        </ul>
        <ul>
            <li class="sy">商家服务</li>
            <li><a href="#">联系我们</a></li>
            <li><a href="#">客服服务</a></li>
            <li><a href="#">物流服务</a></li>
            <li><a href="#">缺货赔付</a></li>
        </ul>
        <ul>
            <li class="sy">关于我们</li>
            <li><a href="#">知识产权</a></li>
            <li><a href="#">网站合作</a></li>
            <li><a href="#">规则意见</a></li>
            <li><a href="#">帮助中心</a></li>
        </ul>
        <ul>
            <li class="sy">其他服务</li>
            <li><a href="#">诚聘英才</a></li>
            <li><a href="#">法律声明</a></li>

        </ul><div class="clear"></div>
    </div>
    <div class="foot">
        <p>使用本网站即表示接受 尚美衣店用户协议</p>
        <p>版权所有——————————————————</p>

    </div>
</div>

</body>
</html>
