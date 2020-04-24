var xmlhttp;

//请求
function request(url, data, func, method) {
    //初始化
    if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
        xmlhttp = new XMLHttpRequest();
    } else {// code for IE6, IE5
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    //设置数据
    xmlhttp.onreadystatechange = function () {
        updateAuthorization(func, xmlhttp);
    }; //func;


    xmlhttp.open(method, url, true);
    //xmlhttp.setRequestHeader("Content-Type", "application/json; charset=utf-8");
    xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
    var token = sessionStorage.getItem("key_token");
    if (token != null) {
        xmlhttp.setRequestHeader("Authorization", token);
    }
    //请求
    if (data == null) {
        xmlhttp.send();
    } else {
        xmlhttp.send(data);
    }

}

function updateAuthorization(func) {
    if (xmlhttp.readyState == 4) {
        var token = xmlhttp.getResponseHeader("Authorization");
        if (token != null) {
            sessionStorage.setItem("key_token", "Bearer" + token);
        }
        func();
    }

}

function formatDate(timestamp) {
//timestamp是整数，否则要parseInt转换
    var time = new Date(timestamp);
    var y = time.getFullYear();
    var m = time.getMonth() + 1;
    var d = time.getDate();
    var h = time.getHours();
    var mm = time.getMinutes();
    var s = time.getSeconds();
    return y + '-' + add0(m) + '-' + add0(d) + ' ' + add0(h) + ':' + add0(mm) + ':' + add0(s);
}

function add0(m) {
    return m < 10 ? '0' + m : m
}