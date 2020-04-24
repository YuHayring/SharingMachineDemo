function json(jsonStr) {
    request({
        url: 'localhost:8080/sharing/register.do',
        method: 'put',
        data: {
            type: 103,
            name: "深圳老咸鱼",
            id: "hayring",
            password: "KID1412"
        }
    })
}

var reg = {
    type: 103,
    name: "深圳老咸鱼",
    id: "hayring",
    password: "KID1412"
}

function ajax() {
    var xmlHttp;
    if (window.XMLHttpRequest) {
        xmlHttp = new XMLHttpRequest;
    } else {
        xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    xmlHttp.onreadystatechange = function () {
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
            flag = xmlHttp.responseText;
        }
    }
    xmlHttp.open("post", "/sharing/register.do", false);
    xmlHttp.setRequestHeader("Content-type",
        "application/json");
    var data = document.getElementById("src").nodeValue;
    xmlHttp.send(JSON.stringify(reg));
}

function tokenTest() {
    request("test/tokenTest.do", null, tokenTestBack, "get");
}

function tokenTestBack() {

    if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
        alert("tokensuccess=" + xmlhttp.responseText);
    }
}


//请求
function request(url, data, func, type) {
    //初始化
    if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
        xmlhttp = new XMLHttpRequest();
    } else {// code for IE6, IE5
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    //设置数据
    xmlhttp.onreadystatechange = func;
    xmlhttp.open(type, url, true);
    xmlhttp.setRequestHeader("Content-Type", "application/json; charset=utf-8");
    var token = localStorage.getItem("key_token");
    if (token != null) {
        xmlhttp.setRequestHeader("Authorization", "Bearer" + token);
    }
    //请求
    if (data == null) {
        xmlhttp.send();
    } else {
        xmlhttp.send(JSON.stringify(data));
    }

}