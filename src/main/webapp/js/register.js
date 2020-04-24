const REGIST_ARGS = 103;


var xmlhttp;

//检查密码是否相同
function mycheck() {
    if (document.getElementById("password").value !== document.getElementById("p1").value) {
        alert("两次输入的密码不正确，请更正。");
        return false;
    } else {
        return true;
    }
}

//注册
function register() {
    if (mycheck()) {
        //初始化
        if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
            xmlhttp = new XMLHttpRequest();
        } else {// code for IE6, IE5
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
        }
        //设置数据
        xmlhttp.onreadystatechange = registSuccess;
        xmlhttp.open("post", "registerJson.do", true);
        xmlhttp.setRequestHeader("Content-Type", "application/json; charset=utf-8");
        var loginParam = {
            type: REGIST_ARGS,
            id: document.getElementById("id").value,
            password: document.getElementById("password").value,
            name: document.getElementById("userName").value
        };
        var json = JSON.stringify(loginParam);
        //发送
        xmlhttp.send(json);
    }


}

function registSuccess() {
    if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {

        var response = JSON.parse(xmlhttp.responseText);
        switch (response.type) {
            //注册失败
            case ERROR_MESSAGE: {
                alert(response.message);
            }
                break;
            //注册成功
            case 1: {
                window.location.href = 'login.html?msg=注册成功';
                unescape();
            }
                break;
        }
    }
}