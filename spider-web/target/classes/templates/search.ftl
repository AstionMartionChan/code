<html xmlns="http://www.w3.org/1999/html">
<style type="text/css">
    .main{
        text-align: center; /*让div内部文字居中*/
        background-color: #fff;
        border-radius: 20px;
        /*width: 300px;*/
        /*height: 350px;*/
        /*margin: auto;*/
        /*position: absolute;*/
        /*top: 0;*/
        /*left: 0;*/
        /*right: 0;*/
        /*bottom: 0;*/
    }

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
    table th
    {
        background-color: #CCE8EB;
        width: 100px;
        height: 10px;
    }
    table tr:nth-child(odd)
    {
        background: #fff;
    }
    table tr:nth-child(even)
    {
        background: #F5FAFA;
    }

    #searchContent {
        height: 30px;
        width: 500px;
    }
    #button #sort {
        height: 30px;
        width: 80px;
    }
</style>
<body>
<div class="main">
    <h2>京东商品搜索</h2>
    <form action="/spider-web/list" method="get" id="form">
        <input name="searchContent" type="text" id="searchContent"/>
        <select id="sort">
            <option selected value="">默认</option>
            <option value="asc">价格升序</option>
            <option value="desc">价格倒序</option>
        </select>
        <input type="button"  value="搜索" id="button">
    </form>
    </br>
    <table class="resultTable">
        <#--<caption>-->
            <#--<h2>商品列表</h2>-->
        <#--</caption>-->
        <thead>
        <tr>
            <th>
                图片
            </th>
            <th>
                名称
            </th>
            <th>
                价格
            </th>
            <th>
                促销价格
            </th>
            <th>
                时间
            </th>
        </tr>
        </thead>
    </table>
</div>

</body>

<script src="http://code.jquery.com/jquery-2.1.4.min.js"></script>
<script type="text/javascript">
    $("#button").click(function (){
        var searchContent = $("#searchContent").val();
        var sort = $("#sort").val();
        $.ajax({
            url: "/spider-web/list",
            type: "GET",
            data: {
                "searchContent": searchContent,
                "sort": sort
            },
            dataType: "json",
            success: function(data) {

                $(".appendTr").remove();

                var listContent = "";
                $.each(data, function(index, skuInfo) {
                    listContent += '<tr class="appendTr"><td><a href="/spider-web/detail?id=' + skuInfo.id + '" target="_blank"><img height="150" width="150"  src="' + skuInfo.img_url + '" /></a></td><td><a href="/spider-web/detail?id=' + skuInfo.id + '" target="_blank">' + skuInfo.title + '</a></td><td>' + skuInfo.op_price + ' </td> <td>' + skuInfo.p_price + ' </td> <td>' + skuInfo.time + ' </td></tr>'
                });

                $(".resultTable").append(listContent);
            }

        });
    });

</script>
</html>
