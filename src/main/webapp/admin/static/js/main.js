var add = "../../machine/";
var xmlhttp;
var machine;


//登出
function logout() {
    var id = sessionStorage.getItem("id");
    request("../" + id + "/session", null, goBack, "delete")
}

function goBack() {
    if (xmlhttp.status === 204) {
        sessionStorage.removeItem("key_token")
        window.location.href = "../../rootLogin.html?msg=已注销"
    }
    if (xmlhttp.status == 500) {
        alert("服务器错误");
    }

}


//获取设备信息
function getMachine() {
    // var address = add + "?machineId=" +  document.getElementById("machineIdInput").value;
    // var frame = document.getElementById("showMachine");
    // frame.src = address;
    // frame.style.display="block";

    var madd = add + document.getElementById("machineIdInput").value;
    //请求
    request(madd, null, showMachine, "get");

    document.getElementById("control").hidden = true;
    var div = document.getElementById("machineInfo");
    if (div.cells.length !== 0) {
        for (var i = 4; i > -1; i--) {
            div.deleteCell(i)
        }
    }
}

//显示设备信息
function showMachine() {
    if (xmlhttp.status === 200) {
        machine = JSON.parse(xmlhttp.responseText);

        //显示信息
        document.getElementById("noneLabel").hidden = true;

        var div = document.getElementById("machineInfo");
        div.insertCell(0).innerText = machine.id;
        switch (machine.status) {

            case -1 : {
                document.getElementById("control").hidden = true;
                div.insertCell(1).innerText = "关机";
            }
                break;
            case 0 : {
                div.insertCell(1).innerText = "等待";
                document.getElementById("control").hidden = false;
                document.getElementById("control").value = "启动";
            }
                break;
            case 1 : {
                document.getElementById("control").hidden = true;
                div.insertCell(1).innerText = "运行中";
            }
        }
        div.insertCell(2).innerText = machine.address;
        div.insertCell(3).innerText = machine.longitude;
        div.insertCell(4).innerText = machine.latitude;
    } else if (xmlhttp.status == 404) {
        alert("找不到设备");
    } else if (xmlhttp.status == 401) {
        window.location = "/sharing/rootLogin.html?msg=%e6%9c%aa%e7%99%bb%e5%bd%95"
    }
}

//获取用户信息
function getUserInfo() {
    var id = sessionStorage.getItem("id");
    request("../" + id, null, getInfoBack, "get")
}

//显示信息
function getInfoBack() {
    if (xmlhttp.status === 200) {

        var user = JSON.parse(xmlhttp.responseText);
        var div = document.getElementById("userNameLabel");
        div.innerText += user.name;
    } else if (xmlhttp.status == 401) {
        window.location = "/sharing/rootLogin.html?msg=%e6%9c%aa%e7%99%bb%e5%bd%95"
    } else if (xmlhttp == 500) {
        alert("服务器错误")
    }

}


//运行
function maintenance() {
    request("../../machine/" + machine.id + "/maintain", null, runBack, "post");
}

function runBack() {
    if (xmlhttp.status === 200) {
        alert("运行成功");
    }
}

