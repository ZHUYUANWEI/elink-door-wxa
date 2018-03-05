<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<head>
    <title>Java后端WebSocket的Tomcat实现</title>
</head>
<body>
    Welcome<br/><input id="text" type="text"/>
    <button onclick="send()">发送消息</button>
    <hr/>
    <button onclick="closeWebSocket()">关闭WebSocket连接</button>
    <hr/>
    <button onclick="queryRequest()">访客</button>
    <hr/>
    <button onclick="queryRequest2()">测试</button>
    <hr/>
    <div id="message"></div>
</body>
<script src="${ctx}/resources/js/jquery-1.10.2.js"></script>
<script type="text/javascript">
    var websocket = null;
    //判断当前浏览器是否支持WebSocket
    if ('WebSocket' in window) {
        websocket = new WebSocket("ws://localhost:8080/elinkDoorBackend/websocket");
    }
    else {
        alert('当前浏览器 Not support websocket')
    }

    //连接发生错误的回调方法
    websocket.onerror = function () {
        setMessageInnerHTML("WebSocket连接发生错误");
    };

    //连接成功建立的回调方法
    websocket.onopen = function () {
        setMessageInnerHTML("WebSocket连接成功");
    }

    //接收到消息的回调方法
    websocket.onmessage = function (event) {
        setMessageInnerHTML(event.data);
    }

    //连接关闭的回调方法
    websocket.onclose = function () {
        setMessageInnerHTML("WebSocket连接关闭");
    }

    //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function () {
        closeWebSocket();
    }

    //将消息显示在网页上
    function setMessageInnerHTML(innerHTML) {
        document.getElementById('message').innerHTML += innerHTML + '<br/>';
    }

    //关闭WebSocket连接
    function closeWebSocket() {
        websocket.close();
    }

    //发送消息
    function send() {
        var message = document.getElementById('text').value;
        websocket.send(message);
    }
    function queryRequest(){   
    	console.log("临时ajax请求");
    	var dataj = { "cardNo": "24102180301", "personId":"241021803", "period": "h12", "unlockCount": "12", "visitorName": "王兴亮", "phone": "15053207853", "startTime": "2017-09-29 15:42:00", "endTime":"2017-10-04 10:00:00"};
        $.ajax({
            type: 'GET',
            url: 'getGuestQR',
            data: dataj,
            dataType: "json",
            async: false,
            cache: false,
            error: function () {
                alert("again");
                return false;
            },
            success: function (data) {
                //alert("win");
            }
        });

    }
    function queryRequest2(){   
    	console.log("临时ajax请求");
    	var dataj = { "visitorName": "王兴亮", "phone": "15053207853", "startTime": "2017-09-29 15:42:00", "endTime":"2017-10-04 10:00:00"};
        $.ajax({
            type: 'GET',
            url: 'getCustomQR',
            data: dataj,
            dataType: "json",
            async: false,
            cache: false,
            error: function () {
                alert("again");
                return false;
            },
            success: function (data) {
                //alert("win");
            }
        });

    }
</script>
</html>