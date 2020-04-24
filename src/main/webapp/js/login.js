var targetLocation;

//body加载调用
function bodyOnLoad() {
    var msg = getQueryVariable("msg");
    if (msg != false) {

        var msgDiv = getNewMessageDiv();
        msgDiv.innerText = decodeURI(msg);
    }
}


//body下插入div
function getNewMessageDiv() {
    var divObj = document.createElement("div");
    divObj.id = "message";
    var first = document.body.firstChild;//得到页面的第一个元素。
    document.body.insertBefore(divObj, first);//在得到的第一个元素之前插入。
    return divObj
}

//获取url参数
function getQueryVariable(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split("=");
        if (pair[0] == variable) {
            return pair[1];
        }
    }
    return false;
}

//登录
function login() {
    var url = targetLocation + "/" + document.getElementById("id").value + "/session?password=" + document.getElementById("password").value;
    var method = "post";
    request(url, null, loginSuccess, method);

}

//响应
function loginSuccess() {
    switch (xmlhttp.status) {
        case 200: {
            var response = JSON.parse(xmlhttp.responseText);
            sessionStorage.setItem("key_token", "Bearer" + response.token);
            sessionStorage.setItem("id", response.id);
            window.location.href = targetLocation + "/static/main.html";

        }
            break;
        case 401: {
            alert("用户名或密码错误");
        }
            break;
        case 500: {
            alert("服务器错误")
        }
            break;
        default: {
            alert("错误！状态码：" + xmlhttp.status);
        }

    }
}

//清除登录参数
function clear() {
    document.getElementById("id").value = "";
    document.getElementById("password").value = "";
    var div = document.getElementById("p1")
    if (div != null) {
        div.value = "";
    }
}